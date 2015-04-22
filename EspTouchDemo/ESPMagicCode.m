//
//  ESPMagicCode.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPMagicCode.h"
#import "ESP_ByteUtil.h"
#import "ESP_CRC8.h"

#define MAGIC_CODE_LEN  8
#define MAGIC_THRESHOLD 16
#define MAGIC_NUMBER    128

@implementation ESPMagicCode

- (id) initWithTotalLen: (UInt8) totalLen andApSsid: (NSString*) apSsid
{
    self = [super init];
    if (self) {
        if (totalLen < MAGIC_THRESHOLD) {
            totalLen += MAGIC_NUMBER;
        }
        NSData *lengthData = [ESP_ByteUtil splitUint8To2Bytes:totalLen];
        [lengthData getBytes:&_lengthHigh range:NSMakeRange(0, 1)];
        [lengthData getBytes:&_lengthLow range:NSMakeRange(1, 1)];
        ESP_CRC8 *crc = [[ESP_CRC8 alloc]init];
        NSData *ssidData = [ESP_ByteUtil getBytesByNSString:apSsid];
        int ssidLen = [apSsid length];
        Byte ssidBytes[ssidLen];
        [ssidData getBytes:ssidBytes length:ssidLen];
        [crc updateWithBuf:ssidBytes Nbytes:ssidLen];
        UInt8 crcValue = [crc getValue];
        NSData *crcData = [ESP_ByteUtil splitUint8To2Bytes:crcValue];
        [crcData getBytes:&_ssidCrcHigh range:NSMakeRange(0, 1)];
        [crcData getBytes:&_ssidCrcLow range:NSMakeRange(1, 1)];
    }
    return self;
}

- (NSData*) getBytes
{
    Byte magicBytes[MAGIC_CODE_LEN];
    magicBytes[0] = 0x00;
    magicBytes[1] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x00 andLow:_lengthHigh];
    magicBytes[2] = 0x00;
    magicBytes[3] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x01 andLow:_lengthLow];
    magicBytes[4] = 0x00;
    magicBytes[5] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x02 andLow:_ssidCrcHigh];
    magicBytes[6] = 0x00;
    magicBytes[7] = [ESP_ByteUtil combine2bytesToOneWithHigh:0x03 andLow:_ssidCrcLow];
    return [[NSData alloc]initWithBytes:magicBytes length:MAGIC_CODE_LEN];
}

- (NSData *) getU16s
{
    NSData *bytesData = [self getBytes];
    Byte bytes[MAGIC_CODE_LEN];
    [bytesData getBytes:bytes length:MAGIC_CODE_LEN];
    int len = MAGIC_CODE_LEN / 2;
    UInt16 u16s[len];
    Byte high, low;
    for (int i = 0; i < len;  i++) {
        high = bytes[i * 2];
        low = bytes[i * 2 + 1];
        u16s[i] = [ESP_ByteUtil combine2bytesToU16WithHigh:high andLow:low];
    }
    return [[NSData alloc]initWithBytes:u16s length:MAGIC_CODE_LEN];
}

- (NSString *)description
{
    NSData* data = [self getBytes];
    return [ESP_ByteUtil getHexStringByData:data];
}

@end
