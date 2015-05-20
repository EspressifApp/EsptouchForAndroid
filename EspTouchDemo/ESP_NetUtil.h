//
//  ESPNetUtil.h
//  EspTouchDemo
//
//  Created by 白 桦 on 5/15/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESP_NetUtil : NSObject

/**
 * get the local ip address by IOS System
 *
 */
+ (NSData *) getLocalInetAddress;

/**
 * parse InetAddress
 */
+ (NSData *) parseInetAddrByData: (NSData *) inetAddrData andOffset: (int) offset andCount: (int) count;

/**
 * descrpion inetAddrData for print pretty
 */
+ (NSString *) descriptionInetAddrByData: (NSData *) inetAddrData;

/**
 * parse bssid
 *
 * @param bssid the bssid
 * @return byte converted from bssid
 */
+ (NSData *) parseBssid2bytes: (NSString *) bssid;

@end
