//
//  ESPTouchGenerator.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPTouchGenerator.h"
#import "ESP_ByteUtil.h"
#import "ESPGuideCode.h"
#import "ESPDatumCode.h"


@implementation ESPTouchGenerator

- (id) initWithSsid: (NSString *) apSsid andApBssid: (NSString *) apBssid andApPassword: (NSString *) apPwd andInetAddrData: (NSData *) ipAddrData andIsSsidHidden: (BOOL) isSsidHidden
{
    self = [super init];
    if (self)
    {
        // generate guide code
        ESPGuideCode *gc = [[ESPGuideCode alloc]init];
        NSData *gcData1 = [gc getU16s];
        NSUInteger gcData1Len = [gcData1 length];
        UInt16 gcU16_1[gcData1Len/2];
        [gcData1 getBytes:gcU16_1 length:gcData1Len];
        _gcBytes2 = [[NSMutableArray alloc]initWithCapacity:gcData1Len];
        for (int i = 0; i < gcData1Len/2; i++)
        {
            NSData* data = [ESP_ByteUtil genSpecBytesWithU16:gcU16_1[i]];
            [_gcBytes2 addObject:data];
        }
        
        // generate data code
        ESPDatumCode *dc = [[ESPDatumCode alloc]initWithSsid:apSsid andApBssid:apBssid andApPwd:apPwd andInetAddrData:ipAddrData andIsSsidHidden:isSsidHidden];
        NSData *dcData1 = [dc getU16s];
        NSUInteger dcDataLen = [dcData1 length];
        UInt16 dcU16_1[dcDataLen/2];
        [dcData1 getBytes:dcU16_1 length:dcDataLen];
        _dcBytes2 = [[NSMutableArray alloc]initWithCapacity:dcDataLen];
        for (int i = 0; i < dcDataLen/2; i++)
        {
            NSData* data = [ESP_ByteUtil genSpecBytesWithU16:dcU16_1[i]];
            [_dcBytes2 addObject:data];
        }
    }
    return self;
}

- (NSArray *) getGCBytes2
{
    return _gcBytes2;
}

- (NSArray *) getDCBytes2
{
    return  _dcBytes2;
}
@end
