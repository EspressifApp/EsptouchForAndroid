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
#import "ESPMagicCode.h"
#import "ESPPrefixCode.h"
#import "ESPDatumCode.h"


@implementation ESPTouchGenerator

- (id) initWithSsid: (NSString *) apSsid andApPassword: (NSString *) apPwd
{
    self = [super init];
    if (self)
    {
        // the u8 total len of apSsid and apPassword
        UInt8 totalLen = [apSsid length] + [apPwd length];
        // the u8 len of apPassword
        UInt8 pwdLen = [apPwd length];
        
        // generate guide code
        ESPGuideCode *gc = [[ESPGuideCode alloc]init];
        NSData *gcData1 = [gc getBytes];
        NSUInteger gcData1Len = [gcData1 length];
        Byte gcBytes1[gcData1Len];
        [gcData1 getBytes:gcBytes1 length:gcData1Len];
        _gcBytes2 = [[NSMutableArray alloc]initWithCapacity:gcData1Len];
        for (int i = 0; i < gcData1Len; i++)
        {
            NSData* data = [ESP_ByteUtil genSpecBytesWithU8:gcBytes1[i]];
            [_gcBytes2 addObject:data];
        }
        
        // generate magic code
        ESPMagicCode *mc = [[ESPMagicCode alloc]initWithTotalLen:totalLen andApSsid:apSsid];
        NSData *mcData1 = [mc getU16s];
        NSUInteger mcData1Len = [mcData1 length];
        UInt16 mcU16_1[mcData1Len/2];
        [mcData1 getBytes:mcU16_1 length:mcData1Len];
        _mcBytes2 = [[NSMutableArray alloc]initWithCapacity:mcData1Len/2];
        for (int i = 0; i < mcData1Len/2; i++)
        {
            NSData* data = [ESP_ByteUtil genSpecBytesWithU16:mcU16_1[i]];
            [_mcBytes2 addObject:data];
        }
        
        // generate prefix code
        ESPPrefixCode *pc = [[ESPPrefixCode alloc]initWithPwdLen:pwdLen];
        NSData *pcData1 = [pc getU16s];
        NSUInteger pcData1Len = [pcData1 length];
        UInt16 pcU16_1[pcData1Len/2];
        [pcData1 getBytes:pcU16_1 length:pcData1Len];
        _pcBytes2 = [[NSMutableArray alloc]initWithCapacity:pcData1Len/2];
        for (int i = 0; i < pcData1Len/2; i++)
        {
            NSData* data = [ESP_ByteUtil genSpecBytesWithU16:pcU16_1[i]];
            [_pcBytes2 addObject:data];
        }
        
        // generate data code
        ESPDatumCode *dc = [[ESPDatumCode alloc]initWithSsid:apSsid andApPwd:apPwd];
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

- (NSArray *) getMCBytes2
{
    return  _mcBytes2;
}

- (NSArray *) getPCBytes2
{
    return  _pcBytes2;
}

- (NSArray *) getDCBytes2
{
    return  _dcBytes2;
}
@end
