# EspTouch for Android
This APP is used to configure ESP devices to connect target AP.  
The devices need run smart config: [esp-idf](https://github.com/espressif/esp-idf/tree/master/examples/wifi/smart_config) or [ESP8266_RTOS_SDK](https://github.com/espressif/ESP8266_RTOS_SDK/tree/master/examples/wifi/smart_config)  

**Note: EspTouchV2 is not compatible with EspTouch**

## Licence
- See [Licence](ESPRESSIF_MIT_LICENSE)

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
  implementation 'com.github.EspressifApp:lib-esptouch-android:1.0.0'
  ```
  ```
  implementation 'com.github.EspressifApp:lib-esptouch-v2-android:2.1.0'
  ```

## Lib Source Code
- EspTouch: [lib-esptouch-android](https://github.com/EspressifApp/lib-esptouch-android)
- EspTouchV2: [lib-esptouch-v2-android](https://github.com/EspressifApp/lib-esptouch-v2-android)

## Api
- EspTouch: [doc](https://github.com/EspressifApp/lib-esptouch-android/blob/main/esptouch/README.md)
- EspTouchV2: [doc](https://github.com/EspressifApp/lib-esptouch-v2-android/blob/main/esptouch-v2/README.md)

## Version Log
- See [Log](log/log-en.md)

## Releases
- See [releases](https://github.com/EspressifApp/EsptouchForAndroid/releases)
