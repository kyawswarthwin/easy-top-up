package com.moribanxenia.easytopup;


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

public class offersactivity extends Activity implements B4AActivity{
	public static offersactivity mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.offersactivity");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (offersactivity).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
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
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
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
		activityBA = new BA(this, layout, processBA, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.offersactivity");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.moribanxenia.easytopup.offersactivity", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (offersactivity) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (offersactivity) Resume **");
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
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
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
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
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
		return offersactivity.class;
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
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (offersactivity) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
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
        processBA.runHook("onresume", this, null);
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
            BA.LogInfo("** Activity (offersactivity) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _v5 = "";
public static String _v6 = "";
public com.datasteam.b4a.xtraviews.DialogView _vvvvvvv4 = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _vvvvvvvvvvv3 = null;
public static int _vvvvvvvvvvv2 = 0;
public anywheresoftware.b4a.phone.Phone.PhoneSms _vvvvvv0 = null;
public com.moribanxenia.easytopup.main _vvvvvvvvvvv1 = null;
public com.moribanxenia.easytopup.welcomeactivity _vvvvvv3 = null;
public com.moribanxenia.easytopup.statemanager _vvvvv6 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static class _activation{
public boolean IsInitialized;
public String PhoneNumber;
public String Activation;
public String Deactivation;
public void Initialize() {
IsInitialized = true;
PhoneNumber = "";
Activation = "";
Deactivation = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _offers = null;
anywheresoftware.b4a.objects.collections.Map _colsimoperator = null;
anywheresoftware.b4a.objects.collections.List _simoperator = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
anywheresoftware.b4a.objects.LabelWrapper _lblmessage = null;
anywheresoftware.b4a.objects.ButtonWrapper _btnactivate = null;
anywheresoftware.b4a.objects.ButtonWrapper _btndeactivate = null;
anywheresoftware.b4a.objects.drawable.ColorDrawable[] _cd = null;
anywheresoftware.b4a.objects.drawable.StateListDrawable _sld = null;
int _i = 0;
com.moribanxenia.easytopup.offersactivity._activation _a = null;
 //BA.debugLineNum = 21;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 22;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 23;BA.debugLine="Dim root, Offers, colSIMOperator As Map";
_root = new anywheresoftware.b4a.objects.collections.Map();
_offers = new anywheresoftware.b4a.objects.collections.Map();
_colsimoperator = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 24;BA.debugLine="Dim SIMOperator As List";
_simoperator = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 26;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim lblTitle, lblMessage As Label";
_lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
_lblmessage = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnActivate, btnDeactivate As Button";
_btnactivate = new anywheresoftware.b4a.objects.ButtonWrapper();
_btndeactivate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim cd(3) As ColorDrawable";
_cd = new anywheresoftware.b4a.objects.drawable.ColorDrawable[(int) (3)];
{
int d0 = _cd.length;
for (int i0 = 0;i0 < d0;i0++) {
_cd[i0] = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
}
}
;
 //BA.debugLineNum = 30;BA.debugLine="Dim sld As StateListDrawable";
_sld = new anywheresoftware.b4a.objects.drawable.StateListDrawable();
 //BA.debugLineNum = 32;BA.debugLine="Activity.Title = Title";
mostCurrent._activity.setTitle((Object)(_v5));
 //BA.debugLineNum = 34;BA.debugLine="Try";
try { //BA.debugLineNum = 35;BA.debugLine="parser.Initialize(File.ReadString(File.DirAssets";
_parser.Initialize(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.json"));
 //BA.debugLineNum = 36;BA.debugLine="root = parser.NextObject";
_root = _parser.NextObject();
 //BA.debugLineNum = 37;BA.debugLine="Offers = root.Get(Offer)";
_offers.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)(_v6))));
 //BA.debugLineNum = 38;BA.debugLine="SIMOperator = Offers.Get(Main.SIMOperator)";
_simoperator.setObject((java.util.List)(_offers.Get((Object)(mostCurrent._vvvvvvvvvvv1._vvv7))));
 //BA.debugLineNum = 40;BA.debugLine="If Offer = \"plans\" Or Offer = \"specialPacks\" The";
if ((_v6).equals("plans") || (_v6).equals("specialPacks")) { 
 //BA.debugLineNum = 41;BA.debugLine="iPanelHeight = 220dip";
_vvvvvvvvvvv2 = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (220));
 }else {
 //BA.debugLineNum = 43;BA.debugLine="iPanelHeight = 180dip";
_vvvvvvvvvvv2 = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (180));
 };
 //BA.debugLineNum = 45;BA.debugLine="sv.Initialize((SIMOperator.Size * iPanelHeight)";
mostCurrent._vvvvvvvvvvv3.Initialize(mostCurrent.activityBA,(int) ((_simoperator.getSize()*_vvvvvvvvvvv2)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 46;BA.debugLine="Activity.AddView(sv, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvvvvv3.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 48;BA.debugLine="For i = 0 To SIMOperator.Size - 1";
{
final int step22 = 1;
final int limit22 = (int) (_simoperator.getSize()-1);
for (_i = (int) (0) ; (step22 > 0 && _i <= limit22) || (step22 < 0 && _i >= limit22); _i = ((int)(0 + _i + step22)) ) {
 //BA.debugLineNum = 49;BA.debugLine="colSIMOperator = SIMOperator.Get(i)";
_colsimoperator.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_simoperator.Get(_i)));
 //BA.debugLineNum = 51;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 52;BA.debugLine="sv.Panel.AddView(pnl, 0, 5dip + (i * iPanelHeig";
mostCurrent._vvvvvvvvvvv3.getPanel().AddView((android.view.View)(_pnl.getObject()),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_i*_vvvvvvvvvvv2)),mostCurrent._vvvvvvvvvvv3.getWidth(),_vvvvvvvvvvv2);
 //BA.debugLineNum = 54;BA.debugLine="lblTitle.Initialize(\"\")";
_lbltitle.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 55;BA.debugLine="lblTitle.Text = colSIMOperator.Get(\"title\")";
_lbltitle.setText(_colsimoperator.Get((Object)("title")));
 //BA.debugLineNum = 56;BA.debugLine="lblTitle.Typeface = Typeface.DEFAULT_BOLD";
_lbltitle.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 57;BA.debugLine="lblTitle.TextSize = 20";
_lbltitle.setTextSize((float) (20));
 //BA.debugLineNum = 58;BA.debugLine="pnl.AddView(lblTitle, 10dip, 10dip, pnl.Width -";
_pnl.AddView((android.view.View)(_lbltitle.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_pnl.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 60;BA.debugLine="lblMessage.Initialize(\"\")";
_lblmessage.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 61;BA.debugLine="If Offer = \"plans\" Or Offer = \"specialPacks\" Th";
if ((_v6).equals("plans") || (_v6).equals("specialPacks")) { 
 //BA.debugLineNum = 62;BA.debugLine="lblMessage.Text = \"ေဈးႏႈန္း: \" & colSIMOperato";
_lblmessage.setText((Object)("ေဈးႏႈန္း: "+BA.ObjectToString(_colsimoperator.Get((Object)("price")))+anywheresoftware.b4a.keywords.Common.CRLF+"Voice: "+BA.ObjectToString(_colsimoperator.Get((Object)("voice")))+anywheresoftware.b4a.keywords.Common.CRLF+"SMS: "+BA.ObjectToString(_colsimoperator.Get((Object)("sms")))+anywheresoftware.b4a.keywords.Common.CRLF+"Data: "+BA.ObjectToString(_colsimoperator.Get((Object)("data")))+anywheresoftware.b4a.keywords.Common.CRLF+"ကန္႔သတ္ခ်က္: "+BA.ObjectToString(_colsimoperator.Get((Object)("limitation")))+anywheresoftware.b4a.keywords.Common.CRLF+"အက်ံဳးဝင္သည့္ကာလ: "+BA.ObjectToString(_colsimoperator.Get((Object)("validityPeriod")))));
 }else {
 //BA.debugLineNum = 64;BA.debugLine="lblMessage.Text = \"ေဈးႏႈန္း: \" & colSIMOperato";
_lblmessage.setText((Object)("ေဈးႏႈန္း: "+BA.ObjectToString(_colsimoperator.Get((Object)("price")))+anywheresoftware.b4a.keywords.Common.CRLF+"ပမာဏ: "+BA.ObjectToString(_colsimoperator.Get((Object)("volume")))+anywheresoftware.b4a.keywords.Common.CRLF+"ကန္႔သတ္ခ်က္: "+BA.ObjectToString(_colsimoperator.Get((Object)("limitation")))+anywheresoftware.b4a.keywords.Common.CRLF+"အက်ံဳးဝင္သည့္ကာလ: "+BA.ObjectToString(_colsimoperator.Get((Object)("validityPeriod")))));
 };
 //BA.debugLineNum = 66;BA.debugLine="lblMessage.Typeface = Main.SmartZawgyi";
_lblmessage.setTypeface((android.graphics.Typeface)(mostCurrent._vvvvvvvvvvv1._vvv6.getObject()));
 //BA.debugLineNum = 67;BA.debugLine="lblMessage.TextSize = 16";
_lblmessage.setTextSize((float) (16));
 //BA.debugLineNum = 68;BA.debugLine="pnl.AddView(lblMessage, 10dip, lblTitle.Top + l";
_pnl.AddView((android.view.View)(_lblmessage.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_lbltitle.getTop()+_lbltitle.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))),(int) (_pnl.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),mostCurrent._vvvvvvvvvvv1._wrap_content);
 //BA.debugLineNum = 70;BA.debugLine="Dim a As Activation";
_a = new com.moribanxenia.easytopup.offersactivity._activation();
 //BA.debugLineNum = 71;BA.debugLine="a.PhoneNumber = colSIMOperator.Get(\"phoneNumber";
_a.PhoneNumber = BA.ObjectToString(_colsimoperator.Get((Object)("phoneNumber")));
 //BA.debugLineNum = 72;BA.debugLine="a.Activation = colSIMOperator.Get(\"activation\")";
_a.Activation = BA.ObjectToString(_colsimoperator.Get((Object)("activation")));
 //BA.debugLineNum = 73;BA.debugLine="a.Deactivation = colSIMOperator.Get(\"deactivati";
_a.Deactivation = BA.ObjectToString(_colsimoperator.Get((Object)("deactivation")));
 //BA.debugLineNum = 75;BA.debugLine="btnActivate.Initialize(\"btnActivate\")";
_btnactivate.Initialize(mostCurrent.activityBA,"btnActivate");
 //BA.debugLineNum = 76;BA.debugLine="btnActivate.Tag = a";
_btnactivate.setTag((Object)(_a));
 //BA.debugLineNum = 77;BA.debugLine="btnActivate.Text = \"Activate\"";
_btnactivate.setText((Object)("Activate"));
 //BA.debugLineNum = 78;BA.debugLine="pnl.AddView(btnActivate, (pnl.Width / 2) + 5dip";
_pnl.AddView((android.view.View)(_btnactivate.getObject()),(int) ((_pnl.getWidth()/(double)2)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))),(int) (_pnl.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),(int) ((_pnl.getWidth()/(double)2)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));
 //BA.debugLineNum = 80;BA.debugLine="btnDeactivate.Initialize(\"btnDeactivate\")";
