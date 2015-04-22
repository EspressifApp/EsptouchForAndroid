//
//  ESPPrefixCode.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPPrefixCode.h"
#import "ESP_ByteUtil.h"
#import "ESP_CRC8.h"

#define PREFIX_CODE_LEN 8

@implementation ESPPrefixCode

- (id) initWithPwdLen: (UInt8) pwdLen
{
    self = [super init];
    if (self)
    {
        NSData* pwdLenData = [ESP_ByteUtil splitUint8To2Bytes:pwdLen];
        [pwdLenData getBytes:&_pwdLengthHigh range:NSMakeRange(0, 1)];
        [pwdLenData getBytes:&_pwdLengthLow range:NSMakeRange(1, 1)];
        ESP_CRC8 *crc8 = [[ESP_CRC8 alloc]init];
        [crc8 updateWithValue:pwdLen];
        UInt8 crcValue = [crc8 getValue];
        NSData* crcBytesData = [ESP_ByteUtil splitUint8To2Bytes:crcValue];
        [crcBytesData getBytes:&_pwdLenCrcHigh range:NSMakeRange(0, 1)];
        [crcBytesData getBytes:&_pwdLenCrcLow range:NSMakeRange(1, 1)];
    }
    return self;
}
- (NSData *) getBytes
{
    Byte magicBytes[PREFIX_CODE_LEN];
    magicBytes[0] = 0x00;
    magicBytes[1] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x04 andLow:_pwdLengthHigh];
    magicBytes[2] = 0x00;
    magicBytes[3] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x05 andLow:_pwdLengthLow];
    magicBytes[4] = 0x00;
    magicBytes[5] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x06 andLow:_pwdLenCrcHigh];
    magicBytes[6] = 0x00;
    magicBytes[7] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x07 andLow:_pwdLenCrcLow];
    return [[NSData alloc] initWithBytes:magicBytes length:PREFIX_CODE_LEN];
}

- (NSData *) getU16s
{
    NSData *bytesData = [self getBytes];
    Byte bytes[PREFIX_CODE_LEN];
    [bytesData getBytes:bytes length:PREFIX_CODE_LEN];
    int len = PREFIX_CODE_LEN / 2;
    UInt16 u16s[len];
    Byte high, low;
    for (int i = 0; i < len; i++)
    {
        high = bytes[i * 2];
        low = bytes[i * 2 + 1];
        u16s[i] = [ESP_ByteUtil combine2bytesToU16WithHigh:high andLow:low];
    }
    return [[NSData alloc]initWithBytes:u16s length:PREFIX_CODE_LEN];
}

- (NSString *)description
{
    NSData* data = [self getBytes];
    return [ESP_ByteUtil getHexStringByData:data];
}

@end
