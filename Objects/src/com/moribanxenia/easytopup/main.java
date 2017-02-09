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
			processBA = new BA(this.getApplicationContext(), null, null, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.main");
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
		activityBA = new BA(this, layout, processBA, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.moribanxenia.easytopup.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
        super.onNewIntent(intent);
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
public static anywheresoftware.b4a.phone.PackageManagerWrapper _vvvvvvvvvvvvv5 = null;
public static String _vvv0 = "";
public static String _vvvv1 = "";
public static String _vvvv2 = "";
public static com.moribanxenia.easytopup.keyvaluestore _vvvvvvvv0 = null;
public static anywheresoftware.b4a.objects.ParseObjectWrapper.ParseWrapper _vvvvvvvvv1 = null;
public static anywheresoftware.b4a.gps.GPS _vvvvvvvvv2 = null;
public static int _id_action_home = 0;
public static int _id_action_overflow = 0;
public static anywheresoftware.b4a.objects.Timer _vvvvvvvvvv1 = null;
public static int _vvvvvvvvvv7 = 0;
public static int _type_home = 0;
public static int _type_plansandpackages = 0;
public static int _type_vas = 0;
public static int _type_stores = 0;
public static int _type_about = 0;
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvvv3 = null;
public static int _fill_parent = 0;
public static int _wrap_content = 0;
public static int _vvvvvvvvvv0 = 0;
public static anywheresoftware.b4a.phone.Phone _vvvvvvvvvvvvv7 = null;
public static String _vvvv4 = "";
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _vvvvvvvvv5 = null;
public anywheresoftware.b4a.objects.ParseObjectWrapper.ParseInstallationWrapper _vvvvvvvvvvvvv6 = null;
public static boolean _vvvvvvvvvvv1 = false;
public anywheresoftware.b4a.object.XmlLayoutBuilder _vvvvvvvvvvv5 = null;
public de.amberhome.SimpleActionBar.ActionBarWrapper _vvvvvvvv6 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _vvvvvvvvv4 = null;
public de.amberhome.quickaction.ICSMenu _vvvvvvvv5 = null;
public anywheresoftware.b4a.objects.collections.Map _vvvvvvvv7 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbanner = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlcontent = null;
public static String[] _vvvvvvvvv7 = null;
public de.amberhome.viewpager.AHPageContainer _vvvvvvvvv6 = null;
public de.amberhome.viewpager.AHPageContainer _vvvvvvvvvv2 = null;
public de.amberhome.viewpager.AHViewPager _vvvvvvvvv0 = null;
public de.amberhome.viewpager.AHViewPager _vvvvvvvvvv4 = null;
public de.amberhome.viewpager.AHViewPagerTabs _vvvvvvvvvv5 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvvvv6 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbldeveloper = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvvvvvvv3 = null;
public com.datasteam.b4a.xtraviews.DialogView _vvvvvvvvvvv2 = null;
public static String _vvvvvvvvvvv4 = "";
public static String _vvvvvvvvvvvv3 = "";
public static String _vvvvvvvvvvvv0 = "";
public anywheresoftware.b4a.objects.StringUtils _vvvvvvvvvvv7 = null;
public anywheresoftware.b4a.phone.Phone.PhoneCalls _vvvvvvvvvvv6 = null;
public anywheresoftware.b4a.phone.Phone.PhoneSms _vvvvvvvvvvvvv1 = null;
public com.moribanxenia.easytopup.packagesactivity _vvvvvvv7 = null;
public com.moribanxenia.easytopup.smschannelsactivity _vvvvvvv0 = null;
public com.moribanxenia.easytopup.notification _vvvvvvvv1 = null;
public com.moribanxenia.easytopup.receiver _vvvvvvvv2 = null;
public com.moribanxenia.easytopup.welcomeactivity _vvvvvvvv3 = null;
public com.moribanxenia.easytopup.statemanager _vvvvvvvv4 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (packagesactivity.mostCurrent != null);
vis = vis | (smschannelsactivity.mostCurrent != null);
vis = vis | (notification.mostCurrent != null);
vis = vis | (welcomeactivity.mostCurrent != null);
return vis;}
public static String  _ab_itemclicked(int _itemid) throws Exception{
 //BA.debugLineNum = 241;BA.debugLine="Sub AB_ItemClicked(ItemID As Int)";
 //BA.debugLineNum = 242;BA.debugLine="Select ItemID";
switch (BA.switchObjectToInt(_itemid,_id_action_overflow)) {
case 0:
 //BA.debugLineNum = 244;BA.debugLine="menu.Show(ab.GetActionView(ItemID))";
mostCurrent._vvvvvvvv5.Show(mostCurrent._vvvvvvvv6.GetActionView(_itemid));
 break;
}
;
 //BA.debugLineNum = 246;BA.debugLine="End Sub";
return "";
}
public static String  _ac_click(int _position,int _actionitemid) throws Exception{
String _soldsimoperator = "";
 //BA.debugLineNum = 248;BA.debugLine="Sub AC_Click (Position As Int, ActionItemID As Int)";
 //BA.debugLineNum = 249;BA.debugLine="Dim sOldSIMOperator As String = SIMOperator";
_soldsimoperator = _vvvv4;
 //BA.debugLineNum = 250;BA.debugLine="SIMOperator = SIMOperatorName.GetKeyAt(Position)";
_vvvv4 = BA.ObjectToString(mostCurrent._vvvvvvvv7.GetKeyAt(_position));
 //BA.debugLineNum = 251;BA.debugLine="If sOldSIMOperator <> SIMOperator Then Activity_Create(False)";
if ((_soldsimoperator).equals(_vvvv4) == false) { 
_activity_create(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.ImageViewWrapper _iv = null;
int _i = 0;
de.amberhome.quickaction.ActionItem _ai = null;
 //BA.debugLineNum = 83;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 84;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 85;BA.debugLine="Dim iv As ImageView";
_iv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 87;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 88;BA.debugLine="If Not (File.Exists(File.DirInternal, \"database\")) Then File.Copy(File.DirAssets, \"database\", File.DirInternal, \"database\")";
if (anywheresoftware.b4a.keywords.Common.Not(anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"database"))) { 
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"database",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"database");};
 //BA.debugLineNum = 89;BA.debugLine="kvs.Initialize(File.DirInternal, \"database\")";
_vvvvvvvv0._initialize(processBA,anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"database");
 //BA.debugLineNum = 91;BA.debugLine="Parse.TrackOpening";
_vvvvvvvvv1.TrackOpening();
 //BA.debugLineNum = 92;BA.debugLine="Parse.EnableNotifications(Notification)";
_vvvvvvvvv1.EnableNotifications(processBA,(java.lang.Class)(mostCurrent._vvvvvvvv1.getObject()));
 //BA.debugLineNum = 93;BA.debugLine="Parse.Subscribe(\"Broadcast\", Notification)";
_vvvvvvvvv1.Subscribe(processBA,"Broadcast",(java.lang.Class)(mostCurrent._vvvvvvvv1.getObject()));
 //BA.debugLineNum = 94;BA.debugLine="StartService(Receiver)";
anywheresoftware.b4a.keywords.Common.StartService(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvv2.getObject()));
 //BA.debugLineNum = 95;BA.debugLine="gps1.Initialize(\"GPS\")";
_vvvvvvvvv2.Initialize("GPS");
 //BA.debugLineNum = 96;BA.debugLine="If Not (StateManager.GetSetting2(\"IsRegistered\", False)) Then Register";
if (anywheresoftware.b4a.keywords.Common.Not(BA.ObjectToBoolean(mostCurrent._vvvvvvvv4._vv6(mostCurrent.activityBA,"IsRegistered",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False))))) { 
_vvvvvvvvv3();};
 }else {
 //BA.debugLineNum = 98;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 };
 //BA.debugLineNum = 101;BA.debugLine="If FirstTime OR ab.IsInitialized = False Then";
if (_firsttime || mostCurrent._vvvvvvvv6.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 102;BA.debugLine="menu.Initialize(\"AC\")";
mostCurrent._vvvvvvvv5.Initialize(processBA,"AC");
 //BA.debugLineNum = 103;BA.debugLine="For i = 0 To SIMOperatorName.Size - 1";
{
final int step64 = 1;
final int limit64 = (int) (mostCurrent._vvvvvvvv7.getSize()-1);
for (_i = (int) (0); (step64 > 0 && _i <= limit64) || (step64 < 0 && _i >= limit64); _i = ((int)(0 + _i + step64))) {
 //BA.debugLineNum = 104;BA.debugLine="Dim ai As AHActionItem";
_ai = new de.amberhome.quickaction.ActionItem();
 //BA.debugLineNum = 105;BA.debugLine="ai.Initialize(i, SIMOperatorName.GetValueAt(i), Null)";
_ai.Initialize(_i,BA.ObjectToString(mostCurrent._vvvvvvvv7.GetValueAt(_i)),(android.graphics.drawable.Drawable)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 106;BA.debugLine="menu.AddActionItem(ai)";
mostCurrent._vvvvvvvv5.AddActionItem(_ai);
 }
};
 };
 //BA.debugLineNum = 110;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 112;BA.debugLine="overflowIcon.Initialize(LoadBitmap(File.DirAssets, \"ic_action_overflow.png\"))";
mostCurrent._vvvvvvvvv4.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_action_overflow.png").getObject()));
 //BA.debugLineNum = 114;BA.debugLine="ab.Initialize(\"AB\")";
mostCurrent._vvvvvvvv6.Initialize(mostCurrent.activityBA,"AB");
 //BA.debugLineNum = 115;BA.debugLine="ab.SubTitle = SIMOperatorName.GetDefault(SIMOperator, \"MECTel\")";
mostCurrent._vvvvvvvv6.setSubTitle((java.lang.CharSequence)(mostCurrent._vvvvvvvv7.GetDefault((Object)(_vvvv4),(Object)("MECTel"))));
 //BA.debugLineNum = 116;BA.debugLine="ab.AddHomeAction(ID_ACTION_HOME, AppIcon)";
mostCurrent._vvvvvvvv6.AddHomeAction(_id_action_home,(android.graphics.drawable.Drawable)(mostCurrent._vvvvvvvvv5.getObject()));
 //BA.debugLineNum = 117;BA.debugLine="ab.AddAction(ID_ACTION_OVERFLOW, overflowIcon)";
mostCurrent._vvvvvvvv6.AddAction(_id_action_overflow,(android.graphics.drawable.Drawable)(mostCurrent._vvvvvvvvv4.getObject()));
 //BA.debugLineNum = 118;BA.debugLine="Activity.AddView(ab, 0, 0, 100%x, 48dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv6.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 120;BA.debugLine="containerBanner.Initialize";
mostCurrent._vvvvvvvvv6.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 121;BA.debugLine="For i = 0 To asBanner.Length - 1";
{
final int step78 = 1;
final int limit78 = (int) (mostCurrent._vvvvvvvvv7.length-1);
for (_i = (int) (0); (step78 > 0 && _i <= limit78) || (step78 < 0 && _i >= limit78); _i = ((int)(0 + _i + step78))) {
 //BA.debugLineNum = 122;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 123;BA.debugLine="iv.Initialize(\"\")";
_iv.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 124;BA.debugLine="iv.Bitmap = kvs.GetBitmap(asBanner(i))";
_iv.setBitmap((android.graphics.Bitmap)(_vvvvvvvv0._vvvvv2(mostCurrent._vvvvvvvvv7[_i]).getObject()));
 //BA.debugLineNum = 125;BA.debugLine="iv.Gravity = Gravity.FILL";
_iv.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 126;BA.debugLine="pnl.AddView(iv, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_iv.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 //BA.debugLineNum = 127;BA.debugLine="containerBanner.AddPage(pnl, \"\")";
mostCurrent._vvvvvvvvv6.AddPage((android.view.View)(_pnl.getObject()),"");
 }
};
 //BA.debugLineNum = 129;BA.debugLine="pagerBanner.Initialize(containerBanner, \"Banner\")";
mostCurrent._vvvvvvvvv0.Initialize(mostCurrent.activityBA,mostCurrent._vvvvvvvvv6,"Banner");
 //BA.debugLineNum = 130;BA.debugLine="pnlBanner.AddView(pagerBanner, 0, 0, 100%x, 37%x)";
mostCurrent._pnlbanner.AddView((android.view.View)(mostCurrent._vvvvvvvvv0.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA));
 //BA.debugLineNum = 131;BA.debugLine="tmrBanner.Initialize(\"tmrBanner\", 8000)";
_vvvvvvvvvv1.Initialize(processBA,"tmrBanner",(long) (8000));
 //BA.debugLineNum = 132;BA.debugLine="tmrBanner.Enabled = True";
_vvvvvvvvvv1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 134;BA.debugLine="container.Initialize";
mostCurrent._vvvvvvvvvv2.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 135;BA.debugLine="For i = 0 To 4";
{
final int step91 = 1;
final int limit91 = (int) (4);
for (_i = (int) (0); (step91 > 0 && _i <= limit91) || (step91 < 0 && _i >= limit91); _i = ((int)(0 + _i + step91))) {
 //BA.debugLineNum = 136;BA.debugLine="Select i";
switch (_i) {
case 0:
 //BA.debugLineNum = 138;BA.debugLine="pnl = CreatePanel(TYPE_HOME)";
_pnl = _vvvvvvvvvv3(_type_home);
 //BA.debugLineNum = 139;BA.debugLine="container.AddPage(pnl, \"Home\")";
mostCurrent._vvvvvvvvvv2.AddPage((android.view.View)(_pnl.getObject()),"Home");
 break;
case 1:
 //BA.debugLineNum = 141;BA.debugLine="If SIMOperator <> \"41404\" Then";
if ((_vvvv4).equals("41404") == false) { 
 //BA.debugLineNum = 142;BA.debugLine="pnl = CreatePanel(TYPE_PLANSANDPACKAGES)";
_pnl = _vvvvvvvvvv3(_type_plansandpackages);
 //BA.debugLineNum = 143;BA.debugLine="container.AddPage(pnl, \"Plans & Packages\")";
mostCurrent._vvvvvvvvvv2.AddPage((android.view.View)(_pnl.getObject()),"Plans & Packages");
 };
 break;
case 2:
 //BA.debugLineNum = 146;BA.debugLine="pnl = CreatePanel(TYPE_VAS)";
_pnl = _vvvvvvvvvv3(_type_vas);
 //BA.debugLineNum = 147;BA.debugLine="container.AddPage(pnl, \"VAS\")";
mostCurrent._vvvvvvvvvv2.AddPage((android.view.View)(_pnl.getObject()),"VAS");
 break;
case 3:
 //BA.debugLineNum = 149;BA.debugLine="pnl = CreatePanel(TYPE_STORES)";
_pnl = _vvvvvvvvvv3(_type_stores);
 //BA.debugLineNum = 150;BA.debugLine="container.AddPage(pnl, \"Stores\")";
mostCurrent._vvvvvvvvvv2.AddPage((android.view.View)(_pnl.getObject()),"Stores");
 break;
case 4:
 //BA.debugLineNum = 152;BA.debugLine="pnl = CreatePanel(TYPE_ABOUT)";
_pnl = _vvvvvvvvvv3(_type_about);
 //BA.debugLineNum = 153;BA.debugLine="container.AddPage(pnl, \"About\")";
mostCurrent._vvvvvvvvvv2.AddPage((android.view.View)(_pnl.getObject()),"About");
 break;
}
;
 }
};
 //BA.debugLineNum = 157;BA.debugLine="pager.Initialize(container, \"Pager\")";
mostCurrent._vvvvvvvvvv4.Initialize(mostCurrent.activityBA,mostCurrent._vvvvvvvvvv2,"Pager");
 //BA.debugLineNum = 159;BA.debugLine="tabs.Initialize(pager)";
mostCurrent._vvvvvvvvvv5.Initialize(mostCurrent.activityBA,mostCurrent._vvvvvvvvvv4);
 //BA.debugLineNum = 160;BA.debugLine="tabs.LineHeight = 5dip";
mostCurrent._vvvvvvvvvv5.setLineHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 161;BA.debugLine="tabs.UpperCaseTitle = True";
mostCurrent._vvvvvvvvvv5.setUpperCaseTitle(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 162;BA.debugLine="tabs.TextColor = Colors.LightGray";
mostCurrent._vvvvvvvvvv5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 163;BA.debugLine="tabs.TextColorCenter = Colors.DarkGray";
mostCurrent._vvvvvvvvvv5.setTextColorCenter(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 164;BA.debugLine="tabs.LineColorCenter = Colors.DarkGray";
mostCurrent._vvvvvvvvvv5.setLineColorCenter(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 165;BA.debugLine="tabs.BackgroundColorPressed = Colors.RGB(51, 181, 229)";
mostCurrent._vvvvvvvvvv5.setBackgroundColorPressed(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (51),(int) (181),(int) (229)));
 //BA.debugLineNum = 166;BA.debugLine="pnlContent.AddView(tabs, 0, 0, FILL_PARENT, WRAP_CONTENT)";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvvvvvvv5.getObject()),(int) (0),(int) (0),_fill_parent,_wrap_content);
 //BA.debugLineNum = 168;BA.debugLine="line.Initialize(\"\")";
mostCurrent._vvvvvvvvvv6.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 169;BA.debugLine="line.Color = tabs.LineColorCenter";
mostCurrent._vvvvvvvvvv6.setColor(mostCurrent._vvvvvvvvvv5.getLineColorCenter());
 //BA.debugLineNum = 170;BA.debugLine="pnlContent.AddView(line, 0, tabs.Top + tabs.Height + 35dip, pnlContent.Width, 2dip)";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvvvvvvv6.getObject()),(int) (0),(int) (mostCurrent._vvvvvvvvvv5.getTop()+mostCurrent._vvvvvvvvvv5.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35))),mostCurrent._pnlcontent.getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
 //BA.debugLineNum = 172;BA.debugLine="pnlContent.AddView(pager, 0, line.Top + line.Height, pnlContent.Width, pnlContent.Height - (line.Top + line.Height))";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvvvvvvv4.getObject()),(int) (0),(int) (mostCurrent._vvvvvvvvvv6.getTop()+mostCurrent._vvvvvvvvvv6.getHeight()),mostCurrent._pnlcontent.getWidth(),(int) (mostCurrent._pnlcontent.getHeight()-(mostCurrent._vvvvvvvvvv6.getTop()+mostCurrent._vvvvvvvvvv6.getHeight())));
 //BA.debugLineNum = 174;BA.debugLine="lblDeveloper.Text = \"Developed By: \" & AppPublisher";
mostCurrent._lbldeveloper.setText((Object)("Developed By: "+_vvvv2));
 //BA.debugLineNum = 175;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 194;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean 'Return True to consume the event";
 //BA.debugLineNum = 195;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_MENU Then AB_ItemClicked(ID_ACTION_OVERFLOW)";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MENU) { 
_ab_itemclicked(_id_action_overflow);};
 //BA.debugLineNum = 196;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 198;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 199;BA.debugLine="StateManager.SaveSettings";
mostCurrent._vvvvvvvv4._vvv4(mostCurrent.activityBA);
 //BA.debugLineNum = 200;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 177;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 178;BA.debugLine="Activity.Title = AppName";
mostCurrent._activity.setTitle((Object)(_vvv0));
 //BA.debugLineNum = 180;BA.debugLine="pagerBanner.GotoPage(iCurrentBanner, False)";
mostCurrent._vvvvvvvvv0.GotoPage(_vvvvvvvvvv7,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 181;BA.debugLine="pager.GotoPage(iCurrentPage, False)";
mostCurrent._vvvvvvvvvv4.GotoPage(_vvvvvvvvvv0,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 183;BA.debugLine="If StateManager.GetSetting2(\"FirstTime\", True) Then";
if (BA.ObjectToBoolean(mostCurrent._vvvvvvvv4._vv6(mostCurrent.activityBA,"FirstTime",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True)))) { 
 //BA.debugLineNum = 184;BA.debugLine="StartActivity(WelcomeActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvv3.getObject()));
 }else if(_vvvvvvvvv2.getGPSEnabled()==anywheresoftware.b4a.keywords.Common.False && _vvvvvvvvvvv1==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 186;BA.debugLine="IsGPSCheck = True";
_vvvvvvvvvvv1 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 187;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 188;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 189;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 190;BA.debugLine="If Dialog.Msgbox(\"သတိေပးခ်က္\", \"ပ႐ိုမိုးရွင္း ႏွင့္ ကံစမ္းမဲအစီအစဥ္မ်ားအတြက္ GPS ဖြင့္ထားေပးရန္ လိုအပ္ပါသည္။\", \"အိုေက\", \"\", \"ပယ္ဖ်က္ပါ\", Null) = DialogResponse.POSITIVE Then StartActivity(gps1.LocationSettingsIntent)";
if (mostCurrent._vvvvvvvvvvv2.MsgBox(mostCurrent.activityBA,"သတိေပးခ်က္","ပ႐ိုမိုးရွင္း ႏွင့္ ကံစမ္းမဲအစီအစဥ္မ်ားအတြက္ GPS ဖြင့္ထားေပးရန္ လိုအပ္ပါသည္။","အိုေက","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_vvvvvvvvv2.getLocationSettingsIntent()));};
 };
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv3() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
 //BA.debugLineNum = 534;BA.debugLine="Sub BalanceTransfer";
 //BA.debugLineNum = 535;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 536;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 537;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 538;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.LoadLayout(\"BalanceTransferAndTopMeUpDialog\")";
_dialoglayout = mostCurrent._vvvvvvvvvvv2.LoadLayout(mostCurrent.activityBA,"BalanceTransferAndTopMeUpDialog");
 //BA.debugLineNum = 539;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.Get(\"lblPhoneNumber\")";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 540;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Views.Get(\"edtPhoneNumber\")";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 541;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Views.Get(\"btnContactPicker\")";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 542;BA.debugLine="Dim lblAmount As Label = DialogLayout.Views.Get(\"lblAmount\")";
_lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
_lblamount.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblAmount")));
 //BA.debugLineNum = 543;BA.debugLine="Dim edtAmount As EditText = DialogLayout.Views.Get(\"edtAmount\")";
_edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtamount.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtAmount")));
 //BA.debugLineNum = 545;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtAmount.Top + edtAmount.Height + 10dip";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtamount.getTop()+_edtamount.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 547;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405","41406")) {
case 0:
 //BA.debugLineNum = 548;BA.debugLine="Case \"41405\" : sCode = \"*155*\"' Ooredoo";
mostCurrent._vvvvvvvvvvv4 = "*155*";
 break;
case 1:
 //BA.debugLineNum = 549;BA.debugLine="Case \"41406\" : sCode = \"*979*2*4*\"' Telenor";
mostCurrent._vvvvvvvvvvv4 = "*979*2*4*";
 break;
}
;
 //BA.debugLineNum = 552;BA.debugLine="lblPhoneNumber.Text = \"ေငြလက္ခံမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။\"";
_lblphonenumber.setText((Object)("ေငြလက္ခံမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 554;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17301547\")";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 556;BA.debugLine="lblAmount.Text = \"လႊဲေျပာင္းေပးလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။\"";
_lblamount.setText((Object)("လႊဲေျပာင္းေပးလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 558;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0 AND edtAmount.Text.Length > 0";
while (!(_edtphonenumber.getText().length()>0 && _edtamount.getText().length()>0)) {
 //BA.debugLineNum = 559;BA.debugLine="If DialogLayout.Show(\"ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္\", \"ေငြလႊဲပါ\", \"\", \"ပယ္ဖ်က္ပါ\", Null) = DialogResponse.CANCEL Then Return";
if (_dialoglayout.Show("ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္","ေငြလႊဲပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 560;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 561;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 562;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_edtamount.getText().length()==0) { 
 //BA.debugLineNum = 564;BA.debugLine="edtAmount.RequestFocus";
_edtamount.RequestFocus();
 //BA.debugLineNum = 565;BA.debugLine="ToastMessageShow(\"ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 568;BA.debugLine="If SIMOperator = \"41405\" Then' Ooredoo";
if ((_vvvv4).equals("41405")) { 
 //BA.debugLineNum = 569;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtAmount.Text & \"*\" & edtPhoneNumber.Text & \"*1#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtamount.getText()+"*"+_edtphonenumber.getText()+"*1#","UTF8"))));
 }else {
 //BA.debugLineNum = 571;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPhoneNumber.Text & \"*\" & edtAmount.Text & \"*1#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtphonenumber.getText()+"*"+_edtamount.getText()+"*1#","UTF8"))));
 };
 //BA.debugLineNum = 573;BA.debugLine="End Sub";
return "";
}
public static String  _banner_pagechanged(int _position) throws Exception{
 //BA.debugLineNum = 260;BA.debugLine="Sub Banner_PageChanged (Position As Int)";
 //BA.debugLineNum = 261;BA.debugLine="iCurrentBanner = Position";
_vvvvvvvvvv7 = _position;
 //BA.debugLineNum = 262;BA.debugLine="End Sub";
return "";
}
public static String  _btncontactpicker_click() throws Exception{
com.moribanxenia.contactpicker.ContactPicker _cp = null;
 //BA.debugLineNum = 521;BA.debugLine="Sub btnContactPicker_Click";
 //BA.debugLineNum = 522;BA.debugLine="Dim cp As ContactPicker";
_cp = new com.moribanxenia.contactpicker.ContactPicker();
 //BA.debugLineNum = 523;BA.debugLine="cp.Initialize(\"cp\")";
_cp.Initialize("cp");
 //BA.debugLineNum = 524;BA.debugLine="cp.Show";
_cp.Show(processBA);
 //BA.debugLineNum = 525;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv0(String[] _phonenumber) throws Exception{
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
int _i = 0;
 //BA.debugLineNum = 891;BA.debugLine="Sub Call(PhoneNumber() As String)";
 //BA.debugLineNum = 892;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 893;BA.debugLine="Dim iResult As Int = 0";
_iresult = (int) (0);
 //BA.debugLineNum = 895;BA.debugLine="If PhoneNumber.Length > 1 Then";
if (_phonenumber.length>1) { 
 //BA.debugLineNum = 896;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 897;BA.debugLine="Dialog.Options.Dimensions.Height = (PhoneNumber.Length * 60dip) + ((PhoneNumber.Length - 1) * 1dip)";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Height = (int) ((_phonenumber.length*anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)))+((_phonenumber.length-1)*anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 898;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 899;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 900;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.LoadLayout(\"ListDialog\")";
_dialoglayout = mostCurrent._vvvvvvvvvvv2.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 901;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get(\"lvwList\")";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 903;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 904;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravity.CENTER_VERTICAL";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 905;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Colors.DarkGray";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 907;BA.debugLine="bd = xml.GetDrawable(\"17301645\")";
_bd.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._vvvvvvvvvvv5.GetDrawable("17301645")));
 //BA.debugLineNum = 909;BA.debugLine="For i = 0 To PhoneNumber.Length - 1";
{
final int step813 = 1;
final int limit813 = (int) (_phonenumber.length-1);
for (_i = (int) (0); (step813 > 0 && _i <= limit813) || (step813 < 0 && _i >= limit813); _i = ((int)(0 + _i + step813))) {
 //BA.debugLineNum = 910;BA.debugLine="lvwList.AddTwoLinesAndBitmap(PhoneNumber(i), \"\", bd.Bitmap)";
_lvwlist.AddTwoLinesAndBitmap(_phonenumber[_i],"",_bd.getBitmap());
 }
};
 //BA.debugLineNum = 913;BA.debugLine="iResult = DialogLayout.Show(\"ေခၚဆိုမည့္ဖုန္းနံပါတ္ေရြးခ်ယ္ရန္\", \"\", \"\", \"ပယ္ဖ်က္ပါ\", Null)";
_iresult = _dialoglayout.Show("ေခၚဆိုမည့္ဖုန္းနံပါတ္ေရြးခ်ယ္ရန္","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 914;BA.debugLine="If iResult = DialogResponse.CANCEL Then Return";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 };
 //BA.debugLineNum = 917;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(PhoneNumber(iResult), \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(_phonenumber[_iresult],"UTF8"))));
 //BA.debugLineNum = 918;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvv1() throws Exception{
 //BA.debugLineNum = 649;BA.debugLine="Sub CallCenter";
 //BA.debugLineNum = 650;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41404","41405","41406")) {
case 0:
case 1:
 //BA.debugLineNum = 651;BA.debugLine="Case \"41401\", \"41404\" : Call(Array As String(\"106\"))' MPT, MPT CDMA";
_vvvvvvvvvvv0(new String[]{"106"});
 break;
case 2:
 //BA.debugLineNum = 652;BA.debugLine="Case \"41405\" : Call(Array As String(\"234\", \"09970000234\"))' Ooredoo";
_vvvvvvvvvvv0(new String[]{"234","09970000234"});
 break;
case 3:
 //BA.debugLineNum = 653;BA.debugLine="Case \"41406\" : Call(Array As String(\"979\", \"09790097900\"))' Telenor";
_vvvvvvvvvvv0(new String[]{"979","09790097900"});
 break;
default:
 //BA.debugLineNum = 654;BA.debugLine="Case Else : Call(Array As String(\"1212\"))' MECTel";
_vvvvvvvvvvv0(new String[]{"1212"});
 break;
}
;
 //BA.debugLineNum = 656;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvv2() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 778;BA.debugLine="Sub CallForwarding";
 //BA.debugLineNum = 779;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 781;BA.debugLine="sName = \"Call Forwarding\"";
_sname = "Call Forwarding";
 //BA.debugLineNum = 782;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံေခၚဆိုေသာ အဝင္ဖုန္းမ်ားကို မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားသည့္ အျခားဖုန္းနံပါတ္တစ္ခုသို႔ လႊဲေျပာင္းေပးႏိုင္ပါသည္။<br>\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံေခၚဆိုေသာ အဝင္ဖုန္းမ်ားကို မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားသည့္ အျခားဖုန္းနံပါတ္တစ္ခုသို႔ လႊဲေျပာင္းေပးႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 783;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41404")) {
case 0:
 //BA.debugLineNum = 785;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။\"";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 786;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 787;BA.debugLine="sSubscription = \"CF\"";
_ssubscription = "CF";
 //BA.debugLineNum = 788;BA.debugLine="sUnsubscription = \"CF OFF\"";
_sunsubscription = "CF OFF";
 break;
case 1:
 //BA.debugLineNum = 790;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၉၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၉၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 791;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 792;BA.debugLine="sSubscription = \"Orderdata CF\"";
_ssubscription = "Orderdata CF";
 //BA.debugLineNum = 793;BA.debugLine="sUnsubscription = \"Cancel CF\"";
_sunsubscription = "Cancel CF";
 break;
}
;
 //BA.debugLineNum = 796;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription)";
_vvvvvvvvvvvv4(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 797;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvv5() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
 //BA.debugLineNum = 611;BA.debugLine="Sub CallMeBack";
 //BA.debugLineNum = 612;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 613;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 614;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 615;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.LoadLayout(\"CallMeBackDialog\")";
_dialoglayout = mostCurrent._vvvvvvvvvvv2.LoadLayout(mostCurrent.activityBA,"CallMeBackDialog");
 //BA.debugLineNum = 616;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.Get(\"lblPhoneNumber\")";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 617;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Views.Get(\"edtPhoneNumber\")";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 618;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Views.Get(\"btnContactPicker\")";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 620;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtPhoneNumber.Top + edtPhoneNumber.Height + 10dip";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtphonenumber.getTop()+_edtphonenumber.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 622;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405","41406")) {
case 0:
 //BA.debugLineNum = 623;BA.debugLine="Case \"41405\" : sCode = \"*122*\"' Ooredoo";
mostCurrent._vvvvvvvvvvv4 = "*122*";
 break;
case 1:
 //BA.debugLineNum = 624;BA.debugLine="Case \"41406\" : sCode = \"*979*3*2*\"' Telenor";
mostCurrent._vvvvvvvvvvv4 = "*979*3*2*";
 break;
}
;
 //BA.debugLineNum = 627;BA.debugLine="lblPhoneNumber.Text = \"ျပန္ေခၚေစလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။\"";
_lblphonenumber.setText((Object)("ျပန္ေခၚေစလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 629;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17301547\")";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 631;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0";
while (!(_edtphonenumber.getText().length()>0)) {
 //BA.debugLineNum = 632;BA.debugLine="If DialogLayout.Show(\"မိမိဖုန္းအားျပန္ေခၚေပးပါရန္\", \"ပို႔ပါ\", \"\", \"ပယ္ဖ်က္ပါ\", Null) = DialogResponse.CANCEL Then Return";
if (_dialoglayout.Show("မိမိဖုန္းအားျပန္ေခၚေပးပါရန္","ပို႔ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 633;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 634;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 635;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 638;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPhoneNumber.Text & \"#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtphonenumber.getText()+"#","UTF8"))));
 //BA.debugLineNum = 639;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvv6() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 799;BA.debugLine="Sub CallWaiting";
 //BA.debugLineNum = 800;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 802;BA.debugLine="sName = \"Call Waiting\"";
_sname = "Call Waiting";
 //BA.debugLineNum = 803;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းေျပာဆိုေနစဥ္အတြင္း ထပ္မံေရာက္ရွိလာေသာ အဝင္ဖုန္းကို လက္ခံေျပာဆိုႏိုင္ေစၿပီး၊ လက္ရွိေျပာဆိုေနမႈကို ေစာင့္ဆိုင္းခိုင္းထားႏိုင္ပါသည္။ ဖုန္းေခၚဆိုမႈႏွစ္ခုအၾကားတြင္လည္း ဖုန္းခ်စရာမလိုပဲ အျပန္အလွန္ေျပာဆိုႏုိင္ပါသည္။<br>\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းေျပာဆိုေနစဥ္အတြင္း ထပ္မံေရာက္ရွိလာေသာ အဝင္ဖုန္းကို လက္ခံေျပာဆိုႏိုင္ေစၿပီး၊ လက္ရွိေျပာဆိုေနမႈကို ေစာင့္ဆိုင္းခိုင္းထားႏိုင္ပါသည္။ ဖုန္းေခၚဆိုမႈႏွစ္ခုအၾကားတြင္လည္း ဖုန္းခ်စရာမလိုပဲ အျပန္အလွန္ေျပာဆိုႏုိင္ပါသည္။<br>";
 //BA.debugLineNum = 804;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41404")) {
case 0:
 //BA.debugLineNum = 806;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။\"";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 807;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 808;BA.debugLine="sSubscription = \"CW\"";
_ssubscription = "CW";
 //BA.debugLineNum = 809;BA.debugLine="sUnsubscription = \"CW OFF\"";
_sunsubscription = "CW OFF";
 break;
case 1:
 //BA.debugLineNum = 811;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 812;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 813;BA.debugLine="sSubscription = \"Orderdata CW\"";
_ssubscription = "Orderdata CW";
 //BA.debugLineNum = 814;BA.debugLine="sUnsubscription = \"Cancel CW\"";
_sunsubscription = "Cancel CW";
 break;
}
;
 //BA.debugLineNum = 817;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription)";
_vvvvvvvvvvvv4(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 818;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvv7() throws Exception{
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
 //BA.debugLineNum = 408;BA.debugLine="Sub CheckBalance";
 //BA.debugLineNum = 409;BA.debugLine="Dim iResult As Int = 0";
_iresult = (int) (0);
 //BA.debugLineNum = 411;BA.debugLine="sCode = \"\"";
mostCurrent._vvvvvvvvvvv4 = "";
 //BA.debugLineNum = 413;BA.debugLine="If SIMOperator = \"41401\" OR SIMOperator = \"41404\" OR SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then";
if ((_vvvv4).equals("41401") || (_vvvv4).equals("41404") || (_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
 //BA.debugLineNum = 414;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 415;BA.debugLine="Dialog.Options.Dimensions.Height = 121dip";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (121));
 //BA.debugLineNum = 416;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 417;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 418;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.LoadLayout(\"ListDialog\")";
_dialoglayout = mostCurrent._vvvvvvvvvvv2.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 419;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get(\"lvwList\")";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 421;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 422;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Typeface = SmartZawgyi";
_lvwlist.getTwoLinesAndBitmap().Label.setTypeface((android.graphics.Typeface)(_vvvv3.getObject()));
 //BA.debugLineNum = 423;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravity.CENTER_VERTICAL";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 424;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Colors.DarkGray";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 426;BA.debugLine="lvwList.AddTwoLinesAndBitmap(\"ဖုန္းလက္က်န္ေငြ\", \"\", LoadBitmap(File.DirAssets, \"balance.png\"))";
_lvwlist.AddTwoLinesAndBitmap("ဖုန္းလက္က်န္ေငြ","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"balance.png").getObject()));
 //BA.debugLineNum = 427;BA.debugLine="lvwList.AddTwoLinesAndBitmap(\"အင္တာနက္ႏွင့္အပိုဆုမ်ား\", \"\", LoadBitmap(File.DirAssets, \"bonus.png\"))";
_lvwlist.AddTwoLinesAndBitmap("အင္တာနက္ႏွင့္အပိုဆုမ်ား","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bonus.png").getObject()));
 //BA.debugLineNum = 429;BA.debugLine="iResult = DialogLayout.Show(\"ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္\", \"\", \"\", \"ပယ္ဖ်က္ပါ\", Null)";
_iresult = _dialoglayout.Show("ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 430;BA.debugLine="If iResult = DialogResponse.CANCEL Then Return";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 };
 //BA.debugLineNum = 432;BA.debugLine="If iResult = 0 Then";
if (_iresult==0) { 
 //BA.debugLineNum = 433;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41405","41406","41404")) {
case 0:
case 1:
case 2:
 //BA.debugLineNum = 434;BA.debugLine="Case \"41401\", \"41405\", \"41406\" : sCode = \"*124#\"' MPT, Ooredoo, Telenor";
mostCurrent._vvvvvvvvvvv4 = "*124#";
 break;
case 3:
 //BA.debugLineNum = 435;BA.debugLine="Case \"41404\" : sCode = \"*162\"' MPT CDMA";
mostCurrent._vvvvvvvvvvv4 = "*162";
 break;
default:
 //BA.debugLineNum = 436;BA.debugLine="Case Else : sCode = \"*123#\"' MECTel";
mostCurrent._vvvvvvvvvvv4 = "*123#";
 break;
}
;
 }else {
 //BA.debugLineNum = 439;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41404","41405","41406")) {
case 0:
case 1:
 //BA.debugLineNum = 440;BA.debugLine="Case \"41401\", \"41404\" : sPhoneNumber = \"1331\" : sSMSBody = \"QER\"' MPT, MPT CDMA";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 440;BA.debugLine="Case \"41401\", \"41404\" : sPhoneNumber = \"1331\" : sSMSBody = \"QER\"' MPT, MPT CDMA";
mostCurrent._vvvvvvvvvvvv0 = "QER";
 break;
case 2:
 //BA.debugLineNum = 441;BA.debugLine="Case \"41405\" : sPhoneNumber = \"2230\" : sSMSBody = \"b\"' Ooredoo";
mostCurrent._vvvvvvvvvvvv3 = "2230";
 //BA.debugLineNum = 441;BA.debugLine="Case \"41405\" : sPhoneNumber = \"2230\" : sSMSBody = \"b\"' Ooredoo";
mostCurrent._vvvvvvvvvvvv0 = "b";
 break;
case 3:
 //BA.debugLineNum = 442;BA.debugLine="Case \"41406\" : sCode = \"*124*1#\"' Telenor";
mostCurrent._vvvvvvvvvvv4 = "*124*1#";
 break;
}
;
 };
 //BA.debugLineNum = 445;BA.debugLine="If sCode = \"\" Then";
if ((mostCurrent._vvvvvvvvvvv4).equals("")) { 
 //BA.debugLineNum = 446;BA.debugLine="ps.Send(sPhoneNumber, sSMSBody)";
mostCurrent._vvvvvvvvvvvvv1.Send(mostCurrent._vvvvvvvvvvvv3,mostCurrent._vvvvvvvvvvvv0);
 }else {
 //BA.debugLineNum = 448;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4,"UTF8"))));
 };
 //BA.debugLineNum = 450;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvv2() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 763;BA.debugLine="Sub CLIR";
 //BA.debugLineNum = 764;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 766;BA.debugLine="sName = \"CLIR\"";
_sname = "CLIR";
 //BA.debugLineNum = 767;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြေခၚဆိုမည့္ အျခားဖုန္းမ်ားတြင္ ေခၚဆိုသူနံပါတ္ေဖာ္ျပျခင္းကို ေရွာင္ရွားလိုပါက အသံုးျပဳႏိုင္သည္။ သတိျပဳရန္မွာ ေအာ္ပေရတာတူ ဖုန္းအခ်င္းခ်င္းသာ အျပည့္အဝအလုပ္လုပ္ေဆာင္ပါမည္။<br>\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြေခၚဆိုမည့္ အျခားဖုန္းမ်ားတြင္ ေခၚဆိုသူနံပါတ္ေဖာ္ျပျခင္းကို ေရွာင္ရွားလိုပါက အသံုးျပဳႏိုင္သည္။ သတိျပဳရန္မွာ ေအာ္ပေရတာတူ ဖုန္းအခ်င္းခ်င္းသာ အျပည့္အဝအလုပ္လုပ္ေဆာင္ပါမည္။<br>";
 //BA.debugLineNum = 768;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405")) {
case 0:
 //BA.debugLineNum = 770;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၂၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၂၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 771;BA.debugLine="sSubscription = \"*311#\"";
_ssubscription = "*311#";
 //BA.debugLineNum = 772;BA.debugLine="sUnsubscription = \"*311*0#\"";
_sunsubscription = "*311*0#";
 break;
}
;
 //BA.debugLineNum = 775;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription)";
_vvvvvvvvvvvv4(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 776;BA.debugLine="End Sub";
return "";
}
public static String  _cp_result(boolean _success,String _displayname,String _phonenumber,int _phonetype) throws Exception{
 //BA.debugLineNum = 527;BA.debugLine="Sub cp_Result (Success As Boolean, DisplayName As String, PhoneNumber As String, PhoneType As Int)";
 //BA.debugLineNum = 528;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 529;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").RequestFocus";
mostCurrent._vvvvvvvvvvv2.getViews().EditText("edtPhoneNumber").RequestFocus();
 //BA.debugLineNum = 530;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Text = PhoneNumber";
mostCurrent._vvvvvvvvvvv2.getViews().EditText("edtPhoneNumber").setText((Object)(_phonenumber));
 };
 //BA.debugLineNum = 532;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _vvvvvvvvvv3(int _paneltype) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwhome = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwplansandpackages = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwvas = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwstores = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwabout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblappname = null;
anywheresoftware.b4a.objects.LabelWrapper _lblappversion = null;
 //BA.debugLineNum = 264;BA.debugLine="Sub CreatePanel(PanelType As Int) As Panel";
 //BA.debugLineNum = 265;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 266;BA.debugLine="Dim lvwHome, lvwPlansAndPackages, lvwVAS, lvwStores, lvwAbout As ListView";
_lvwhome = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwplansandpackages = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwvas = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwstores = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwabout = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 267;BA.debugLine="Dim lblAppName, lblAppVersion As Label";
_lblappname = new anywheresoftware.b4a.objects.LabelWrapper();
_lblappversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 269;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 270;BA.debugLine="Select PanelType";
switch (BA.switchObjectToInt(_paneltype,_type_home,_type_plansandpackages,_type_vas,_type_stores,_type_about)) {
case 0:
 //BA.debugLineNum = 272;BA.debugLine="lvwHome.Initialize(\"lvwHome\")";
_lvwhome.Initialize(mostCurrent.activityBA,"lvwHome");
 //BA.debugLineNum = 273;BA.debugLine="lvwHome.TwoLinesAndBitmap.Label.TextColor = Colors.DarkGray";
_lvwhome.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 274;BA.debugLine="lvwHome.TwoLinesAndBitmap.SecondLabel.Typeface = SmartZawgyi";
_lvwhome.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvvv3.getObject()));
 //BA.debugLineNum = 275;BA.debugLine="If SIMOperator = \"41401\" OR SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwHome.AddTwoLinesAndBitmap2(\"What's My Number?\", \"မိမိဖုန္းနံပါတ္ကိုစစ္ေဆးရန္\", LoadBitmap(File.DirAssets, \"whatismynumber.png\"), 1)";
if ((_vvvv4).equals("41401") || (_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwhome.AddTwoLinesAndBitmap2("What's My Number?","မိမိဖုန္းနံပါတ္ကိုစစ္ေဆးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"whatismynumber.png").getObject()),(Object)(1));};
 //BA.debugLineNum = 276;BA.debugLine="lvwHome.AddTwoLinesAndBitmap2(\"Check Balance\", \"ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္\", LoadBitmap(File.DirAssets, \"checkbalance.png\"), 2)";
_lvwhome.AddTwoLinesAndBitmap2("Check Balance","ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"checkbalance.png").getObject()),(Object)(2));
 //BA.debugLineNum = 277;BA.debugLine="lvwHome.AddTwoLinesAndBitmap2(\"Top Up\", \"ဖုန္းေငြျဖည့္ရန္\", LoadBitmap(File.DirAssets, \"topup.png\"), 3)";
_lvwhome.AddTwoLinesAndBitmap2("Top Up","ဖုန္းေငြျဖည့္ရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"topup.png").getObject()),(Object)(3));
 //BA.debugLineNum = 278;BA.debugLine="If SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwHome.AddTwoLinesAndBitmap2(\"Balance Transfer\", \"ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္\", LoadBitmap(File.DirAssets, \"balancetransfer.png\"), 4)";
if ((_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwhome.AddTwoLinesAndBitmap2("Balance Transfer","ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"balancetransfer.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 279;BA.debugLine="If SIMOperator = \"41405\" Then lvwHome.AddTwoLinesAndBitmap2(\"Top Me Up\", \"မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္\", LoadBitmap(File.DirAssets, \"topmeup.png\"), 5)";
if ((_vvvv4).equals("41405")) { 
_lvwhome.AddTwoLinesAndBitmap2("Top Me Up","မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"topmeup.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 280;BA.debugLine="If SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwHome.AddTwoLinesAndBitmap2(\"Call Me Back\", \"မိမိဖုန္းအားျပန္ေခၚေပးပါရန္\", LoadBitmap(File.DirAssets, \"callmeback.png\"), 6)";
if ((_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwhome.AddTwoLinesAndBitmap2("Call Me Back","မိမိဖုန္းအားျပန္ေခၚေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"callmeback.png").getObject()),(Object)(6));};
 //BA.debugLineNum = 281;BA.debugLine="If SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwHome.AddTwoLinesAndBitmap2(\"USSD\", \"ဝန္ေဆာင္မႈမ်ားကိုစီမံရန္\", LoadBitmap(File.DirAssets, \"ussd.png\"), 7)";
if ((_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwhome.AddTwoLinesAndBitmap2("USSD","ဝန္ေဆာင္မႈမ်ားကိုစီမံရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ussd.png").getObject()),(Object)(7));};
 //BA.debugLineNum = 282;BA.debugLine="lvwHome.AddTwoLinesAndBitmap2(\"Call Center\", \"အေထြေထြဝန္ေဆာင္မႈမ်ားကိုစံုစမ္းေမးျမန္းရန္\", LoadBitmap(File.DirAssets, \"callcenter.png\"), 8)";
_lvwhome.AddTwoLinesAndBitmap2("Call Center","အေထြေထြဝန္ေဆာင္မႈမ်ားကိုစံုစမ္းေမးျမန္းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"callcenter.png").getObject()),(Object)(8));
 //BA.debugLineNum = 283;BA.debugLine="pnl.AddView(lvwHome, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_lvwhome.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break;
case 1:
 //BA.debugLineNum = 285;BA.debugLine="lvwPlansAndPackages.Initialize(\"lvwPlansAndPackages\")";
_lvwplansandpackages.Initialize(mostCurrent.activityBA,"lvwPlansAndPackages");
 //BA.debugLineNum = 286;BA.debugLine="lvwPlansAndPackages.TwoLinesAndBitmap.Label.TextColor = Colors.DarkGray";
_lvwplansandpackages.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 287;BA.debugLine="lvwPlansAndPackages.TwoLinesAndBitmap.SecondLabel.Typeface = SmartZawgyi";
_lvwplansandpackages.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvvv3.getObject()));
 //BA.debugLineNum = 288;BA.debugLine="If SIMOperator = \"41401\" Then lvwPlansAndPackages.AddTwoLinesAndBitmap2(\"Swe Thahar Plan\", \"ေဆြသဟာအစီအစဥ္\", LoadBitmap(File.DirAssets, \"vas.png\"), 1)";
if ((_vvvv4).equals("41401")) { 
_lvwplansandpackages.AddTwoLinesAndBitmap2("Swe Thahar Plan","ေဆြသဟာအစီအစဥ္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(1));};
 //BA.debugLineNum = 289;BA.debugLine="If SIMOperator = \"41401\" Then lvwPlansAndPackages.AddTwoLinesAndBitmap2(\"Voice Packages\", \"Voice Package မ်ား\", LoadBitmap(File.DirAssets, \"vas.png\"), 2)";
if ((_vvvv4).equals("41401")) { 
_lvwplansandpackages.AddTwoLinesAndBitmap2("Voice Packages","Voice Package မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(2));};
 //BA.debugLineNum = 290;BA.debugLine="If SIMOperator = \"41401\" OR SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwPlansAndPackages.AddTwoLinesAndBitmap2(\"Internet Packages\", \"အင္တာနက္ Package မ်ား\", LoadBitmap(File.DirAssets, \"vas.png\"), 3)";
if ((_vvvv4).equals("41401") || (_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwplansandpackages.AddTwoLinesAndBitmap2("Internet Packages","အင္တာနက္ Package မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(3));};
 //BA.debugLineNum = 291;BA.debugLine="If SIMOperator <> \"41404\" AND SIMOperator <> \"41405\" Then lvwPlansAndPackages.AddTwoLinesAndBitmap2(\"Internet Service\", \"အင္တာနက္ဝန္ေဆာင္မႈ\", LoadBitmap(File.DirAssets, \"vas.png\"), 4)";
if ((_vvvv4).equals("41404") == false && (_vvvv4).equals("41405") == false) { 
_lvwplansandpackages.AddTwoLinesAndBitmap2("Internet Service","အင္တာနက္ဝန္ေဆာင္မႈ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 292;BA.debugLine="If SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwPlansAndPackages.AddTwoLinesAndBitmap2(\"Send Me APN Settings\", \"မိမိဖုန္းအား APN Setting မ်ားပို႔ေပးပါရန္\", LoadBitmap(File.DirAssets, \"settings.png\"), 5)";
if ((_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwplansandpackages.AddTwoLinesAndBitmap2("Send Me APN Settings","မိမိဖုန္းအား APN Setting မ်ားပို႔ေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"settings.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 293;BA.debugLine="pnl.AddView(lvwPlansAndPackages, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_lvwplansandpackages.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break;
case 2:
 //BA.debugLineNum = 295;BA.debugLine="lvwVAS.Initialize(\"lvwVAS\")";
_lvwvas.Initialize(mostCurrent.activityBA,"lvwVAS");
 //BA.debugLineNum = 296;BA.debugLine="lvwVAS.TwoLinesAndBitmap.Label.TextColor = Colors.DarkGray";
_lvwvas.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 297;BA.debugLine="lvwVAS.TwoLinesAndBitmap.SecondLabel.Typeface = SmartZawgyi";
_lvwvas.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvvv3.getObject()));
 //BA.debugLineNum = 298;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41404","41405","41406")) {
case 0:
case 1:
 break;
case 2:
 //BA.debugLineNum = 301;BA.debugLine="lvwVAS.AddTwoLinesAndBitmap2(\"FunTone\", \"FunTone\", LoadBitmap(File.DirAssets, \"vas.png\"), 1)";
_lvwvas.AddTwoLinesAndBitmap2("FunTone","FunTone",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(1));
 break;
case 3:
 //BA.debugLineNum = 303;BA.debugLine="lvwVAS.AddTwoLinesAndBitmap2(\"My Tune\", \"My Tune\", LoadBitmap(File.DirAssets, \"vas.png\"), 1)";
_lvwvas.AddTwoLinesAndBitmap2("My Tune","My Tune",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(1));
 break;
default:
 //BA.debugLineNum = 305;BA.debugLine="lvwVAS.AddTwoLinesAndBitmap2(\"Hello Music\", \"Hello Music\", LoadBitmap(File.DirAssets, \"vas.png\"), 1)";
_lvwvas.AddTwoLinesAndBitmap2("Hello Music","Hello Music",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(1));
 break;
}
;
 //BA.debugLineNum = 307;BA.debugLine="If SIMOperator = \"41405\" OR SIMOperator = \"41406\" Then lvwVAS.AddTwoLinesAndBitmap2(\"SMS Channels\", \"SMS Channel မ်ား\", LoadBitmap(File.DirAssets, \"vas.png\"), 2)";
if ((_vvvv4).equals("41405") || (_vvvv4).equals("41406")) { 
_lvwvas.AddTwoLinesAndBitmap2("SMS Channels","SMS Channel မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(2));};
 //BA.debugLineNum = 308;BA.debugLine="If SIMOperator = \"41405\" Then lvwVAS.AddTwoLinesAndBitmap2(\"CLIR\", \"CLIR\", LoadBitmap(File.DirAssets, \"vas.png\"), 3)";
if ((_vvvv4).equals("41405")) { 
_lvwvas.AddTwoLinesAndBitmap2("CLIR","CLIR",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(3));};
 //BA.debugLineNum = 309;BA.debugLine="If SIMOperator = \"41401\" OR SIMOperator = \"41404\" Then lvwVAS.AddTwoLinesAndBitmap2(\"Call Forwarding\", \"Call Forwarding\", LoadBitmap(File.DirAssets, \"vas.png\"), 4)";
if ((_vvvv4).equals("41401") || (_vvvv4).equals("41404")) { 
_lvwvas.AddTwoLinesAndBitmap2("Call Forwarding","Call Forwarding",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 310;BA.debugLine="If SIMOperator = \"41401\" OR SIMOperator = \"41404\" Then lvwVAS.AddTwoLinesAndBitmap2(\"Call Waiting\", \"Call Waiting\", LoadBitmap(File.DirAssets, \"vas.png\"), 5)";
if ((_vvvv4).equals("41401") || (_vvvv4).equals("41404")) { 
_lvwvas.AddTwoLinesAndBitmap2("Call Waiting","Call Waiting",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 311;BA.debugLine="If SIMOperator <> \"41404\" AND SIMOperator <> \"41405\" Then lvwVAS.AddTwoLinesAndBitmap2(\"Missed Call Alert\", \"Missed Call Alert\", LoadBitmap(File.DirAssets, \"vas.png\"), 6)";
if ((_vvvv4).equals("41404") == false && (_vvvv4).equals("41405") == false) { 
_lvwvas.AddTwoLinesAndBitmap2("Missed Call Alert","Missed Call Alert",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(6));};
 //BA.debugLineNum = 312;BA.debugLine="If SIMOperator <> \"41404\" AND SIMOperator <> \"41405\" Then lvwVAS.AddTwoLinesAndBitmap2(\"Voice Mail\", \"Voice Mail\", LoadBitmap(File.DirAssets, \"vas.png\"), 7)";
if ((_vvvv4).equals("41404") == false && (_vvvv4).equals("41405") == false) { 
_lvwvas.AddTwoLinesAndBitmap2("Voice Mail","Voice Mail",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vas.png").getObject()),(Object)(7));};
 //BA.debugLineNum = 313;BA.debugLine="pnl.AddView(lvwVAS, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_lvwvas.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break;
case 3:
 //BA.debugLineNum = 315;BA.debugLine="lvwStores.Initialize(\"lvwStores\")";
_lvwstores.Initialize(mostCurrent.activityBA,"lvwStores");
 //BA.debugLineNum = 316;BA.debugLine="lvwStores.TwoLinesLayout.ItemHeight = 121dip";
_lvwstores.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (121)));
 //BA.debugLineNum = 317;BA.debugLine="lvwStores.TwoLinesLayout.Label.Top = 15dip";
_lvwstores.getTwoLinesLayout().Label.setTop(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15)));
 //BA.debugLineNum = 318;BA.debugLine="lvwStores.TwoLinesLayout.Label.Typeface = SmartZawgyi";
_lvwstores.getTwoLinesLayout().Label.setTypeface((android.graphics.Typeface)(_vvvv3.getObject()));
 //BA.debugLineNum = 319;BA.debugLine="lvwStores.TwoLinesLayout.Label.Gravity = Gravity.CENTER_HORIZONTAL";
_lvwstores.getTwoLinesLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 320;BA.debugLine="lvwStores.TwoLinesLayout.Label.TextColor = Colors.DarkGray";
_lvwstores.getTwoLinesLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 321;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Top = lvwStores.TwoLinesLayout.Label.Top + lvwStores.TwoLinesLayout.Label.Height + 2dip";
_lvwstores.getTwoLinesLayout().SecondLabel.setTop((int) (_lvwstores.getTwoLinesLayout().Label.getTop()+_lvwstores.getTwoLinesLayout().Label.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 322;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Height = 59dip";
_lvwstores.getTwoLinesLayout().SecondLabel.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (59)));
 //BA.debugLineNum = 323;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Typeface = SmartZawgyi";
_lvwstores.getTwoLinesLayout().SecondLabel.setTypeface((android.graphics.Typeface)(_vvvv3.getObject()));
 //BA.debugLineNum = 324;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Gravity = Gravity.CENTER_HORIZONTAL";
_lvwstores.getTwoLinesLayout().SecondLabel.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 325;BA.debugLine="lvwStores.AddTwoLines2(\"မေကြးေရွာ့ပင္းေမာ(လ္)\", \"အမွတ္(၈)၊ ဗိုလ္ခ်ဳပ္လမ္း၊ \" & CRLF & \"ပြဲႀကိဳရပ္ကြက္၊ မေကြးၿမိဳ႕။\" & CRLF & \"ဖုန္း ၀၆၃-၂၆၇၁၁၊ ၀၆၃-၂၇၂၂၃။\", 1)";
_lvwstores.AddTwoLines2("မေကြးေရွာ့ပင္းေမာ(လ္)","အမွတ္(၈)၊ ဗိုလ္ခ်ဳပ္လမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ပြဲႀကိဳရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၆၇၁၁၊ ၀၆၃-၂၇၂၂၃။",(Object)(1));
 //BA.debugLineNum = 326;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မေကြး-၁)\", \"အမွတ္(၁၀)၊ ျပည္ေတာ္သာလမ္း၊ \" & CRLF & \"ေဈးလယ္စိုးရပ္ကြက္၊ မေကြးၿမိဳ႕။\" & CRLF & \"ဖုန္း ၀၆၃-၂၅၄၅၂၊ ၀၆၃-၂၆၁၇၁။\", 2)";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မေကြး-၁)","အမွတ္(၁၀)၊ ျပည္ေတာ္သာလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ေဈးလယ္စိုးရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၅၄၅၂၊ ၀၆၃-၂၆၁၇၁။",(Object)(2));
 //BA.debugLineNum = 327;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မေကြး-၂)\", \"အမွတ္(၃၆)၊ ဗိုလ္ခ်ဳပ္လမ္း ႏွင့္ မဲထီးလမ္းေထာင့္၊ \" & CRLF & \"ရြာသစ္ရပ္ကြက္၊ မေကြးၿမိဳ႕။\" & CRLF & \"ဖုန္း ၀၆၃-၂၃၆၉၂၊ ၀၆၃-၂၈၁၉၃။\", 3)";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မေကြး-၂)","အမွတ္(၃၆)၊ ဗိုလ္ခ်ဳပ္လမ္း ႏွင့္ မဲထီးလမ္းေထာင့္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ရြာသစ္ရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၃၆၉၂၊ ၀၆၃-၂၈၁၉၃။",(Object)(3));
 //BA.debugLineNum = 328;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မင္းဘူး)\", \"အမွတ္(၈၁၂)၊ မင္းဘူး-စကုလမ္း၊ \" & CRLF & \"အမွတ္(၃)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။\" & CRLF & \"ဖုန္း ၀၆၅-၂၁၅၄၇။\", 4)";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မင္းဘူး)","အမွတ္(၈၁၂)၊ မင္းဘူး-စကုလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၃)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၅-၂၁၅၄၇။",(Object)(4));
 //BA.debugLineNum = 329;BA.debugLine="pnl.AddView(lvwStores, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_lvwstores.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break;
case 4:
 //BA.debugLineNum = 331;BA.debugLine="ivAppIcon.Initialize(\"\")";
mostCurrent._vvvvvvvvvvvvv3.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 332;BA.debugLine="ivAppIcon.Bitmap = AppIcon.Bitmap";
mostCurrent._vvvvvvvvvvvvv3.setBitmap(mostCurrent._vvvvvvvvv5.getBitmap());
 //BA.debugLineNum = 333;BA.debugLine="ivAppIcon.Gravity = Gravity.FILL";
mostCurrent._vvvvvvvvvvvvv3.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 334;BA.debugLine="pnl.AddView(ivAppIcon, 50%x - 50dip, 10dip, 100dip, 100dip)";
_pnl.AddView((android.view.View)(mostCurrent._vvvvvvvvvvvvv3.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 336;BA.debugLine="lblAppName.Initialize(\"\")";
_lblappname.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 337;BA.debugLine="lblAppName.Text = AppName";
_lblappname.setText((Object)(_vvv0));
 //BA.debugLineNum = 338;BA.debugLine="lblAppName.Gravity = Gravity.CENTER";
_lblappname.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 339;BA.debugLine="lblAppName.TextSize = 19.5";
_lblappname.setTextSize((float) (19.5));
 //BA.debugLineNum = 340;BA.debugLine="lblAppName.TextColor = Colors.DarkGray";
_lblappname.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 341;BA.debugLine="pnl.AddView(lblAppName, 0, ivAppIcon.Top + ivAppIcon.Height + 10dip, 100%x, WRAP_CONTENT)";
_pnl.AddView((android.view.View)(_lblappname.getObject()),(int) (0),(int) (mostCurrent._vvvvvvvvvvvvv3.getTop()+mostCurrent._vvvvvvvvvvvvv3.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_wrap_content);
 //BA.debugLineNum = 343;BA.debugLine="lblAppVersion.Initialize(\"\")";
_lblappversion.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 344;BA.debugLine="lblAppVersion.Text = \"Version: \" & AppVersion";
_lblappversion.setText((Object)("Version: "+_vvvv1));
 //BA.debugLineNum = 345;BA.debugLine="lblAppVersion.Gravity = Gravity.CENTER";
_lblappversion.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 346;BA.debugLine="lblAppVersion.TextSize = 16.5";
_lblappversion.setTextSize((float) (16.5));
 //BA.debugLineNum = 347;BA.debugLine="lblAppVersion.TextColor = -7829368";
_lblappversion.setTextColor((int) (-7829368));
 //BA.debugLineNum = 348;BA.debugLine="pnl.AddView(lblAppVersion, 0, lblAppName.Top + su.MeasureMultilineTextHeight(lblAppName, lblAppName.Text), 100%x, WRAP_CONTENT)";
_pnl.AddView((android.view.View)(_lblappversion.getObject()),(int) (0),(int) (_lblappname.getTop()+mostCurrent._vvvvvvvvvvv7.MeasureMultilineTextHeight((android.widget.TextView)(_lblappname.getObject()),_lblappname.getText())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_wrap_content);
 //BA.debugLineNum = 350;BA.debugLine="line.Initialize(\"\")";
mostCurrent._vvvvvvvvvv6.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 351;BA.debugLine="line.Color = Colors.LightGray";
mostCurrent._vvvvvvvvvv6.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 352;BA.debugLine="pnl.AddView(line, 0, lblAppVersion.Top + su.MeasureMultilineTextHeight(lblAppVersion, lblAppVersion.Text) + 10dip, FILL_PARENT, 1dip)";
_pnl.AddView((android.view.View)(mostCurrent._vvvvvvvvvv6.getObject()),(int) (0),(int) (_lblappversion.getTop()+mostCurrent._vvvvvvvvvvv7.MeasureMultilineTextHeight((android.widget.TextView)(_lblappversion.getObject()),_lblappversion.getText())+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),_fill_parent,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)));
 //BA.debugLineNum = 354;BA.debugLine="lvwAbout.Initialize(\"lvwAbout\")";
_lvwabout.Initialize(mostCurrent.activityBA,"lvwAbout");
 //BA.debugLineNum = 355;BA.debugLine="lvwAbout.SingleLineLayout.Label.Gravity = Gravity.CENTER";
_lvwabout.getSingleLineLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 356;BA.debugLine="lvwAbout.SingleLineLayout.Label.TextSize = lblAppVersion.TextSize";
_lvwabout.getSingleLineLayout().Label.setTextSize(_lblappversion.getTextSize());
 //BA.debugLineNum = 357;BA.debugLine="lvwAbout.SingleLineLayout.Label.TextColor = Colors.DarkGray";
_lvwabout.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 358;BA.debugLine="lvwAbout.AddSingleLine2(\"Developer: \" & AppPublisher, 1)";
_lvwabout.AddSingleLine2("Developer: "+_vvvv2,(Object)(1));
 //BA.debugLineNum = 359;BA.debugLine="lvwAbout.AddSingleLine2(\"Contact: 09-450049060, 09-43165226\", 2)";
_lvwabout.AddSingleLine2("Contact: 09-450049060, 09-43165226",(Object)(2));
 //BA.debugLineNum = 360;BA.debugLine="pnl.AddView(lvwAbout, 0, line.Top + line.Height, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_lvwabout.getObject()),(int) (0),(int) (mostCurrent._vvvvvvvvvv6.getTop()+mostCurrent._vvvvvvvvvv6.getHeight()),_fill_parent,_fill_parent);
 break;
}
;
 //BA.debugLineNum = 363;BA.debugLine="Return pnl";
if (true) return _pnl;
 //BA.debugLineNum = 364;BA.debugLine="End Sub";
return null;
}
public static String  _vvvvvvvvvvvvv4() throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 886;BA.debugLine="Sub GetPackageName As String";
 //BA.debugLineNum = 887;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 888;BA.debugLine="Return r.GetStaticField(\"anywheresoftware.b4a.BA\", \"packageName\")";
if (true) return BA.ObjectToString(_r.GetStaticField("anywheresoftware.b4a.BA","packageName"));
 //BA.debugLineNum = 889;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
packagesactivity._process_globals();
smschannelsactivity._process_globals();
notification._process_globals();
receiver._process_globals();
welcomeactivity._process_globals();
statemanager._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 50;BA.debugLine="Dim AppIcon As BitmapDrawable = pm.GetApplicationIcon(GetPackageName)";
mostCurrent._vvvvvvvvv5 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._vvvvvvvvv5.setObject((android.graphics.drawable.BitmapDrawable)(_vvvvvvvvvvvvv5.GetApplicationIcon(_vvvvvvvvvvvvv4())));
 //BA.debugLineNum = 52;BA.debugLine="Dim pi As ParseInstallation";
mostCurrent._vvvvvvvvvvvvv6 = new anywheresoftware.b4a.objects.ParseObjectWrapper.ParseInstallationWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Dim IsGPSCheck As Boolean = False";
_vvvvvvvvvvv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 55;BA.debugLine="Dim xml As XmlLayoutBuilder";
mostCurrent._vvvvvvvvvvv5 = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 57;BA.debugLine="Dim ab As AHActionBar";
mostCurrent._vvvvvvvv6 = new de.amberhome.SimpleActionBar.ActionBarWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Dim overflowIcon As BitmapDrawable";
mostCurrent._vvvvvvvvv4 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 59;BA.debugLine="Dim menu As AHPopupMenu";
mostCurrent._vvvvvvvv5 = new de.amberhome.quickaction.ICSMenu();
 //BA.debugLineNum = 61;BA.debugLine="Dim SIMOperatorName As Map = CreateMap(\"41401\": \"MPT\", \"41404\": \"MPT CDMA\", \"41403\": \"MECTel\", \"41405\": \"Ooredoo\", \"41406\": \"Telenor\")";
mostCurrent._vvvvvvvv7 = new anywheresoftware.b4a.objects.collections.Map();
mostCurrent._vvvvvvvv7 = anywheresoftware.b4a.keywords.Common.createMap(new Object[] {(Object)("41401"),(Object)("MPT"),(Object)("41404"),(Object)("MPT CDMA"),(Object)("41403"),(Object)("MECTel"),(Object)("41405"),(Object)("Ooredoo"),(Object)("41406"),(Object)("Telenor")});
 //BA.debugLineNum = 63;BA.debugLine="Dim pnlBanner, pnlContent As Panel";
mostCurrent._pnlbanner = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pnlcontent = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Dim asBanner() As String = Array As String(\"banner.jpg\", \"banner2.jpg\", \"banner3.jpg\", \"banner4.jpg\")";
mostCurrent._vvvvvvvvv7 = new String[]{"banner.jpg","banner2.jpg","banner3.jpg","banner4.jpg"};
 //BA.debugLineNum = 66;BA.debugLine="Dim containerBanner, container As AHPageContainer";
mostCurrent._vvvvvvvvv6 = new de.amberhome.viewpager.AHPageContainer();
mostCurrent._vvvvvvvvvv2 = new de.amberhome.viewpager.AHPageContainer();
 //BA.debugLineNum = 67;BA.debugLine="Dim pagerBanner, pager As AHViewPager";
mostCurrent._vvvvvvvvv0 = new de.amberhome.viewpager.AHViewPager();
mostCurrent._vvvvvvvvvv4 = new de.amberhome.viewpager.AHViewPager();
 //BA.debugLineNum = 68;BA.debugLine="Dim tabs As AHViewPagerTabs";
mostCurrent._vvvvvvvvvv5 = new de.amberhome.viewpager.AHViewPagerTabs();
 //BA.debugLineNum = 69;BA.debugLine="Dim line As Panel";
mostCurrent._vvvvvvvvvv6 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Dim lblDeveloper As Label";
mostCurrent._lbldeveloper = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Dim ivAppIcon As ImageView";
mostCurrent._vvvvvvvvvvvvv3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Dim Dialog As DialogView";
mostCurrent._vvvvvvvvvvv2 = new com.datasteam.b4a.xtraviews.DialogView();
 //BA.debugLineNum = 77;BA.debugLine="Dim sCode, sPhoneNumber, sSMSBody As String";
mostCurrent._vvvvvvvvvvv4 = "";
mostCurrent._vvvvvvvvvvvv3 = "";
mostCurrent._vvvvvvvvvvvv0 = "";
 //BA.debugLineNum = 78;BA.debugLine="Dim su As StringUtils";
mostCurrent._vvvvvvvvvvv7 = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 79;BA.debugLine="Dim pc As PhoneCalls";
mostCurrent._vvvvvvvvvvv6 = new anywheresoftware.b4a.phone.Phone.PhoneCalls();
 //BA.debugLineNum = 80;BA.debugLine="Dim ps As PhoneSms";
mostCurrent._vvvvvvvvvvvvv1 = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
public static String  _gps_locationchanged(anywheresoftware.b4a.gps.LocationWrapper _location1) throws Exception{
anywheresoftware.b4a.objects.ParseObjectWrapper.ParseGeoPointWrapper _pgp = null;
 //BA.debugLineNum = 232;BA.debugLine="Sub GPS_LocationChanged (Location1 As Location)";
 //BA.debugLineNum = 233;BA.debugLine="gps1.Stop";
_vvvvvvvvv2.Stop();
 //BA.debugLineNum = 234;BA.debugLine="Dim pgp As ParseGeoPoint";
_pgp = new anywheresoftware.b4a.objects.ParseObjectWrapper.ParseGeoPointWrapper();
 //BA.debugLineNum = 235;BA.debugLine="pi = pi.GetCurrentInstallation";
mostCurrent._vvvvvvvvvvvvv6 = mostCurrent._vvvvvvvvvvvvv6.GetCurrentInstallation();
 //BA.debugLineNum = 236;BA.debugLine="pgp.Initialize(Location1.Latitude, Location1.Longitude)";
_pgp.Initialize(_location1.getLatitude(),_location1.getLongitude());
 //BA.debugLineNum = 237;BA.debugLine="pi.Put(\"Location\", pgp)";
mostCurrent._vvvvvvvvvvvvv6.Put("Location",(Object)(_pgp.getObject()));
 //BA.debugLineNum = 238;BA.debugLine="pi.SaveEventually(\"Parse\", 2)";
mostCurrent._vvvvvvvvvvvvv6.SaveEventually(processBA,"Parse",(int) (2));
 //BA.debugLineNum = 239;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvv0() throws Exception{
String _sname = "";
String _sdescription = "";
String _sactivation = "";
String _sdeactivation = "";
 //BA.debugLineNum = 689;BA.debugLine="Sub InternetService";
 //BA.debugLineNum = 690;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sActivation, sDeactivation As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_sactivation = "";
_sdeactivation = "";
 //BA.debugLineNum = 692;BA.debugLine="sName = \"အင္တာနက္ဝန္ေဆာင္မႈ\"";
_sname = "အင္တာနက္ဝန္ေဆာင္မႈ";
 //BA.debugLineNum = 693;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41406")) {
case 0:
 //BA.debugLineNum = 695;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ ၁၀၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ ေကာက္ခံသြားပါလိမ့္မည္။\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ ၁၀၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 696;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 697;BA.debugLine="sActivation = \"Orderdata service\"";
_sactivation = "Orderdata service";
 break;
case 1:
 //BA.debugLineNum = 699;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ ရပ္ဆိုင္းရန္ သို႔မဟုတ္ ျပန္လည္အသံုးျပဳရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ ရပ္ဆိုင္းရန္ သို႔မဟုတ္ ျပန္လည္အသံုးျပဳရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 700;BA.debugLine="sPhoneNumber = \"500\"";
mostCurrent._vvvvvvvvvvvv3 = "500";
 //BA.debugLineNum = 701;BA.debugLine="sActivation = \"internet on\"";
_sactivation = "internet on";
 //BA.debugLineNum = 702;BA.debugLine="sDeactivation = \"internet off\"";
_sdeactivation = "internet off";
 break;
default:
 //BA.debugLineNum = 704;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 705;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvvvvvvvv3 = "233";
 //BA.debugLineNum = 706;BA.debugLine="sActivation = \"Open EVDO\"";
_sactivation = "Open EVDO";
 break;
}
;
 //BA.debugLineNum = 709;BA.debugLine="Plan(sName, sDescription, sPhoneNumber, sActivation, sDeactivation)";
_vvvvvvvvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_sactivation,_sdeactivation);
 //BA.debugLineNum = 710;BA.debugLine="End Sub";
return "";
}
public static String  _lvwabout_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 880;BA.debugLine="Sub lvwAbout_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 881;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(2))) {
case 0:
 //BA.debugLineNum = 882;BA.debugLine="Case 2 : Call(Array As String(\"09450049060\", \"0943165226\"))";
_vvvvvvvvvvv0(new String[]{"09450049060","0943165226"});
 break;
}
;
 //BA.debugLineNum = 884;BA.debugLine="End Sub";
return "";
}
public static String  _lvwhome_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 386;BA.debugLine="Sub lvwHome_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 387;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6),(Object)(7),(Object)(8))) {
case 0:
 //BA.debugLineNum = 388;BA.debugLine="Case 1 : WhatIsMyNumber";
_vvvvvvvvvvvvvv2();
 break;
case 1:
 //BA.debugLineNum = 389;BA.debugLine="Case 2 : CheckBalance";
_vvvvvvvvvvvv7();
 break;
case 2:
 //BA.debugLineNum = 390;BA.debugLine="Case 3 : TopUp";
_vvvvvvvvvvvvvv3();
 break;
case 3:
 //BA.debugLineNum = 391;BA.debugLine="Case 4 : BalanceTransfer";
_vvvvvvvvvvv3();
 break;
case 4:
 //BA.debugLineNum = 392;BA.debugLine="Case 5 : TopMeUp";
_vvvvvvvvvvvvvv4();
 break;
case 5:
 //BA.debugLineNum = 393;BA.debugLine="Case 6 : CallMeBack";
_vvvvvvvvvvvv5();
 break;
case 6:
 //BA.debugLineNum = 394;BA.debugLine="Case 7 : USSD";
_vvvvvvvvvvvvvv5();
 break;
case 7:
 //BA.debugLineNum = 395;BA.debugLine="Case 8 : CallCenter";
_vvvvvvvvvvvv1();
 break;
}
;
 //BA.debugLineNum = 397;BA.debugLine="End Sub";
return "";
}
public static String  _lvwlist_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 658;BA.debugLine="Sub lvwList_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 659;BA.debugLine="Dialog.Dismiss(Position)";
mostCurrent._vvvvvvvvvvv2.Dismiss(_position);
 //BA.debugLineNum = 660;BA.debugLine="End Sub";
return "";
}
public static String  _lvwplansandpackages_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 662;BA.debugLine="Sub lvwPlansAndPackages_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 663;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5))) {
case 0:
 //BA.debugLineNum = 664;BA.debugLine="Case 1 : SweThaharPlan";
_vvvvvvvvvvvvvv6();
 break;
case 1:
 //BA.debugLineNum = 665;BA.debugLine="Case 2 : ShowPackages(\"Voice Packages\", \"voicePackages\")";
_vvvvvvvvvvvvvv7("Voice Packages","voicePackages");
 break;
case 2:
 //BA.debugLineNum = 666;BA.debugLine="Case 3 : ShowPackages(\"Internet Packages\", \"internetPackages\")";
_vvvvvvvvvvvvvv7("Internet Packages","internetPackages");
 break;
case 3:
 //BA.debugLineNum = 667;BA.debugLine="Case 4 : InternetService";
_vvvvvvvvvvvvv0();
 break;
case 4:
 //BA.debugLineNum = 668;BA.debugLine="Case 5 : SendMeAPNSettings";
_vvvvvvvvvvvvvv0();
 break;
}
;
 //BA.debugLineNum = 670;BA.debugLine="End Sub";
return "";
}
public static String  _lvwstores_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 871;BA.debugLine="Sub lvwStores_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 872;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4))) {
case 0:
 //BA.debugLineNum = 873;BA.debugLine="Case 1 : Call(Array As String(\"06326711\", \"06327223\"))";
_vvvvvvvvvvv0(new String[]{"06326711","06327223"});
 break;
case 1:
 //BA.debugLineNum = 874;BA.debugLine="Case 2 : Call(Array As String(\"06325452\", \"06326171\"))";
_vvvvvvvvvvv0(new String[]{"06325452","06326171"});
 break;
case 2:
 //BA.debugLineNum = 875;BA.debugLine="Case 3 : Call(Array As String(\"06323692\", \"06328193\"))";
_vvvvvvvvvvv0(new String[]{"06323692","06328193"});
 break;
case 3:
 //BA.debugLineNum = 876;BA.debugLine="Case 4 : Call(Array As String(\"06521547\"))";
_vvvvvvvvvvv0(new String[]{"06521547"});
 break;
}
;
 //BA.debugLineNum = 878;BA.debugLine="End Sub";
return "";
}
public static String  _lvwvas_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 720;BA.debugLine="Sub lvwVAS_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 721;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6),(Object)(7))) {
case 0:
 //BA.debugLineNum = 722;BA.debugLine="Case 1 : RingbackTone";
_vvvvvvvvvvvvvvv1();
 break;
case 1:
 //BA.debugLineNum = 723;BA.debugLine="Case 2 : SMSChannels";
_vvvvvvvvvvvvvvv2();
 break;
case 2:
 //BA.debugLineNum = 724;BA.debugLine="Case 3 : CLIR";
_vvvvvvvvvvvvv2();
 break;
case 3:
 //BA.debugLineNum = 725;BA.debugLine="Case 4 : CallForwarding";
_vvvvvvvvvvvv2();
 break;
case 4:
 //BA.debugLineNum = 726;BA.debugLine="Case 5 : CallWaiting";
_vvvvvvvvvvvv6();
 break;
case 5:
 //BA.debugLineNum = 727;BA.debugLine="Case 6 : MissedCallAlert";
_vvvvvvvvvvvvvvv3();
 break;
case 6:
 //BA.debugLineNum = 728;BA.debugLine="Case 7 : VoiceMail";
_vvvvvvvvvvvvvvv4();
 break;
}
;
 //BA.debugLineNum = 730;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvv3() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 820;BA.debugLine="Sub MissedCallAlert";
 //BA.debugLineNum = 821;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 823;BA.debugLine="sName = \"Missed Call Alert\"";
_sname = "Missed Call Alert";
 //BA.debugLineNum = 824;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြ၏ဖုန္း စက္ပိတ္ထားသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ ဆက္သြယ္မႈဧရိယာျပင္ပသို႔ ေရာက္ရွိေနသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ မိတ္ေဆြထံေခၚဆိုရန္ႀကိဳးစားေသာ အဝင္ဖုန္းမ်ားအေရအတြက္ကို မိတ္ေဆြ၏ဖုန္း ကြန္ရက္ေပၚသို႔ျပန္လည္ေရာက္ရွိလာေသာအခါ SMS ျဖင့္ သတိေပးခ်က္အျဖစ္ ပို႔ေပးမည္ျဖစ္သည္။<br>\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြ၏ဖုန္း စက္ပိတ္ထားသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ ဆက္သြယ္မႈဧရိယာျပင္ပသို႔ ေရာက္ရွိေနသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ မိတ္ေဆြထံေခၚဆိုရန္ႀကိဳးစားေသာ အဝင္ဖုန္းမ်ားအေရအတြက္ကို မိတ္ေဆြ၏ဖုန္း ကြန္ရက္ေပၚသို႔ျပန္လည္ေရာက္ရွိလာေသာအခါ SMS ျဖင့္ သတိေပးခ်က္အျဖစ္ ပို႔ေပးမည္ျဖစ္သည္။<br>";
 //BA.debugLineNum = 825;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41406")) {
case 0:
 //BA.debugLineNum = 827;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။\"";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 828;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 829;BA.debugLine="sSubscription = \"MCA\"";
_ssubscription = "MCA";
 //BA.debugLineNum = 830;BA.debugLine="sUnsubscription = \"MCA OFF\"";
_sunsubscription = "MCA OFF";
 break;
case 1:
 //BA.debugLineNum = 832;BA.debugLine="sDescription = sDescription & \"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။\"";
_sdescription = _sdescription+"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 833;BA.debugLine="sPhoneNumber = \"222\"";
mostCurrent._vvvvvvvvvvvv3 = "222";
 //BA.debugLineNum = 834;BA.debugLine="sSubscription = \"MCA ON\"";
_ssubscription = "MCA ON";
 //BA.debugLineNum = 835;BA.debugLine="sUnsubscription = \"MCA OFF\"";
_sunsubscription = "MCA OFF";
 break;
default:
 //BA.debugLineNum = 837;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 838;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvvvvvvvv3 = "233";
 //BA.debugLineNum = 839;BA.debugLine="sSubscription = \"Open MCA\"";
_ssubscription = "Open MCA";
 //BA.debugLineNum = 840;BA.debugLine="sUnsubscription = \"Cancel MCA\"";
_sunsubscription = "Cancel MCA";
 break;
}
;
 //BA.debugLineNum = 843;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription)";
_vvvvvvvvvvvv4(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 844;BA.debugLine="End Sub";
return "";
}
public static String  _opttopupother_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 513;BA.debugLine="Sub optTopUpOther_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 514;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 515;BA.debugLine="Dialog.Views.Label(\"lblPhoneNumber\").Visible = True";
mostCurrent._vvvvvvvvvvv2.getViews().Label("lblPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 516;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Visible = True";
mostCurrent._vvvvvvvvvvv2.getViews().EditText("edtPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 517;BA.debugLine="Dialog.Views.Button(\"btnContactPicker\").Visible = True";
mostCurrent._vvvvvvvvvvv2.getViews().Button("btnContactPicker").setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 519;BA.debugLine="End Sub";
return "";
}
public static String  _opttopupself_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 505;BA.debugLine="Sub optTopUpSelf_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 506;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 507;BA.debugLine="Dialog.Views.Label(\"lblPhoneNumber\").Visible = False";
mostCurrent._vvvvvvvvvvv2.getViews().Label("lblPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 508;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Visible = False";
mostCurrent._vvvvvvvvvvv2.getViews().EditText("edtPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 509;BA.debugLine="Dialog.Views.Button(\"btnContactPicker\").Visible = False";
mostCurrent._vvvvvvvvvvv2.getViews().Button("btnContactPicker").setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 511;BA.debugLine="End Sub";
return "";
}
public static String  _pager_pagechanged(int _position) throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _ap = null;
flm.b4a.animationplus.AnimationPlusWrapper _ap2 = null;
flm.b4a.animationplus.AnimationSet _a = null;
 //BA.debugLineNum = 366;BA.debugLine="Sub Pager_PageChanged (Position As Int)";
 //BA.debugLineNum = 367;BA.debugLine="iCurrentPage = Position";
_vvvvvvvvvv0 = _position;
 //BA.debugLineNum = 368;BA.debugLine="pager.RequestFocus";
mostCurrent._vvvvvvvvvv4.RequestFocus();
 //BA.debugLineNum = 369;BA.debugLine="If Position = 3 OR Position = 4 Then";
if (_position==3 || _position==4) { 
 //BA.debugLineNum = 370;BA.debugLine="Dim ap, ap2 As AnimationPlus";
_ap = new flm.b4a.animationplus.AnimationPlusWrapper();
_ap2 = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 371;BA.debugLine="Dim a As AnimationSet";
_a = new flm.b4a.animationplus.AnimationSet();
 //BA.debugLineNum = 372;BA.debugLine="ap.InitializeAlpha(\"\", 0, 1)";
_ap.InitializeAlpha(mostCurrent.activityBA,"",(float) (0),(float) (1));
 //BA.debugLineNum = 373;BA.debugLine="ap.StartOffset = 500";
_ap.setStartOffset((long) (500));
 //BA.debugLineNum = 374;BA.debugLine="ap.Duration = 800";
_ap.setDuration((long) (800));
 //BA.debugLineNum = 375;BA.debugLine="ap2.InitializeScaleCenter(\"\", 0, 0, 1, 1, ivAppIcon)";
_ap2.InitializeScaleCenter(mostCurrent.activityBA,"",(float) (0),(float) (0),(float) (1),(float) (1),(android.view.View)(mostCurrent._vvvvvvvvvvvvv3.getObject()));
 //BA.debugLineNum = 376;BA.debugLine="ap2.Duration = 1500";
_ap2.setDuration((long) (1500));
 //BA.debugLineNum = 377;BA.debugLine="ap2.SetInterpolator(ap2.INTERPOLATOR_OVERSHOOT)";
_ap2.SetInterpolator(_ap2.INTERPOLATOR_OVERSHOOT);
 //BA.debugLineNum = 378;BA.debugLine="a.Initialize(False)";
_a.Initialize(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 379;BA.debugLine="a.AddAnimation(ap)";
_a.AddAnimation(_ap);
 //BA.debugLineNum = 380;BA.debugLine="a.AddAnimation(ap2)";
_a.AddAnimation(_ap2);
 //BA.debugLineNum = 381;BA.debugLine="a.PersistAfter = True";
_a.setPersistAfter(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 382;BA.debugLine="a.Start(ivAppIcon)";
_a.Start((android.view.View)(mostCurrent._vvvvvvvvvvvvv3.getObject()));
 };
 //BA.debugLineNum = 384;BA.debugLine="End Sub";
return "";
}
public static String  _parse_donesave(boolean _success,int _taskid) throws Exception{
 //BA.debugLineNum = 213;BA.debugLine="Sub Parse_DoneSave(Success As Boolean, TaskID As Int)";
 //BA.debugLineNum = 214;BA.debugLine="Log(Success & \":\" & TaskID)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_success)+":"+BA.NumberToString(_taskid));
 //BA.debugLineNum = 215;BA.debugLine="Select TaskID";
switch (_taskid) {
case 1:
 //BA.debugLineNum = 217;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 218;BA.debugLine="gps1.Start(0, 0)";
_vvvvvvvvv2.Start(processBA,(long) (0),(float) (0));
 }else {
 //BA.debugLineNum = 220;BA.debugLine="Register";
_vvvvvvvvv3();
 };
 break;
case 2:
 //BA.debugLineNum = 223;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 224;BA.debugLine="Log(\"Registered\")";
anywheresoftware.b4a.keywords.Common.Log("Registered");
 //BA.debugLineNum = 225;BA.debugLine="StateManager.SetSetting(\"IsRegistered\", True)";
mostCurrent._vvvvvvvv4._vvv6(mostCurrent.activityBA,"IsRegistered",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True));
 }else {
 //BA.debugLineNum = 227;BA.debugLine="gps1.Start(0, 0)";
_vvvvvvvvv2.Start(processBA,(long) (0),(float) (0));
 };
 break;
}
;
 //BA.debugLineNum = 230;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv1(String _name,String _description,String _phonenumber,String _activation,String _deactivation) throws Exception{
String _sdeactivate = "";
int _iresult = 0;
 //BA.debugLineNum = 920;BA.debugLine="Sub Plan(Name As String, Description As String, PhoneNumber As String, Activation As String, Deactivation As String)";
 //BA.debugLineNum = 921;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 922;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 923;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 924;BA.debugLine="Dim sDeactivate As String";
_sdeactivate = "";
 //BA.debugLineNum = 925;BA.debugLine="If Deactivation.Length > 0 Then sDeactivate = \"Deactivate\"";
if (_deactivation.length()>0) { 
_sdeactivate = "Deactivate";};
 //BA.debugLineNum = 926;BA.debugLine="Dim iResult As Int = Dialog.Msgbox(Name, Description, \"Activate\", sDeactivate, \"ပယ္ဖ်က္ပါ\", Null)";
_iresult = mostCurrent._vvvvvvvvvvv2.MsgBox(mostCurrent.activityBA,_name,_description,"Activate",_sdeactivate,"ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 927;BA.debugLine="If iResult = DialogResponse.POSITIVE Then";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 928;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 929;BA.debugLine="ps.Send(PhoneNumber, Activation)";
mostCurrent._vvvvvvvvvvvvv1.Send(_phonenumber,_activation);
 }else {
 //BA.debugLineNum = 931;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Activation, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(_activation,"UTF8"))));
 };
 }else if(_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 934;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 935;BA.debugLine="ps.Send(PhoneNumber, Deactivation)";
mostCurrent._vvvvvvvvvvvvv1.Send(_phonenumber,_deactivation);
 }else {
 //BA.debugLineNum = 937;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Deactivation, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(_deactivation,"UTF8"))));
 };
 };
 //BA.debugLineNum = 940;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private pm As PackageManager";
_vvvvvvvvvvvvv5 = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Public Const AppName As String = pm.GetApplicationLabel(GetPackageName)";
_vvv0 = _vvvvvvvvvvvvv5.GetApplicationLabel(_vvvvvvvvvvvvv4());
 //BA.debugLineNum = 18;BA.debugLine="Public Const AppVersion As String = pm.GetVersionName(GetPackageName)";
_vvvv1 = _vvvvvvvvvvvvv5.GetVersionName(_vvvvvvvvvvvvv4());
 //BA.debugLineNum = 19;BA.debugLine="Public Const AppPublisher As String = \"Kyaw Swar Thwin\"";
_vvvv2 = BA.__b (new byte[] {24,56,41,-72,98,12,33,-10,33,112,1,-16,34,41,34}, 487418);
 //BA.debugLineNum = 21;BA.debugLine="Private kvs As KeyValueStore";
_vvvvvvvv0 = new com.moribanxenia.easytopup.keyvaluestore();
 //BA.debugLineNum = 23;BA.debugLine="Private Parse As Parse";
_vvvvvvvvv1 = new anywheresoftware.b4a.objects.ParseObjectWrapper.ParseWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private gps1 As GPS";
_vvvvvvvvv2 = new anywheresoftware.b4a.gps.GPS();
 //BA.debugLineNum = 26;BA.debugLine="Private Const ID_ACTION_HOME As Int = 0";
_id_action_home = (int) (0);
 //BA.debugLineNum = 27;BA.debugLine="Private Const ID_ACTION_OVERFLOW As Int = 99";
_id_action_overflow = (int) (99);
 //BA.debugLineNum = 29;BA.debugLine="Private tmrBanner As Timer";
_vvvvvvvvvv1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 30;BA.debugLine="Private iCurrentBanner As Int = 0";
_vvvvvvvvvv7 = (int) (0);
 //BA.debugLineNum = 32;BA.debugLine="Private Const TYPE_HOME As Int = 1";
_type_home = (int) (1);
 //BA.debugLineNum = 33;BA.debugLine="Private Const TYPE_PLANSANDPACKAGES As Int = 2";
_type_plansandpackages = (int) (2);
 //BA.debugLineNum = 34;BA.debugLine="Private Const TYPE_VAS As Int = 3";
_type_vas = (int) (3);
 //BA.debugLineNum = 35;BA.debugLine="Private Const TYPE_STORES As Int = 4";
_type_stores = (int) (4);
 //BA.debugLineNum = 36;BA.debugLine="Private Const TYPE_ABOUT As Int = 5";
_type_about = (int) (5);
 //BA.debugLineNum = 38;BA.debugLine="Public SmartZawgyi As Typeface = Typeface.LoadFromAssets(\"SmartZawgyi.ttf\")";
_vvvv3 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
_vvvv3.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets(BA.__b (new byte[] {0,44,20,70,54,5,10,27,52,41,1,77,33,52,23}, 523881))));
 //BA.debugLineNum = 40;BA.debugLine="Public Const FILL_PARENT As Int = -1";
_fill_parent = (int) (-1);
 //BA.debugLineNum = 41;BA.debugLine="Public Const WRAP_CONTENT As Int = -2";
_wrap_content = (int) (-2);
 //BA.debugLineNum = 43;BA.debugLine="Private iCurrentPage As Int";
_vvvvvvvvvv0 = 0;
 //BA.debugLineNum = 45;BA.debugLine="Private p As Phone";
_vvvvvvvvvvvvv7 = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 46;BA.debugLine="Public SIMOperator As String = p.GetSimOperator";
_vvvv4 = _vvvvvvvvvvvvv7.GetSimOperator();
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv3() throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneId _pid = null;
 //BA.debugLineNum = 202;BA.debugLine="Sub Register";
 //BA.debugLineNum = 203;BA.debugLine="Log(\"Registering\")";
anywheresoftware.b4a.keywords.Common.Log("Registering");
 //BA.debugLineNum = 204;BA.debugLine="Dim pid As PhoneId";
_pid = new anywheresoftware.b4a.phone.Phone.PhoneId();
 //BA.debugLineNum = 205;BA.debugLine="pi = pi.GetCurrentInstallation";
mostCurrent._vvvvvvvvvvvvv6 = mostCurrent._vvvvvvvvvvvvv6.GetCurrentInstallation();
 //BA.debugLineNum = 206;BA.debugLine="pi.Put(\"Manufacturer\", p.Manufacturer)";
mostCurrent._vvvvvvvvvvvvv6.Put("Manufacturer",(Object)(_vvvvvvvvvvvvv7.getManufacturer()));
 //BA.debugLineNum = 207;BA.debugLine="pi.Put(\"ModelNumber\", p.Model)";
mostCurrent._vvvvvvvvvvvvv6.Put("ModelNumber",(Object)(_vvvvvvvvvvvvv7.getModel()));
 //BA.debugLineNum = 208;BA.debugLine="pi.Put(\"IMEI\", pid.GetDeviceId)";
mostCurrent._vvvvvvvvvvvvv6.Put("IMEI",(Object)(_pid.GetDeviceId()));
 //BA.debugLineNum = 209;BA.debugLine="pi.Put(\"IMSI\", pid.GetSubscriberId)";
mostCurrent._vvvvvvvvvvvvv6.Put("IMSI",(Object)(_pid.GetSubscriberId()));
 //BA.debugLineNum = 210;BA.debugLine="pi.SaveEventually(\"Parse\", 1)";
mostCurrent._vvvvvvvvvvvvv6.SaveEventually(processBA,"Parse",(int) (1));
 //BA.debugLineNum = 211;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvv1() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 732;BA.debugLine="Sub RingbackTone";
 //BA.debugLineNum = 733;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 735;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ ျပန္လည္ေျဖဆိုမႈကို ေစာင့္ဆိုင္းေနစဥ္အတြင္း မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားေသာ ဂီတသံစဥ္ကို ခံစားနားဆင္ေနႏိုင္ပါသည္။<br>\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ ျပန္လည္ေျဖဆိုမႈကို ေစာင့္ဆိုင္းေနစဥ္အတြင္း မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားေသာ ဂီတသံစဥ္ကို ခံစားနားဆင္ေနႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 736;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405","41406")) {
case 0:
 //BA.debugLineNum = 738;BA.debugLine="sName = \"FunTone\"";
_sname = "FunTone";
 //BA.debugLineNum = 739;BA.debugLine="sDescription = sDescription & \"- တစ္ရက္စာဝန္ေဆာင္ခ ၇၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ ေန႔စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ လစဥ္ ၇၅၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- FunTone IVR နံပါတ္ ၃၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ FunTone အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။\"";
_sdescription = _sdescription+"- တစ္ရက္စာဝန္ေဆာင္ခ ၇၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ ေန႔စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ လစဥ္ ၇၅၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- FunTone IVR နံပါတ္ ၃၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ FunTone အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 740;BA.debugLine="sSubscription = \"*3333#\"";
_ssubscription = "*3333#";
 //BA.debugLineNum = 741;BA.debugLine="sUnsubscription = \"*3333*0#\"";
_sunsubscription = "*3333*0#";
 break;
case 1:
 //BA.debugLineNum = 743;BA.debugLine="sName = \"My Tune\"";
_sname = "My Tune";
 //BA.debugLineNum = 744;BA.debugLine="sDescription = sDescription & \"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ႏွစ္စဥ္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- My Tune IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ My Tune အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။\"";
_sdescription = _sdescription+"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ႏွစ္စဥ္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- My Tune IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ My Tune အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 745;BA.debugLine="sPhoneNumber = \"333\"";
mostCurrent._vvvvvvvvvvvv3 = "333";
 //BA.debugLineNum = 746;BA.debugLine="sSubscription = \"MT ON\"";
_ssubscription = "MT ON";
 //BA.debugLineNum = 747;BA.debugLine="sUnsubscription = \"MT OFF\"";
_sunsubscription = "MT OFF";
 break;
default:
 //BA.debugLineNum = 749;BA.debugLine="sName = \"Hello Music\"";
_sname = "Hello Music";
 //BA.debugLineNum = 750;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၂၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ဝယ္ယူႏိုင္ပါသည္။<br>- Hello Music IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Hello Music အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၂၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ဝယ္ယူႏိုင္ပါသည္။<br>- Hello Music IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Hello Music အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 751;BA.debugLine="sPhoneNumber = \"333\"";
mostCurrent._vvvvvvvvvvvv3 = "333";
 //BA.debugLineNum = 752;BA.debugLine="sSubscription = \"register\"";
_ssubscription = "register";
 //BA.debugLineNum = 753;BA.debugLine="sUnsubscription = \"unregister\"";
_sunsubscription = "unregister";
 break;
}
;
 //BA.debugLineNum = 756;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription)";
_vvvvvvvvvvvv4(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 757;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv0() throws Exception{
 //BA.debugLineNum = 712;BA.debugLine="Sub SendMeAPNSettings";
 //BA.debugLineNum = 713;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405","41406")) {
case 0:
 //BA.debugLineNum = 714;BA.debugLine="Case \"41405\" : sCode = \"*133*6*1#\"' Ooredoo";
mostCurrent._vvvvvvvvvvv4 = "*133*6*1#";
 break;
case 1:
 //BA.debugLineNum = 715;BA.debugLine="Case \"41406\" : sCode = \"*979*3*1*3#\"' Telenor";
mostCurrent._vvvvvvvvvvv4 = "*979*3*1*3#";
 break;
}
;
 //BA.debugLineNum = 717;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4,"UTF8"))));
 //BA.debugLineNum = 718;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv7(String _title,String _package) throws Exception{
 //BA.debugLineNum = 676;BA.debugLine="Sub ShowPackages(Title As String, Package As String)";
 //BA.debugLineNum = 677;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 678;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 679;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 680;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401")) {
case 0:
 //BA.debugLineNum = 682;BA.debugLine="If Dialog.Msgbox(\"သတိေပးခ်က္\", \"- ဤဝန္ေဆာင္မႈကို အသံုးျပဳရန္အတြက္ ေဆြသဟာအစီအစဥ္ ရယူထားရန္ လိုအပ္ပါသည္။\", \"အိုေက\", \"\", \"ပယ္ဖ်က္ပါ\", Null) = DialogResponse.CANCEL Then Return";
if (mostCurrent._vvvvvvvvvvv2.MsgBox(mostCurrent.activityBA,"သတိေပးခ်က္","- ဤဝန္ေဆာင္မႈကို အသံုးျပဳရန္အတြက္ ေဆြသဟာအစီအစဥ္ ရယူထားရန္ လိုအပ္ပါသည္။","အိုေက","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 break;
}
;
 //BA.debugLineNum = 684;BA.debugLine="PackagesActivity.Title = Title";
mostCurrent._vvvvvvv7._v5 = _title;
 //BA.debugLineNum = 685;BA.debugLine="PackagesActivity.Package = Package";
mostCurrent._vvvvvvv7._v6 = _package;
 //BA.debugLineNum = 686;BA.debugLine="StartActivity(PackagesActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv7.getObject()));
 //BA.debugLineNum = 687;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvv2() throws Exception{
 //BA.debugLineNum = 759;BA.debugLine="Sub SMSChannels";
 //BA.debugLineNum = 760;BA.debugLine="StartActivity(SMSChannelsActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv0.getObject()));
 //BA.debugLineNum = 761;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv6() throws Exception{
 //BA.debugLineNum = 672;BA.debugLine="Sub SweThaharPlan";
 //BA.debugLineNum = 673;BA.debugLine="Plan(\"ေဆြသဟာအစီအစဥ္\", \"ဤအစီအစဥ္ကို အသံုးျပဳျခင္းအားျဖင့္<br>- ျပည္တြင္းဖုန္းေခၚဆိုမႈမ်ားအတြက္ တစ္မိနစ္လွ်င္ ၃၅ က်ပ္၊<br>- SMS တစ္ေစာင္ ေပးပို႔လွ်င္ ၁၅ က်ပ္၊<br>- အင္တာနက္အသံုးျပဳခမွာ တစ္မီဂါဘိုက္လွ်င္ ၁၅ က်ပ္ႏႈန္းသာ က်သင့္မည္ျဖစ္သည္။<br>- အက်ံဳးဝင္သည့္ကာလမွာ ရက္ ၃၀ ျဖစ္ၿပီး<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္ပါသည္။\", \"1332\", \"SWE\", \"UNSUB SWE\")";
_vvvvvvvvvvvvvv1("ေဆြသဟာအစီအစဥ္","ဤအစီအစဥ္ကို အသံုးျပဳျခင္းအားျဖင့္<br>- ျပည္တြင္းဖုန္းေခၚဆိုမႈမ်ားအတြက္ တစ္မိနစ္လွ်င္ ၃၅ က်ပ္၊<br>- SMS တစ္ေစာင္ ေပးပို႔လွ်င္ ၁၅ က်ပ္၊<br>- အင္တာနက္အသံုးျပဳခမွာ တစ္မီဂါဘိုက္လွ်င္ ၁၅ က်ပ္ႏႈန္းသာ က်သင့္မည္ျဖစ္သည္။<br>- အက်ံဳးဝင္သည့္ကာလမွာ ရက္ ၃၀ ျဖစ္ၿပီး<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္ပါသည္။","1332","SWE","UNSUB SWE");
 //BA.debugLineNum = 674;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbanner_tick() throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Sub tmrBanner_Tick";
 //BA.debugLineNum = 255;BA.debugLine="iCurrentBanner = iCurrentBanner + 1";
_vvvvvvvvvv7 = (int) (_vvvvvvvvvv7+1);
 //BA.debugLineNum = 256;BA.debugLine="If iCurrentBanner > asBanner.Length - 1 Then iCurrentBanner = 0";
if (_vvvvvvvvvv7>mostCurrent._vvvvvvvvv7.length-1) { 
_vvvvvvvvvv7 = (int) (0);};
 //BA.debugLineNum = 257;BA.debugLine="pagerBanner.GotoPage(iCurrentBanner, True)";
mostCurrent._vvvvvvvvv0.GotoPage(_vvvvvvvvvv7,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 258;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv4() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
 //BA.debugLineNum = 575;BA.debugLine="Sub TopMeUp";
 //BA.debugLineNum = 576;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 577;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 578;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 579;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.LoadLayout(\"BalanceTransferAndTopMeUpDialog\")";
_dialoglayout = mostCurrent._vvvvvvvvvvv2.LoadLayout(mostCurrent.activityBA,"BalanceTransferAndTopMeUpDialog");
 //BA.debugLineNum = 580;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.Get(\"lblPhoneNumber\")";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 581;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Views.Get(\"edtPhoneNumber\")";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 582;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Views.Get(\"btnContactPicker\")";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 583;BA.debugLine="Dim lblAmount As Label = DialogLayout.Views.Get(\"lblAmount\")";
_lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
_lblamount.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblAmount")));
 //BA.debugLineNum = 584;BA.debugLine="Dim edtAmount As EditText = DialogLayout.Views.Get(\"edtAmount\")";
_edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtamount.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtAmount")));
 //BA.debugLineNum = 586;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtAmount.Top + edtAmount.Height + 10dip";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtamount.getTop()+_edtamount.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 588;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405")) {
case 0:
 //BA.debugLineNum = 589;BA.debugLine="Case \"41405\" : sCode = \"*126*\"' Ooredoo";
mostCurrent._vvvvvvvvvvv4 = "*126*";
 break;
}
;
 //BA.debugLineNum = 592;BA.debugLine="lblPhoneNumber.Text = \"ေငြျဖည့္ေပးမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။\"";
_lblphonenumber.setText((Object)("ေငြျဖည့္ေပးမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 594;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17301547\")";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 596;BA.debugLine="lblAmount.Text = \"ျဖည့္ေပးေစလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။\"";
_lblamount.setText((Object)("ျဖည့္ေပးေစလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 598;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0 AND edtAmount.Text.Length > 0";
while (!(_edtphonenumber.getText().length()>0 && _edtamount.getText().length()>0)) {
 //BA.debugLineNum = 599;BA.debugLine="If DialogLayout.Show(\"မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္\", \"ပို႔ပါ\", \"\", \"ပယ္ဖ်က္ပါ\", Null) = DialogResponse.CANCEL Then Return";
if (_dialoglayout.Show("မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္","ပို႔ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 600;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 601;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 602;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_edtamount.getText().length()==0) { 
 //BA.debugLineNum = 604;BA.debugLine="edtAmount.RequestFocus";
_edtamount.RequestFocus();
 //BA.debugLineNum = 605;BA.debugLine="ToastMessageShow(\"ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 608;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPhoneNumber.Text & \"*\" & edtAmount.Text & \"#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtphonenumber.getText()+"*"+_edtamount.getText()+"#","UTF8"))));
 //BA.debugLineNum = 609;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv3() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _opttopupself = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _opttopupother = null;
anywheresoftware.b4a.objects.LabelWrapper _lblpin = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtpin = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
 //BA.debugLineNum = 452;BA.debugLine="Sub TopUp";
 //BA.debugLineNum = 453;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvvvvv2.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 454;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 455;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 456;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.LoadLayout(\"TopUpDialog\")";
_dialoglayout = mostCurrent._vvvvvvvvvvv2.LoadLayout(mostCurrent.activityBA,"TopUpDialog");
 //BA.debugLineNum = 457;BA.debugLine="Dim optTopUpSelf As RadioButton = DialogLayout.Views.Get(\"optTopUpSelf\")";
_opttopupself = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
_opttopupself.setObject((android.widget.RadioButton)(_dialoglayout.getViews().Get("optTopUpSelf")));
 //BA.debugLineNum = 458;BA.debugLine="Dim optTopUpOther As RadioButton = DialogLayout.Views.Get(\"optTopUpOther\")";
_opttopupother = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
_opttopupother.setObject((android.widget.RadioButton)(_dialoglayout.getViews().Get("optTopUpOther")));
 //BA.debugLineNum = 459;BA.debugLine="Dim lblPIN As Label = DialogLayout.Views.Get(\"lblPIN\")";
_lblpin = new anywheresoftware.b4a.objects.LabelWrapper();
_lblpin.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPIN")));
 //BA.debugLineNum = 460;BA.debugLine="Dim edtPIN As EditText = DialogLayout.Views.Get(\"edtPIN\")";
_edtpin = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtpin.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPIN")));
 //BA.debugLineNum = 461;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.Get(\"lblPhoneNumber\")";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 462;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Views.Get(\"edtPhoneNumber\")";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 463;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Views.Get(\"btnContactPicker\")";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 465;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtPhoneNumber.Top + edtPhoneNumber.Height + 10dip";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtphonenumber.getTop()+_edtphonenumber.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 467;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41405","41406","41404")) {
case 0:
case 1:
case 2:
 //BA.debugLineNum = 468;BA.debugLine="Case \"41401\", \"41405\", \"41406\" : sCode = \"*123*\"' MPT, Ooredoo, Telenor";
mostCurrent._vvvvvvvvvvv4 = "*123*";
 break;
case 3:
 //BA.debugLineNum = 469;BA.debugLine="Case \"41404\" : sCode = \"*166*\"' MPT CDMA";
mostCurrent._vvvvvvvvvvv4 = "*166*";
 break;
default:
 //BA.debugLineNum = 470;BA.debugLine="Case Else : sCode = \"*124*\"' MECTel";
mostCurrent._vvvvvvvvvvv4 = "*124*";
 break;
}
;
 //BA.debugLineNum = 473;BA.debugLine="optTopUpSelf.Text = \"မိမိဖုန္း\"";
_opttopupself.setText((Object)("မိမိဖုန္း"));
 //BA.debugLineNum = 475;BA.debugLine="If SIMOperator <> \"41401\" AND SIMOperator <> \"41405\" AND SIMOperator <> \"41406\" Then optTopUpOther.Enabled = False";
if ((_vvvv4).equals("41401") == false && (_vvvv4).equals("41405") == false && (_vvvv4).equals("41406") == false) { 
_opttopupother.setEnabled(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 476;BA.debugLine="optTopUpOther.Text = \"အျခားဖုန္း\"";
_opttopupother.setText((Object)("အျခားဖုန္း"));
 //BA.debugLineNum = 478;BA.debugLine="lblPIN.Text = \"Top Up ကတ္မွ PIN နံပါတ္႐ိုက္ထည့္ပါ။\"";
_lblpin.setText((Object)("Top Up ကတ္မွ PIN နံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 480;BA.debugLine="lblPhoneNumber.Text = \"ေငြျဖည့္ေပးလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။\"";
_lblphonenumber.setText((Object)("ေငြျဖည့္ေပးလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 482;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17301547\")";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 484;BA.debugLine="Do Until (optTopUpSelf.Checked = True AND edtPIN.Text.Length > 0) OR (optTopUpOther.Checked = True AND (edtPIN.Text.Length > 0 AND edtPhoneNumber.Text.Length > 0))";
while (!((_opttopupself.getChecked()==anywheresoftware.b4a.keywords.Common.True && _edtpin.getText().length()>0) || (_opttopupother.getChecked()==anywheresoftware.b4a.keywords.Common.True && (_edtpin.getText().length()>0 && _edtphonenumber.getText().length()>0)))) {
 //BA.debugLineNum = 485;BA.debugLine="If DialogLayout.Show(\"ဖုန္းေငြျဖည့္ရန္\", \"ေငြျဖည့္ပါ\", \"\", \"ပယ္ဖ်က္ပါ\", Null) = DialogResponse.CANCEL Then Return";
if (_dialoglayout.Show("ဖုန္းေငြျဖည့္ရန္","ေငြျဖည့္ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 486;BA.debugLine="If edtPIN.Text.Length = 0 Then";
if (_edtpin.getText().length()==0) { 
 //BA.debugLineNum = 487;BA.debugLine="edtPIN.RequestFocus";
_edtpin.RequestFocus();
 //BA.debugLineNum = 488;BA.debugLine="ToastMessageShow(\"PIN နံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("PIN နံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_opttopupother.getChecked()==anywheresoftware.b4a.keywords.Common.True && _edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 490;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 491;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 494;BA.debugLine="If optTopUpSelf.Checked Then";
if (_opttopupself.getChecked()) { 
 //BA.debugLineNum = 495;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPIN.Text & \"#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtpin.getText()+"#","UTF8"))));
 }else if(_opttopupother.getChecked()) { 
 //BA.debugLineNum = 497;BA.debugLine="If SIMOperator = \"41405\" Then' Ooredoo";
if ((_vvvv4).equals("41405")) { 
 //BA.debugLineNum = 498;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPhoneNumber.Text & \"*\" & edtPIN.Text & \"*1#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtphonenumber.getText()+"*"+_edtpin.getText()+"*1#","UTF8"))));
 }else {
 //BA.debugLineNum = 500;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPIN.Text & \"*\" & edtPhoneNumber.Text & \"#\", \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4+_edtpin.getText()+"*"+_edtphonenumber.getText()+"#","UTF8"))));
 };
 };
 //BA.debugLineNum = 503;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 641;BA.debugLine="Sub USSD";
 //BA.debugLineNum = 642;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41405","41406")) {
case 0:
 //BA.debugLineNum = 643;BA.debugLine="Case \"41405\" : sCode = \"*133#\"' Ooredoo";
mostCurrent._vvvvvvvvvvv4 = "*133#";
 break;
case 1:
 //BA.debugLineNum = 644;BA.debugLine="Case \"41406\" : sCode = \"*979#\"' Telenor";
mostCurrent._vvvvvvvvvvv4 = "*979#";
 break;
}
;
 //BA.debugLineNum = 646;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4,"UTF8"))));
 //BA.debugLineNum = 647;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvv4(String _name,String _description,String _phonenumber,String _subscription,String _unsubscription) throws Exception{
int _iresult = 0;
 //BA.debugLineNum = 942;BA.debugLine="Sub VAS(Name As String, Description As String, PhoneNumber As String, Subscription As String, Unsubscription As String)";
 //BA.debugLineNum = 943;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 944;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 945;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typeface = SmartZawgyi";
mostCurrent._vvvvvvvvvvv2.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvvv3.getObject());
 //BA.debugLineNum = 946;BA.debugLine="Dim iResult As Int = Dialog.Msgbox(Name, Description, \"Subscribe\", \"Unsubscribe\", \"ပယ္ဖ်က္ပါ\", Null)";
_iresult = mostCurrent._vvvvvvvvvvv2.MsgBox(mostCurrent.activityBA,_name,_description,"Subscribe","Unsubscribe","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 947;BA.debugLine="If iResult = DialogResponse.POSITIVE Then";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 948;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 949;BA.debugLine="ps.Send(PhoneNumber, Subscription)";
mostCurrent._vvvvvvvvvvvvv1.Send(_phonenumber,_subscription);
 }else {
 //BA.debugLineNum = 951;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Subscription, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(_subscription,"UTF8"))));
 };
 }else if(_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 954;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 955;BA.debugLine="ps.Send(PhoneNumber, Unsubscription)";
mostCurrent._vvvvvvvvvvvvv1.Send(_phonenumber,_unsubscription);
 }else {
 //BA.debugLineNum = 957;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Unsubscription, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(_unsubscription,"UTF8"))));
 };
 };
 //BA.debugLineNum = 960;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvv4() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 846;BA.debugLine="Sub VoiceMail";
 //BA.debugLineNum = 847;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription As String";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 849;BA.debugLine="sName = \"Voice Mail\"";
_sname = "Voice Mail";
 //BA.debugLineNum = 850;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းမကိုင္ႏိုင္သည့္အခ်ိန္တြင္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ အသံျဖင့္ အမွာစကားခ်န္ထားခဲ့ႏိုင္ပါသည္။<br>\"";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းမကိုင္ႏိုင္သည့္အခ်ိန္တြင္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ အသံျဖင့္ အမွာစကားခ်န္ထားခဲ့ႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 851;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41406")) {
case 0:
 //BA.debugLineNum = 853;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။<br>- Voice Mail IVR နံပါတ္ ၁၅၅၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။\"";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။<br>- Voice Mail IVR နံပါတ္ ၁၅၅၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 854;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvvvvvvv3 = "1331";
 //BA.debugLineNum = 855;BA.debugLine="sSubscription = \"VMS\"";
_ssubscription = "VMS";
 //BA.debugLineNum = 856;BA.debugLine="sUnsubscription = \"VMS OFF\"";
_sunsubscription = "VMS OFF";
 break;
case 1:
 //BA.debugLineNum = 858;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၀၀ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၀၀ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 859;BA.debugLine="sSubscription = \"*979*2*3*1*1#\"";
_ssubscription = "*979*2*3*1*1#";
 //BA.debugLineNum = 860;BA.debugLine="sUnsubscription = \"*979*2*3*2*1#\"";
_sunsubscription = "*979*2*3*2*1#";
 break;
default:
 //BA.debugLineNum = 862;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၄၄၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။\"";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၄၄၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 863;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvvvvvvvv3 = "233";
 //BA.debugLineNum = 864;BA.debugLine="sSubscription = \"Open VMS\"";
_ssubscription = "Open VMS";
 //BA.debugLineNum = 865;BA.debugLine="sUnsubscription = \"Cancel VMS\"";
_sunsubscription = "Cancel VMS";
 break;
}
;
 //BA.debugLineNum = 868;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscription, sUnsubscription)";
_vvvvvvvvvvvv4(_sname,_sdescription,mostCurrent._vvvvvvvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 869;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvv2() throws Exception{
 //BA.debugLineNum = 399;BA.debugLine="Sub WhatIsMyNumber";
 //BA.debugLineNum = 400;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvvv4,"41401","41405","41406")) {
case 0:
 //BA.debugLineNum = 401;BA.debugLine="Case \"41401\" : sCode = \"*888#\"' MPT";
mostCurrent._vvvvvvvvvvv4 = "*888#";
 break;
case 1:
 //BA.debugLineNum = 402;BA.debugLine="Case \"41405\" : sCode = \"*133*5#\"' Ooredoo";
mostCurrent._vvvvvvvvvvv4 = "*133*5#";
 break;
case 2:
 //BA.debugLineNum = 403;BA.debugLine="Case \"41406\" : sCode = \"*97#\"' Telenor";
mostCurrent._vvvvvvvvvvv4 = "*97#";
 break;
}
;
 //BA.debugLineNum = 405;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvvv6.Call(mostCurrent._vvvvvvvvvvv7.EncodeUrl(mostCurrent._vvvvvvvvvvv4,"UTF8"))));
 //BA.debugLineNum = 406;BA.debugLine="End Sub";
return "";
}
}
