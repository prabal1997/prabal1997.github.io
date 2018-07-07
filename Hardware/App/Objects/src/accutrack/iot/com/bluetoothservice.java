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
public static String  _astream_error() throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Sub AStream_Error";
 //BA.debugLineNum = 169;BA.debugLine="ToastMessageShow(\"Connection is broken.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Connection is broken.",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _astream_newdata(byte[] _buffer) throws Exception{
String _data = "";
int _tempvalue = 0;
 //BA.debugLineNum = 37;BA.debugLine="Sub AStream_NewData (Buffer() As Byte)";
 //BA.debugLineNum = 38;BA.debugLine="If Buffer.Length > 0 Then";
if (_buffer.length>0) { 
 //BA.debugLineNum = 39;BA.debugLine="Dim data As String= BytesToString(Buffer, 0, Buffer.Length, \"US-ASCII\")";
_data = anywheresoftware.b4a.keywords.Common.BytesToString(_buffer,(int) (0),_buffer.length,"US-ASCII");
 //BA.debugLineNum = 40;BA.debugLine="If data = \"a\" OR data = \"b\" Then";
if ((_data).equals("a") || (_data).equals("b")) { 
 //BA.debugLineNum = 41;BA.debugLine="tempString = data";
_tempstring = _data;
 }else {
 //BA.debugLineNum = 43;BA.debugLine="If Not(data.CharAt(0) = \"a\") AND Not(data.CharAt(0) = \"b\") Then";
if (anywheresoftware.b4a.keywords.Common.Not(_data.charAt((int) (0))==BA.ObjectToChar("a")) && anywheresoftware.b4a.keywords.Common.Not(_data.charAt((int) (0))==BA.ObjectToChar("b"))) { 
 //BA.debugLineNum = 44;BA.debugLine="data = tempString & data";
_data = _tempstring+_data;
 };
 //BA.debugLineNum = 47;BA.debugLine="Dim tempValue As Int";
_tempvalue = 0;
 //BA.debugLineNum = 49;BA.debugLine="If (data.CharAt(0) = \"a\") Then";
if ((_data.charAt((int) (0))==BA.ObjectToChar("a"))) { 
 //BA.debugLineNum = 50;BA.debugLine="data = data.SubString(1)";
_data = _data.substring((int) (1));
 //BA.debugLineNum = 51;BA.debugLine="Try";
try { //BA.debugLineNum = 52;BA.debugLine="tempValue = data";
_tempvalue = (int)(Double.parseDouble(_data));
 //BA.debugLineNum = 53;BA.debugLine="If data = \"\" Then";
if ((_data).equals("")) { 
 //BA.debugLineNum = 54;BA.debugLine="data = \"-1\"";
_data = "-1";
 };
 //BA.debugLineNum = 56;BA.debugLine="aSensor.Add(data)";
_asensor.Add((Object)(_data));
 } 
       catch (Exception e43) {
			processBA.setLastException(e43); };
 }else if((_data.charAt((int) (0))==BA.ObjectToChar("b"))) { 
 //BA.debugLineNum = 60;BA.debugLine="data = data.SubString(1)";
_data = _data.substring((int) (1));
 //BA.debugLineNum = 61;BA.debugLine="Try";
try { //BA.debugLineNum = 62;BA.debugLine="tempValue = data";
_tempvalue = (int)(Double.parseDouble(_data));
 //BA.debugLineNum = 63;BA.debugLine="If data = \"\" Then";
if ((_data).equals("")) { 
 //BA.debugLineNum = 64;BA.debugLine="data = \"-1\"";
_data = "-1";
 };
 //BA.debugLineNum = 66;BA.debugLine="bSensor.Add(data)";
_bsensor.Add((Object)(_data));
 } 
       catch (Exception e53) {
			processBA.setLastException(e53); };
 };
 //BA.debugLineNum = 71;BA.debugLine="tempString = \"\"";
_tempstring = "";
 };
 };
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _datetimevalue() throws Exception{
long _now = 0L;
String _dt = "";
 //BA.debugLineNum = 158;BA.debugLine="Sub DateTimeValue As String";
 //BA.debugLineNum = 159;BA.debugLine="Dim now As Long";
_now = 0L;
 //BA.debugLineNum = 160;BA.debugLine="Dim dt As String";
_dt = "";
 //BA.debugLineNum = 161;BA.debugLine="DateTime.DateFormat = \"dd MMM yyyy\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd MMM yyyy");
 //BA.debugLineNum = 162;BA.debugLine="dt = DateTime.Date(DateTime.Now)";
_dt = anywheresoftware.b4a.keywords.Common.DateTime.Date(anywheresoftware.b4a.keywords.Common.DateTime.getNow());
 //BA.debugLineNum = 163;BA.debugLine="DateTime.DateFormat = \"hh:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("hh:mm:ss");
 //BA.debugLineNum = 164;BA.debugLine="dt = dt & \" \" & DateTime.Time(now)";
_dt = _dt+" "+anywheresoftware.b4a.keywords.Common.DateTime.Time(_now);
 //BA.debugLineNum = 165;BA.debugLine="Return dt";
if (true) return _dt;
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 147;BA.debugLine="Sub JobDone(job As HttpJob)";
 //BA.debugLineNum = 148;BA.debugLine="If job.Success Then";
if (_job._success) { 
 //BA.debugLineNum = 151;BA.debugLine="Log(job.GetString)";
anywheresoftware.b4a.keywords.Common.Log(_job._getstring());
 //BA.debugLineNum = 152;BA.debugLine="job.Release";
_job._release();
 }else {
 //BA.debugLineNum = 154;BA.debugLine="Log(job.ErrorMessage)";
anywheresoftware.b4a.keywords.Common.Log(_job._errormessage);
 };
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 13;BA.debugLine="Dim tempString As String = \"\"";
_tempstring = "";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _senddatatimer_tick() throws Exception{
String _asensorstring = "";
String _bsensorstring = "";
int _i = 0;
String _data = "";
anywheresoftware.b4a.samples.httputils2.httpjob _register = null;
anywheresoftware.b4a.samples.httputils2.httpjob _register2 = null;
 //BA.debugLineNum = 76;BA.debugLine="Sub sendDataTimer_tick";
 //BA.debugLineNum = 78;BA.debugLine="Dim aSensorString As String = \"\"";
_asensorstring = "";
 //BA.debugLineNum = 79;BA.debugLine="Dim bSensorString As String = \"\"";
_bsensorstring = "";
 //BA.debugLineNum = 81;BA.debugLine="If aSensor.Size > 0 Then";
if (_asensor.getSize()>0) { 
 //BA.debugLineNum = 82;BA.debugLine="For i = 0 To aSensor.Size - 1";
{
final int step63 = 1;
final int limit63 = (int) (_asensor.getSize()-1);
for (_i = (int) (0); (step63 > 0 && _i <= limit63) || (step63 < 0 && _i >= limit63); _i = ((int)(0 + _i + step63))) {
 //BA.debugLineNum = 83;BA.debugLine="If Not(i = aSensor.Size - 1) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_i==_asensor.getSize()-1)) { 
 //BA.debugLineNum = 84;BA.debugLine="aSensorString = aSensorString & aSensor.Get(i) & \",\"";
_asensorstring = _asensorstring+BA.ObjectToString(_asensor.Get(_i))+",";
 }else {
 //BA.debugLineNum = 86;BA.debugLine="aSensorString = aSensorString & aSensor.Get(i)";
_asensorstring = _asensorstring+BA.ObjectToString(_asensor.Get(_i));
 };
 }
};
 };
 //BA.debugLineNum = 91;BA.debugLine="If bSensor.Size > 0 Then";
if (_bsensor.getSize()>0) { 
 //BA.debugLineNum = 92;BA.debugLine="For i = 0 To bSensor.Size - 1";
{
final int step72 = 1;
final int limit72 = (int) (_bsensor.getSize()-1);
for (_i = (int) (0); (step72 > 0 && _i <= limit72) || (step72 < 0 && _i >= limit72); _i = ((int)(0 + _i + step72))) {
 //BA.debugLineNum = 93;BA.debugLine="If Not(i = bSensor.Size - 1) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_i==_bsensor.getSize()-1)) { 
 //BA.debugLineNum = 94;BA.debugLine="bSensorString = bSensorString & bSensor.Get(i) & \",\"";
_bsensorstring = _bsensorstring+BA.ObjectToString(_bsensor.Get(_i))+",";
 }else {
 //BA.debugLineNum = 96;BA.debugLine="bSensorString = bSensorString & bSensor.Get(i)";
_bsensorstring = _bsensorstring+BA.ObjectToString(_bsensor.Get(_i));
 };
 }
};
 };
 //BA.debugLineNum = 101;BA.debugLine="Dim data As String = \"\"";
