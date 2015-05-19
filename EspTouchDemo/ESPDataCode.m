//
//  ESPDataCode.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPDataCode.h"
#import "ESP_ByteUtil.h"
#import "ESP_CRC8.h"

#define INDEX_MAX   127

@implementation ESPDataCode

- (id) initWithU8: (UInt8) u8 andIndex: (int) index
{
    if (index > INDEX_MAX)
    {
        perror("index > INDEX_MAX");
    }
    self = [super init];
    if (self)
    {
        NSData* u8Data = [ESP_ByteUtil splitUint8To2Bytes:u8];
        [u8Data getBytes:&_dataHigh range:NSMakeRange(0, 1)];
        [u8Data getBytes:&_dataLow range:NSMakeRange(1, 1)];
        ESP_CRC8 *crc = [[ESP_CRC8 alloc]init];
        [crc updateWithValue:u8];
        [crc updateWithValue:index];
        NSData* crcData = [ESP_ByteUtil splitUint8To2Bytes:[crc getValue]];
        [crcData getBytes:&_crcHigh range:NSMakeRange(0, 1)];
        [crcData getBytes:&_crcLow range:NSMakeRange(1, 1)];
        _seqHeader = index;
    }
    return self;
}

- (NSData *) getBytes
{
    Byte bytes[DATA_CODE_LEN];
    bytes[0] = 0x00;
    bytes[1] = [ESP_ByteUtil combine2bytesToOneWithHigh:_crcHigh andLow:_dataHigh];
    bytes[2] = 0x01;
    bytes[3] = _seqHeader;
    bytes[4] = 0x00;
    bytes[5] = [ESP_ByteUtil combine2bytesToOneWithHigh:_crcLow andLow:_dataLow];
    NSData* data = [[NSData alloc]initWithBytes:bytes length:DATA_CODE_LEN];
    return data;
}

- (NSString *)description
{
    NSData* data = [self getBytes];
    return [ESP_ByteUtil getHexStringByData:data];
}

@end
