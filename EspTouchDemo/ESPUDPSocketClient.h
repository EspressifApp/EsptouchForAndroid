//
//  ESPUDPSocketClient.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/13/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ESPUDPSocketClient : NSObject

- (void) close;

- (void) interrupt;

/**
 * send the data by UDP
 *
 * @param bytes
 *            the array of datas to be sent
 * @param targetHost
 *            the host name of target, e.g. 192.168.1.101
 * @param targetPort
 *            the port of target
 * @param interval
 *            the milliseconds to between each UDP sent
 */
- (void) sendDataWithBytesArray2: (NSArray *) bytesArray2 ToTargetHostName: (NSString *)targetHostName WithPort: (int) port
      andInterval: (long) interval;

/**
 * send the data by UDP
 *
 * @param data
 *            the data to be sent
 * @param offset
 * 			  the offset which data to be sent
 * @param count
 * 			  the count of the data
 * @param targetHost
 *            the host name of target, e.g. 192.168.1.101
 * @param targetPort
 *            the port of target
 * @param interval
 *            the milliseconds to between each UDP sent
 */
- (void) sendDataWithBytesArray2: (NSArray *) bytesArray2 Offset: (NSUInteger) offset Count: (NSUInteger) count ToTargetHostName: (NSString *)targetHostName WithPort: (int) port
                     andInterval: (long) interval;
@end
