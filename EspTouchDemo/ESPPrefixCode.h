//
//  ESPPrefixCode.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 *            high 5 bits   low 4 bits
 * 5th 9bits:   0x4        pwd length(high)
 * 6th 9bits:   0x5        pwd length(low)
 * 7th 9bits:   0x6        pwd len crc(high)
 * 8th 9bits:   0x7        pwd len crc(low)
 *
 * @author afunx
 *
 */
@interface ESPPrefixCode : NSObject
{
    @private
    Byte _pwdLengthHigh;
    @private
    Byte _pwdLengthLow;
    @private
    Byte _pwdLenCrcHigh;
    @private
    Byte _pwdLenCrcLow;
}

- (NSData *) getBytes;

- (NSData *) getU16s;

/**
 * Constructor of PrefixCode
 *
 * @param pwdLen
 *            the total len of Ap's ssid
 */
- (id) initWithPwdLen: (UInt8) pwdLen;

@end
