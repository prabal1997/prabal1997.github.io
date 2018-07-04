package accutrack.iot.com;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class bluetoothservice extends android.app.Service {
	public static class bluetoothservice_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, bluetoothservice.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static bluetoothservice mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return bluetoothservice.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "accutrack.iot.com", "accutrack.iot.com.bluetoothservice");
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        processBA.setActivityPaused(false);
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "accutrack.iot.com.bluetoothservice", processBA, _service);
		}
        BA.LogInfo("** Service (bluetoothservice) Create **");
        processBA.raiseEvent(null, "service_create");
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		handleStart(intent);
    }
    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
    	handleStart(intent);
		return android.app.Service.START_NOT_STICKY;
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (bluetoothservice) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = new anywheresoftware.b4a.objects.IntentWrapper();
    			if (intent != null) {
    				if (intent.hasExtra("b4a_internal_intent"))
    					iw.setObject((android.content.Intent) intent.getParcelableExtra("b4a_internal_intent"));
    				else
    					iw.setObject(intent);
    			}
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
        BA.LogInfo("** Service (bluetoothservice) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.randomaccessfile.AsyncStreams _astream = null;
public static anywheresoftware.b4a.objects.collections.List _asensor = null;
public static anywheresoftware.b4a.objects.collections.List _bsensor = null;
public static anywheresoftware.b4a.objects.Timer _senddatatimer = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public accutrack.iot.com.main _main = null;
public accutrack.iot.com.chatactivity _chatactivity = null;
public static String  _astream_error() throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub AStream_Error";
 //BA.debugLineNum = 61;BA.debugLine="ToastMessageShow(\"Connection is broken.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Connection is broken.",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _astream_newdata(byte[] _buffer) throws Exception{
String _data = "";
 //BA.debugLineNum = 36;BA.debugLine="Sub AStream_NewData (Buffer() As Byte)";
 //BA.debugLineNum = 37;BA.debugLine="If Buffer.Length > 0 Then";
if (_buffer.length>0) { 
 //BA.debugLineNum = 38;BA.debugLine="Dim data As String= BytesToString(Buffer, 0, Buffer.Length, \"US-ASCII\")";
_data = anywheresoftware.b4a.keywords.Common.BytesToString(_buffer,(int) (0),_buffer.length,"US-ASCII");
 //BA.debugLineNum = 39;BA.debugLine="If (data.CharAt(0) = \"a\") Then";
if ((_data.charAt((int) (0))==BA.ObjectToChar("a"))) { 
 //BA.debugLineNum = 40;BA.debugLine="data = data.SubString(1)";
_data = _data.substring((int) (1));
 //BA.debugLineNum = 41;BA.debugLine="If data = \"\" Then";
if ((_data).equals("")) { 
 //BA.debugLineNum = 42;BA.debugLine="data = \"0\"";
_data = "0";
 };
 //BA.debugLineNum = 44;BA.debugLine="aSensor.Add(data)";
_asensor.Add((Object)(_data));
 }else if((_data.charAt((int) (0))==BA.ObjectToChar("b"))) { 
 //BA.debugLineNum = 46;BA.debugLine="data = data.SubString(1)";
_data = _data.substring((int) (1));
 //BA.debugLineNum = 47;BA.debugLine="If data = \"\" Then";
if ((_data).equals("")) { 
 //BA.debugLineNum = 48;BA.debugLine="data = \"0\"";
_data = "0";
 };
 //BA.debugLineNum = 50;BA.debugLine="bSensor.Add(data)";
_bsensor.Add((Object)(_data));
 };
 };
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim AStream As AsyncStreams";
_astream = new anywheresoftware.b4a.randomaccessfile.AsyncStreams();
 //BA.debugLineNum = 10;BA.debugLine="Dim aSensor As List";
_asensor = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 11;BA.debugLine="Dim bSensor As List";
_bsensor = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 12;BA.debugLine="Dim sendDataTimer As Timer";
_senddatatimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 13;BA.debugLine="End Sub";
return "";
}
public static String  _senddatatimer_tick() throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub sendDataTimer_tick";
 //BA.debugLineNum = 56;BA.debugLine="ToastMessageShow(bSensor.Get(0), False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToString(_bsensor.Get((int) (0))),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 15;BA.debugLine="aSensor.Initialize";
_asensor.Initialize();
 //BA.debugLineNum = 16;BA.debugLine="bSensor.Initialize";
_bsensor.Initialize();
 //BA.debugLineNum = 18;BA.debugLine="sendDataTimer.Initialize(\"sendDataTimer\",10000)";
_senddatatimer.Initialize(processBA,"sendDataTimer",(long) (10000));
 //BA.debugLineNum = 19;BA.debugLine="sendDataTimer.Enabled = True";
_senddatatimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 33;BA.debugLine="AStream.Close";
_astream.Close();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 23;BA.debugLine="Try";
try { //BA.debugLineNum = 24;BA.debugLine="If AStream.IsInitialized = False Then";
if (_astream.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 25;BA.debugLine="AStream.Initialize(Main.serial1.InputStream,  Null, \"AStream\")";
_astream.Initialize(processBA,mostCurrent._main._serial1.getInputStream(),(java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.Null),"AStream");
 };
 } 
       catch (Exception e18) {
			processBA.setLastException(e18); };
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
}
