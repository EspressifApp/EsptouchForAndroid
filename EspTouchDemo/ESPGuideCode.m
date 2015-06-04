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
    guideU16s[0] = 515;
    guideU16s[1] = 514;
    guideU16s[2] = 513;
    guideU16s[3] = 512;
    NSData* data = [[NSData alloc]initWithBytes:guideU16s length:GUIDE_CODE_LEN*2];
    return data;
}

- (NSString *)description
{
    NSData* data = [self getU16s];
    return [ESP_ByteUtil getHexStringByData:data];
}


@end
