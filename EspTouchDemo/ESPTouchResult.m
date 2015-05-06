//
//  ESPTouchResult.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/14/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPTouchResult.h"

@implementation ESPTouchResult

- (id) initWithIsSuc: (BOOL) isSuc andBssid: (NSString *) bssid
{
    self = [super init];
    if (self)
    {
        self.isSuc = isSuc;
        self.bssid = bssid;
        self.isCancelled = NO;
    }
    return self;
}

- (NSString *)description
{
    return [[NSString alloc]initWithFormat:@"[isSuc: %@,bssid: %@]",self.isSuc? @"YES":@"NO",self.bssid];
}

@end
