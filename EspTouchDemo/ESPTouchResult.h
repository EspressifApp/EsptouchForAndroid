//
//  ESPTouchResult.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/14/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESPTouchResult : NSObject

@property (nonatomic,assign) BOOL isSuc;

@property (nonatomic,strong) NSString * bssid;

@property (atomic,assign) BOOL isCancelled;

- (id) initWithIsSuc: (BOOL) isSuc andBssid: (NSString *) bssid;

@end
