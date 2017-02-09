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

public class packagesactivity extends Activity implements B4AActivity{
	public static packagesactivity mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.packagesactivity");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (packagesactivity).");
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
		activityBA = new BA(this, layout, processBA, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.packagesactivity");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.moribanxenia.easytopup.packagesactivity", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (packagesactivity) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (packagesactivity) Resume **");
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
		return packagesactivity.class;
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
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (packagesactivity) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (packagesactivity) Resume **");
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
public static String _v5 = "";
public static String _v6 = "";
public anywheresoftware.b4a.objects.ScrollViewWrapper _vvvvvvvvvvvvvvv5 = null;
public static int _vvvvvvvvvvvvvvv6 = 0;
public anywheresoftware.b4a.phone.Phone.PhoneSms _vvvvvvvvvvvvv1 = null;
public com.moribanxenia.easytopup.main _vvvvvvv6 = null;
public com.moribanxenia.easytopup.smschannelsactivity _vvvvvvv0 = null;
public com.moribanxenia.easytopup.notification _vvvvvvvv1 = null;
public com.moribanxenia.easytopup.receiver _vvvvvvvv2 = null;
public com.moribanxenia.easytopup.welcomeactivity _vvvvvvvv3 = null;
public com.moribanxenia.easytopup.statemanager _vvvvvvvv4 = null;

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
public void Initialize() {
IsInitialized = true;
PhoneNumber = "";
Activation = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _packages = null;
anywheresoftware.b4a.objects.collections.Map _colsimoperator = null;
anywheresoftware.b4a.objects.collections.List _simoperator = null;
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
anywheresoftware.b4a.objects.LabelWrapper _lblmessage = null;
anywheresoftware.b4a.objects.ButtonWrapper _btnactivate = null;
anywheresoftware.b4a.objects.drawable.ColorDrawable[] _cd = null;
anywheresoftware.b4a.objects.drawable.StateListDrawable _sld = null;
int _i = 0;
com.moribanxenia.easytopup.packagesactivity._activation _a = null;
 //BA.debugLineNum = 19;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 20;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 21;BA.debugLine="Dim root, Packages, colSIMOperator As Map";
_root = new anywheresoftware.b4a.objects.collections.Map();
_packages = new anywheresoftware.b4a.objects.collections.Map();
_colsimoperator = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 22;BA.debugLine="Dim SIMOperator As List";
_simoperator = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 24;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblTitle, lblMessage As Label";
_lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
_lblmessage = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnActivate As Button";
_btnactivate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim cd(3) As ColorDrawable";
_cd = new anywheresoftware.b4a.objects.drawable.ColorDrawable[(int) (3)];
{
int d0 = _cd.length;
for (int i0 = 0;i0 < d0;i0++) {
_cd[i0] = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
}
}
;
 //BA.debugLineNum = 28;BA.debugLine="Dim sld As StateListDrawable";
_sld = new anywheresoftware.b4a.objects.drawable.StateListDrawable();
 //BA.debugLineNum = 30;BA.debugLine="Activity.Title = Title";
mostCurrent._activity.setTitle((Object)(_v5));
 //BA.debugLineNum = 32;BA.debugLine="parser.Initialize(File.ReadString(File.DirAssets, \"packages.json\"))";
_parser.Initialize(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"packages.json"));
 //BA.debugLineNum = 33;BA.debugLine="root = parser.NextObject";
_root = _parser.NextObject();
 //BA.debugLineNum = 34;BA.debugLine="Packages = root.Get(Package)";
_packages.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)(_v6))));
 //BA.debugLineNum = 35;BA.debugLine="SIMOperator = Packages.Get(Main.SIMOperator)";
_simoperator.setObject((java.util.List)(_packages.Get((Object)(mostCurrent._vvvvvvv6._vvvv4))));
 //BA.debugLineNum = 37;BA.debugLine="sv.Initialize((SIMOperator.Size * iPanelHeight) + 10dip)";
mostCurrent._vvvvvvvvvvvvvvv5.Initialize(mostCurrent.activityBA,(int) ((_simoperator.getSize()*_vvvvvvvvvvvvvvv6)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 38;BA.debugLine="Activity.AddView(sv, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvvvvvvvvv5.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 40;BA.debugLine="For i = 0 To SIMOperator.Size - 1";
{
final int step25 = 1;
final int limit25 = (int) (_simoperator.getSize()-1);
for (_i = (int) (0); (step25 > 0 && _i <= limit25) || (step25 < 0 && _i >= limit25); _i = ((int)(0 + _i + step25))) {
 //BA.debugLineNum = 41;BA.debugLine="colSIMOperator = SIMOperator.Get(i)";
_colsimoperator.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_simoperator.Get(_i)));
 //BA.debugLineNum = 43;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 44;BA.debugLine="sv.Panel.AddView(pnl, 0, 5dip + (i * iPanelHeight), sv.Width, iPanelHeight)";
mostCurrent._vvvvvvvvvvvvvvv5.getPanel().AddView((android.view.View)(_pnl.getObject()),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))+(_i*_vvvvvvvvvvvvvvv6)),mostCurrent._vvvvvvvvvvvvvvv5.getWidth(),_vvvvvvvvvvvvvvv6);
 //BA.debugLineNum = 46;BA.debugLine="lblTitle.Initialize(\"\")";
