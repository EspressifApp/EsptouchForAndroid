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

- (id) initWithApSsid: (NSString *) apSsid andApPwd: (NSString *) apPwd;

/**
 * Interrupt the Esptouch Task when User tap back or close the Application.
 */
- (void) interrupt;

/**
 * Note: !!!Don't call the task at UI Main Thread
 *
 * Smart Config v1.1 support the API
 *
 * @return the ESPTouchResult
 */
- (ESPTouchResult*) executeForResult;

@end
