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
 *            the Ap's ssid
 * @param apBssid
 *            the Ap's bssid
 * @param apPwd
 *            the Ap's password ssid
 * @param ipAddrData
 *            the ip address of the phone or pad
 * @param isSsidHidden
 *            whether the Ap's ssid is hidden
 *
 */
- (id) initWithSsid: (NSString *) apSsid andApBssid: (NSString *) apBssid andApPwd: (NSString*) apPwd andInetAddrData: (NSData *) ipAddrData andIsSsidHidden: (BOOL) isSsidHidden;

- (NSData *) getBytes;

- (NSData *) getU16s;

@end
