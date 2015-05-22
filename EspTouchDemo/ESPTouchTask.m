//
//  ESPTouchTask.m
//  EspTouchDemo
//
//  Created by 白 桦 on 4/14/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

//  The usage of NSCondition refer to: https://gist.github.com/prachigauriar/8118909

#import "ESPTouchTask.h"
#import "ESP_ByteUtil.h"
#import "ESPTouchGenerator.h"
#import "ESPUDPSocketClient.h"
#import "ESPUDPSocketServer.h"
#import "ESP_NetUtil.h"
#import "ESPTouchTaskParameter.h"

#define ONE_DATA_LEN    3

@interface ESPTouchTask ()

@property (nonatomic,strong) NSString *_apSsid;

@property (nonatomic,strong) NSString *_apBssid;

@property (nonatomic,strong) NSString *_apPwd;

@property (atomic,assign) BOOL _isSuc;

@property (atomic,assign) BOOL _isInterrupt;

@property (nonatomic,strong) ESPUDPSocketClient *_client;

@property (nonatomic,strong) ESPUDPSocketServer *_server;

@property (atomic,strong) ESPTouchResult *_esptouchResult;

@property (atomic,strong) NSCondition *_condition;

@property (nonatomic,assign) __block BOOL _isWakeUp;

@property (nonatomic,assign) volatile BOOL _isExecutedAlready;

@property (nonatomic,assign) BOOL _isSsidHidden;

@property (nonatomic,strong) ESPTaskParameter *_parameter;

@end

@implementation ESPTouchTask

- (id) initWithApSsid: (NSString *)apSsid andApBssid: (NSString *) apBssid andApPwd: (NSString *)apPwd andIsSsidHiden: (BOOL) isSsidHidden
{
    if (apSsid==nil||[apSsid isEqualToString:@""]) {
        perror("ESPTouchTask initWithApSsid() apSsid shouldn't be null or empty");
    }
    // the apSsid should be null or empty
    assert(apSsid!=nil&&![apSsid isEqualToString:@""]);
    if (apPwd == nil)
    {
        apPwd = @"";
    }
    
    self = [super init];
    if (self)
    {
        if (DEBUG_ON)
        {
            NSLog(@"ESPTouchTask init");
        }
        self._apSsid = apSsid;
        self._apPwd = apPwd;
        self._apBssid = apBssid;
        self._parameter = [[ESPTaskParameter alloc]init];
        self._client = [[ESPUDPSocketClient alloc]init];
        self._server = [[ESPUDPSocketServer alloc]initWithPort: [self._parameter getPortListening]
                                              AndSocketTimeout: [self._parameter getWaitUdpTotalMillisecond]];
        self._isSuc = NO;
        self._isInterrupt = NO;
        self._isWakeUp = NO;
        self._isExecutedAlready = NO;
        self._condition = [[NSCondition alloc]init];
        self._isSsidHidden = isSsidHidden;
    }
    return self;
}

- (id) initWithApSsid: (NSString *)apSsid andApBssid: (NSString *) apBssid andApPwd: (NSString *)apPwd andIsSsidHiden: (BOOL) isSsidHidden andTimeoutMillisecond: (int) timeoutMillisecond
{
    ESPTouchTask *_self = [self initWithApSsid:apSsid andApBssid:apBssid andApPwd:apPwd andIsSsidHiden:isSsidHidden];
    if (_self) {
        [_self._parameter setWaitUdpTotalMillisecond:timeoutMillisecond];
    }
    return _self;
}

