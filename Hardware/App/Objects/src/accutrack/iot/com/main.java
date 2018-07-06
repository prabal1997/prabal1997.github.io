package accutrack.iot.com;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "accutrack.iot.com", "accutrack.iot.com.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "accutrack.iot.com", "accutrack.iot.com.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "accutrack.iot.com.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Serial.BluetoothAdmin _admin = null;
public static anywheresoftware.b4a.objects.Serial _serial1 = null;
public static anywheresoftware.b4a.objects.collections.List _founddevices = null;
public static String _connectto = "";
public static boolean _connected = false;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsearchfordevices = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _serverping = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public accutrack.iot.com.chatactivity _chatactivity = null;
public accutrack.iot.com.bluetoothservice _bluetoothservice = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"admin",_admin,"bluetoothService",Debug.moduleToString(accutrack.iot.com.bluetoothservice.class),"btnSearchForDevices",mostCurrent._btnsearchfordevices,"ChatActivity",Debug.moduleToString(accutrack.iot.com.chatactivity.class),"connected",_connected,"connectto",_connectto,"foundDevices",_founddevices,"HttpUtils2Service",mostCurrent._httputils2service,"serial1",_serial1,"serverPing",mostCurrent._serverping};
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (chatactivity.mostCurrent != null);
return vis;}