_data = "";
 //BA.debugLineNum = 102;BA.debugLine="data = data";
_data = _data;
 //BA.debugLineNum = 120;BA.debugLine="Log(data)";
anywheresoftware.b4a.keywords.Common.Log(_data);
 //BA.debugLineNum = 127;BA.debugLine="Dim Register As HttpJob";
_register = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 128;BA.debugLine="Register.Initialize(\"rest\", Me)";
_register._initialize(processBA,"rest",bluetoothservice.getObject());
 //BA.debugLineNum = 129;BA.debugLine="Register.Download(\"http://40.113.245.233:5000/RealtimeUpdate/0/\" & aSensorString)";
_register._download("http://40.113.245.233:5000/RealtimeUpdate/0/"+_asensorstring);
 //BA.debugLineNum = 131;BA.debugLine="Dim Register2 As HttpJob";
_register2 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 132;BA.debugLine="Register2.Initialize(\"rest\", Me)";
_register2._initialize(processBA,"rest",bluetoothservice.getObject());
 //BA.debugLineNum = 133;BA.debugLine="Register2.Download(\"http://40.113.245.233:5000/RealtimeUpdate/1/\" & bSensorString)";
_register2._download("http://40.113.245.233:5000/RealtimeUpdate/1/"+_bsensorstring);
 //BA.debugLineNum = 143;BA.debugLine="aSensor.Clear";
_asensor.Clear();
 //BA.debugLineNum = 144;BA.debugLine="bSensor.Clear";
_bsensor.Clear();
 //BA.debugLineNum = 145;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 16;BA.debugLine="aSensor.Initialize";
_asensor.Initialize();
 //BA.debugLineNum = 17;BA.debugLine="bSensor.Initialize";
_bsensor.Initialize();
 //BA.debugLineNum = 19;BA.debugLine="sendDataTimer.Initialize(\"sendDataTimer\",2500)";
_senddatatimer.Initialize(processBA,"sendDataTimer",(long) (2500));
 //BA.debugLineNum = 20;BA.debugLine="sendDataTimer.Enabled = True";
_senddatatimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 34;BA.debugLine="AStream.Close";
_astream.Close();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 24;BA.debugLine="Try";
try { //BA.debugLineNum = 25;BA.debugLine="If AStream.IsInitialized = False Then";
if (_astream.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 26;BA.debugLine="AStream.Initialize(Main.serial1.InputStream,  Null, \"AStream\")";
_astream.Initialize(processBA,mostCurrent._main._serial1.getInputStream(),(java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.Null),"AStream");
 };
 } 
       catch (Exception e19) {
			processBA.setLastException(e19); };
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
}
