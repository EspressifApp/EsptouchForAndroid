# EspTouch for Android
This APP is used to configure ESP devices to connect target AP.  
The devices need run smart config: [esp-idf](https://github.com/espressif/esp-idf/tree/master/examples/wifi/smart_config) or [ESP8266_RTOS_SDK](https://github.com/espressif/ESP8266_RTOS_SDK/tree/master/examples/wifi/smart_config)  

**Note: EspTouchV2 is not compatible with EspTouch**

## Licence
- See [Licence](LICENSE)

## How to import
- Add this in your root `build.gradle` at the end of repositories:
  ```
  allprojects {
      repositories {
          ...
          maven { url 'https://jitpack.io' }
      }
  }
   ```
- And add a dependency code to your app module's `build.gradle` file.
  ```
  implementation 'com.github.EspressifApp:lib-esptouch-android:1.1.1'
  ```
  ```
  implementation 'com.github.EspressifApp:lib-esptouch-v2-android:2.2.1'
  ```

## Lib Source Code
- EspTouch: [esptouch](esptouch)
- EspTouchV2: [esptouch-v2](esptouch-v2)

## Api
- EspTouch: [doc](esptouch/README.md)
- EspTouchV2: [doc](esptouch-v2/README.md)

## Version Log
- APP [Log](log/log-en.md)
- Lib EspTouch [Log](esptouch/ChangeLogs/log_en.md)
- Lib EspTouchV2 [Log](esptouch-v2/ChangeLogs/log_en.md)

## Releases
- See [releases](https://github.com/EspressifApp/EsptouchForAndroid/releases)
