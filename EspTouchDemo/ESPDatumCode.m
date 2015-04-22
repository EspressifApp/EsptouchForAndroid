//
//  ESPDatumCode.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPDatumCode.h"
#import "ESPDataCode.h"
#import "ESP_ByteUtil.h"


@implementation ESPDatumCode

- (id) initWithSsid:(NSString *)apSsid andApPwd:(NSString *)apPwd
{
    self = [super init];
    if (self)
    {
        NSString *info = [[NSString alloc]initWithFormat:@"%@%@", apPwd, apSsid];
        NSUInteger infoLen = [info length];
        _dataCodes = [[NSMutableArray alloc]initWithCapacity:infoLen];
        UInt8 infoU8s[infoLen];
        for (int i = 0; i < infoLen; i++)
        {
            infoU8s[i] = [info characterAtIndex:i];
        }
        for (int i = 0; i < infoLen; i++)
        {
            ESPDataCode *dataCode = [[ESPDataCode alloc]initWithU8:infoU8s[i] andIndex:i];
            [_dataCodes addObject:dataCode];
        }
    }
    return self;
}

- (NSData *) getBytes
{
    Byte datumCode[[_dataCodes count] * DATA_CODE_LEN];
    for (int i = 0; i < [_dataCodes count]; i++)
    {
        ESPDataCode *dataCode = [_dataCodes objectAtIndex:i];
        NSData *bytesData = [dataCode getBytes];
        Byte bytes[DATA_CODE_LEN];
        [bytesData getBytes:bytes length:DATA_CODE_LEN];
        void *dest = datumCode + i * DATA_CODE_LEN * sizeof(Byte);
        void *src = bytes;
        memcpy(dest, src, DATA_CODE_LEN);
    }
    return [[NSData alloc]initWithBytes:datumCode length:sizeof(datumCode)];
}

- (NSData *) getU16s
{
    NSData *dataBytes = [self getBytes];
    int totalLen = [dataBytes length];
    Byte bytes[totalLen];
    [dataBytes getBytes:bytes length:totalLen];
    int len = totalLen / 2;
    UInt16 dataU16s[len];
    Byte high, low;
    for (int i = 0; i < len; i++)
    {
        high = bytes[i * 2];
        low = bytes[i * 2 + 1];
        dataU16s[i] = [ESP_ByteUtil combine2bytesToU16WithHigh:high andLow:low];
    }
    return [[NSData alloc]initWithBytes:dataU16s length:totalLen];
}

- (NSString *)description
{
    NSData* data = [self getBytes];
    return [ESP_ByteUtil getHexStringByData:data];
}


@end
