//
//  ESPMagicCode.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 *            high 5 bits    low 4 bits
 * 1st 9bits:   0x0         length(high)
 * 2nd 9bits:   0x1         length(low)
 * 3rd 9bits:   0x2         ssid crc(high)
 * 4th 9bits:   0x3         ssid crc(low)
 *
 * @author afunx
 *
 */
@interface ESPMagicCode : NSObject
{
    // the len here means the length of all data to be transformed
    @private
    Byte _lengthHigh;
    @private
    Byte _lengthLow;
    // the crc here means the crc of the Ap's ssid be transformed
    @private
    Byte _ssidCrcHigh;
    @private
    Byte _ssidCrcLow;
}

- (NSData *) getBytes;

- (NSData *) getU16s;

/**
 * Constructor of MagicCode
 *
 * @param totalLen
 *            the total len of Ap's password and Ap's ssid
 * @param apSsid
 *            the Ap's ssid
 */
- (id) initWithTotalLen: (UInt8) totalLen andApSsid: (NSString*) apSsid;

@end
