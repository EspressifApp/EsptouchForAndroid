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


#define INTERVAL_GUIDE_CODE_MILLISECOND     8

#define INTERVAL_DATA_CODE_MILLISECOND      8

#define TIMEOUT_MILLISECOND_GUIDE_CODE      2000

#define TIMEOUT_MILLISECOND_DATA_CODE       4000

#define TIMEOUT_MILLISECOND_TOTAL_CODE (TIMEOUT_MILLISECOND_GUIDE_CODE + TIMEOUT_MILLISECOND_DATA_CODE)

/*
 * TOTAL_REPEAT_TIME means execute how many circle times
 */
#define TOTAL_REPEAT_TIME                   1

/*
 * WAIT_UDP_RESPONSE_MILLISECOND means just wait the device send udp broadcast response,
 * but don't send udp broadcast at the sametime
 */
#define WAIT_UDP_RESPONSE_MILLISECOND       10000

/**
 * the len of the Esptouch result 1st byte is the total length of ssid and
 * password, the other 6 bytes are the device's bssid
 */
#define ESP_TOUCH_RESULT_ONE_LEN            1

#define ESP_TOUCH_RESULT_MAC_LEN            6

#define ESP_TOUCH_RESULT_IP_LEN             4

#define ESP_TOUCH_RESULT_TOTAL_LEN  (ESP_TOUCH_RESULT_ONE_LEN + ESP_TOUCH_RESULT_MAC_LEN + ESP_TOUCH_RESULT_IP_LEN)

/**
 * The port which device will send broadcast when it configured suc
 */
#define PORT_LISTENING                      18266

/**
 * Time between the device receive the Ap's ssid,password and the device send broadcast
 */
#define TIME_MILLISECOND_DEVICE_SEND_BROADCAST  4000

/**
 * The timeout for Esptouch wait the device sending broadcast
 */
#define WAIT_TIMEOUT_MILLISECOND            48000

/**
 * The threshold number how many UDP broadcast received when we think the
 * device is configured suc
 */
#define THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT  1

/**
 * The broadcast host name
 */
#define TARGET_HOSTNAME  @"255.255.255.255"

/**
 * The target port
 */
#define TARGET_PORT  7001

@interface ESPTouchTask ()

@property (nonatomic,strong) NSString *_apSsid;

@property (nonatomic,strong) NSString *_apPwd;

@property (atomic,assign) BOOL _isSuc;

@property (atomic,assign) BOOL _isInterrupt;

@property (nonatomic,strong) ESPUDPSocketClient *_client;

@property (nonatomic,strong) ESPUDPSocketServer *_server;

@property (atomic,strong) ESPTouchResult *_esptouchResult;

@property (atomic,strong) NSCondition *_condition;

@property (nonatomic,assign) __block BOOL _isWakeUp;

@property (nonatomic,assign) volatile BOOL _isExecutedAlready;

@end

@implementation ESPTouchTask

