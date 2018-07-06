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
public static String _tempstring = "";
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public accutrack.iot.com.main _main = null;
public accutrack.iot.com.chatactivity _chatactivity = null;
  public Object[] GetGlobals() {
		return new Object[] {"aSensor",_asensor,"AStream",_astream,"bSensor",_bsensor,"ChatActivity",Debug.moduleToString(accutrack.iot.com.chatactivity.class),"HttpUtils2Service",mostCurrent._httputils2service,"Main",Debug.moduleToString(accutrack.iot.com.main.class),"sendDataTimer",_senddatatimer,"Service",mostCurrent._service,"tempString",_tempstring};
}
public static String  _astream_error() throws Exception{
		Debug.PushSubsStack("AStream_Error (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
 BA.debugLineNum = 158;BA.debugLine="Sub AStream_Error";
Debug.ShouldStop(536870912);
 BA.debugLineNum = 159;BA.debugLine="ToastMessageShow(\"Connection is broken.\", True)";
Debug.ShouldStop(1073741824);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Connection is broken.",anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 160;BA.debugLine="End Sub";
Debug.ShouldStop(-2147483648);
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
 BA.debugLineNum = 37;BA.debugLine="Sub AStream_NewData (Buffer() As Byte)";
Debug.ShouldStop(16);
 BA.debugLineNum = 38;BA.debugLine="If Buffer.Length > 0 Then";
Debug.ShouldStop(32);
if (_buffer.length>0) { 
 BA.debugLineNum = 39;BA.debugLine="Dim data As String= BytesToString(Buffer, 0, Buffer.Length, \"US-ASCII\")";
Debug.ShouldStop(64);
_data = anywheresoftware.b4a.keywords.Common.BytesToString(_buffer,(int) (0),_buffer.length,"US-ASCII");Debug.locals.put("data", _data);Debug.locals.put("data", _data);
 BA.debugLineNum = 40;BA.debugLine="If data = \"a\" OR data = \"b\" Then";
Debug.ShouldStop(128);
if ((_data).equals("a") || (_data).equals("b")) { 
 BA.debugLineNum = 41;BA.debugLine="tempString = data";
Debug.ShouldStop(256);
_tempstring = _data;
 }else {
 BA.debugLineNum = 43;BA.debugLine="If Not(data.CharAt(0) = \"a\") AND Not(data.CharAt(0) = \"b\") Then";
Debug.ShouldStop(1024);
if (anywheresoftware.b4a.keywords.Common.Not(_data.charAt((int) (0))==BA.ObjectToChar("a")) && anywheresoftware.b4a.keywords.Common.Not(_data.charAt((int) (0))==BA.ObjectToChar("b"))) { 
 BA.debugLineNum = 44;BA.debugLine="data = tempString & data";
Debug.ShouldStop(2048);
_data = _tempstring+_data;Debug.locals.put("data", _data);
 };
 BA.debugLineNum = 47;BA.debugLine="If (data.CharAt(0) = \"a\") Then";
Debug.ShouldStop(16384);
if ((_data.charAt((int) (0))==BA.ObjectToChar("a"))) { 
 BA.debugLineNum = 48;BA.debugLine="data = data.SubString(1)";
Debug.ShouldStop(32768);
_data = _data.substring((int) (1));Debug.locals.put("data", _data);
 BA.debugLineNum = 49;BA.debugLine="If data = \"\" Then";
Debug.ShouldStop(65536);
if ((_data).equals("")) { 
 BA.debugLineNum = 50;BA.debugLine="data = \"-1\"";
Debug.ShouldStop(131072);
_data = "-1";Debug.locals.put("data", _data);
 };
 BA.debugLineNum = 52;BA.debugLine="aSensor.Add(data)";
Debug.ShouldStop(524288);
_asensor.Add((Object)(_data));
 }else 
{ BA.debugLineNum = 53;BA.debugLine="Else If (data.CharAt(0) = \"b\") Then";
Debug.ShouldStop(1048576);
if ((_data.charAt((int) (0))==BA.ObjectToChar("b"))) { 
 BA.debugLineNum = 54;BA.debugLine="data = data.SubString(1)";
Debug.ShouldStop(2097152);
_data = _data.substring((int) (1));Debug.locals.put("data", _data);
 BA.debugLineNum = 55;BA.debugLine="If data = \"\" Then";
Debug.ShouldStop(4194304);
if ((_data).equals("")) { 
 BA.debugLineNum = 56;BA.debugLine="data = \"-1\"";
Debug.ShouldStop(8388608);
_data = "-1";Debug.locals.put("data", _data);
 };
 BA.debugLineNum = 58;BA.debugLine="bSensor.Add(data)";
Debug.ShouldStop(33554432);
_bsensor.Add((Object)(_data));
 }};
 BA.debugLineNum = 61;BA.debugLine="tempString = \"\"";
Debug.ShouldStop(268435456);
_tempstring = "";
 };
 };
 BA.debugLineNum = 64;BA.debugLine="End Sub";
Debug.ShouldStop(-2147483648);
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
 BA.debugLineNum = 148;BA.debugLine="Sub DateTimeValue As String";
Debug.ShouldStop(524288);
 BA.debugLineNum = 149;BA.debugLine="Dim now As Long";
Debug.ShouldStop(1048576);
_now = 0L;Debug.locals.put("now", _now);
 BA.debugLineNum = 150;BA.debugLine="Dim dt As String";
Debug.ShouldStop(2097152);
_dt = "";Debug.locals.put("dt", _dt);
 BA.debugLineNum = 151;BA.debugLine="DateTime.DateFormat = \"dd MMM yyyy\"";
Debug.ShouldStop(4194304);
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd MMM yyyy");
 BA.debugLineNum = 152;BA.debugLine="dt = DateTime.Date(DateTime.Now)";
Debug.ShouldStop(8388608);
_dt = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());Debug.locals.put("dt", _dt);
 BA.debugLineNum = 153;BA.debugLine="DateTime.DateFormat = \"hh:mm:ss\"";
Debug.ShouldStop(16777216);
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("hh:mm:ss");
 BA.debugLineNum = 154;BA.debugLine="dt = dt & \" \" & DateTime.Time(now)";
Debug.ShouldStop(33554432);
_dt = _dt+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_now);Debug.locals.put("dt", _dt);
 BA.debugLineNum = 155;BA.debugLine="Return dt";