_lbltitle.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 47;BA.debugLine="lblTitle.Text = colSIMOperator.Get(\"package\")";
_lbltitle.setText(_colsimoperator.Get((Object)("package")));
 //BA.debugLineNum = 48;BA.debugLine="lblTitle.Typeface = Typeface.DEFAULT_BOLD";
_lbltitle.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 49;BA.debugLine="lblTitle.TextSize = 20";
_lbltitle.setTextSize((float) (20));
 //BA.debugLineNum = 50;BA.debugLine="pnl.AddView(lblTitle, 10dip, 10dip, pnl.Width - 20dip, 30dip)";
_pnl.AddView((android.view.View)(_lbltitle.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_pnl.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 52;BA.debugLine="lblMessage.Initialize(\"\")";
_lblmessage.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 53;BA.debugLine="lblMessage.Text = \"ေဈးႏႈန္း: \" & colSIMOperator.Get(\"price\") & CRLF & \"ပမာဏ: \" & colSIMOperator.Get(\"volume\") & CRLF & \"အက်ံဳးဝင္သည့္ကာလ: \" & colSIMOperator.Get(\"validityPeriod\")";
_lblmessage.setText((Object)("ေဈးႏႈန္း: "+BA.ObjectToString(_colsimoperator.Get((Object)("price")))+anywheresoftware.b4a.keywords.Common.CRLF+"ပမာဏ: "+BA.ObjectToString(_colsimoperator.Get((Object)("volume")))+anywheresoftware.b4a.keywords.Common.CRLF+"အက်ံဳးဝင္သည့္ကာလ: "+BA.ObjectToString(_colsimoperator.Get((Object)("validityPeriod")))));
 //BA.debugLineNum = 54;BA.debugLine="lblMessage.Typeface = Main.SmartZawgyi";
_lblmessage.setTypeface((android.graphics.Typeface)(mostCurrent._vvvvvvv6._vvvv3.getObject()));
 //BA.debugLineNum = 55;BA.debugLine="lblMessage.TextSize = 16";
_lblmessage.setTextSize((float) (16));
 //BA.debugLineNum = 56;BA.debugLine="pnl.AddView(lblMessage, 10dip, lblTitle.Top + lblTitle.Height + 5dip, pnl.Width - 20dip, Main.WRAP_CONTENT)";
_pnl.AddView((android.view.View)(_lblmessage.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_lbltitle.getTop()+_lbltitle.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))),(int) (_pnl.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),mostCurrent._vvvvvvv6._wrap_content);
 //BA.debugLineNum = 58;BA.debugLine="Dim a As Activation";
_a = new com.moribanxenia.easytopup.packagesactivity._activation();
 //BA.debugLineNum = 59;BA.debugLine="a.PhoneNumber = colSIMOperator.Get(\"phoneNumber\")";
_a.PhoneNumber = BA.ObjectToString(_colsimoperator.Get((Object)("phoneNumber")));
 //BA.debugLineNum = 60;BA.debugLine="a.Activation = colSIMOperator.Get(\"activation\")";
_a.Activation = BA.ObjectToString(_colsimoperator.Get((Object)("activation")));
 //BA.debugLineNum = 62;BA.debugLine="btnActivate.Initialize(\"btnActivate\")";
_btnactivate.Initialize(mostCurrent.activityBA,"btnActivate");
 //BA.debugLineNum = 63;BA.debugLine="btnActivate.Tag = a";
_btnactivate.setTag((Object)(_a));
 //BA.debugLineNum = 64;BA.debugLine="btnActivate.Text = \"Activate\"";
_btnactivate.setText((Object)("Activate"));
 //BA.debugLineNum = 65;BA.debugLine="pnl.AddView(btnActivate, (pnl.Width / 2) + 5dip, pnl.Height - 50dip, (pnl.Width / 2) - 15dip, 40dip)";
_pnl.AddView((android.view.View)(_btnactivate.getObject()),(int) ((_pnl.getWidth()/(double)2)+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5))),(int) (_pnl.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),(int) ((_pnl.getWidth()/(double)2)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));
 //BA.debugLineNum = 67;BA.debugLine="If i Mod 2 = 1 Then";