_btndeactivate.Initialize(mostCurrent.activityBA,"btnDeactivate");
 //BA.debugLineNum = 81;BA.debugLine="btnDeactivate.Tag = a";
_btndeactivate.setTag((Object)(_a));
 //BA.debugLineNum = 82;BA.debugLine="btnDeactivate.Text = \"Deactivate\"";
_btndeactivate.setText((Object)("Deactivate"));
 //BA.debugLineNum = 83;BA.debugLine="If Offer = \"plans\" Then pnl.AddView(btnDeactiva";
if ((_v6).equals("plans")) { 
_pnl.AddView((android.view.View)(_btndeactivate.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_pnl.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),(int) ((_pnl.getWidth()/(double)2)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));};
 //BA.debugLineNum = 85;BA.debugLine="If i Mod 2 = 1 Then";
if (_i%2==1) { 
 //BA.debugLineNum = 86;BA.debugLine="pnl.Color = Colors.RGB(0, 162, 232)";
_pnl.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 88;BA.debugLine="lblTitle.TextColor = Colors.White";
_lbltitle.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 89;BA.debugLine="lblMessage.TextColor = Colors.White";
_lblmessage.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 91;BA.debugLine="btnActivate.TextColor = Colors.RGB(0, 162, 232";
_btnactivate.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 92;BA.debugLine="cd(0).Initialize(Colors.White, 5dip)";
_cd[(int) (0)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.White,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 93;BA.debugLine="cd(1).Initialize(Colors.LightGray, 5dip)";
_cd[(int) (1)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.LightGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 94;BA.debugLine="cd(2).Initialize(Colors.DarkGray, 5dip)";
_cd[(int) (2)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.DarkGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 95;BA.debugLine="sld.Initialize";
_sld.Initialize();
 //BA.debugLineNum = 96;BA.debugLine="sld.AddState(sld.State_Disabled, cd(1))";
_sld.AddState(_sld.State_Disabled,(android.graphics.drawable.Drawable)(_cd[(int) (1)].getObject()));
 //BA.debugLineNum = 97;BA.debugLine="sld.AddState(sld.State_Pressed, cd(2))";
_sld.AddState(_sld.State_Pressed,(android.graphics.drawable.Drawable)(_cd[(int) (2)].getObject()));
 //BA.debugLineNum = 98;BA.debugLine="sld.AddCatchAllState(cd(0))";
_sld.AddCatchAllState((android.graphics.drawable.Drawable)(_cd[(int) (0)].getObject()));
 //BA.debugLineNum = 99;BA.debugLine="btnActivate.Background = sld";
_btnactivate.setBackground((android.graphics.drawable.Drawable)(_sld.getObject()));
 //BA.debugLineNum = 100;BA.debugLine="btnDeactivate.TextColor = Colors.RGB(0, 162, 2";
_btndeactivate.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 101;BA.debugLine="sld.Initialize";
_sld.Initialize();
 //BA.debugLineNum = 102;BA.debugLine="sld.AddState(sld.State_Disabled, cd(1))";
_sld.AddState(_sld.State_Disabled,(android.graphics.drawable.Drawable)(_cd[(int) (1)].getObject()));
 //BA.debugLineNum = 103;BA.debugLine="sld.AddState(sld.State_Pressed, cd(2))";
_sld.AddState(_sld.State_Pressed,(android.graphics.drawable.Drawable)(_cd[(int) (2)].getObject()));
 //BA.debugLineNum = 104;BA.debugLine="sld.AddCatchAllState(cd(0))";
_sld.AddCatchAllState((android.graphics.drawable.Drawable)(_cd[(int) (0)].getObject()));
 //BA.debugLineNum = 105;BA.debugLine="btnDeactivate.Background = sld";
_btndeactivate.setBackground((android.graphics.drawable.Drawable)(_sld.getObject()));
 }else {
 //BA.debugLineNum = 107;BA.debugLine="pnl.Color = Colors.White";
_pnl.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 109;BA.debugLine="lblTitle.TextColor = Colors.RGB(0, 162, 232)";
_lbltitle.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 110;BA.debugLine="lblMessage.TextColor = Colors.RGB(0, 162, 232)";
_lblmessage.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 112;BA.debugLine="btnActivate.TextColor = Colors.White";
_btnactivate.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 113;BA.debugLine="cd(0).Initialize(Colors.RGB(0, 162, 232), 5dip";
_cd[(int) (0)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 114;BA.debugLine="cd(1).Initialize(Colors.LightGray, 5dip)";
_cd[(int) (1)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.LightGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 115;BA.debugLine="cd(2).Initialize(Colors.DarkGray, 5dip)";
_cd[(int) (2)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.DarkGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 116;BA.debugLine="sld.Initialize";
_sld.Initialize();
 //BA.debugLineNum = 117;BA.debugLine="sld.AddState(sld.State_Disabled, cd(1))";
_sld.AddState(_sld.State_Disabled,(android.graphics.drawable.Drawable)(_cd[(int) (1)].getObject()));
 //BA.debugLineNum = 118;BA.debugLine="sld.AddState(sld.State_Pressed, cd(2))";
_sld.AddState(_sld.State_Pressed,(android.graphics.drawable.Drawable)(_cd[(int) (2)].getObject()));
 //BA.debugLineNum = 119;BA.debugLine="sld.AddCatchAllState(cd(0))";
_sld.AddCatchAllState((android.graphics.drawable.Drawable)(_cd[(int) (0)].getObject()));
 //BA.debugLineNum = 120;BA.debugLine="btnActivate.Background = sld";
_btnactivate.setBackground((android.graphics.drawable.Drawable)(_sld.getObject()));
 //BA.debugLineNum = 121;BA.debugLine="btnDeactivate.TextColor = Colors.White";
_btndeactivate.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 122;BA.debugLine="sld.Initialize";
_sld.Initialize();
 //BA.debugLineNum = 123;BA.debugLine="sld.AddState(sld.State_Disabled, cd(1))";
_sld.AddState(_sld.State_Disabled,(android.graphics.drawable.Drawable)(_cd[(int) (1)].getObject()));
 //BA.debugLineNum = 124;BA.debugLine="sld.AddState(sld.State_Pressed, cd(2))";
_sld.AddState(_sld.State_Pressed,(android.graphics.drawable.Drawable)(_cd[(int) (2)].getObject()));
 //BA.debugLineNum = 125;BA.debugLine="sld.AddCatchAllState(cd(0))";
_sld.AddCatchAllState((android.graphics.drawable.Drawable)(_cd[(int) (0)].getObject()));
 //BA.debugLineNum = 126;BA.debugLine="btnDeactivate.Background = sld";
_btndeactivate.setBackground((android.graphics.drawable.Drawable)(_sld.getObject()));
 };
 }
};
 } 
       catch (Exception e93) {
			processBA.setLastException(e93); //BA.debugLineNum = 130;BA.debugLine="Dialog.Options.Elements.Title.Typeface = Main.Sm";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(mostCurrent._vvvvvvvvvvv1._vvv6.getObject());
 //BA.debugLineNum = 131;BA.debugLine="Dialog.Options.Elements.Message.Typeface = Main.";
mostCurrent._vvvvvvv4.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(mostCurrent._vvvvvvvvvvv1._vvv6.getObject());
 //BA.debugLineNum = 132;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Ty";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(mostCurrent._vvvvvvvvvvv1._vvv6.getObject());
 //BA.debugLineNum = 133;BA.debugLine="Dialog.Msgbox(\"သတိေပးခ်က္\", \"ဝန္ေဆာင္မႈ မရွိေသးပ";
mostCurrent._vvvvvvv4.MsgBox(mostCurrent.activityBA,"သတိေပးခ်က္","ဝန္ေဆာင္မႈ မရွိေသးပါ။","အိုေက","","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 134;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 142;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 138;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static String  _btnactivate_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _btn = null;
com.moribanxenia.easytopup.offersactivity._activation _a = null;
 //BA.debugLineNum = 146;BA.debugLine="Sub btnActivate_Click";
 //BA.debugLineNum = 147;BA.debugLine="Dim btn As Button = Sender";
_btn = new anywheresoftware.b4a.objects.ButtonWrapper();
_btn.setObject((android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 148;BA.debugLine="Dim a As Activation = btn.Tag";
_a = (com.moribanxenia.easytopup.offersactivity._activation)(_btn.getTag());
 //BA.debugLineNum = 149;BA.debugLine="ps.Send(a.PhoneNumber, a.Activation)";
mostCurrent._vvvvvv0.Send(_a.PhoneNumber,_a.Activation);
 //BA.debugLineNum = 150;BA.debugLine="ToastMessageShow(\"Activate လုပ္ၿပီးပါၿပီ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Activate လုပ္ၿပီးပါၿပီ။",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _btndeactivate_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _btn = null;
com.moribanxenia.easytopup.offersactivity._activation _a = null;
 //BA.debugLineNum = 153;BA.debugLine="Sub btnDeactivate_Click";
 //BA.debugLineNum = 154;BA.debugLine="Dim btn As Button = Sender";
_btn = new anywheresoftware.b4a.objects.ButtonWrapper();
_btn.setObject((android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 155;BA.debugLine="Dim a As Activation = btn.Tag";
_a = (com.moribanxenia.easytopup.offersactivity._activation)(_btn.getTag());
 //BA.debugLineNum = 156;BA.debugLine="ps.Send(a.PhoneNumber, a.Deactivation)";
mostCurrent._vvvvvv0.Send(_a.PhoneNumber,_a.Deactivation);
 //BA.debugLineNum = 157;BA.debugLine="ToastMessageShow(\"Deactivate လုပ္ၿပီးပါၿပီ။\", Tru";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Deactivate လုပ္ၿပီးပါၿပီ။",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Private Dialog As DialogView";
mostCurrent._vvvvvvv4 = new com.datasteam.b4a.xtraviews.DialogView();
 //BA.debugLineNum = 15;BA.debugLine="Private sv As ScrollView";
mostCurrent._vvvvvvvvvvv3 = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Private iPanelHeight As Int";
_vvvvvvvvvvv2 = 0;
 //BA.debugLineNum = 18;BA.debugLine="Private ps As PhoneSms";
mostCurrent._vvvvvv0 = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Type Activation(PhoneNumber As String, Activation";
;
 //BA.debugLineNum = 9;BA.debugLine="Public Title, Offer As String";
_v5 = "";
_v6 = "";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
}
