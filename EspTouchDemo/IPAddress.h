//
//  IPAddress.h
//  LocalIpDemo
//
//  Created by 白 桦 on 5/11/15.
//  Copyright (c) 2015 白 桦. All rights reserved.
//

#ifndef LocalIpDemo_IPAddress_h
#define LocalIpDemo_IPAddress_h

#define MAXADDRS    32

extern char *if_names[MAXADDRS];
extern char *ip_names[MAXADDRS];
extern char *hw_addrs[MAXADDRS];
extern unsigned long ip_addrs[MAXADDRS];

// Function prototypes

void InitAddresses();
void FreeAddresses();
void GetIPAddresses();
void GetHWAddresses();

#endif