- (id) initWithApSsid: (NSString *) apSsid andApPwd: (NSString *) apPwd
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
        self._client = [[ESPUDPSocketClient alloc]init];
        self._server = [[ESPUDPSocketServer alloc]initWithPort:PORT_LISTENING AndSocketTimeout:WAIT_TIMEOUT_MILLISECOND
                        + WAIT_UDP_RESPONSE_MILLISECOND];
        self._isSuc = NO;
        self._isInterrupt = NO;
        self._isWakeUp = NO;
        self._isExecutedAlready = NO;
        self._condition = [[NSCondition alloc]init];
    }
    return self;
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
        Byte expectOneByte = [ESP_ByteUtil getBytesByNSString:apSsidAndPwd].length + 7;
        if (DEBUG_ON) {
            NSLog(@"ESPTouchTask __listenAsyn() expectOneByte: %d",expectOneByte);
        }
        Byte receiveOneByte = -1;
        NSData *receiveData = nil;
        int correctBroadcastCount = 0;
        while (correctBroadcastCount < THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT)
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
                int timeout = (int)(WAIT_TIMEOUT_MILLISECOND - consume*1000);
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
                    if (correctBroadcastCount == THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT)
                    {
                        if (DEBUG_ON)
                        {
                            NSLog(@"ESPTouchTask __listenAsyn() receive enough correct broadcast");
                        }
                        if (receiveData != nil)
                        {
                            NSString *bssid =
                            [ESP_ByteUtil parseBssid:(Byte *)[receiveData bytes] Offset:ESP_TOUCH_RESULT_ONE_LEN Count:ESP_TOUCH_RESULT_MAC_LEN];
                            NSData *inetAddrData = [ESP_NetUtil parseInetAddrByData:receiveData andOffset:ESP_TOUCH_RESULT_ONE_LEN
                                                    + ESP_TOUCH_RESULT_MAC_LEN andCount:ESP_TOUCH_RESULT_IP_LEN];
                            self._esptouchResult = [[ESPTouchResult alloc]initWithIsSuc:YES andBssid:bssid andInetAddrData:inetAddrData];
                        }
                        self._isSuc = YES;
                        break;
                    }
                }
            }
            else if (expectDataLen == ESP_TOUCH_RESULT_TOTAL_LEN
                     && receiveData == nil)
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
    NSTimeInterval lastTime = currentTime - TIMEOUT_MILLISECOND_TOTAL_CODE;
    
    NSArray *gcBytes2 = [generator getGCBytes2];
    NSArray *dcBytes2 = [generator getDCBytes2];
    
    int index = 0;
    const int one_data_len = 3;
    
    while (!self._isInterrupt)
    {
        if (currentTime - lastTime >= TIMEOUT_MILLISECOND_TOTAL_CODE/1000.0)
        {
            if (DEBUG_ON)
            {
                NSLog(@"ESPTouchTask __execute() send gc code ");
            }
            // send guide code
            while (!self._isInterrupt && [[NSDate date] timeIntervalSince1970] - currentTime < TIMEOUT_MILLISECOND_GUIDE_CODE/1000.0)
            {
                [self._client sendDataWithBytesArray2:gcBytes2 ToTargetHostName:TARGET_HOSTNAME WithPort:TARGET_PORT andInterval:INTERVAL_GUIDE_CODE_MILLISECOND];
                // check whether the udp is send enough time
                if ([[NSDate date] timeIntervalSince1970] - startTime > WAIT_TIMEOUT_MILLISECOND/1000.0)
                {
                    break;
                }
            }
            lastTime = currentTime;
        }
        else
        {
            [self._client sendDataWithBytesArray2:dcBytes2 Offset:index Count:one_data_len ToTargetHostName:TARGET_HOSTNAME WithPort:TARGET_PORT andInterval:INTERVAL_DATA_CODE_MILLISECOND];
        }
        currentTime = [[NSDate date] timeIntervalSince1970];
        // check whether the udp is send enough time
        if ([[NSDate date] timeIntervalSince1970] - startTime > WAIT_TIMEOUT_MILLISECOND/1000.0)
        {
            break;
        }
        index = (index + one_data_len) % [dcBytes2 count];
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
    ESPTouchGenerator *generator = [[ESPTouchGenerator alloc]initWithSsid:self._apSsid andApPassword:self._apPwd andInetAddrData:localInetAddrData];
    // listen the esptouch result asyn
    [self __listenAsyn:ESP_TOUCH_RESULT_TOTAL_LEN];
    ESPTouchResult *esptouchResult = [[ESPTouchResult alloc]initWithIsSuc:NO andBssid:nil andInetAddrData:nil];
    BOOL isSuc = NO;
    for (int i = 0; i < TOTAL_REPEAT_TIME; i++)
    {
        isSuc = [self __execute:generator];
        if (isSuc)
        {
            self._esptouchResult.isCancelled = self.isCancelled;
            return self._esptouchResult;
        }
    }
    
    [self __sleep: WAIT_UDP_RESPONSE_MILLISECOND];
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
    NSDate *date = [NSDate dateWithTimeIntervalSinceNow: WAIT_UDP_RESPONSE_MILLISECOND/1000.0];
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
