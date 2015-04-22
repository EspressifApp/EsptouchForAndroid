package com.espressif.iot.esptouch.protocol;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;

public class DatumCode implements ICodeData {
	
	private final DataCode[] mDataCodes;
	
	/**
	 * Constructor of DatumCode
	 * @param apSsid the Ap's ssid
	 * @param apPassword the Ap's password
	 */
	public DatumCode(String apSsid, String apPassword) {
		// note apPassword must before apSsid
		String info = apPassword + apSsid;
		int infoLen = info.length();
		mDataCodes = new DataCode[infoLen];
		char[] infoChars = new char[infoLen];
		for (int i = 0; i < infoChars.length; i++) {
			infoChars[i] = info.charAt(i);
		}
		for (int i = 0; i < infoLen; i++) {
			mDataCodes[i] = new DataCode(infoChars[i], i);
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
			dataU8s[i] = ByteUtil.combine2bytesToU8(high, low);
		}
		return dataU8s;
	}
}
