package com.espressif.iot.esptouch.protocol;

import com.espressif.iot.esptouch.task.IEsptouchGenerator;
import com.espressif.iot.esptouch.util.ByteUtil;

public class EsptouchGenerator implements IEsptouchGenerator {

	private final byte[][] mGcBytes2;
	private final byte[][] mMcBytes2;
	private final byte[][] mPcBytes2;
	private final byte[][] mDcBytes2;

	/**
	 * Constructor of EsptouchGenerator, it will cost some time(maybe a bit much)
	 * 
	 * @param apSsid
	 *            the Ap's ssid
	 * @param apPassword
	 *            the Ap's password
	 */
	public EsptouchGenerator(String apSsid, String apPassword) {

		// the u8 total len of apSsid and apPassword
		char totalLen = (char) (apSsid.length() + apPassword.length());
		// the u8 len of apPassword
		char pwdLen = (char) apPassword.length();

		// generate guide code
		GuideCode gc = new GuideCode();
		byte[] gcBytes1 = gc.getBytes();
		mGcBytes2 = new byte[gcBytes1.length][];

		for (int i = 0; i < mGcBytes2.length; i++) {
			mGcBytes2[i] = ByteUtil.genSpecBytes(gcBytes1[i]);
		}

		// generate magic code
		MagicCode mc = new MagicCode(totalLen, apSsid);
		char[] mcU81 = mc.getU8s();
		mMcBytes2 = new byte[mcU81.length][];

		for (int i = 0; i < mMcBytes2.length; i++) {
			mMcBytes2[i] = ByteUtil.genSpecBytes(mcU81[i]);
		}

		// generate prefix code
		PrefixCode pc = new PrefixCode(pwdLen);
		char[] pcU81 = pc.getU8s();
		mPcBytes2 = new byte[pcU81.length][];

		for (int i = 0; i < mPcBytes2.length; i++) {
			mPcBytes2[i] = ByteUtil.genSpecBytes(pcU81[i]);
		}

		// generate data code
		DatumCode dc = new DatumCode(apSsid, apPassword);
		char[] dcU81 = dc.getU8s();
		mDcBytes2 = new byte[dcU81.length][];

		for (int i = 0; i < mDcBytes2.length; i++) {
			mDcBytes2[i] = ByteUtil.genSpecBytes(dcU81[i]);
		}
	}

	@Override
	public byte[][] getGCBytes2() {

		return mGcBytes2;
	}

	@Override
	public byte[][] getMCBytes2() {
		return mMcBytes2;
	}

	@Override
	public byte[][] getPCBytes2() {
		return mPcBytes2;
	}

	@Override
	public byte[][] getDCBytes2() {
		return mDcBytes2;
	}

}
