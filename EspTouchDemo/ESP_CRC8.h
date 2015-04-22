//
//  ESP_CRC8.h
//  EspTouchDemo
//
//  Created by 白 桦 on 3/23/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import <Foundation/Foundation.h>

#define CRC_POLYNOM 0x8c

#define CRC_INITIAL 0x00

// the interface is copied from the interface Checksum from Java
@interface ESP_CRC8 : NSObject

/**
 * Returns the current calculated checksum value.
 *
 * @return the checksum.
 */
- (long)getValue;

/**
 * Resets the checksum value applied before beginning calculations on a new
 * stream of data.
 */
- (void)reset;

/**
 * Updates the checksum with the given bytes.
 *
 * @param buf
 *            the byte array from which to read the bytes.
 * @param off
 *            the initial position in {@code buf} to read the bytes from.
 * @param nbytes
 *            the number of bytes to read from {@code buf}.
 */
- (void)updateWithBuf:(Byte[])buf Off:(int)off Nbytes:(int)nbytes;

/**
 * Updates the checksum with the given bytes.
 *
 * @param buf
 *            the byte array from which to read the bytes.
 * @param nbytes
 *            the number of bytes to read from {@code buf}.
 */
- (void)updateWithBuf:(Byte [])buf Nbytes:(int)nbytes;

/**
 * Updates the checksum value with the given byte.
 *
 * @param val
 *            the byte to update the checksum with.
 */
- (void)updateWithValue:(int)value;

@end
