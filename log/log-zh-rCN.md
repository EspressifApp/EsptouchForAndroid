[[English]](log-en.md)

# Update Log

## v2.3.2
- 因为 ESP32-C5 支持 5G 频率，允许 APP 在 5G 频率下进行 smart config.
- EspTouchV2
  - 升级 esptouch-v2 库到 2.2.1
- EspTouch
  - 升级 esptouch 库到 1.1.1

## v2.3.1
- 升级 gradle
- 升级 `targetSdkVersion` 到 32
- 修改 `sourceCompatibility` 和 `targetCompatibility` 为 JavaVersion.VERSION_1_8
- EspTouchV2
  - 升级 esptouch-v2 库到 2.2.0
- EspTouch
  - 升级 esptouch 库到 1.1.0

## v2.3.0
- EspTouchV2 
    - 增加设置配网设备个数
    - 自定义数据最大长度改为 64 字节
    - 更新 esptouch-v2 库到 2.1.0

## v2.0.0
- 支持 EspTouchV2 协议
    - 注: EspTouchV2 与 EspTouch 不能兼容

## v1.1.1
EspTouch v0.3.7.3
- 优化权限检查和状态提示

## v1.1.1
EspTouch v0.3.7.2
- 优化权限检查和状态提示

## v1.1.0
EspTouch v0.3.7.2
- 使用AndroidX库
- 修改应用主题
- 增加签名文件

## v1.0.0
EspTouch v0.3.7.1
- 增加中文
- 在Android9.0下若获取不到Wi-Fi信息，检查GPS状态
- 隐藏EsptouchTask的aes构造函数, 设备暂不支持
