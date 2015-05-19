//
//  ESPGuideCode.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPGuideCode.h"
#import "ESP_ByteUtil.h"

#define GUIDE_CODE_LEN  4

@implementation ESPGuideCode

- (NSData *) getU16s
{
    UInt16 guideU16s[GUIDE_CODE_LEN];
    guideU16s[0] = 756;
    guideU16s[1] = 755;
    guideU16s[2] = 754;
    guideU16s[3] = 753;
    NSData* data = [[NSData alloc]initWithBytes:guideU16s length:GUIDE_CODE_LEN*2];
    return data;
}

- (NSString *)description
{
    NSData* data = [self getU16s];
    return [ESP_ByteUtil getHexStringByData:data];
}


@end
