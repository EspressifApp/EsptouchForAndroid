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

- (NSData*) getBytes
{
    Byte bytes[GUIDE_CODE_LEN];
    bytes[0] = 0x01;
    bytes[1] = 0x02;
    bytes[2] = 0x03;
    bytes[3] = 0x04;
    NSData* data = [[NSData alloc]initWithBytes:bytes length:GUIDE_CODE_LEN];
    return data;
}

- (NSString *)description
{
    NSData* data = [self getBytes];
    return [ESP_ByteUtil getHexStringByData:data];
}


@end
