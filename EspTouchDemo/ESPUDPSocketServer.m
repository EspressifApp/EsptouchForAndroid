//
//  ESPUDPSocketServer.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/13/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPUDPSocketServer.h"
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include "ESPTouchTask.h"

@interface ESPUDPSocketServer ()

@property(nonatomic,assign) int _sck_fd;
@property(nonatomic,assign) int _port;
// it is used to check whether the socket is closed already to prevent close more than once.
// especially, when you close the socket second time, it is created just now, it will crash.
//
//      // suppose fd1 = 4, fd1 belong to obj1
// e.g. int fd1 = socket(AF_INET,SOCK_DRAM,0);
//      close(fd1);
//
//      // suppose fd2 = 4 as well, fd2 belong to obj2
//      int fd2 = socket(AF_INET,SOCK_DRAM,0);
//
//      // obj1's dealloc() is called by system, so
//      close(fd1);
//
//      // Amazing!!! at the moment, fd2 is close by others
//
@property(nonatomic,assign) volatile bool _isClosed;
// it is used to lock the close method
@property(nonatomic,strong) volatile NSLock *_lock;

@end

@implementation ESPUDPSocketServer

- (id) initWithPort: (int) port AndSocketTimeout: (int) socketTimeout
{
    self = [super init];
    if (self)
    {
        // create local
        self._lock = [[NSLock alloc]init];
        // create socket
        self._isClosed = NO;
        self._sck_fd = socket(AF_INET,SOCK_DGRAM,0);
        if (DEBUG_ON)
        {
            NSLog(@"##########################server init(): _sck_fd=%d", self._sck_fd);
        }
        if (self._sck_fd < 0)
        {
            if (DEBUG_ON)
            {
                perror("server: _skd_fd init() fail\n");
            }
            return nil;
        }
        // init socket params
        struct sockaddr_in server_addr;
        socklen_t addr_len;
        memset(&server_addr, 0, sizeof(server_addr));
        server_addr.sin_family = AF_INET;
        server_addr.sin_port = htons(port);
        server_addr.sin_addr.s_addr = INADDR_ANY;
        addr_len = sizeof(server_addr);
        // set broadcast
        const int opt = 1;
        if (setsockopt(self._sck_fd,SOL_SOCKET,SO_BROADCAST,(char *)&opt, sizeof(opt)) < 0)
        {
            if (DEBUG_ON)
            {
                perror("server init(): setsockopt SO_BROADCAST fail\n");
            }
            [self close];
            return nil;
        }
        // set timeout
        [self setSocketTimeout:socketTimeout];
        // bind
        if (bind(self._sck_fd, (struct sockaddr*)&server_addr, addr_len) < 0)
        {
            if (DEBUG_ON)
            {
                perror("server init(): bind fail\n");
            }
            [self close];
            return nil;
        }
    }
    return self;
}

// make sure the socket will be closed sometime
- (void)dealloc
{
    if (DEBUG_ON)
    {
        NSLog(@"###################server dealloc()");
    }
    [self close];
}

- (void) close
{
    [self._lock lock];
    if (!self._isClosed)
    {
        if (DEBUG_ON)
        {
            NSLog(@"###################server close() fd=%d",self._sck_fd);
        }
        close(self._sck_fd);
        self._isClosed = true;
    }
    [self._lock unlock];
}

- (void) interrupt
{
    [self close];
}

- (void) setSocketTimeout: (int) timeout
{
    struct timeval tv;
    tv.tv_sec = timeout/1000;
    tv.tv_usec = timeout%1000*1000;
    if (setsockopt(self._sck_fd,SOL_SOCKET,SO_RCVTIMEO,(char *)&tv, sizeof(tv)) < 0)
    {
        if (DEBUG_ON)
        {
            perror("server: setsockopt SO_RCVTIMEO fail\n");
        }
    }
}

- (Byte) receiveOneByte
{
    ssize_t recNumber = recv(self._sck_fd, _buffer, BUFFER_SIZE, 0);
    if (recNumber > 0)
    {
        return _buffer[0];
    }
    else if(recNumber == 0)
    {
        if (DEBUG_ON)
        {
            perror("server: receiveOneByte socket is closed by the other\n");
        }
    }
    else
    {
        if (DEBUG_ON)
        {
            perror("server: receiveOneByte fail\n");
        }
    }
    return UINT8_MAX;
}

- (NSData *) receiveSpecLenBytes: (int)len
{
    ssize_t recNumber = recv(self._sck_fd, _buffer, BUFFER_SIZE, 0);
    if (recNumber==len)
    {
        NSData *data = [[NSData alloc]initWithBytes:_buffer length:recNumber];
        return data;
    }
    else if(recNumber==0)
    {
        if (DEBUG_ON)
        {
            perror("server: receiveOneByte socket is closed by the other\n");
        }
    }
    else if(recNumber<0)
    {
        if (DEBUG_ON)
        {
            perror("server: receiveOneByte fail\n");
        }
    }
    else
    {
        // receive rubbish message, just ignore it
    }
    return nil;
}

@end
