package com.espressif.iot.esptouch.protocol;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;

public class GuideCode implements ICodeData {

	public static final int GUIDE_CODE_LEN = 4;

	@Override
	public byte[] getBytes() {
		throw new RuntimeException("DataCode don't support getBytes()");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		char[] dataU8s = getU8s();
		for (int i = 0; i < GUIDE_CODE_LEN; i++) {
			String hexString = ByteUtil.convertU8ToHexString(dataU8s[i]);
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
		char[] guidesU8s = new char[GUIDE_CODE_LEN];
		guidesU8s[0] = 259;
		guidesU8s[1] = 258;
		guidesU8s[2] = 257;
		guidesU8s[3] = 256;
		return guidesU8s;
	}
}
