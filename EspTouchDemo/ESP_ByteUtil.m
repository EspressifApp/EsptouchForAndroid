//
//  ESP_ByteUtil.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/7/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESP_ByteUtil.h"

@implementation ESP_ByteUtil

+ (Byte) convertUint8toByte:(char) uint8
{
    return (Byte)uint8;
}

+ (UInt8) convertByte2Uint8:(Byte) b
{
    return (UInt8) (b & 0xff);
}

+ (NSString *) convertByte2HexString:(Byte) b
{
    UInt8 u8 = [self convertByte2Uint8:b];
    return [NSString stringWithFormat:@"%x",u8];
}

+ (unsigned int)intFromHexString:(NSString *) hexStr
{
    unsigned int hexInt = 0;
    
    // Create scanner
    NSScanner *scanner = [NSScanner scannerWithString:hexStr];
    
    // Tell scanner to skip the # character
    //    [scanner setCharactersToBeSkipped:[NSCharacterSet characterSetWithCharactersInString:@"#"]];
    
    // Scan hex value
    [scanner scanHexInt:&hexInt];
    
    return hexInt;
}

+ (NSData *) splitUint8To2Bytes: (UInt8) uint8
{
    NSString *hexString = [NSString stringWithFormat:@"%x",uint8];
    Byte low;
    Byte high;
    if (hexString.length > 1)
    {
        high = [self intFromHexString:[hexString substringWithRange:NSMakeRange(0, 1)]];
        low =[self intFromHexString:[hexString substringWithRange:NSMakeRange(1, 1)]];
    }
    else
    {
        high = 0;
        low = [self intFromHexString:[hexString substringWithRange:NSMakeRange(0, 1)]];
    }
    
    Byte bytes[] = { high, low };
    NSData *data = [[NSData alloc] initWithBytes:bytes length:2];
    return data;
}

+ (Byte) combine2bytesToOneWithHigh: (Byte) high andLow: (Byte) low
{
    return (Byte) (high << 4 | low);
}

+ (UInt16) combine2bytesToU16WithHigh: (Byte) high andLow: (Byte) low
{
    UInt8 highU8 = [self convertByte2Uint8:high];
    UInt8 lowU8 = [self convertByte2Uint8:low];
    return (highU8 << 8 | lowU8);
}

+ (Byte) randomByte
{
    return arc4random() % 256;
}

+ (NSData *) randomBytes: (UInt8) len
{
    Byte bytes[len];
    for (int i = 0; i < len; i++) {
        bytes[i] = [self randomByte];
    }
    NSData *data = [[NSData alloc]initWithBytes:bytes length:len];
    return data;
}

+ (NSData *) genSpecBytesWithU16:(UInt16)len
{
    Byte bytes[len];
    for (int i = 0; i < len; i++) {
        bytes[i] = '1';
    }
    NSData *data = [[NSData alloc]initWithBytes:bytes length:len];
    return data;
}

+ (NSData *) genSpecBytesWithU8:(Byte) len
{
    UInt8 u8 = [self convertByte2Uint8:len];
    return [self genSpecBytesWithU16:u8];
}

+ (NSString *) parseBssid:(Byte[]) bssidBytes Offset: (int) offset Count: (int) count
{
    Byte bytes[count];
    for (int i = 0; i < count; i++ )
    {
        bytes[i] = bssidBytes[i + offset];
    }
    return [self parseBssid:bytes Len:count];
}

+ (NSString *) parseBssid:(Byte[]) bssidBytes Len:(int)len
{
    NSMutableString *mStr = [[NSMutableString alloc]init];
    int k;
    NSString* hexK;
    NSString* str;
    for (int i = 0; i < len; i++)
    {
        k = 0xff & bssidBytes[i];
        hexK = [NSString stringWithFormat:@"%x", k];
        str = ((k < 16) ? ([NSString stringWithFormat:@"0%@",hexK ]) : (hexK));
        [mStr appendString:str];
    }
    return mStr;
}

+ (NSData *) getBytesByNSString: (NSString *)string
{
    NSUInteger numberOfBytes = [string lengthOfBytesUsingEncoding:ESPTOUCH_NSStringEncoding];
    Byte bytes[numberOfBytes];
    NSRange range = NSMakeRange(0, numberOfBytes);
    [string getBytes:bytes maxLength:numberOfBytes usedLength:nil encoding:ESPTOUCH_NSStringEncoding options:0 range:range remainingRange:NULL];
    NSData *data = [[NSData alloc]initWithBytes:bytes length:numberOfBytes];
    return data;
}

+ (NSString *) getHexStringByData:(NSData *)data
{
    NSMutableString* mStr = [[NSMutableString alloc]init];
    NSUInteger totalLen = [data length];
    Byte bytes[totalLen];
    [data getBytes:&bytes length:totalLen];
    for (int i = 0; i < totalLen; i++)
    {
        NSString *hexString = [[NSString alloc]initWithFormat:@"0x%.2x ",bytes[i]];
        [mStr appendString:hexString];
    }
    return mStr;
}

@end
