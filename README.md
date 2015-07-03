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

==================================v0.3.4==================================

1.  Espressif's Smart Config is updated to v2.4, and some paremeters are changed.

    <b>Esptouch v0.3.4 only support Espressif's Smart Config v2.4</b>

2.  Usage:

    The same as v0.3.3.

=================================v0.3.3==================================

1.  Espressif's Smart Config is updated to v2.2, and the protocol is changed.

    <b>Esptouch v0.3.3 only support Espressif's Smart Config v2.2</b>

2.  Usage:

    The usage of v0.3.0 is supported, besides one new API is added:

    List<IEsptouchResult> executeForResults(int expectTaskResultCount)

    The only differece is that it return list, and require expectTaskResultCount

==================================v0.3.2==================================

1.  Espressif's Smart Config is updated to v2.2, and the protocol is changed.

    <b>Esptouch v0.3.2 only support Espressif's Smart Config v2.2</b>

2.  Usage:

    The same as v0.3.0.

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

    // <b>note: one task can't executed more than once:</b>

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

==================================v0.2.2==================================

1.  add isCancelled API in ESPTouchTask and ESPTouchResult to check whether the task

    is cancelled by user directly.

==================================v0.2.1==================================

1.  fix the bug when SSID char is more than one byte value(0xff), the apk will crash

    thx for the engineer in NATop YoungYang's discovery

2.  the encoding charset could be set, the default one is "UTF-8":

    change the constant ESPTOUCH_ENCODING_CHARSET in ByteUtil.java

    (It will lead to ESPTOUCH fail for wrong CHARSET is set.

     Whether the CHARSET is correct is depend on the phone or pad.

     More info and discussion please refer to http://bbs.espressif.com/viewtopic.php?f=8&t=397)

==================================v0.2.0==================================

1.  add check valid mechanism to forbid such situation:

        String apSsid = "";// or apSsid = null

        String apPassword = "pwd";

        IEsptouchTask esptouchTask = new EsptouchTask(apSsid, apPassword);
   
2.  add check whether the task is executed to forbid such situation,
	
	thx for the engineer in smartline YuguiYu's proposal:

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

==================================v0.1.9==================================

1.  fix the bug that some Android device can't receive broadcast,
	
	thx for the engineer in Joyoung xushx's help


2.  fix some old bugs in the App


3.  Add new Interface of Esptouch task( Smart Configure must v1.1 to support it)

    	The usage of it is like this:

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

==================================v0.1.7==================================

1.  The entrance of the Demo is com.espressif.iot.esptouch.demo_activity.EsptouchDemoActivity.java


2.  IEsptouchTask is the interface of Esptouch task.

    	The usage of it is like this:

    	// create the Esptouch task

    	IEsptouchTask esptouchTask = new EsptouchTask(apSsid, apPassword);

    	// execute syn util it suc or timeout

    	boolean result = esptouchTask.execute();

    	// when you'd like to interrupt it, just call the method below, and esptouchTask.execute() will return false after it:

    	esptouchTask.interrupt();
   
3. The abstract interface is in the package com.espressif.iot.esptouch
 
4. More info about the EspTouch Demo, please read the source code and annotation