- (void) __listenAsyn: (const int) expectDataLen
{
    dispatch_queue_t  queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        if (DEBUG_ON)
        {
            NSLog(@"ESPTouchTask __listenAsyn() start an asyn listen task, current thread is: %@", [NSThread currentThread]);
        }
        NSTimeInterval startTimestamp = [[NSDate date] timeIntervalSince1970];
        NSString *apSsidAndPwd = [NSString stringWithFormat:@"%@%@",self._apSsid,self._apPwd];
        Byte expectOneByte = [ESP_ByteUtil getBytesByNSString:apSsidAndPwd].length + 8;
        if (DEBUG_ON) {
            NSLog(@"ESPTouchTask __listenAsyn() expectOneByte: %d",expectOneByte);
        }
        Byte receiveOneByte = -1;
        NSData *receiveData = nil;
        int correctBroadcastCount = 0;
        while (correctBroadcastCount < [self._parameter getThresholdSucBroadcastCount])
        {
            receiveData = [self._server receiveSpecLenBytes:expectDataLen];
            if (receiveData != nil)
            {
                [receiveData getBytes:&receiveOneByte length:1];
            }
            if (receiveOneByte == expectOneByte)
            {
                correctBroadcastCount++;
                if (DEBUG_ON)
                {
                    NSLog(@"ESPTouchTask __listenAsyn() receive %d correct broadcast",correctBroadcastCount);
                }
                // change the socket's timeout
                NSTimeInterval consume = [[NSDate date] timeIntervalSince1970] - startTimestamp;
                int timeout = (int)([self._parameter getWaitUdpTotalMillisecond] - consume*1000);
                if (timeout < 0)
                {
                    if (DEBUG_ON)
                    {
                        NSLog(@"ESPTouchTask __listenAsyn() esptouch timeout");
                    }
                    break;
                }
                else
                {
                    if (DEBUG_ON)
                    {
                        NSLog(@"ESPTouchTask __listenAsyn() socketServer's new timeout is %d milliseconds",timeout);
                    }
                    [self._server setSocketTimeout:timeout];
                    if (correctBroadcastCount == [self._parameter getThresholdSucBroadcastCount])
                    {
                        if (DEBUG_ON)
                        {
                            NSLog(@"ESPTouchTask __listenAsyn() receive enough correct broadcast");
                        }
                        if (receiveData != nil)
                        {
                            NSString *bssid =
                            [ESP_ByteUtil parseBssid:(Byte *)[receiveData bytes]
                                              Offset:[self._parameter getEsptouchResultOneLen]
                                               Count:[self._parameter getEsptouchResultMacLen]];
                            NSData *inetAddrData =
                            [ESP_NetUtil parseInetAddrByData:receiveData
                                                   andOffset:[self._parameter getEsptouchResultOneLen] + [self._parameter getEsptouchResultMacLen]
                                                    andCount:[self._parameter getEsptouchResultIpLen]];
                            self._esptouchResult = [[ESPTouchResult alloc]initWithIsSuc:YES andBssid:bssid andInetAddrData:inetAddrData];
                        }
                        self._isSuc = YES;
                        break;
                    }
                }
            }
            else if (expectDataLen == [self._parameter getEsptouchResultTotalLen] && receiveData == nil)
            {
                if (DEBUG_ON)
                {
                    NSLog(@"ESPTouchTask __listenAsyn() esptouch timeout 3");
                }
                break;
            }
            else
            {
                if (DEBUG_ON)
                {
                    NSLog(@"ESPTouchTask __listenAsyn() receive rubbish message, just ignore");
                }
            }
        }
        [self __interrupt];
        if (DEBUG_ON)
        {
            NSLog(@"ESPTouchTask __listenAsyn() esptouch finished");
        }
        if (DEBUG_ON)
        {
            NSLog(@"ESPTouchTask __listenAsyn() finish");
        }
    });
}

- (void) interrupt
{
    if (DEBUG_ON)
    {
        NSLog(@"ESPTouchTask interrupt()");
    }
    self.isCancelled = YES;
    [self __interrupt];
}

- (void) __interrupt
{
    self._isInterrupt = YES;
    [self._client interrupt];
    [self._server interrupt];
    // notify the ESPTouchTask to wake up from sleep mode
    [self __notify];
}

