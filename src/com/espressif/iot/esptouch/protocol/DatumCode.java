package com.espressif.iot.esptouch.protocol;

import java.net.InetAddress;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.CRC8;
import com.espressif.iot.esptouch.util.EspNetUtil;

public class DatumCode implements ICodeData {
	
	// define by the Esptouch protocol, all of the datum code should add 1 at last to prevent 0
	private static final int EXTRA_LEN = 40;
	private static final int EXTRA_HEAD_LEN = 5;
	
	private final DataCode[] mDataCodes;
	
	/**
	 * Constructor of DatumCode
	 * @param apSsid the Ap's ssid
	 * @param apBssid the Ap's bssid
	 * @param apPassword the Ap's password
	 * @param ipAddress the ip address of the phone or pad
	 * @param isSsidHidden whether the Ap's ssid is hidden
	 */
	public DatumCode(String apSsid, String apBssid, String apPassword,
			InetAddress ipAddress, boolean isSsidHiden) {
		// Data = total len(1 byte) + apPwd len(1 byte) + SSID CRC(1 byte) +
		// BSSID CRC(1 byte) + TOTAL XOR(1 byte)+ ipAddress(4 byte) + apPwd + apSsid apPwdLen <=
		// 105 at the moment
		
		// total xor
		char totalXor = 0;
		
		char apPwdLen = (char) ByteUtil.getBytesByString(apPassword).length;
		CRC8 crc = new CRC8();
		crc.update(ByteUtil.getBytesByString(apSsid));
		char apSsidCrc = (char) crc.getValue();
		
		crc.reset();
		crc.update(EspNetUtil.parseBssid2bytes(apBssid));
		char apBssidCrc = (char) crc.getValue();
		
		char apSsidLen = (char) ByteUtil.getBytesByString(apSsid).length;
		// hostname parse
		String ipAddrStrs[] = ipAddress.getHostAddress().split("\\.");
		int ipLen = ipAddrStrs.length;
		
		char ipAddrChars[] = new char[ipLen];
		// only support ipv4 at the moment
		for (int i = 0; i < ipLen; ++i) {
			ipAddrChars[i] = (char) Integer.parseInt(ipAddrStrs[i]);
		}
		
		
		char _totalLen = (char) (EXTRA_HEAD_LEN + ipLen + apPwdLen + apSsidLen);
		char totalLen = isSsidHiden ? (char) (EXTRA_HEAD_LEN + ipLen + apPwdLen + apSsidLen)
				: (char) (EXTRA_HEAD_LEN + ipLen + apPwdLen);
		
		// build data codes
		mDataCodes = new DataCode[totalLen];
		mDataCodes[0] = new DataCode(_totalLen, 0);
		totalXor ^= _totalLen;
		mDataCodes[1] = new DataCode(apPwdLen, 1);
		totalXor ^= apPwdLen;
		mDataCodes[2] = new DataCode(apSsidCrc, 2);
		totalXor ^= apSsidCrc;
		mDataCodes[3] = new DataCode(apBssidCrc, 3);
		totalXor ^= apBssidCrc;
		mDataCodes[4] = null;
		for (int i = 0; i < ipLen; ++i) {
			mDataCodes[i + EXTRA_HEAD_LEN] = new DataCode(ipAddrChars[i], i + EXTRA_HEAD_LEN);
			totalXor ^= ipAddrChars[i];
		}
		
		byte[] apPwdBytes = ByteUtil.getBytesByString(apPassword);
		char[] apPwdChars = new char[apPwdBytes.length];
		for (int i = 0;i < apPwdBytes.length; i++) {
			apPwdChars[i] = ByteUtil.convertByte2Uint8(apPwdBytes[i]);
		}
		for (int i = 0; i < apPwdChars.length; i++) {
			mDataCodes[i + EXTRA_HEAD_LEN + ipLen] = new DataCode(
					apPwdChars[i], i + EXTRA_HEAD_LEN + ipLen);
			totalXor ^= apPwdChars[i];
		}

		byte[] apSsidBytes = ByteUtil.getBytesByString(apSsid);
		char[] apSsidChars = new char[apSsidBytes.length];
		
		// totalXor will xor apSsidChars no matter whether the ssid is hidden
		for (int i = 0; i < apSsidBytes.length; i++) {
			apSsidChars[i] = ByteUtil.convertByte2Uint8(apSsidBytes[i]);
			totalXor ^= apSsidChars[i];
		}
		
		if (isSsidHiden) {
			for (int i = 0; i < apSsidChars.length; i++) {
				mDataCodes[i + EXTRA_HEAD_LEN + ipLen + apPwdLen] = new DataCode(
						apSsidChars[i], i + EXTRA_HEAD_LEN + ipLen + apPwdLen);
			}
		}
		
		// set total xor last
		mDataCodes[4] = new DataCode(totalXor, 4);
	}
	
	@Override
	public byte[] getBytes() {
		byte[] datumCode = new byte[mDataCodes.length * DataCode.DATA_CODE_LEN];
		for (int i = 0; i < mDataCodes.length; i++) {
			System.arraycopy(mDataCodes[i].getBytes(), 0, datumCode, i
					* DataCode.DATA_CODE_LEN, DataCode.DATA_CODE_LEN);
		}
		return datumCode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		byte[] dataBytes = getBytes();
		for (int i = 0; i < dataBytes.length; i++) {
			String hexString = ByteUtil.convertByte2HexString(dataBytes[i]);
			sb.append("0x");
			if (hexString.length() == 1) {
				sb.append("0");
			}
			sb.append(hexString).append(" ");
		}
		return sb.toString();
	}
	
	@Override
	public char[] getU8s() {
		byte[] dataBytes = getBytes();
		int len = dataBytes.length / 2;
		char[] dataU8s = new char[len];
		byte high, low;
		for (int i = 0; i < len; i++) {
			high = dataBytes[i * 2];
			low = dataBytes[i * 2 + 1];
			dataU8s[i] = (char) (ByteUtil.combine2bytesToU16(high, low) + EXTRA_LEN);
		}
		return dataU8s;
	}
}
