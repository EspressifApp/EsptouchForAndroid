package com.espressif.iot.esptouch.protocol;

import java.net.InetAddress;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.CRC8;

public class DatumCode implements ICodeData {
	
	// define by the Esptouch protocol, all of the datum code should add 1 at last to prevent 0
	private static final int EXTRA_LEN = 200;
	
	private final DataCode[] mDataCodes;
	
	/**
	 * Constructor of DatumCode
	 * @param apSsid the Ap's ssid
	 * @param apPassword the Ap's password
	 * @param ipAddress the ip address of the phone or pad
	 */
	public DatumCode(String apSsid, String apPassword, InetAddress ipAddress) {
		// Data = total len(1 byte) + apPwd len(1 byte) + SSID CRC(1 byte) + ipAddress(4 byte) + apPwd + apSsid
		// apPwdLen <= 104 at the moment
		char apPwdLen = (char) ByteUtil.getBytesByString(apPassword).length;
		CRC8 crc = new CRC8();
		crc.update(ByteUtil.getBytesByString(apSsid));
		char apSsidCrc = (char) crc.getValue();
		
		char apSsidLen = (char) ByteUtil.getBytesByString(apSsid).length;
		// hostname parse
		String ipAddrStrs[] = ipAddress.getHostAddress().split("\\.");
		int ipLen = ipAddrStrs.length;
		
		char ipAddrChars[] = new char[ipLen];
		// only support ipv4 at the moment
		for (int i = 0; i < ipLen; ++i) {
			ipAddrChars[i] = (char) Integer.parseInt(ipAddrStrs[i]);
		}
		
		char totalLen = (char) (3 + ipLen + apPwdLen + apSsidLen);
		
		// build data codes
		mDataCodes = new DataCode[totalLen];
		mDataCodes[0] = new DataCode(totalLen, 0);
		mDataCodes[1] = new DataCode(apPwdLen, 1);
		mDataCodes[2] = new DataCode(apSsidCrc, 2);
		for (int i = 0; i < ipLen; ++i) {
			mDataCodes[i + 3] = new DataCode(ipAddrChars[i], i + 3);
		}
		byte[] apSsidPwdBytes = ByteUtil.getBytesByString(apPassword + apSsid);
		char[] apSsidPwdChars = new char[apSsidPwdBytes.length];
		for (int i = 0;i < apSsidPwdBytes.length; i++) {
			apSsidPwdChars[i] = ByteUtil.convertByte2Uint8(apSsidPwdBytes[i]);
		}
		for (int i = 0; i < apSsidPwdChars.length; i++) {
			mDataCodes[i + 3 + ipLen] = new DataCode(apSsidPwdChars[i], i + 3
					+ ipLen);
		}
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
			dataU8s[i] = (char) (ByteUtil.combine2bytesToU8(high, low) + EXTRA_LEN);
		}
		return dataU8s;
	}
}