- (BOOL) __execute: (ESPTouchGenerator *)generator
{
    NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970];
    NSTimeInterval currentTime = startTime;
    NSTimeInterval lastTime = currentTime - [self._parameter getTimeoutTotalCodeMillisecond];
    
    NSArray *gcBytes2 = [generator getGCBytes2];
    NSArray *dcBytes2 = [generator getDCBytes2];
    
    int index = 0;
    
    while (!self._isInterrupt)
    {
        if (currentTime - lastTime >= [self._parameter getTimeoutTotalCodeMillisecond]/1000.0)
        {
            if (DEBUG_ON)
            {
                NSLog(@"ESPTouchTask __execute() send gc code ");
            }
            // send guide code
            while (!self._isInterrupt && [[NSDate date] timeIntervalSince1970] - currentTime < [self._parameter getTimeoutGuideCodeMillisecond]/1000.0)
            {
                [self._client sendDataWithBytesArray2:gcBytes2
                                     ToTargetHostName:[self._parameter getTargetHostname]
                                             WithPort:[self._parameter getTargetPort]
                                          andInterval:[self._parameter getIntervalGuideCodeMillisecond]];
                // check whether the udp is send enough time
                if ([[NSDate date] timeIntervalSince1970] - startTime > [self._parameter getWaitUdpSendingMillisecond]/1000.0)
                {
                    break;
                }
            }
            lastTime = currentTime;
        }
        else
        {
            [self._client sendDataWithBytesArray2:dcBytes2
                                           Offset:index
                                            Count:ONE_DATA_LEN
                                 ToTargetHostName:[self._parameter getTargetHostname]
                                         WithPort:[self._parameter getTargetPort]
                                      andInterval:[self._parameter getIntervalDataCodeMillisecond]];
        }
        currentTime = [[NSDate date] timeIntervalSince1970];
        // check whether the udp is send enough time
        if ([[NSDate date] timeIntervalSince1970] - startTime > [self._parameter getWaitUdpSendingMillisecond]/1000.0)
        {
            break;
        }
        index = (index + ONE_DATA_LEN) % [dcBytes2 count];
    }
    
    return self._isSuc;
}

- (void) __checkTaskValid
{
    if (self._isExecutedAlready)
    {
        perror("ESPTouchTask __checkTaskValid() fail, the task could be executed only once");
    }
    // !!!NOTE: the esptouch task could be executed only once
    assert(!self._isExecutedAlready);
    self._isExecutedAlready = YES;
}

- (ESPTouchResult *) executeForResult
{
    [self __checkTaskValid];
    
    NSData *localInetAddrData = [ESP_NetUtil getLocalInetAddress];
    if (DEBUG_ON)
    {
        NSLog(@"ESPTouchTask executeForResult() localInetAddr: %@", [ESP_NetUtil descriptionInetAddrByData:localInetAddrData]);
    }
    // generator the esptouch byte[][] to be transformed, which will cost
    // some time(maybe a bit much)
    ESPTouchGenerator *generator = [[ESPTouchGenerator alloc]initWithSsid:self._apSsid andApBssid:self._apBssid andApPassword:self._apPwd andInetAddrData:localInetAddrData andIsSsidHidden:self._isSsidHidden];
    // listen the esptouch result asyn
    [self __listenAsyn:[self._parameter getEsptouchResultTotalLen]];
    ESPTouchResult *esptouchResult = [[ESPTouchResult alloc]initWithIsSuc:NO andBssid:nil andInetAddrData:nil];
    BOOL isSuc = NO;
    for (int i = 0; i < [self._parameter getTotalRepeatTime]; i++)
    {
        isSuc = [self __execute:generator];
        if (isSuc)
        {
            self._esptouchResult.isCancelled = self.isCancelled;
            return self._esptouchResult;
        }
    }
    
    [self __sleep: [self._parameter getWaitUdpReceivingMillisecond]];
    [self __interrupt];
    esptouchResult.isCancelled = self.isCancelled;
    return esptouchResult;
}

// sleep some milliseconds
- (BOOL) __sleep :(long) milliseconds
{
    if (DEBUG_ON)
    {
        NSLog(@"ESPTouchTask __sleep() start");
    }
    NSDate *date = [NSDate dateWithTimeIntervalSinceNow: milliseconds/1000.0];
    [self._condition lock];
    BOOL signaled = NO;
    while (!self._isWakeUp && (signaled = [self._condition waitUntilDate:date]))
    {
    }
    [self._condition unlock];
    if (DEBUG_ON)
    {
        NSLog(@"ESPTouchTask __sleep() end, receive signal is %@", signaled ? @"YES" : @"NO");
    }
    return signaled;
}

// notify the sleep thread to wake up
- (void) __notify
{
    if (DEBUG_ON)
    {
        NSLog(@"ESPTouchTask __notify()");
    }
    [self._condition lock];
    self._isWakeUp = YES;
    [self._condition signal];
    [self._condition unlock];
}

@end
