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
#import "ESP_NetUtil.h"

// define by the Esptouch protocol, all of the datum code should add EXTRA_LEN to prevent 0
#define EXTRA_LEN   40
#define EXTRA_HEAD_LEN  5

@implementation ESPDatumCode

- (id) initWithSsid: (NSString *) apSsid andApBssid: (NSString *) apBssid andApPwd: (NSString*) apPwd andInetAddrData: (NSData *) ipAddrData andIsSsidHidden: (BOOL) isSsidHidden
{
    self = [super init];
    if (self)
    {
        // Data = total len(1 byte) + apPwd len(1 byte) + SSID CRC(1 byte) +
		// BSSID CRC(1 byte) + TOTAL XOR(1 byte) + ipAddress(4 byte) + apPwd + apSsid apPwdLen <=
		// 105 at the moment
        
        // total xor
        UInt8 totalXor = 0;
        
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
        
        [crc reset];
        NSData *apBssidData = [ESP_NetUtil parseBssid2bytes:apBssid];
        int apBssidDataLen = (int)[apBssidData length];
        Byte apBssidBytes[apBssidDataLen];
        [apBssidData getBytes:apBssidBytes];
        [crc updateWithBuf:apBssidBytes Nbytes:apBssidDataLen];
        UInt8 apBssidCrc = [crc getValue];
        
        UInt8 apSsidLen = sizeof(apSsidBytes);
        
        // only support ipv4 at the moment
        UInt8 ipLen = [ipAddrData length];
        Byte ipAddrUint8s[ipLen];
        [ipAddrData getBytes:ipAddrUint8s];
        
        UInt8 _totalLen = EXTRA_HEAD_LEN + ipLen + apPwdLen + apSsidLen;
        UInt8 totalLen = isSsidHidden ? (EXTRA_HEAD_LEN + ipLen + apPwdLen + apSsidLen):(EXTRA_HEAD_LEN + ipLen + apPwdLen);

        
        // build data codes
        _dataCodes = [[NSMutableArray alloc]initWithCapacity:totalLen];
        ESPDataCode *dataCode = [[ESPDataCode alloc]initWithU8:_totalLen andIndex:0];
        [_dataCodes addObject:dataCode];
        totalXor ^= _totalLen;
        dataCode = [[ESPDataCode alloc]initWithU8:apPwdLen andIndex:1];
        [_dataCodes addObject:dataCode];
        totalXor ^= apPwdLen;
        dataCode = [[ESPDataCode alloc]initWithU8:apSsidCrc andIndex:2];
        [_dataCodes addObject:dataCode];
        totalXor ^= apSsidCrc;
        dataCode = [[ESPDataCode alloc]initWithU8:apBssidCrc andIndex:3];
        [_dataCodes addObject:dataCode];
        totalXor ^= apBssidCrc;
        // ESPDataCode 4 is nil
        for (int i = 0; i < ipLen; i++)
        {
            dataCode = [[ESPDataCode alloc]initWithU8:ipAddrUint8s[i] andIndex:i + EXTRA_HEAD_LEN];
            [_dataCodes addObject:dataCode];
            totalXor ^= ipAddrUint8s[i];
        }
        for (int i = 0; i < apPwdLen; i++)
        {
            dataCode = [[ESPDataCode alloc]initWithU8:apPwdBytes[i] andIndex:i + EXTRA_HEAD_LEN + ipLen];
            [_dataCodes addObject:dataCode];
            totalXor ^= apPwdBytes[i];
        }
        
        // totalXor will xor apSsidChars no matter whether the ssid is hidden
        for (int i = 0; i < apSsidLen; i++)
        {
            totalXor ^= apSsidBytes[i];
        }
        
        if (isSsidHidden)
        {
            for (int i = 0; i < apSsidLen; i++)
            {
                dataCode = [[ESPDataCode alloc]initWithU8:apSsidBytes[i] andIndex:i + EXTRA_HEAD_LEN + ipLen + apPwdLen];
                [_dataCodes addObject:dataCode];
            }
        }
        
        // add total xor last
        dataCode = [[ESPDataCode alloc]initWithU8:totalXor andIndex:4];
        [_dataCodes insertObject:dataCode atIndex:4];
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
