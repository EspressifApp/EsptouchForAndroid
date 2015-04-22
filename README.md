==================================v0.2.0==================================

1.  add check valid mechanism to forbid such situation:

        NSString *apSsid = @"";// or apSsid = null

        NSString *apPassword = @"pwd";

        EsptouchTask *esptouchTask = [[ESPTouchTask alloc]initWithApSsid:apSsid andApPwd:apPwd]; 
   
2.  add check whether the task is executed to forbid such situation,
    
    thx for the engineer in smartline YuguiYu's proposal:

        NSString *apSsid = @"ssid";

        NSString *apPassword = @"pwd";

        EsptouchTask *esptouchTask = [[ESPTouchTask alloc]initWithApSsid:apSsid andApPwd:apPwd]; 

        // wrong usage, which shouldn't happen

        {

            [esptouchTask execute];

            [esptouchTask execute];

        }

        // correct usage

        {
       
            [esptouchTask execute];

            EsptouchTask *esptouchTask = [[ESPTouchTask alloc]initWithApSsid:apSsid andApPwd:apPwd]; 

            [esptouchTask execute];

        }

==================================v0.1.9==================================

1.  fix some old bugs in the App


2.  Add new Interface of Esptouch task( Smart Configure must v1.1 to support it)

        The usage of it is like this:

        // create the Esptouch task

        EsptouchTask *esptouchTask = [[ESPTouchTask alloc]initWithApSsid:apSsid andApPwd:apPwd]; 

        // execute syn util it suc or timeout

        EsptouchResult *result = [esptouchTask executeForResult];

        // check whehter the execute is suc

        BOOL isSuc = result.isSuc;

        // get the device's bssid, the format of the bssid is like this format: @"18fe3497f310"

        NSString *bssid = result.bssid;

        // when you'd like to interrupt it, just call the method below, and [esptouchTask execute] will return NO after it:

        [esptouchTask interrupt];

==================================v0.1.7==================================

1.  The entrance of the Demo is ESPViewController.m


2.  EsptouchTask.h is the interface of Esptouch task.

        The usage of it is like this:

        // create the Esptouch task

        EsptouchTask *esptouchTask = [[ESPTouchTask alloc]initWithApSsid:apSsid andApPwd:apPwd]; 

        // execute syn util it suc or timeout

        BOOL result = [esptouchTask execute];

        // when you'd like to interrupt it, just call the method below, and [esptouchTask execute] will return NO after it:

        [esptouchTask interrupt];
   
3. The abstract interface is in the group esptouch
 
4. More info about the EspTouch Demo, please read the source code and annotation
