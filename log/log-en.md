[[简体中文]](log-zh-rCN.md)

# Update Log

## v2.0.0
- Support EspTouchV2
    - EspTouchV2 is not compatible with EspTouch

## v1.1.1
EspTouch v0.3.7.2
- Optimize permission check

## v1.1.0
EspTouch v0.3.7.2
- Migrate to AndroidX
- Change App theme
- Add signature

## v1.0.0
EspTouch v0.3.7.1
- Add Chinese
- Check location(GPS) state if no wifi info on Android 9
- Hide EsptouchTask's aes constructor, device doesn't support currently

## v0.3.7.0
- Request location permission if Android SDK greater than 27
- Add option to select Broadcast or Multicast

## v0.3.6.2
- Add new constructor for EsptouchTask
```
    String ssidStr = "ssid";
    byte[] ssid = ByteUtil.getBytesByString(ssid);
    String bssidStr = "aa:bb:cc:dd:ee:ff";
    byte [] bssid = EspNetUtil.parseBssid2bytes(bssidStr);
    String passwordStr = "password";
    byte[] password = ByteUtil.getBytesByString(passwordStr);
    EspAes aes = null;
    EsptouchTask task = new EsptouchTask(ssid, bssid, password, aes, context);
```

## v0.3.6.1
- Modify bssid post sequence

## v0.3.6.0
- Fix bug that cann't configure Chinese SSID

## v0.3.4.7
- Change project from Eclipse to Android Studio
- Modify app theme
- Listen the change of wifi state when configuring
- Support AES128 encryption
```
    byte[] secretKey = "1234567890123456".getBytes(); // TODO use your own key
    EspAES aes = new EspAES(secretKey);
    EsptouchTask task = new EsptouchTask(apSsid, apBssid, apPassword, aes, context);
```

## v0.3.4.6
- isSsidHidden is true forever
- Espressif's Smart Config is updated to v2.4.
    - Esptouch v0.3.4.6 support latest Espressif's Smart Config

