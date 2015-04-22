package com.espressif.iot.esptouch.protocol;

import com.espressif.iot.esptouch.task.ICodeData;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.CRC8;

/**
 *            high 5 bits   low 4 bits 
 * 5th 9bits:   0x4        pwd length(high)
 * 6th 9bits:   0x5        pwd length(low)
 * 7th 9bits:   0x6        pwd len crc(high)
 * 8th 9bits:   0x7        pwd len crc(low)
 * 
 * @author afunx
 * 
 */
public class PrefixCode implements ICodeData{

	public static final int PREFIX_CODE_LEN = 8;

	private final byte mPwdLengthHigh;
	private final byte mPwdLengthLow;
	private final byte mPwdLenCrcHigh;
	private final byte mPwdLenCrcLow;
    
    /**
     * Constructor of PrefixCode
     * 
     * @param pwdLen the lengh of Ap's password
     */
    public PrefixCode(char pwdLen)
    {
    	byte[] pwdLengthBytes = ByteUtil.splitUint8To2bytes(pwdLen);
    	mPwdLengthHigh = pwdLengthBytes[0];
    	mPwdLengthLow = pwdLengthBytes[1];
    	CRC8 crc8 = new CRC8();
        crc8.update(pwdLen);
        char crcValue = (char) crc8.getValue();
        byte[] crcBytes = ByteUtil.splitUint8To2bytes(crcValue);
        mPwdLenCrcHigh = crcBytes[0];
        mPwdLenCrcLow = crcBytes[1];
    }
    
	@Override
	public byte[] getBytes() {
		byte[] magicBytes = new byte[PREFIX_CODE_LEN];
        magicBytes[0] = 0x00;
        magicBytes[1] = ByteUtil.combine2bytesToOne((byte) 0x04, mPwdLengthHigh);
        magicBytes[2] = 0x00;
        magicBytes[3] = ByteUtil.combine2bytesToOne((byte) 0x05, mPwdLengthLow);
        magicBytes[4] = 0x00;
        magicBytes[5] = ByteUtil.combine2bytesToOne((byte) 0x06, mPwdLenCrcHigh);
        magicBytes[6] = 0x00;
        magicBytes[7] = ByteUtil.combine2bytesToOne((byte) 0x07, mPwdLenCrcLow);
        return magicBytes;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        byte[] preficBytes = getBytes();
        for (int i = 0; i < PREFIX_CODE_LEN; i++) {
            String hexString = ByteUtil.convertByte2HexString(preficBytes[i]);
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
        // test pwd len < 16
        char pwdLen = 10;
        PrefixCode pc = new PrefixCode(pwdLen);
        byte[] bytes = pc.getBytes();
        if(bytes[0]==0x00&&bytes[1]==0x40&&bytes[2]==0x00&&bytes[3]==0x5a&&bytes[4]==0x00&&bytes[5]==0x67&&bytes[6]==0x00&&bytes[7]==0x7e)
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
        // test pwd len > 16
        char pwdLen = 20;
        PrefixCode pc = new PrefixCode(pwdLen);
        byte[] bytes = pc.getBytes();
        if(bytes[0]==0x00&&bytes[1]==0x41&&bytes[2]==0x00&&bytes[3]==0x54&&bytes[4]==0x00&&bytes[5]==0x6f&&bytes[6]==0x00&&bytes[7]==0x7c)
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
