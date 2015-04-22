//
//  ESPUDPSocketServer.h
//  EspTouchDemo
//
//  Created by 白 桦 on 4/13/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

#define BUFFER_SIZE 64

@interface ESPUDPSocketServer : NSObject
{
    @private
    Byte _buffer[BUFFER_SIZE];
}

- (void) close;

- (void) interrupt;

/**
 * Set the socket timeout in milliseconds
 *
 * @param timeout
 *            the timeout in milliseconds or 0 for no timeout.
 * @return true whether the timeout is set suc
 */
- (void) setSocketTimeout: (int) timeout;

/**
 * Receive one byte from the port
 *
 * @return one byte receive from the port or UINT8_MAX(it impossible receive it from the socket)
 */
- (Byte) receiveOneByte;

- (NSData *) receiveSpecLenBytes: (int)len;

- (id) initWithPort: (int) port AndSocketTimeout: (int) socketTimeout;

@end
