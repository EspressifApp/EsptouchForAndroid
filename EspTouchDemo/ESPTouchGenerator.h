//
//  ESPTouchGenerator.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/9/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESPTouchGenerator : NSObject
{
@private
    NSMutableArray *_gcBytes2;
@private
    NSMutableArray *_mcBytes2;
@private
    NSMutableArray *_pcBytes2;
@private
    NSMutableArray *_dcBytes2;
}
/**
 * Constructor of EsptouchGenerator, it will cost some time(maybe a bit much)
 *
 * @param apSsid
 *            the Ap's ssid
 * @param apPwd
 *            the Ap's password
 */
- (id) initWithSsid: (NSString *) apSsid andApPassword: (NSString *) apPwd;

/**
 * Get guide code by the format of byte[][]
 * @return guide code by the format of byte[][]
 */
- (NSArray *) getGCBytes2;

/**
 * Get magic code by the format of byte[][]
 * @return magic code by the format of byte[][]
 */
- (NSArray *) getMCBytes2;

/**
 * Get prefix code by the format of byte[][]
 * @return prefix code by the format of byte[][]
 */
- (NSArray *) getPCBytes2;

/**
 * Get data code by the format of byte[][]
 * @return data code by the format of byte[][]
 */
- (NSArray *) getDCBytes2;

@end