Debug.ShouldStop(67108864);
if (true) return _dt;
 BA.debugLineNum = 156;BA.debugLine="End Sub";
Debug.ShouldStop(134217728);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
		Debug.PushSubsStack("JobDone (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
Debug.locals.put("job", _job);
 BA.debugLineNum = 137;BA.debugLine="Sub JobDone(job As HttpJob)";
Debug.ShouldStop(256);
 BA.debugLineNum = 138;BA.debugLine="If job.Success Then";
Debug.ShouldStop(512);
if (_job._success) { 
 BA.debugLineNum = 139;BA.debugLine="ToastMessageShow (\"SuccessFully Finish !\", True)";
Debug.ShouldStop(1024);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("SuccessFully Finish !",anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 141;BA.debugLine="Log(job.GetString)";
Debug.ShouldStop(4096);
anywheresoftware.b4a.keywords.Common.Log(_job._getstring());
 BA.debugLineNum = 142;BA.debugLine="job.Release";
Debug.ShouldStop(8192);
_job._release();
 }else {
 BA.debugLineNum = 144;BA.debugLine="Log(job.ErrorMessage)";
Debug.ShouldStop(32768);
anywheresoftware.b4a.keywords.Common.Log(_job._errormessage);
 };
 BA.debugLineNum = 146;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 13;BA.debugLine="Dim tempString As String = \"\"";
_tempstring = "";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _senddatatimer_tick() throws Exception{
		Debug.PushSubsStack("sendDataTimer_tick (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
String _asensorstring = "";
String _bsensorstring = "";
int _i = 0;
String _data = "";
anywheresoftware.b4a.samples.httputils2.httpjob _register = null;
anywheresoftware.b4a.samples.httputils2.httpjob _register2 = null;
 BA.debugLineNum = 66;BA.debugLine="Sub sendDataTimer_tick";
Debug.ShouldStop(2);
 BA.debugLineNum = 68;BA.debugLine="Dim aSensorString As String = \"\"";
Debug.ShouldStop(8);
_asensorstring = "";Debug.locals.put("aSensorString", _asensorstring);Debug.locals.put("aSensorString", _asensorstring);
 BA.debugLineNum = 69;BA.debugLine="Dim bSensorString As String = \"\"";
Debug.ShouldStop(16);
_bsensorstring = "";Debug.locals.put("bSensorString", _bsensorstring);Debug.locals.put("bSensorString", _bsensorstring);
 BA.debugLineNum = 71;BA.debugLine="If aSensor.Size > 0 Then";
Debug.ShouldStop(64);
if (_asensor.getSize()>0) { 
 BA.debugLineNum = 72;BA.debugLine="For i = 0 To aSensor.Size - 1";
Debug.ShouldStop(128);
{
final int step54 = 1;
final int limit54 = (int) (_asensor.getSize()-1);
for (_i = (int) (0); (step54 > 0 && _i <= limit54) || (step54 < 0 && _i >= limit54); _i = ((int)(0 + _i + step54))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 73;BA.debugLine="If Not(i = aSensor.Size - 1) Then";
Debug.ShouldStop(256);
if (anywheresoftware.b4a.keywords.Common.Not(_i==_asensor.getSize()-1)) { 
 BA.debugLineNum = 74;BA.debugLine="aSensorString = aSensorString & aSensor.Get(i) & \",\"";
Debug.ShouldStop(512);
_asensorstring = _asensorstring+BA.ObjectToString(_asensor.Get(_i))+",";Debug.locals.put("aSensorString", _asensorstring);
 }else {
 BA.debugLineNum = 76;BA.debugLine="aSensorString = aSensorString & aSensor.Get(i)";
Debug.ShouldStop(2048);
_asensorstring = _asensorstring+BA.ObjectToString(_asensor.Get(_i));Debug.locals.put("aSensorString", _asensorstring);
 };
 }
}Debug.locals.put("i", _i);
;
 };
 BA.debugLineNum = 81;BA.debugLine="If bSensor.Size > 0 Then";
Debug.ShouldStop(65536);
if (_bsensor.getSize()>0) { 
 BA.debugLineNum = 82;BA.debugLine="For i = 0 To bSensor.Size - 1";
Debug.ShouldStop(131072);
{
final int step63 = 1;
final int limit63 = (int) (_bsensor.getSize()-1);
for (_i = (int) (0); (step63 > 0 && _i <= limit63) || (step63 < 0 && _i >= limit63); _i = ((int)(0 + _i + step63))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 83;BA.debugLine="If Not(i = bSensor.Size - 1) Then";
Debug.ShouldStop(262144);
if (anywheresoftware.b4a.keywords.Common.Not(_i==_bsensor.getSize()-1)) { 
 BA.debugLineNum = 84;BA.debugLine="bSensorString = bSensorString & bSensor.Get(i) & \",\"";
Debug.ShouldStop(524288);
_bsensorstring = _bsensorstring+BA.ObjectToString(_bsensor.Get(_i))+",";Debug.locals.put("bSensorString", _bsensorstring);
 }else {
 BA.debugLineNum = 86;BA.debugLine="bSensorString = bSensorString & bSensor.Get(i)";
Debug.ShouldStop(2097152);
_bsensorstring = _bsensorstring+BA.ObjectToString(_bsensor.Get(_i));Debug.locals.put("bSensorString", _bsensorstring);
 };
 }
}Debug.locals.put("i", _i);
;
 };
 BA.debugLineNum = 91;BA.debugLine="Dim data As String = \"\"";
Debug.ShouldStop(67108864);
_data = "";Debug.locals.put("data", _data);Debug.locals.put("data", _data);
 BA.debugLineNum = 92;BA.debugLine="data = data";
Debug.ShouldStop(134217728);
_data = _data;Debug.locals.put("data", _data);
 BA.debugLineNum = 110;BA.debugLine="Log(data)";
Debug.ShouldStop(8192);
anywheresoftware.b4a.keywords.Common.Log(_data);
 BA.debugLineNum = 117;BA.debugLine="Dim Register As HttpJob";
Debug.ShouldStop(1048576);
_register = new anywheresoftware.b4a.samples.httputils2.httpjob();Debug.locals.put("Register", _register);
 BA.debugLineNum = 118;BA.debugLine="Register.Initialize(\"rest\", Me)";
Debug.ShouldStop(2097152);
_register._initialize(processBA,"rest",bluetoothservice.getObject());
 BA.debugLineNum = 119;BA.debugLine="Register.Download(\"http://40.113.192.222:5000/RealtimeUpdate/0/\" & aSensorString)";
Debug.ShouldStop(4194304);
_register._download("http://40.113.192.222:5000/RealtimeUpdate/0/"+_asensorstring);
 BA.debugLineNum = 121;BA.debugLine="Dim Register2 As HttpJob";
Debug.ShouldStop(16777216);
_register2 = new anywheresoftware.b4a.samples.httputils2.httpjob();Debug.locals.put("Register2", _register2);
 BA.debugLineNum = 122;BA.debugLine="Register2.Initialize(\"rest\", Me)";
Debug.ShouldStop(33554432);
_register2._initialize(processBA,"rest",bluetoothservice.getObject());
 BA.debugLineNum = 123;BA.debugLine="Register2.Download(\"http://40.113.192.222:5000/RealtimeUpdate/1/\" & bSensorString)";
Debug.ShouldStop(67108864);
_register2._download("http://40.113.192.222:5000/RealtimeUpdate/1/"+_bsensorstring);
 BA.debugLineNum = 133;BA.debugLine="aSensor.Clear";
Debug.ShouldStop(16);
_asensor.Clear();
 BA.debugLineNum = 134;BA.debugLine="bSensor.Clear";
Debug.ShouldStop(32);
_bsensor.Clear();
 BA.debugLineNum = 135;BA.debugLine="End Sub";
Debug.ShouldStop(64);
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
 BA.debugLineNum = 15;BA.debugLine="Sub Service_Create";
Debug.ShouldStop(16384);
 BA.debugLineNum = 16;BA.debugLine="aSensor.Initialize";
Debug.ShouldStop(32768);
_asensor.Initialize();
 BA.debugLineNum = 17;BA.debugLine="bSensor.Initialize";
Debug.ShouldStop(65536);
_bsensor.Initialize();
 BA.debugLineNum = 19;BA.debugLine="sendDataTimer.Initialize(\"sendDataTimer\",2500)";
Debug.ShouldStop(262144);
Debug.DebugWarningEngine.CheckInitialize(_senddatatimer);_senddatatimer.Initialize(processBA,"sendDataTimer",(long) (2500));
 BA.debugLineNum = 20;BA.debugLine="sendDataTimer.Enabled = True";
Debug.ShouldStop(524288);
_senddatatimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 21;BA.debugLine="End Sub";
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
public static String  _service_destroy() throws Exception{
		Debug.PushSubsStack("Service_Destroy (bluetoothservice) ","bluetoothservice",2,processBA,mostCurrent);
try {
 BA.debugLineNum = 33;BA.debugLine="Sub Service_Destroy";
Debug.ShouldStop(1);
 BA.debugLineNum = 34;BA.debugLine="AStream.Close";
Debug.ShouldStop(2);
_astream.Close();
 BA.debugLineNum = 35;BA.debugLine="End Sub";
Debug.ShouldStop(4);
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
 BA.debugLineNum = 23;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
Debug.ShouldStop(4194304);
 BA.debugLineNum = 24;BA.debugLine="Try";
Debug.ShouldStop(8388608);
try { BA.debugLineNum = 25;BA.debugLine="If AStream.IsInitialized = False Then";
Debug.ShouldStop(16777216);
if (_astream.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 26;BA.debugLine="AStream.Initialize(Main.serial1.InputStream,  Null, \"AStream\")";
Debug.ShouldStop(33554432);
_astream.Initialize(processBA,mostCurrent._main._serial1.getInputStream(),(java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.Null),"AStream");
 };
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); };
 BA.debugLineNum = 31;BA.debugLine="End Sub";
Debug.ShouldStop(1073741824);
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
