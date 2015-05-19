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
#import "ESP_CRC8.h"

// define by the Esptouch protocol, all of the datum code should add EXTRA_LEN to prevent 0
#define EXTRA_LEN   200

@implementation ESPDatumCode

- (id) initWithSsid: (NSString *) apSsid andApPwd: (NSString*) apPwd andInetAddrData: (NSData *) ipAddrData
{
    self = [super init];
    if (self)
    {
        // Data = total len(1 byte) + apPwd len(1 byte) + SSID CRC(1 byte) + ipAddress(4 byte) + apPwd + apSsid
		// apPwdLen <= 104 at the moment
        NSData *apPwdBytesData = [ESP_ByteUtil getBytesByNSString:apPwd];
        NSData *apSsidBytesData = [ESP_ByteUtil getBytesByNSString:apSsid];
        Byte apPwdBytes[[apPwdBytesData length]];
        Byte apSsidBytes[[apSsidBytesData length]];
        [apPwdBytesData getBytes:apPwdBytes];
        [apSsidBytesData getBytes:apSsidBytes];
        Byte apPwdLen = [apPwdBytesData length];
        ESP_CRC8 *crc = [[ESP_CRC8 alloc]init];
        [crc updateWithBuf:apSsidBytes Nbytes:(int)sizeof(apSsidBytes)];
        Byte apSsidCrc = [crc getValue];
        
        UInt8 apSsidLen = sizeof(apSsidBytes);
        
        // only support ipv4 at the moment
        UInt8 ipLen = [ipAddrData length];
        Byte ipAddrUint8s[ipLen];
        [ipAddrData getBytes:ipAddrUint8s];
        
        UInt8 totalLen = 3 + ipLen + apPwdLen + apSsidLen;
        
        // build data codes
        _dataCodes = [[NSMutableArray alloc]initWithCapacity:totalLen];
        ESPDataCode *dataCode = [[ESPDataCode alloc]initWithU8:totalLen andIndex:0];
        [_dataCodes addObject:dataCode];
        dataCode = [[ESPDataCode alloc]initWithU8:apPwdLen andIndex:1];
        [_dataCodes addObject:dataCode];
        dataCode = [[ESPDataCode alloc]initWithU8:apSsidCrc andIndex:2];
        [_dataCodes addObject:dataCode];
        for (int i = 0; i < ipLen; i++) {
            dataCode = [[ESPDataCode alloc]initWithU8:ipAddrUint8s[i] andIndex:i + 3];
            [_dataCodes addObject:dataCode];
        }
        for (int i = 0; i < apPwdLen; i++) {
            dataCode = [[ESPDataCode alloc]initWithU8:apPwdBytes[i] andIndex:i + 3 + ipLen];
            [_dataCodes addObject:dataCode];
        }
        for (int i = 0; i < apSsidLen; i++) {
            dataCode = [[ESPDataCode alloc]initWithU8:apSsidBytes[i] andIndex:i + 3 + ipLen + apPwdLen];
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
    NSInteger totalLen = [dataBytes length];
    Byte bytes[totalLen];
    [dataBytes getBytes:bytes length:totalLen];
    NSInteger len = totalLen / 2;
    UInt16 dataU16s[len];
    Byte high, low;
    for (int i = 0; i < len; i++)
    {
        high = bytes[i * 2];
        low = bytes[i * 2 + 1];
        dataU16s[i] = [ESP_ByteUtil combine2bytesToU16WithHigh:high andLow:low] + EXTRA_LEN;
    }
    return [[NSData alloc]initWithBytes:dataU16s length:totalLen];
}

- (NSString *)description
{
    NSData* data = [self getBytes];
    return [ESP_ByteUtil getHexStringByData:data];
}


@end
