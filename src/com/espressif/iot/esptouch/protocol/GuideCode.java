package com.espressif.iot.esptouch.protocol;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;

public class GuideCode implements ICodeData {

	public static final int GUIDE_CODE_LEN = 4;

	@Override
	public byte[] getBytes() {
		byte[] guideBytes = new byte[GUIDE_CODE_LEN];
		guideBytes[0] = 0x01;
		guideBytes[1] = 0x02;
		guideBytes[2] = 0x03;
		guideBytes[3] = 0x04;
		return guideBytes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		byte[] dataBytes = getBytes();
		for (int i = 0; i < GUIDE_CODE_LEN; i++) {
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
		throw new RuntimeException("DataCode don't support getU8s()");
	}
}
