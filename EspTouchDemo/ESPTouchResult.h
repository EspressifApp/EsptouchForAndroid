//
//  ESPTouchResult.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/14/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESPTouchResult : NSObject

// it is used to check whether the esptouch task is executed suc
@property (nonatomic,assign) BOOL isSuc;

// it is used to store the device's bssid
@property (nonatomic,strong) NSString * bssid;

// it is used to check whether the esptouch task is cancelled by user
@property (atomic,assign) BOOL isCancelled;

// it is used to store the device's ip address
@property (atomic) NSData * ipAddrData;

/**
 * Constructor of EsptouchResult
 *
 * @param isSuc whether the esptouch task is executed suc
 * @param bssid the device's bssid
 * @param ipAddrData the device's ip address
 */
- (id) initWithIsSuc: (BOOL) isSuc andBssid: (NSString *) bssid andInetAddrData: (NSData *) ipAddrData;

@end
