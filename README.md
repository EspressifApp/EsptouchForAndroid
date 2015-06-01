<b>ESPRSSIF MIT License</b>

Copyright © 2015 <ESPRESSIF SYSTEMS (SHANGHAI) PTE LTD>

Permission is hereby granted <b>for use on ESPRESSIF SYSTEMS ESP8266 only, in which case, it is free of charge,</b> to any person obtaining a copy of this software and
associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

<b>乐鑫 MIT 许可证</b>

版权 © 2015  <乐鑫信息科技（上海）有限公司>

<b>该许可证授权仅限于乐鑫信息科技 ESP8266 产品的应用开发。</b>在此情况下，该许可证免费授权任何获得该软件及其相关文档（统称为“软件”）的人无限制地经营该软件，包括无限制
的使用、复制、修改、合并、出版发行、散布、再授权、及贩售软件及软件副本的权利。被授权人在享受这些权利的同时，需服从下面的条件：

在软件和软件的所有副本中都必须包含以上的版权声明和授权声明。

该软件按本来的样子提供，没有任何明确或暗含的担保，包括但不仅限于关于试销性、适合某一特定用途和非侵权的保证。作者和版权持有人在任何情况下均不就由软件或软件使用引起的以合同形式、民事侵权或其它方式提出的任何索赔、损害或其它责任负责。

==================================v0.3.1==================================

1.  Espressif's Smart Config is updated to v2.1, and the protocol is changed.

    <b>Esptouch v0.3.1 only support Espressif's Smart Config v2.1</b>

2.  Usage:

    The same as v0.3.0.(fix some bugs in v0.3.0)

==================================v0.3.0==================================

1.  Espressif's Smart Config is updated to v2.1, and the protocol is changed.

    <b>Esptouch v0.3.0 only support Espressif's Smart Config v2.1</b>

2.  Usage:

    // build esptouch task

    NSString *apSsid = @"wifi-1";

    NSString *apBssid = @"12:34:56:78:9a:bc";

    NSString *apPwd = @"1234567890";

    BOOL isSsidHidden = NO;// whether the Ap's ssid is hidden, it is NO usually

    ESPTouchTask *task = [[ESPTouchTask alloc]initWithApSsid:apSsid

                                                  andApBssid:apBssid

                                                    andApPwd:apPwd

                                              andIsSsidHiden:isSsidHidden];

    // if you'd like to determine the timeout by yourself, use the follow:

    int timeoutMillisecond = 58000;// it should >= 18000, 58000 is default

    ESPTouchTask *task = [[ESPTouchTask alloc]initWithApSsid:apSsid

                                                  andApBssid:apBssid

                                                    andApPwd:apPwd

                                              andIsSsidHiden:isSsidHidden

                                       andTimeoutMillisecond:timeoutMillisecond];

    // execute for result

    ESPTouchResult *esptouchReult = [task executeForResult];

    // <b>note: one task can't executed more than once:</b>

    ESPTouchTask *esptouchTask = [[ESPTouchTask alloc]initXXX...];

    // wrong usage, which shouldn't happen

    {

        [esptouchTask executeForResult];

        [esptouchTask executeForResult];

    }

    // correct usage

    {

        [esptouchTask executeForResult];

        EsptouchTask *esptouchTask = [[ESPTouchTask alloc]initXXX...];

        [esptouchTask executeForResult];

    }

==================================v0.2.2==================================

1.  add isCancelled API in ESPTouchTask and ESPTouchResult to check whether the task

    is cancelled by user directly.

==================================v0.2.1==================================

1.  fix the bug when SSID char is more than one byte value(0xff), esptouch will fail forever

    thx for the engineer in NATop YoungYang's discovery

2.  the encoding charset could be set, the default one is "NSUTF8StringEncoding":

    change the macro ESPTOUCH_NSStringEncoding in ESP_ByteUtil.h

    (It will lead to ESPTOUCH fail for wrong CHARSET is set.

     Whether the CHARSET is correct is depend on the phone or pad.

     More info and discussion please refer to http://bbs.espressif.com/viewtopic.php?f=8&t=397)

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
