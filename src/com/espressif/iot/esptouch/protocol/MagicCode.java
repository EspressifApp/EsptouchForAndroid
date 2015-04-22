package com.espressif.iot.esptouch.protocol;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.CRC8;

/**
 *            high 5 bits    low 4 bits 
 * 1st 9bits:   0x0         length(high)
 * 2nd 9bits:   0x1         length(low)
 * 3rd 9bits:   0x2         ssid crc(high)
 * 4th 9bits:   0x3         ssid crc(low)
 * 
 * @author afunx
 * 
 */
public class MagicCode implements ICodeData{

    public static final int MAGIC_CODE_LEN = 8;
    private static final int MAGIC_THRESHOLD = 16;
    private static final int MAGIC_NUMBER = 128;

    // the len here means the length of all data to be transformed
    private final byte mLengthHigh;
    private final byte mLengthLow;
    // the crc here means the crc of the Ap's ssid be transformed
    private final byte mSsidCrcHigh;
    private final byte mSsidCrcLow;

    /**
     * Constructor of MagicCode
     * 
     * @param totalLen
     *            the total len of Ap's password and Ap's ssid
     * @param ssid
     *            the Ap's ssid
     */
    public MagicCode(char totalLen, String ssid) {
        if (totalLen < MAGIC_THRESHOLD) {
            totalLen += MAGIC_NUMBER;
        }
        byte[] lengthBytes = ByteUtil.splitUint8To2bytes(totalLen);
        mLengthHigh = lengthBytes[0];
        mLengthLow = lengthBytes[1];
        CRC8 crc8 = new CRC8();
        crc8.update(ssid.getBytes());
        char crcValue = (char) crc8.getValue();
        byte[] crcBytes = ByteUtil.splitUint8To2bytes(crcValue);
        mSsidCrcHigh = crcBytes[0];
        mSsidCrcLow = crcBytes[1];
    }

    @Override
    public byte[] getBytes() {
        byte[] magicBytes = new byte[MAGIC_CODE_LEN];
        magicBytes[0] = 0x00;
        magicBytes[1] = ByteUtil.combine2bytesToOne((byte) 0x00, mLengthHigh);
        magicBytes[2] = 0x00;
        magicBytes[3] = ByteUtil.combine2bytesToOne((byte) 0x01, mLengthLow);
        magicBytes[4] = 0x00;
        magicBytes[5] = ByteUtil.combine2bytesToOne((byte) 0x02, mSsidCrcHigh);
        magicBytes[6] = 0x00;
        magicBytes[7] = ByteUtil.combine2bytesToOne((byte) 0x03, mSsidCrcLow);
        return magicBytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        byte[] magicBytes = getBytes();
        for (int i = 0; i < MAGIC_CODE_LEN; i++) {
            String hexString = ByteUtil.convertByte2HexString(magicBytes[i]);
            sb.append("0x");
            if(hexString.length()==1)
            {
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
	
	public static void testLess16()
	{
	    // test ssid and password len < 16
        char len = 10;
        String ssid = "wifi-1";
        MagicCode mc = new MagicCode(len, ssid);
        byte[] bytes = mc.getBytes();
        if(bytes[0]==0x00&&bytes[1]==0x08&&bytes[2]==0x00&&bytes[3]==0x1a&&bytes[4]==0x00&&bytes[5]==0x2e&&bytes[6]==0x00&&bytes[7]==0x35)
        {
            System.out.println("testLess16() pass");
        }
        else
        {
            System.out.println("testLess16() fail");
        }
	}
	public static void testBigger16()
    {
        // test ssid and password len < 16
        char len = 20;
        String ssid = "wifi-1";
        MagicCode mc = new MagicCode(len, ssid);
        byte[] bytes = mc.getBytes();
        if(bytes[0]==0x00&&bytes[1]==0x01&&bytes[2]==0x00&&bytes[3]==0x14&&bytes[4]==0x00&&bytes[5]==0x2e&&bytes[6]==0x00&&bytes[7]==0x35)
        {
            System.out.println("testBigger16() pass");
        }
        else
        {
            System.out.println("testBigger16() fail");
        }
    }
	
	public static void main(String args[])
	{
	    testLess16();
	    testBigger16();
	}
}
