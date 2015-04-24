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
