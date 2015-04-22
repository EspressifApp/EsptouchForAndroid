//
//  ESP_CRC8.m
//  EspTouchDemo
//
//  Created by 白 桦 on 3/23/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESP_CRC8.h"

@implementation ESP_CRC8

static ushort crcTable[256];

NSNumber *_init;
NSNumber *_value;

+(void) initialize
{
    for (ushort dividend=0; dividend < 256; dividend++)
    {
        ushort remainder = dividend;
        for (ushort bit = 0; bit < 8; ++bit)
        {
            if ((remainder & 0x01) != 0)
            {
                remainder = (remainder >> 1) ^ CRC_POLYNOM;
            }
            else
            {
                remainder >>= 1;
            }
            crcTable[dividend] = (ushort) remainder;
        }
    }
}

-(id) init
{
    if(self = [super init])
    {
        _init = [[NSNumber alloc]initWithInt:CRC_INITIAL];
        _value = [[NSNumber alloc] initWithInt:CRC_INITIAL];
    }
    return self;
}

- (long)getValue
{
    return [_value unsignedShortValue] & 0xff;
}

- (void)reset
{
    _value = _init;
}

- (void)updateWithBuf:(Byte[])buf Off:(int)off Nbytes:(int)nbytes
{
    for (int i = 0; i < nbytes; i++)
    {
        int data = buf[off + i] ^ _value.intValue;
        int value = crcTable[data & 0xff] ^ (_value.intValue << 8);
        _value = [NSNumber numberWithInt:value];
    }
}

- (void)updateWithBuf:(Byte [])buf Nbytes:(int)nbytes
{
    [self updateWithBuf:buf Off:0 Nbytes:nbytes];
}


- (void)updateWithValue:(int)value
{
    Byte b[1] = { (Byte)value };
    [self updateWithBuf:b Nbytes:1];
}

@end
