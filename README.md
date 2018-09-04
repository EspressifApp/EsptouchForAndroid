# EspTouch for Android

[![](https://jitpack.io/v/EspressifApp/EsptouchForAndroid.svg)](https://jitpack.io/#EspressifApp/EsptouchForAndroid)

To configure network for Esp8266/Esp32 devices

## Licence
- See [Licence](ESPRESSIF_MIT_LICENSE_V1.LICENSE)

## Version Log
- See [Log](Log.md)

## Gradle Dependency 

- Project level `build.gradle`

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- App level `build.gradle`

```gradle
dependencies {
    implementation 'com.github.EspressifApp:EsptouchForAndroid:0.3.6.2'
}
```


## Jar Release
- See [libs](releases/libs)  
    - If you don't want use [esptouch](esptouch) module, copy the jar to your own project.