public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = main.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

 {
            Activity __a = null;
            if (chatactivity.previousOne != null) {
				__a = chatactivity.previousOne.get();
			}
            else {
                BA ba = chatactivity.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

BA.applicationContext.stopService(new android.content.Intent(BA.applicationContext, bluetoothservice.class));
}
public static class _nameandmac{
public boolean IsInitialized;
public String Name;
public String Mac;
public void Initialize() {
IsInitialized = true;
Name = "";
Mac = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 26;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(33554432);
 BA.debugLineNum = 27;BA.debugLine="If FirstTime Then";
Debug.ShouldStop(67108864);
if (_firsttime) { 
 BA.debugLineNum = 28;BA.debugLine="admin.Initialize(\"admin\")";
Debug.ShouldStop(134217728);
_admin.Initialize(processBA,"admin");
 BA.debugLineNum = 29;BA.debugLine="serial1.Initialize(\"serial1\")";
Debug.ShouldStop(268435456);
Debug.DebugWarningEngine.CheckInitialize(_serial1);_serial1.Initialize("serial1");
 };
 BA.debugLineNum = 31;BA.debugLine="Activity.LoadLayout(\"1\")";
Debug.ShouldStop(1073741824);
mostCurrent._activity.LoadLayout("1",mostCurrent.activityBA);
 BA.debugLineNum = 32;BA.debugLine="End Sub";
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
public static String  _activity_pause(boolean _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 48;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(32768);
 BA.debugLineNum = 49;BA.debugLine="If UserClosed = True Then";
Debug.ShouldStop(65536);
if (_userclosed==anywheresoftware.b4a.keywords.Common.True) { 
 };
 BA.debugLineNum = 51;BA.debugLine="End Sub";
Debug.ShouldStop(262144);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 34;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(2);
 BA.debugLineNum = 35;BA.debugLine="btnSearchForDevices.Enabled = True";
Debug.ShouldStop(4);
mostCurrent._btnsearchfordevices.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 36;BA.debugLine="If admin.IsEnabled = False Then";
Debug.ShouldStop(8);
if (_admin.IsEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 37;BA.debugLine="If admin.Enable = False Then";
Debug.ShouldStop(16);
if (_admin.Enable()==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 38;BA.debugLine="ToastMessageShow(\"Error enabling Bluetooth adapter.\", True)";
Debug.ShouldStop(32);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Error enabling Bluetooth adapter.",anywheresoftware.b4a.keywords.Common.True);
 }else {
 BA.debugLineNum = 40;BA.debugLine="ToastMessageShow(\"Enabling Bluetooth adapter...\", False)";
Debug.ShouldStop(128);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Enabling Bluetooth adapter...",anywheresoftware.b4a.keywords.Common.False);
 };
 };
 BA.debugLineNum = 43;BA.debugLine="End Sub";
Debug.ShouldStop(1024);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _admin_devicefound(String _name,String _macaddress) throws Exception{
		Debug.PushSubsStack("Admin_DeviceFound (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
accutrack.iot.com.main._nameandmac _nm = null;
anywheresoftware.b4a.objects.IntentWrapper _pin = null;
Debug.locals.put("Name", _name);
Debug.locals.put("MacAddress", _macaddress);
 BA.debugLineNum = 69;BA.debugLine="Sub Admin_DeviceFound (Name As String, MacAddress As String)";
Debug.ShouldStop(16);
 BA.debugLineNum = 70;BA.debugLine="Dim nm As NameAndMac";
Debug.ShouldStop(32);
_nm = new accutrack.iot.com.main._nameandmac();Debug.locals.put("nm", _nm);
 BA.debugLineNum = 71;BA.debugLine="nm.Name = Name";
Debug.ShouldStop(64);
_nm.Name = _name;Debug.locals.put("nm", _nm);
 BA.debugLineNum = 72;BA.debugLine="nm.Mac = MacAddress";
Debug.ShouldStop(128);
_nm.Mac = _macaddress;Debug.locals.put("nm", _nm);
 BA.debugLineNum = 73;BA.debugLine="foundDevices.Add(nm)";
Debug.ShouldStop(256);
_founddevices.Add((Object)(_nm));
 BA.debugLineNum = 74;BA.debugLine="If nm.Name.ToLowerCase.Trim.Contains(\"hc-06\") Then";
Debug.ShouldStop(512);
if (_nm.Name.toLowerCase().trim().contains("hc-06")) { 
 BA.debugLineNum = 75;BA.debugLine="connectto = nm.Mac";
Debug.ShouldStop(1024);
_connectto = _nm.Mac;
 BA.debugLineNum = 76;BA.debugLine="serial1.connect(connectto)";
Debug.ShouldStop(2048);
_serial1.Connect(processBA,_connectto);
 BA.debugLineNum = 77;BA.debugLine="Dim pin As Intent";
Debug.ShouldStop(4096);
_pin = new anywheresoftware.b4a.objects.IntentWrapper();Debug.locals.put("pin", _pin);
 BA.debugLineNum = 78;BA.debugLine="pin.Initialize(pin.ACTION_EDIT,\"\")";
Debug.ShouldStop(8192);
_pin.Initialize(_pin.ACTION_EDIT,"");
 BA.debugLineNum = 79;BA.debugLine="pin.PutExtra(\"android.bluetooth.device.extra.PAIRING_KEY\",1234)";
Debug.ShouldStop(16384);
_pin.PutExtra("android.bluetooth.device.extra.PAIRING_KEY",(Object)(1234));
 };
 BA.debugLineNum = 81;BA.debugLine="End Sub";
Debug.ShouldStop(65536);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _admin_discoveryfinished() throws Exception{
		Debug.PushSubsStack("Admin_DiscoveryFinished (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 63;BA.debugLine="Sub Admin_DiscoveryFinished";
Debug.ShouldStop(1073741824);
 BA.debugLineNum = 64;BA.debugLine="If connected = False Then";
Debug.ShouldStop(-2147483648);
if (_connected==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 65;BA.debugLine="ToastMessageShow(\"Unable to find AccuTrack module\", False)";
Debug.ShouldStop(1);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Unable to find AccuTrack module",anywheresoftware.b4a.keywords.Common.False);
 };
 BA.debugLineNum = 67;BA.debugLine="End Sub";
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
public static String  _admin_statechanged(int _newstate,int _oldstate) throws Exception{
		Debug.PushSubsStack("Admin_StateChanged (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("NewState", _newstate);
Debug.locals.put("OldState", _oldstate);
 BA.debugLineNum = 45;BA.debugLine="Sub Admin_StateChanged (NewState As Int, OldState As Int)";
Debug.ShouldStop(4096);
 BA.debugLineNum = 46;BA.debugLine="End Sub";
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
public static String  _btnsearchfordevices_click() throws Exception{
		Debug.PushSubsStack("btnSearchForDevices_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 53;BA.debugLine="Sub btnSearchForDevices_Click";
Debug.ShouldStop(1048576);
 BA.debugLineNum = 55;BA.debugLine="foundDevices.Initialize";
Debug.ShouldStop(4194304);
_founddevices.Initialize();
 BA.debugLineNum = 56;BA.debugLine="If admin.StartDiscovery	= False Then";
Debug.ShouldStop(8388608);
if (_admin.StartDiscovery()==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 57;BA.debugLine="ToastMessageShow(\"Error starting discovery process.\", True)";
Debug.ShouldStop(16777216);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Error starting discovery process.",anywheresoftware.b4a.keywords.Common.True);
 }else {
 BA.debugLineNum = 59;BA.debugLine="ToastMessageShow(\"Trying to find AccuTrack\", False)";
Debug.ShouldStop(67108864);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Trying to find AccuTrack",anywheresoftware.b4a.keywords.Common.False);
 };
 BA.debugLineNum = 61;BA.debugLine="End Sub";
Debug.ShouldStop(268435456);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}

public static void initializeProcessGlobals() {
    if (mostCurrent != null && mostCurrent.activityBA != null) {
Debug.StartDebugging(mostCurrent.activityBA, 23028, new int[] {3, 2, 5}, "15fe8cb8-39d9-495d-b839-40804d8e8e8f");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
chatactivity._process_globals();
bluetoothservice._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSearchForDevices As Button";
mostCurrent._btnsearchfordevices = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private serverPing As CheckBox";
mostCurrent._serverping = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim admin As BluetoothAdmin";
_admin = new anywheresoftware.b4a.objects.Serial.BluetoothAdmin();
 //BA.debugLineNum = 14;BA.debugLine="Dim serial1 As Serial";
_serial1 = new anywheresoftware.b4a.objects.Serial();
 //BA.debugLineNum = 15;BA.debugLine="Dim foundDevices As List";
_founddevices = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 16;BA.debugLine="Type NameAndMac (Name As String, Mac As String)";
;
 //BA.debugLineNum = 17;BA.debugLine="Dim connectto As String";
_connectto = "";
 //BA.debugLineNum = 18;BA.debugLine="Dim connected As Boolean = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _serial1_connected(boolean _success) throws Exception{
		Debug.PushSubsStack("Serial1_Connected (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("Success", _success);
 BA.debugLineNum = 83;BA.debugLine="Sub Serial1_Connected (Success As Boolean)";
Debug.ShouldStop(262144);
 BA.debugLineNum = 84;BA.debugLine="If Success = False Then";
Debug.ShouldStop(524288);
if (_success==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 85;BA.debugLine="ToastMessageShow(\"Error connecting: \" & LastException.Message, True)";
Debug.ShouldStop(1048576);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Error connecting: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage(),anywheresoftware.b4a.keywords.Common.True);
 }else {
 BA.debugLineNum = 87;BA.debugLine="connected = True";
Debug.ShouldStop(4194304);
_connected = anywheresoftware.b4a.keywords.Common.True;
 BA.debugLineNum = 88;BA.debugLine="ToastMessageShow(\"Connected Successfuly!\",False)";
Debug.ShouldStop(8388608);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Connected Successfuly!",anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 90;BA.debugLine="StartService(bluetoothService)";
Debug.ShouldStop(33554432);
anywheresoftware.b4a.keywords.Common.StartService(mostCurrent.activityBA,(Object)(mostCurrent._bluetoothservice.getObject()));
 };
 BA.debugLineNum = 92;BA.debugLine="End Sub";
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
}
