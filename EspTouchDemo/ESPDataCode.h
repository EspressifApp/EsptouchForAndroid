//
//  ESPDataCode.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

#define DATA_CODE_LEN   6

/**
 * one data format:(data code should have 2 to 65 data)
 *
 *              control byte       high 4 bits    low 4 bits
 * 1st 9bits:       0x0             crc(high)      data(high)
 * 2nd 9bits:       0x1                sequence header
 * 3rd 9bits:       0x0             crc(low)       data(low)
 * 
 * sequence header: 0,1,2,...
 *
 * @author afunx
 *
 */
@interface ESPDataCode : NSObject
{
    @private
    Byte _seqHeader;
    @private
    Byte _dataHigh;
    @private
    Byte _dataLow;
    @private
    Byte _crcHigh;
    @private
    Byte _crcLow;
}

- (NSData*) getBytes;

- (id) initWithU8: (UInt8) u8 andIndex: (int) index;

@end
