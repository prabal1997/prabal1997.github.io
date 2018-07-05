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
  public Object[] GetGlobals() {
		return new Object[] {"aSensor",_asensor,"AStream",_astream,"bSensor",_bsensor,"ChatActivity",Debug.moduleToString(accutrack.iot.com.chatactivity.class),"HttpUtils2Service",mostCurrent._httputils2service,"Main",Debug.moduleToString(accutrack.iot.com.main.class),"sendDataTimer",_senddatatimer,"Service",mostCurrent._service};
}
public static String  _astream_error() throws Exception{
		Debug.PushSubsStack("AStream_Error (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
 BA.debugLineNum = 112;BA.debugLine="Sub AStream_Error";
Debug.ShouldStop(32768);
 BA.debugLineNum = 113;BA.debugLine="ToastMessageShow(\"Connection is broken.\", True)";
Debug.ShouldStop(65536);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Connection is broken.",anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 114;BA.debugLine="End Sub";
Debug.ShouldStop(131072);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _astream_newdata(byte[] _buffer) throws Exception{
		Debug.PushSubsStack("AStream_NewData (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
String _data = "";
Debug.locals.put("Buffer", _buffer);
 BA.debugLineNum = 36;BA.debugLine="Sub AStream_NewData (Buffer() As Byte)";
Debug.ShouldStop(8);
 BA.debugLineNum = 37;BA.debugLine="If Buffer.Length > 0 Then";
Debug.ShouldStop(16);
if (_buffer.length>0) { 
 BA.debugLineNum = 38;BA.debugLine="Dim data As String= BytesToString(Buffer, 0, Buffer.Length, \"US-ASCII\")";
Debug.ShouldStop(32);
_data = anywheresoftware.b4a.keywords.Common.BytesToString(_buffer,(int) (0),_buffer.length,"US-ASCII");Debug.locals.put("data", _data);Debug.locals.put("data", _data);
 BA.debugLineNum = 39;BA.debugLine="If (data.CharAt(0) = \"a\") Then";
Debug.ShouldStop(64);
if ((_data.charAt((int) (0))==BA.ObjectToChar("a"))) { 
 BA.debugLineNum = 40;BA.debugLine="data = data.SubString(1)";
Debug.ShouldStop(128);
_data = _data.substring((int) (1));Debug.locals.put("data", _data);
 BA.debugLineNum = 41;BA.debugLine="If data = \"\" Then";
Debug.ShouldStop(256);
if ((_data).equals("")) { 
 BA.debugLineNum = 42;BA.debugLine="data = \"0\"";
Debug.ShouldStop(512);
_data = "0";Debug.locals.put("data", _data);
 };
 BA.debugLineNum = 44;BA.debugLine="aSensor.Add(data)";
Debug.ShouldStop(2048);
_asensor.Add((Object)(_data));
 }else 
{ BA.debugLineNum = 45;BA.debugLine="Else If (data.CharAt(0) = \"b\") Then";
Debug.ShouldStop(4096);
if ((_data.charAt((int) (0))==BA.ObjectToChar("b"))) { 
 BA.debugLineNum = 46;BA.debugLine="data = data.SubString(1)";
Debug.ShouldStop(8192);
_data = _data.substring((int) (1));Debug.locals.put("data", _data);
 BA.debugLineNum = 47;BA.debugLine="If data = \"\" Then";
Debug.ShouldStop(16384);
if ((_data).equals("")) { 
 BA.debugLineNum = 48;BA.debugLine="data = \"0\"";
Debug.ShouldStop(32768);
_data = "0";Debug.locals.put("data", _data);
 };
 BA.debugLineNum = 50;BA.debugLine="bSensor.Add(data)";
Debug.ShouldStop(131072);
_bsensor.Add((Object)(_data));
 }};
 };
 BA.debugLineNum = 53;BA.debugLine="End Sub";
Debug.ShouldStop(1048576);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _datetimevalue() throws Exception{
		Debug.PushSubsStack("DateTimeValue (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
long _now = 0L;
String _dt = "";
 BA.debugLineNum = 102;BA.debugLine="Sub DateTimeValue As String";
Debug.ShouldStop(32);
 BA.debugLineNum = 103;BA.debugLine="Dim now As Long";
Debug.ShouldStop(64);
_now = 0L;Debug.locals.put("now", _now);
 BA.debugLineNum = 104;BA.debugLine="Dim dt As String";
Debug.ShouldStop(128);
_dt = "";Debug.locals.put("dt", _dt);
 BA.debugLineNum = 105;BA.debugLine="DateTime.DateFormat = \"dd MMM yyyy\"";
Debug.ShouldStop(256);
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd MMM yyyy");
 BA.debugLineNum = 106;BA.debugLine="dt = DateTime.Date(DateTime.Now)";
Debug.ShouldStop(512);
_dt = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());Debug.locals.put("dt", _dt);
 BA.debugLineNum = 107;BA.debugLine="DateTime.DateFormat = \"hh:mm:ss\"";
Debug.ShouldStop(1024);
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("hh:mm:ss");
 BA.debugLineNum = 108;BA.debugLine="dt = dt & \" \" & DateTime.Time(now)";
Debug.ShouldStop(2048);
_dt = _dt+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_now);Debug.locals.put("dt", _dt);
 BA.debugLineNum = 109;BA.debugLine="Return dt";
Debug.ShouldStop(4096);
if (true) return _dt;
 BA.debugLineNum = 110;BA.debugLine="End Sub";
Debug.ShouldStop(8192);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
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
		Debug.PushSubsStack("sendDataTimer_tick (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
String _asensorstring = "";
String _bsensorstring = "";
int _i = 0;
String _data = "";
 BA.debugLineNum = 55;BA.debugLine="Sub sendDataTimer_tick";
Debug.ShouldStop(4194304);
 BA.debugLineNum = 57;BA.debugLine="Dim aSensorString As String = \"\"";
Debug.ShouldStop(16777216);
_asensorstring = "";Debug.locals.put("aSensorString", _asensorstring);Debug.locals.put("aSensorString", _asensorstring);
 BA.debugLineNum = 58;BA.debugLine="Dim bSensorString As String = \"\"";
Debug.ShouldStop(33554432);
_bsensorstring = "";Debug.locals.put("bSensorString", _bsensorstring);Debug.locals.put("bSensorString", _bsensorstring);
 BA.debugLineNum = 60;BA.debugLine="If aSensor.Size > 0 Then";
Debug.ShouldStop(134217728);
if (_asensor.getSize()>0) { 
 BA.debugLineNum = 61;BA.debugLine="For i = 0 To aSensor.Size - 1";
Debug.ShouldStop(268435456);
{
final int step45 = 1;
final int limit45 = (int) (_asensor.getSize()-1);
for (_i = (int) (0); (step45 > 0 && _i <= limit45) || (step45 < 0 && _i >= limit45); _i = ((int)(0 + _i + step45))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 62;BA.debugLine="If Not(i = aSensor.Size - 1) Then";
Debug.ShouldStop(536870912);
if (anywheresoftware.b4a.keywords.Common.Not(_i==_asensor.getSize()-1)) { 
 BA.debugLineNum = 63;BA.debugLine="aSensorString = aSensorString & aSensor.Get(i) & \", \"";
Debug.ShouldStop(1073741824);
_asensorstring = _asensorstring+BA.ObjectToString(_asensor.Get(_i))+", ";Debug.locals.put("aSensorString", _asensorstring);
 }else {
 BA.debugLineNum = 65;BA.debugLine="aSensorString = aSensorString & aSensor.Get(i)";
Debug.ShouldStop(1);
_asensorstring = _asensorstring+BA.ObjectToString(_asensor.Get(_i));Debug.locals.put("aSensorString", _asensorstring);
 };
 }
}Debug.locals.put("i", _i);
;
 };
 BA.debugLineNum = 70;BA.debugLine="If bSensor.Size > 0 Then";
Debug.ShouldStop(32);
if (_bsensor.getSize()>0) { 
 BA.debugLineNum = 71;BA.debugLine="For i = 0 To bSensor.Size - 1";
Debug.ShouldStop(64);
{
final int step54 = 1;
final int limit54 = (int) (_bsensor.getSize()-1);
for (_i = (int) (0); (step54 > 0 && _i <= limit54) || (step54 < 0 && _i >= limit54); _i = ((int)(0 + _i + step54))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 72;BA.debugLine="If Not(i = bSensor.Size - 1) Then";
Debug.ShouldStop(128);
if (anywheresoftware.b4a.keywords.Common.Not(_i==_bsensor.getSize()-1)) { 
 BA.debugLineNum = 73;BA.debugLine="bSensorString = bSensorString & bSensor.Get(i) & \", \"";
Debug.ShouldStop(256);
_bsensorstring = _bsensorstring+BA.ObjectToString(_bsensor.Get(_i))+", ";Debug.locals.put("bSensorString", _bsensorstring);
 }else {
 BA.debugLineNum = 75;BA.debugLine="bSensorString = bSensorString & bSensor.Get(i)";
Debug.ShouldStop(1024);
_bsensorstring = _bsensorstring+BA.ObjectToString(_bsensor.Get(_i));Debug.locals.put("bSensorString", _bsensorstring);
 };
 }
}Debug.locals.put("i", _i);
;
 };
 BA.debugLineNum = 80;BA.debugLine="Dim data As String = \"\"";
Debug.ShouldStop(32768);
_data = "";Debug.locals.put("data", _data);Debug.locals.put("data", _data);
 BA.debugLineNum = 81;BA.debugLine="data = data & \"{\"";
Debug.ShouldStop(65536);
_data = _data+"{";Debug.locals.put("data", _data);
 BA.debugLineNum = 82;BA.debugLine="data = data &   \"\"\"startDateTime\"\": \"\"\" & DateTimeValue & \"\"\",\"";
Debug.ShouldStop(131072);
_data = _data+"\"startDateTime\": \""+_datetimevalue()+"\",";Debug.locals.put("data", _data);
 BA.debugLineNum = 83;BA.debugLine="data = data &   \"\"\"data\"\": [\"";
Debug.ShouldStop(262144);
_data = _data+"\"data\": [";Debug.locals.put("data", _data);
 BA.debugLineNum = 84;BA.debugLine="data = data &    \"{\"\"id\"\": \"\"a\"\", \"\"sensorData\"\": [\" & aSensorString & \"],\"";
Debug.ShouldStop(524288);
_data = _data+"{\"id\": \"a\", \"sensorData\": ["+_asensorstring+"],";Debug.locals.put("data", _data);
 BA.debugLineNum = 85;BA.debugLine="data = data &    \"{\"\"id\"\": \"\"b\"\", \"\"sensorData\"\": [\" & bSensorString & \"]]\"";
Debug.ShouldStop(1048576);
_data = _data+"{\"id\": \"b\", \"sensorData\": ["+_bsensorstring+"]]";Debug.locals.put("data", _data);
 BA.debugLineNum = 98;BA.debugLine="aSensor.Clear";
Debug.ShouldStop(2);
_asensor.Clear();
 BA.debugLineNum = 99;BA.debugLine="bSensor.Clear";
Debug.ShouldStop(4);
_bsensor.Clear();
 BA.debugLineNum = 100;BA.debugLine="End Sub";
Debug.ShouldStop(8);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _service_create() throws Exception{
		Debug.PushSubsStack("Service_Create (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
 BA.debugLineNum = 14;BA.debugLine="Sub Service_Create";
Debug.ShouldStop(8192);
 BA.debugLineNum = 15;BA.debugLine="aSensor.Initialize";
Debug.ShouldStop(16384);
_asensor.Initialize();
 BA.debugLineNum = 16;BA.debugLine="bSensor.Initialize";
Debug.ShouldStop(32768);
_bsensor.Initialize();
 BA.debugLineNum = 18;BA.debugLine="sendDataTimer.Initialize(\"sendDataTimer\",10000)";
Debug.ShouldStop(131072);
Debug.DebugWarningEngine.CheckInitialize(_senddatatimer);_senddatatimer.Initialize(processBA,"sendDataTimer",(long) (10000));
 BA.debugLineNum = 19;BA.debugLine="sendDataTimer.Enabled = True";
Debug.ShouldStop(262144);
_senddatatimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 20;BA.debugLine="End Sub";
Debug.ShouldStop(524288);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _service_destroy() throws Exception{
		Debug.PushSubsStack("Service_Destroy (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
 BA.debugLineNum = 32;BA.debugLine="Sub Service_Destroy";
Debug.ShouldStop(-2147483648);
 BA.debugLineNum = 33;BA.debugLine="AStream.Close";
Debug.ShouldStop(1);
_astream.Close();
 BA.debugLineNum = 34;BA.debugLine="End Sub";
Debug.ShouldStop(2);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
		Debug.PushSubsStack("Service_Start (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
Debug.locals.put("StartingIntent", _startingintent);
 BA.debugLineNum = 22;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
Debug.ShouldStop(2097152);
 BA.debugLineNum = 23;BA.debugLine="Try";
Debug.ShouldStop(4194304);
try { BA.debugLineNum = 24;BA.debugLine="If AStream.IsInitialized = False Then";
Debug.ShouldStop(8388608);
if (_astream.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 25;BA.debugLine="AStream.Initialize(Main.serial1.InputStream,  Null, \"AStream\")";
Debug.ShouldStop(16777216);
_astream.Initialize(processBA,mostCurrent._main._serial1.getInputStream(),(java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.Null),"AStream");
 };
 } 
       catch (Exception e18) {
			processBA.setLastException(e18); };
 BA.debugLineNum = 30;BA.debugLine="End Sub";
Debug.ShouldStop(536870912);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
}
