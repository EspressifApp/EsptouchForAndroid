//
//  ESPDatumCode.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESPDatumCode : NSObject
{
    @private
    NSMutableArray *_dataCodes;
}
/**
 * Constructor of DatumCode
 *
 * @param apSsid
 *            the  and Ap's ssid
 * @param apPwd
 *            the Ap's password ssid
 */
- (id) initWithSsid: (NSString *) apSsid andApPwd: (NSString*) apPwd;

- (NSData *) getBytes;

- (NSData *) getU16s;

@end
