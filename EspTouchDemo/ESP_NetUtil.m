//
//  ESPNetUtil.m
//  EspTouchDemo
//
//  Created by 白 桦 on 5/15/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESP_NetUtil.h"
#include "IPAddress.h"
#include "ESP_ByteUtil.h"

// only support ipV4 at the moment
#define IP_LEN 4

@implementation ESP_NetUtil

+ (NSData *) getLocalInetAddress
{
    InitAddresses();
    GetIPAddresses();
    GetHWAddresses();
    NSData *localInetAddrData = nil;
    Byte localIpBytes[IP_LEN];
    for (int i=0; i<MAXADDRS; ++i)
    {
        static unsigned long localHost = 0x100007F;            // 127.0.0.1
        unsigned long theAddr;
        
        theAddr = ip_addrs[i];
        
        if (theAddr == 0) break;
        if (theAddr == localHost) continue;
//        NSLog(@"theAddr=%lu",theAddr);
        
        // hard coding
        localIpBytes[0] = (theAddr & 0xff)          >> 0;
        localIpBytes[1] = (theAddr & 0xff00)        >> 8;
        localIpBytes[2] = (theAddr & 0xff0000)      >> 16;
        localIpBytes[3] = (theAddr & 0xff000000)    >> 24;
//        NSLog(@"ESP_NetUtil:: %d.%d.%d.%d",localIpBytes[0],localIpBytes[1],localIpBytes[2],localIpBytes[3]);
        
//        if (theAddr != 0) break;
//        NSLog(@"ESP_NetUtil:: Name: %s MAC: %s IP: %s\n", if_names[i], hw_addrs[i], ip_names[i]);
    }
        
    localInetAddrData = [[NSData alloc]initWithBytes:localIpBytes length:IP_LEN];
    
    FreeAddresses();
    
    Byte byte1 = 0;
    Byte byte2 = 0;
    Byte byte3 = 0;
    Byte byte4 = 0;
    
    [localInetAddrData getBytes:&byte1 range:NSMakeRange(0, 1)];
    [localInetAddrData getBytes:&byte2 range:NSMakeRange(1, 1)];
    [localInetAddrData getBytes:&byte3 range:NSMakeRange(2, 1)];
    [localInetAddrData getBytes:&byte4 range:NSMakeRange(3, 1)];
    
    
    NSLog(@"ESP_NetUtil:: %d.%d.%d.%d", byte1,byte2,byte3,byte4);
    
    return localInetAddrData;
}

+ (NSData *) parseInetAddrByData: (NSData *) inetAddrData andOffset: (int) offset andCount: (int) count
{
    return [inetAddrData subdataWithRange:NSMakeRange(offset, count)];
}

+ (NSString *) descriptionInetAddrByData: (NSData *) inetAddrData
{
    Byte inetAddrBytes[IP_LEN];
    [inetAddrData getBytes:inetAddrBytes length:IP_LEN];
    // hard coding
    return [NSString stringWithFormat:@"%d.%d.%d.%d",inetAddrBytes[0],inetAddrBytes[1],inetAddrBytes[2],inetAddrBytes[3]];
}

+ (NSData *) parseBssid2bytes: (NSString *) bssid
{
    NSArray *bssidArray = [bssid componentsSeparatedByString:@":"];
    NSInteger size = [bssidArray count];
    Byte bssidBytes[size];
    for (NSInteger i = 0; i < size; i++) {
        NSString *bssidStr = [bssidArray objectAtIndex:i];
        bssidBytes[i] = strtoul([bssidStr UTF8String], 0, 16);
    }
    return [[NSData alloc]initWithBytes:bssidBytes length:size];
}

@end
