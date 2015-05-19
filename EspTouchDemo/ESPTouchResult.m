//
//  ESPTouchResult.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/14/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPTouchResult.h"
#import "ESP_NetUtil.h"

@implementation ESPTouchResult

- (id) initWithIsSuc: (BOOL) isSuc andBssid: (NSString *) bssid andInetAddrData: (NSData *) ipAddrData
{
    self = [super init];
    if (self)
    {
        self.isSuc = isSuc;
        self.bssid = bssid;
        self.isCancelled = NO;
        self.ipAddrData = ipAddrData;
    }
    return self;
}

- (NSString *)description
{
    NSString *ipAddrDataStr = [ESP_NetUtil descriptionInetAddrByData:self.ipAddrData];
    return [[NSString alloc]initWithFormat:@"[isSuc: %@,isCancelled: %@,bssid: %@,inetAddress: %@]",self.isSuc? @"YES":@"NO",
            self.isCancelled? @"YES":@"NO"
            ,self.bssid
            ,ipAddrDataStr];
}

@end