if (_i%2==1) { 
 //BA.debugLineNum = 68;BA.debugLine="pnl.Color = Colors.RGB(0, 162, 232)";
_pnl.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 70;BA.debugLine="lblTitle.TextColor = Colors.White";
_lbltitle.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 71;BA.debugLine="lblMessage.TextColor = Colors.White";
_lblmessage.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 73;BA.debugLine="btnActivate.TextColor = Colors.RGB(0, 162, 232)";
_btnactivate.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 74;BA.debugLine="cd(0).Initialize(Colors.White, 5dip)";
_cd[(int) (0)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.White,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 75;BA.debugLine="cd(1).Initialize(Colors.LightGray, 5dip)";
_cd[(int) (1)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.LightGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 76;BA.debugLine="cd(2).Initialize(Colors.DarkGray, 5dip)";
_cd[(int) (2)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.DarkGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 77;BA.debugLine="sld.Initialize";
_sld.Initialize();
 //BA.debugLineNum = 78;BA.debugLine="sld.AddState(sld.State_Disabled, cd(1))";
_sld.AddState(_sld.State_Disabled,(android.graphics.drawable.Drawable)(_cd[(int) (1)].getObject()));
 //BA.debugLineNum = 79;BA.debugLine="sld.AddState(sld.State_Pressed, cd(2))";
_sld.AddState(_sld.State_Pressed,(android.graphics.drawable.Drawable)(_cd[(int) (2)].getObject()));
 //BA.debugLineNum = 80;BA.debugLine="sld.AddCatchAllState(cd(0))";
_sld.AddCatchAllState((android.graphics.drawable.Drawable)(_cd[(int) (0)].getObject()));
 //BA.debugLineNum = 81;BA.debugLine="btnActivate.Background = sld";
_btnactivate.setBackground((android.graphics.drawable.Drawable)(_sld.getObject()));
 }else {
 //BA.debugLineNum = 83;BA.debugLine="pnl.Color = Colors.White";
_pnl.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 85;BA.debugLine="lblTitle.TextColor = Colors.RGB(0, 162, 232)";
_lbltitle.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 86;BA.debugLine="lblMessage.TextColor = Colors.RGB(0, 162, 232)";
_lblmessage.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)));
 //BA.debugLineNum = 88;BA.debugLine="btnActivate.TextColor = Colors.White";
_btnactivate.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 89;BA.debugLine="cd(0).Initialize(Colors.RGB(0, 162, 232), 5dip)";
_cd[(int) (0)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (162),(int) (232)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 90;BA.debugLine="cd(1).Initialize(Colors.LightGray, 5dip)";
_cd[(int) (1)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.LightGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 91;BA.debugLine="cd(2).Initialize(Colors.DarkGray, 5dip)";
_cd[(int) (2)].Initialize(anywheresoftware.b4a.keywords.Common.Colors.DarkGray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 92;BA.debugLine="sld.Initialize";
_sld.Initialize();
 //BA.debugLineNum = 93;BA.debugLine="sld.AddState(sld.State_Disabled, cd(1))";
_sld.AddState(_sld.State_Disabled,(android.graphics.drawable.Drawable)(_cd[(int) (1)].getObject()));
 //BA.debugLineNum = 94;BA.debugLine="sld.AddState(sld.State_Pressed, cd(2))";
_sld.AddState(_sld.State_Pressed,(android.graphics.drawable.Drawable)(_cd[(int) (2)].getObject()));
 //BA.debugLineNum = 95;BA.debugLine="sld.AddCatchAllState(cd(0))";
_sld.AddCatchAllState((android.graphics.drawable.Drawable)(_cd[(int) (0)].getObject()));
 //BA.debugLineNum = 96;BA.debugLine="btnActivate.Background = sld";
_btnactivate.setBackground((android.graphics.drawable.Drawable)(_sld.getObject()));
 };
 }
};
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _btnactivate_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _btn = null;
com.moribanxenia.easytopup.packagesactivity._activation _a = null;
 //BA.debugLineNum = 109;BA.debugLine="Sub btnActivate_Click";
 //BA.debugLineNum = 110;BA.debugLine="Dim btn As Button = Sender";
_btn = new anywheresoftware.b4a.objects.ButtonWrapper();
_btn.setObject((android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 111;BA.debugLine="Dim a As Activation = btn.Tag";
_a = (com.moribanxenia.easytopup.packagesactivity._activation)(_btn.getTag());
 //BA.debugLineNum = 112;BA.debugLine="ps.Send(a.PhoneNumber, a.Activation)";
mostCurrent._vvvvvvvvvvvvv1.Send(_a.PhoneNumber,_a.Activation);
 //BA.debugLineNum = 113;BA.debugLine="ToastMessageShow(\"Activate လုပ္ၿပီးပါၿပီ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Activate လုပ္ၿပီးပါၿပီ။",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim sv As ScrollView";
mostCurrent._vvvvvvvvvvvvvvv5 = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim iPanelHeight As Int = 160dip";
_vvvvvvvvvvvvvvv6 = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (160));
 //BA.debugLineNum = 16;BA.debugLine="Dim ps As PhoneSms";
mostCurrent._vvvvvvvvvvvvv1 = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Public Title, Package As String";
_v5 = "";
_v6 = "";
 //BA.debugLineNum = 9;BA.debugLine="Type Activation(PhoneNumber As String, Activation As String)";
;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
}
