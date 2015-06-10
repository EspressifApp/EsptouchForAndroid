//
//  ESPTaskParameter.m
//  EspTouchDemo
//
//  Created by 白 桦 on 5/20/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#import "ESPTouchTaskParameter.h"

@interface ESPTaskParameter()
@property (nonatomic,assign) long intervalGuideCodeMillisecond;
@property (nonatomic,assign) long intervalDataCodeMillisecond;
@property (nonatomic,assign) long timeoutGuideCodeMillisecond;
@property (nonatomic,assign) long timeoutDataCodeMillisecond;
@property (nonatomic,assign) long timeoutTotalCodeMillisecond;
@property (nonatomic,assign) int totalRepeatTime;
@property (nonatomic,assign) int esptouchResultOneLen;
@property (nonatomic,assign) int esptouchResultMacLen;
@property (nonatomic,assign) int esptouchResultIpLen;
@property (nonatomic,assign) int esptouchResultTotalLen;
@property (nonatomic,assign) int portListening;
@property (nonatomic,strong) NSString* targetHostname;
@property (nonatomic,assign) int targetPort;
@property (nonatomic,assign) int waitUdpReceivingMillisecond;
@property (nonatomic,assign) int waitUdpSendingMillisecond;
@property (nonatomic,assign) int thresholdSucBroadcastCount;
@property (nonatomic,assign) int expectTaskResultCount;
@end

@implementation ESPTaskParameter

- (id) init
{
    self = [super init];
    if (self) {
        self.intervalGuideCodeMillisecond = 10;
        self.intervalDataCodeMillisecond = 10;
        self.timeoutGuideCodeMillisecond = 2000;
        self.timeoutDataCodeMillisecond = 4000;
        self.timeoutTotalCodeMillisecond = 2000 + 4000;
        self.totalRepeatTime = 1;
        self.esptouchResultOneLen = 1;
        self.esptouchResultMacLen = 6;
        self.esptouchResultIpLen = 4;
        self.esptouchResultTotalLen = 1 + 6 + 4;
        self.portListening = 18266;
        self.targetHostname = @"255.255.255.255";
        self.targetPort = 7001;
        self.waitUdpReceivingMillisecond = 10000;
        self.waitUdpSendingMillisecond = 48000;
        self.thresholdSucBroadcastCount = 1;
        self.expectTaskResultCount = 1;
    }
    return self;
}

- (long) getIntervalGuideCodeMillisecond
{
    return self.intervalGuideCodeMillisecond;
}

- (long) getIntervalDataCodeMillisecond
{
    return self.intervalDataCodeMillisecond;
}

- (long) getTimeoutGuideCodeMillisecond
{
    return self.timeoutGuideCodeMillisecond;
}

- (long) getTimeoutDataCodeMillisecond
{
    return self.timeoutDataCodeMillisecond;
}

- (long) getTimeoutTotalCodeMillisecond
{
    return self.timeoutTotalCodeMillisecond;
}

- (int) getTotalRepeatTime
{
    return self.totalRepeatTime;
}

- (int) getEsptouchResultOneLen
{
    return self.esptouchResultOneLen;
}


- (int) getEsptouchResultMacLen
{
    return self.esptouchResultMacLen;
}


- (int) getEsptouchResultIpLen
{
    return self.esptouchResultIpLen;
}


- (int) getEsptouchResultTotalLen
{
    return self.esptouchResultTotalLen;
}

- (int) getPortListening
{
    return self.portListening;
}

- (NSString *) getTargetHostname
{
    return self.targetHostname;
}

- (int) getTargetPort
{
    return self.targetPort;
}

- (int) getWaitUdpReceivingMillisecond
{
    return self.waitUdpReceivingMillisecond;
}

- (int) getWaitUdpSendingMillisecond
{
    return self.waitUdpSendingMillisecond;
}

- (int) getWaitUdpTotalMillisecond
{
    return self.waitUdpReceivingMillisecond + self.waitUdpSendingMillisecond;
}

- (int) getThresholdSucBroadcastCount
{
    return self.thresholdSucBroadcastCount;
}

- (void) setWaitUdpTotalMillisecond: (int) waitUdpTotalMillisecond
{
    if (waitUdpTotalMillisecond < self.waitUdpSendingMillisecond + [self getTimeoutTotalCodeMillisecond])
    {
        // if it happen, even one turn about sending udp broadcast can't be completed
        NSLog(@"ESPTouchTaskParameter waitUdpTotalMillisecod is invalid, it is less than mWaitUdpReceivingMilliseond + [self getTimeoutTotalCodeMillisecond]");
        assert(0);
    }
    self.waitUdpSendingMillisecond = waitUdpTotalMillisecond - self.waitUdpReceivingMillisecond;
}

- (int) getExpectTaskResultCount
{
    return self.expectTaskResultCount;
}

- (void) setExpectTaskResultCount: (int) expectTaskResultCount
{
    _expectTaskResultCount = expectTaskResultCount;
}
@end
