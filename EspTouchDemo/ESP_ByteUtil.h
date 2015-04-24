//
//  ESP_ByteUtil.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/7/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESP_ByteUtil : NSObject

#define ESPTOUCH_NSStringEncoding NSUTF8StringEncoding

/**
 * Convert uint8 into char( we treat char as uint8)
 *
 * @param uint8
 *            the unit8 to be converted
 * @return the byte of the unint8
 */
+ (Byte) convertUint8toByte:(char) uint8;

/**
 * Convert char into uint8( we treat char as uint8 )
 *
 * @param b
 *            the byte to be converted
 * @return the UInt8(uint8)
 */
+ (UInt8) convertByte2Uint8:(Byte) b;

/**
 * Convert byte to Hex String
 *
 * @param b
 *            the byte to be converted
 * @return the Hex String
 */
+ (NSString *) convertByte2HexString:(Byte) b;

/**
 * Split uint8 to 2 bytes of high byte and low byte. e.g. 20 = 0x14 should
 * be split to [0x01,0x04] 0x01 is high byte and 0x04 is low byte
 *
 * @param uint8
 *            the char(uint8)
 * @return the high and low bytes be split, byte[0] is high and byte[1] is
 *         low
 */
+ (NSData *) splitUint8To2Bytes: (UInt8) uint8;

/**
 * Combine 2 bytes (high byte and low byte) to one whole byte
 *
 * @param high
 *            the high byte
 * @param low
 *            the low byte
 * @return the whole byte
 */
+ (Byte) combine2bytesToOneWithHigh: (Byte) high andLow: (Byte) low;

/**
 * Combine 2 bytes (high byte and low byte) to one UInt16
 *
 * @param high
 *            the high byte
 * @param low
 *            the low byte
 * @return the UInt8
 */
+ (UInt16) combine2bytesToU16WithHigh: (Byte) high andLow: (Byte) low;

/**
 * Generate the random byte to be sent
 *
 * @return the random byte
 */
+ (Byte) randomByte;

/**
 * Generate the random byte to be sent
 *
 * @param len
 *            the len presented by u8
 * @return the byte[] to be sent
 */
+ (NSData *) randomBytes: (UInt8) len;

+ (NSData *) genSpecBytesWithU16: (UInt16) len;

/**
 * Generate the specific byte to be sent
 * @param len
 *            the len presented by byte
 * @return the byte[]
 */
+ (NSData *) genSpecBytesWithU8:(Byte) len;

+ (NSString *) parseBssid:(Byte[]) bssidBytes Offset: (int) offset Count: (int) count;

/**
 * parse "24,-2,52,-102,-93,-60" to "18,fe,34,9a,a3,c4" parse the bssid from
 * hex to String
 *
 * @param bssidBytes
 *            the hex bytes bssid, e.g. {24,-2,52,-102,-93,-60}
 * @param len
 *            the len of bssidBytes
 * @return the String of bssid, e.g. 18fe349aa3c4
 */
+ (NSString *) parseBssid:(Byte[]) bssidBytes Len: (int) len;

/**
 * @param string the string to be used
 * @return the Byte[] of string according to ESPTOUCH_NSStringEncoding
 */
+ (NSData *) getBytesByNSString: (NSString *)string;

/**
 * get hex string transformed by data
 * @param data the data to be transformed
 * @return the hex String transformed by data
 */
+ (NSString *) getHexStringByData: (NSData *)data;

@end