## v0.3.4.5
- fix the bug when interrupt the esptouch task, it will interrupt the current Thread instead of esptouch task. (thx for the engineer in Opple XingZhiGong's discovery)
- support various encoding Wi-Fi Ssid not merely UTF-8 or other specified one
- Espressif's Smart Config is updated to v2.4.
    - Esptouch v0.3.4.5 only support Espressif's Smart Config v2.4

## v0.3.4.3
- Espressif's Smart Config is updated to v2.4.
    - Esptouch v0.3.4.3 only support Espressif's Smart Config v2.4

## v0.3.4.2
- Espressif's Smart Config is updated to v2.4, and some paremeters are changed.
    - Esptouch v0.3.4.2 only support Espressif's Smart Config v2.4
```
    The usage of v0.3.4 is supported, besides one new API is added:
    void onEsptouchResultAdded(IEsptouchResult result);
    It support asyn callback when one device is connected to AP.
 ```

## v0.3.4
- Espressif's Smart Config is updated to v2.4, and some paremeters are changed.
    - Esptouch v0.3.4 only support Espressif's Smart Config v2.4

## v0.3.3
- Espressif's Smart Config is updated to v2.2, and the protocol is changed.
    - Esptouch v0.3.3 only support Espressif's Smart Config v2.2
```
    The usage of v0.3.0 is supported, besides one new API is added:
    List<IEsptouchResult> executeForResults(int expectTaskResultCount)
    The only differece is that it return list, and require expectTaskResultCount
```

## v0.3.2
- Espressif's Smart Config is updated to v2.2, and the protocol is changed.
    - Esptouch v0.3.2 only support Espressif's Smart Config v2.2

## v0.3.1
- Espressif's Smart Config is updated to v2.1, and the protocol is changed.
    - Esptouch v0.3.1 only support Espressif's Smart Config v2.1
- fix some bugs in v0.3.0

## v0.3.0
- Espressif's Smart Config is updated to v2.1, and the protocol is changed.
    - Esptouch v0.3.0 only support Espressif's Smart Config v2.1
```
    // build esptouch task
    String apSsid = "wifi-1;
    String apBssid = "12:34:56:78:9a:bc";
    String apPwd = "1234567890";
    boolean isSsidHidden = false;// whether the Ap's ssid is hidden, it is false usually
    IEspTouchTask task = new EspTouchTask(apSsid, apBssid, apPassword,
    isSsidHidden, XXXActivity.this);
    // if you'd like to determine the timeout by yourself, use the follow:
    int timeoutMillisecond = 58000;// it should >= 18000, 58000 is default
    IEspTouchTask task = new EspTouchTask(apSsid, apBssid, apPassword,
    isSsidHidden, timeoutMillisecond, XXXActivity.this);
    // execute for result
    IESPTouchResult esptouchReult = task.executeForResult();  
    // note: one task can't executed more than once:
    IESPTouchTask esptouchTask = new EsptouchTask(...)   
    // wrong usage, which shouldn't happen
    {
        esptouchTask.executeForResult();
        esptouchTask.executeForResult();
    }
    // correct usage
    {
        esptouchTask.executeForResult();
        IEsptouchTask esptouchTask = new EsptouchTask(...);
        esptouchTask.executeForResult();
    }
```

## v0.2.2
- add isCancelled API in ESPTouchTask and ESPTouchResult to check whether the task is cancelled by user directly.

## v0.2.1
- fix the bug when SSID char is more than one byte value(0xff), the apk will crash
    - thx for the engineer in NATop YoungYang's discovery 
- the encoding charset could be set, the default one is "UTF-8"
    - change the constant ESPTOUCH_ENCODING_CHARSET in ByteUtil.java
    - It will lead to ESPTOUCH fail for wrong CHARSET is set. Whether the CHARSET is correct is depend on the phone or pad.
    - More info and discussion please refer to http://bbs.espressif.com/viewtopic.php?f=8&t=397)

## v0.2.0
- add check valid mechanism to forbid such situation
```
String apSsid = "";// or apSsid = null
String apPassword = "pwd";
IEsptouchTask esptouchTask = new EsptouchTask(apSsid, apPassword);
```
- add check whether the task is executed to forbid such situation
    - thx for the engineer in smartline YuguiYu's proposal:
    ```
    String apSsid = "ssid";
    String apPassword = "pwd";
    IEsptouchTask esptouchTask = new EsptouchTask(apSsid, apPassword);
    // wrong usage, which shouldn't happen
    {
        esptouchTask.execute();
        esptouchTask.execute();
    }
    // correct usage
    {
        esptouchTask.execute();
        esptouchTask = new EsptouchTask(apSsid, apPassword);
        esptouchTask.execute();
    }
    ```

## v0.1.9
- fix the bug that some Android device can't receive broadcast
    - thx for the engineer in Joyoung xushx's help
- fix some old bugs in the App
- Add new Interface of Esptouch task( Smart Configure must v1.1 to support it)
```
// create the Esptouch task
IEsptouchTask esptouchTask = new EsptouchTask(apSsid, apPassword);
// execute syn util it suc or timeout
IEsptouchResult result = esptouchTask.executeForResult();
// check whehter the execute is suc
boolean isSuc = result.isSuc();
// get the device's bssid, the format of the bssid is like this format: "18fe3497f310"
String bssid = result.getBssid();
// when you'd like to interrupt it, just call the method below, and esptouchTask.execute() will return false after it:
esptouchTask.interrupt();
```

## v0.1.7
- The entrance of the Demo is com.espressif.iot.esptouch.demo_activity.EsptouchDemoActivity.java
- IEsptouchTask is the interface of Esptouch task.
```
// create the Esptouch task
IEsptouchTask esptouchTask = new EsptouchTask(apSsid, apPassword);
// execute syn util it suc or timeout
boolean result = esptouchTask.execute();
// when you'd like to interrupt it, just call the method below, and esptouchTask.execute() will return false after it:
esptouchTask.interrupt();
```
- The abstract interface is in the package com.espressif.iot.esptouch
- More info about the EspTouch Demo, please read the source code and annotation
