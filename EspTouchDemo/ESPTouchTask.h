//
//  ESPTouchTask.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/14/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ESPTouchResult.h"

#define DEBUG_ON   YES

@interface ESPTouchTask : NSObject

@property (atomic,assign) BOOL isCancelled;

/**
 * Constructor of EsptouchTask
 *
 * @param apSsid
 *            the Ap's ssid
 * @param apBssid
 *            the Ap's bssid
 * @param apPassword
 *            the Ap's password
 * @param isSsidHidden
 *            whether the Ap's ssid is hidden
 */
- (id) initWithApSsid: (NSString *)apSsid andApBssid: (NSString *) apBssid andApPwd: (NSString *)apPwd andIsSsidHiden: (BOOL) isSsidHidden;

/**
 * Constructor of EsptouchTask
 *
 * @param apSsid
 *            the Ap's ssid
 * @param apBssid
 *            the Ap's bssid
 * @param apPassword
 *            the Ap's password
 * @param isSsidHidden
 *            whether the Ap's ssid is hidden
 * @param timeoutMillisecond(it should be >= 10000+8000)
 * 			  millisecond of total timeout
 * @param context
 *            the Context of the Application
 */
- (id) initWithApSsid: (NSString *)apSsid andApBssid: (NSString *) apBssid andApPwd: (NSString *)apPwd andIsSsidHiden: (BOOL) isSsidHidden andTimeoutMillisecond: (int) timeoutMillisecond;

/**
 * Interrupt the Esptouch Task when User tap back or close the Application.
 */
- (void) interrupt;

/**
 * Note: !!!Don't call the task at UI Main Thread
 *
 * Smart Config v2.0 support the API
 *
 * @return the ESPTouchResult
 */
- (ESPTouchResult*) executeForResult;

@end
