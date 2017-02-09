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
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
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
        processBA.runHook("onnewintent", this, new Object[] {intent});
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
            BA.LogInfo("** Activity (main) Resume **");
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
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.phone.PackageManagerWrapper _vvvvvvvvv7 = null;
public static String _vvv4 = "";
public static String _vvv5 = "";
public static String _vvv6 = "";
public static int _id_action_home = 0;
public static int _id_action_overflow = 0;
public static anywheresoftware.b4a.objects.collections.Map _vvvv3 = null;
public static anywheresoftware.b4a.objects.Timer _vvvvv2 = null;
public static int _vvvvvv6 = 0;
public static int _type_prepaid = 0;
public static int _type_offers = 0;
public static int _type_support = 0;
public static int _type_stores = 0;
public static int _type_about = 0;
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvv7 = null;
public static int _fill_parent = 0;
public static int _wrap_content = 0;
public static int _vvvvvv7 = 0;
public static anywheresoftware.b4a.phone.Phone _vvvvvvvvv0 = null;
public static String _vvv0 = "";
public static String _vvvvvv1 = "";
public static String _vvvvvv3 = "";
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _vvvv6 = null;
public anywheresoftware.b4a.object.XmlLayoutBuilder _vvvvvvvv2 = null;
public de.amberhome.SimpleActionBar.ActionBarWrapper _vvvv2 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _vvvv4 = null;
public de.amberhome.quickaction.ICSMenu _vvvv1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbanner = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlcontent = null;
public static String[] _vvvv0 = null;
public de.amberhome.viewpager.AHPageContainer _vvvv7 = null;
public de.amberhome.viewpager.AHPageContainer _vvvvv3 = null;
public de.amberhome.viewpager.AHViewPager _vvvvv1 = null;
public de.amberhome.viewpager.AHViewPager _vvvvv5 = null;
public de.amberhome.viewpager.AHViewPagerTabs _vvvvv6 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvv7 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvvv3 = null;
public com.datasteam.b4a.xtraviews.DialogView _vvvvvvvv1 = null;
public static String _vvvvvvv2 = "";
public static String _vvvvvvv3 = "";
public static String _vvvvvvv4 = "";
public anywheresoftware.b4a.objects.StringUtils _vvvvvvv7 = null;
public anywheresoftware.b4a.phone.Phone.PhoneCalls _vvvvvvv6 = null;
public anywheresoftware.b4a.phone.Phone.PhoneSms _vvvvvvv5 = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _vvvvv0 = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _vvvvvv2 = null;
public static boolean _vvvvvv4 = false;
public com.moribanxenia.easytopup.offersactivity _vvvvvvvvvv1 = null;
public com.moribanxenia.easytopup.welcomeactivity _vvvvvv0 = null;
public com.moribanxenia.easytopup.statemanager _vvvvvv5 = null;
public com.moribanxenia.easytopup.starter _vvvv5 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (offersactivity.mostCurrent != null);
vis = vis | (welcomeactivity.mostCurrent != null);
return vis;}
public static String  _ab_itemclicked(int _itemid) throws Exception{
 //BA.debugLineNum = 188;BA.debugLine="Sub AB_ItemClicked(ItemID As Int)";
 //BA.debugLineNum = 189;BA.debugLine="Select ItemID";
switch (BA.switchObjectToInt(_itemid,_id_action_overflow)) {
case 0: {
 //BA.debugLineNum = 191;BA.debugLine="menu.Show(ab.GetActionView(ItemID))";
mostCurrent._vvvv1.Show(mostCurrent._vvvv2.GetActionView(_itemid));
 break; }
}
;
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public static String  _ac_click(int _position,int _actionitemid) throws Exception{
String _soldsimoperator = "";
 //BA.debugLineNum = 195;BA.debugLine="Sub AC_Click (Position As Int, ActionItemID As Int";
 //BA.debugLineNum = 196;BA.debugLine="Dim sOldSIMOperator As String = SIMOperator";
_soldsimoperator = _vvv0;
 //BA.debugLineNum = 197;BA.debugLine="SIMOperator = SIMOperators.GetKeyAt(Position)";
_vvv0 = BA.ObjectToString(_vvvv3.GetKeyAt(_position));
 //BA.debugLineNum = 198;BA.debugLine="If sOldSIMOperator <> SIMOperator Then Activity_C";
if ((_soldsimoperator).equals(_vvv0) == false) { 
_activity_create(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 199;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.ImageViewWrapper _iv = null;
int _i = 0;
de.amberhome.quickaction.ActionItem _ai = null;
 //BA.debugLineNum = 81;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 82;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 83;BA.debugLine="Dim iv As ImageView";
_iv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 85;BA.debugLine="If Not(FirstTime) Then Activity.RemoveAllViews";
if (anywheresoftware.b4a.keywords.Common.Not(_firsttime)) { 
mostCurrent._activity.RemoveAllViews();};
 //BA.debugLineNum = 87;BA.debugLine="If FirstTime Or ab.IsInitialized = False Then";
if (_firsttime || mostCurrent._vvvv2.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 88;BA.debugLine="menu.Initialize(\"AC\")";
mostCurrent._vvvv1.Initialize(processBA,"AC");
 //BA.debugLineNum = 89;BA.debugLine="For i = 0 To SIMOperators.Size - 1";
{
final int step6 = 1;
final int limit6 = (int) (_vvvv3.getSize()-1);
for (_i = (int) (0) ; (step6 > 0 && _i <= limit6) || (step6 < 0 && _i >= limit6); _i = ((int)(0 + _i + step6)) ) {
 //BA.debugLineNum = 90;BA.debugLine="Dim ai As AHActionItem";
_ai = new de.amberhome.quickaction.ActionItem();
 //BA.debugLineNum = 91;BA.debugLine="ai.Initialize(i, SIMOperators.GetValueAt(i), Nu";
_ai.Initialize(_i,BA.ObjectToString(_vvvv3.GetValueAt(_i)),(android.graphics.drawable.Drawable)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 92;BA.debugLine="menu.AddActionItem(ai)";
mostCurrent._vvvv1.AddActionItem(_ai);
 }
};
 };
 //BA.debugLineNum = 96;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 98;BA.debugLine="overflowIcon.Initialize(LoadBitmap(File.DirAssets";
mostCurrent._vvvv4.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_action_overflow.png").getObject()));
 //BA.debugLineNum = 100;BA.debugLine="ab.Initialize(\"AB\")";
mostCurrent._vvvv2.Initialize(mostCurrent.activityBA,"AB");
 //BA.debugLineNum = 101;BA.debugLine="ab.SubTitle = SIMOperators.GetDefault(SIMOperator";
mostCurrent._vvvv2.setSubTitle((java.lang.CharSequence)(_vvvv3.GetDefault((Object)(_vvv0),(Object)("MECTel"))));
 //BA.debugLineNum = 102;BA.debugLine="Starter.Analytics.SendEvent(\"test\", CreateMap(\"SI";
mostCurrent._vvvv5._vvv3.SendEvent("test",anywheresoftware.b4a.keywords.Common.createMap(new Object[] {(Object)("SIMOperator"),_vvvv3.GetDefault((Object)(_vvv0),(Object)("MECTel"))}));
 //BA.debugLineNum = 103;BA.debugLine="ab.AddHomeAction(ID_ACTION_HOME, AppIcon)";
mostCurrent._vvvv2.AddHomeAction(_id_action_home,(android.graphics.drawable.Drawable)(mostCurrent._vvvv6.getObject()));
 //BA.debugLineNum = 104;BA.debugLine="ab.AddAction(ID_ACTION_OVERFLOW, overflowIcon)";
mostCurrent._vvvv2.AddAction(_id_action_overflow,(android.graphics.drawable.Drawable)(mostCurrent._vvvv4.getObject()));
 //BA.debugLineNum = 105;BA.debugLine="Activity.AddView(ab, 0, 0, 100%x, 48dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvv2.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 107;BA.debugLine="containerBanner.Initialize";
mostCurrent._vvvv7.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 108;BA.debugLine="For i = 0 To asBanner.Length - 1";
{
final int step21 = 1;
final int limit21 = (int) (mostCurrent._vvvv0.length-1);
for (_i = (int) (0) ; (step21 > 0 && _i <= limit21) || (step21 < 0 && _i >= limit21); _i = ((int)(0 + _i + step21)) ) {
 //BA.debugLineNum = 109;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 110;BA.debugLine="iv.Initialize(\"\")";
_iv.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 111;BA.debugLine="iv.Bitmap = LoadBitmap(File.DirAssets, asBanner(";
_iv.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),mostCurrent._vvvv0[_i]).getObject()));
 //BA.debugLineNum = 112;BA.debugLine="iv.Gravity = Gravity.FILL";
_iv.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 113;BA.debugLine="pnl.AddView(iv, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_iv.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 //BA.debugLineNum = 114;BA.debugLine="containerBanner.AddPage(pnl, \"\")";
mostCurrent._vvvv7.AddPage((android.view.View)(_pnl.getObject()),"");
 }
};
 //BA.debugLineNum = 116;BA.debugLine="pagerBanner.Initialize(\"Banner\")";
mostCurrent._vvvvv1.Initialize(mostCurrent.activityBA,"Banner");
 //BA.debugLineNum = 117;BA.debugLine="pagerBanner.PageContainer = containerBanner";
mostCurrent._vvvvv1.setPageContainer(mostCurrent._vvvv7);
 //BA.debugLineNum = 118;BA.debugLine="pnlBanner.AddView(pagerBanner, 0, 0, 100%x, 37%x)";
mostCurrent._pnlbanner.AddView((android.view.View)(mostCurrent._vvvvv1.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA));
 //BA.debugLineNum = 119;BA.debugLine="tmrBanner.Initialize(\"tmrBanner\", 8000)";
_vvvvv2.Initialize(processBA,"tmrBanner",(long) (8000));
 //BA.debugLineNum = 120;BA.debugLine="tmrBanner.Enabled = True";
_vvvvv2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 122;BA.debugLine="container.Initialize";
mostCurrent._vvvvv3.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 123;BA.debugLine="For i = 0 To 4";
{
final int step35 = 1;
final int limit35 = (int) (4);
for (_i = (int) (0) ; (step35 > 0 && _i <= limit35) || (step35 < 0 && _i >= limit35); _i = ((int)(0 + _i + step35)) ) {
 //BA.debugLineNum = 124;BA.debugLine="Select i";
switch (_i) {
case 0: {
 //BA.debugLineNum = 126;BA.debugLine="pnl = CreatePanel(TYPE_PREPAID)";
_pnl = _vvvvv4(_type_prepaid);
 //BA.debugLineNum = 127;BA.debugLine="container.AddPage(pnl, \"Prepaid\")";
mostCurrent._vvvvv3.AddPage((android.view.View)(_pnl.getObject()),"Prepaid");
 break; }
case 1: {
 //BA.debugLineNum = 129;BA.debugLine="pnl = CreatePanel(TYPE_OFFERS)";
_pnl = _vvvvv4(_type_offers);
 //BA.debugLineNum = 130;BA.debugLine="container.AddPage(pnl, \"Offers\")";
mostCurrent._vvvvv3.AddPage((android.view.View)(_pnl.getObject()),"Offers");
 break; }
case 2: {
 //BA.debugLineNum = 132;BA.debugLine="pnl = CreatePanel(TYPE_SUPPORT)";
_pnl = _vvvvv4(_type_support);
 //BA.debugLineNum = 133;BA.debugLine="container.AddPage(pnl, \"Support\")";
mostCurrent._vvvvv3.AddPage((android.view.View)(_pnl.getObject()),"Support");
 break; }
case 3: {
 //BA.debugLineNum = 135;BA.debugLine="pnl = CreatePanel(TYPE_STORES)";
_pnl = _vvvvv4(_type_stores);
 //BA.debugLineNum = 136;BA.debugLine="container.AddPage(pnl, \"Stores\")";
mostCurrent._vvvvv3.AddPage((android.view.View)(_pnl.getObject()),"Stores");
 break; }
case 4: {
 //BA.debugLineNum = 138;BA.debugLine="pnl = CreatePanel(TYPE_ABOUT)";
_pnl = _vvvvv4(_type_about);
 //BA.debugLineNum = 139;BA.debugLine="container.AddPage(pnl, \"About\")";
mostCurrent._vvvvv3.AddPage((android.view.View)(_pnl.getObject()),"About");
 break; }
}
;
 }
};
 //BA.debugLineNum = 143;BA.debugLine="pager.Initialize(\"Pager\")";
mostCurrent._vvvvv5.Initialize(mostCurrent.activityBA,"Pager");
 //BA.debugLineNum = 144;BA.debugLine="pager.PageContainer = container";
mostCurrent._vvvvv5.setPageContainer(mostCurrent._vvvvv3);
 //BA.debugLineNum = 146;BA.debugLine="tabs.Initialize(pager)";
mostCurrent._vvvvv6.Initialize(mostCurrent.activityBA,mostCurrent._vvvvv5);
 //BA.debugLineNum = 147;BA.debugLine="tabs.LineHeight = 5dip";
mostCurrent._vvvvv6.setLineHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 148;BA.debugLine="tabs.UpperCaseTitle = True";
mostCurrent._vvvvv6.setUpperCaseTitle(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 149;BA.debugLine="tabs.TextColor = Colors.LightGray";
mostCurrent._vvvvv6.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 150;BA.debugLine="tabs.TextColorCenter = Colors.DarkGray";
mostCurrent._vvvvv6.setTextColorCenter(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 151;BA.debugLine="tabs.LineColorCenter = Colors.DarkGray";
mostCurrent._vvvvv6.setLineColorCenter(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 152;BA.debugLine="tabs.BackgroundColorPressed = Colors.RGB(51, 181,";
mostCurrent._vvvvv6.setBackgroundColorPressed(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (51),(int) (181),(int) (229)));
 //BA.debugLineNum = 153;BA.debugLine="pnlContent.AddView(tabs, 0, 0, FILL_PARENT, WRAP_";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvv6.getObject()),(int) (0),(int) (0),_fill_parent,_wrap_content);
 //BA.debugLineNum = 155;BA.debugLine="line.Initialize(\"\")";
mostCurrent._vvvvv7.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 156;BA.debugLine="line.Color = tabs.LineColorCenter";
mostCurrent._vvvvv7.setColor(mostCurrent._vvvvv6.getLineColorCenter());
 //BA.debugLineNum = 157;BA.debugLine="pnlContent.AddView(line, 0, tabs.Top + tabs.Heigh";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvv7.getObject()),(int) (0),(int) (mostCurrent._vvvvv6.getTop()+mostCurrent._vvvvv6.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35))),mostCurrent._pnlcontent.getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
 //BA.debugLineNum = 159;BA.debugLine="pnlContent.AddView(pager, 0, line.Top + line.Heig";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvv5.getObject()),(int) (0),(int) (mostCurrent._vvvvv7.getTop()+mostCurrent._vvvvv7.getHeight()),mostCurrent._pnlcontent.getWidth(),(int) (mostCurrent._pnlcontent.getHeight()-(mostCurrent._vvvvv7.getTop()+mostCurrent._vvvvv7.getHeight())));
 //BA.debugLineNum = 161;BA.debugLine="AdMobBanner.Initialize2(\"AdMobBanner\", AdMobBanne";
mostCurrent._vvvvv0.Initialize2(mostCurrent.activityBA,"AdMobBanner",_vvvvvv1,mostCurrent._vvvvv0.SIZE_SMART_BANNER);
 //BA.debugLineNum = 162;BA.debugLine="Activity.AddView(AdMobBanner, 0, 100%y - 50dip, 1";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvv0.getObject()),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 163;BA.debugLine="AdMobBanner.LoadAd";
mostCurrent._vvvvv0.LoadAd();
 //BA.debugLineNum = 164;BA.debugLine="AdMobInterstitial.Initialize(\"AdMobInterstitial\",";
mostCurrent._vvvvvv2.Initialize(processBA,"AdMobInterstitial",_vvvvvv3);
 //BA.debugLineNum = 165;BA.debugLine="AdMobInterstitial.LoadAd";
mostCurrent._vvvvvv2.LoadAd();
 //BA.debugLineNum = 166;BA.debugLine="IsAlreadyShowAdMobInterstitial = False";
_vvvvvv4 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 167;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 180;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 181;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_MENU Then AB_ItemCl";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MENU) { 
_ab_itemclicked(_id_action_overflow);};
 //BA.debugLineNum = 182;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 184;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 185;BA.debugLine="StateManager.SaveSettings";
mostCurrent._vvvvvv5._vv0(mostCurrent.activityBA);
 //BA.debugLineNum = 186;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 169;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 170;BA.debugLine="Activity.Title = AppName";
mostCurrent._activity.setTitle((Object)(_vvv4));
 //BA.debugLineNum = 172;BA.debugLine="pagerBanner.GotoPage(iCurrentBanner, False)";
mostCurrent._vvvvv1.GotoPage(_vvvvvv6,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 173;BA.debugLine="pager.GotoPage(iCurrentPage, False)";
mostCurrent._vvvvv5.GotoPage(_vvvvvv7,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 175;BA.debugLine="If StateManager.GetSetting2(\"FirstTime\", True) Th";
if (BA.ObjectToBoolean(mostCurrent._vvvvvv5._vv5(mostCurrent.activityBA,"FirstTime",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True)))) { 
 //BA.debugLineNum = 176;BA.debugLine="StartActivity(WelcomeActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvv0.getObject()));
 };
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static String  _admobinterstitial_adclosed() throws Exception{
 //BA.debugLineNum = 981;BA.debugLine="Sub AdMobInterstitial_AdClosed";
 //BA.debugLineNum = 982;BA.debugLine="AdMobInterstitial.LoadAd";
mostCurrent._vvvvvv2.LoadAd();
 //BA.debugLineNum = 983;BA.debugLine="End Sub";
return "";
}
public static String  _admobinterstitial_failedtoreceivead(String _errorcode) throws Exception{
 //BA.debugLineNum = 985;BA.debugLine="Sub AdMobInterstitial_FailedToReceiveAd (ErrorCode";
 //BA.debugLineNum = 986;BA.debugLine="Log(\"Failed To Receive Ad: \" & ErrorCode)";
anywheresoftware.b4a.keywords.Common.Log("Failed To Receive Ad: "+_errorcode);
 //BA.debugLineNum = 987;BA.debugLine="AdMobInterstitial.LoadAd";
mostCurrent._vvvvvv2.LoadAd();
 //BA.debugLineNum = 988;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv1() throws Exception{
 //BA.debugLineNum = 842;BA.debugLine="Sub APNSettings";
 //BA.debugLineNum = 843;BA.debugLine="sCode = \"\"";
mostCurrent._vvvvvvv2 = "";
 //BA.debugLineNum = 845;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 846;BA.debugLine="Case \"41401\" : sPhoneNumber = \"6006\" : sSMSBody";
mostCurrent._vvvvvvv3 = "6006";
 //BA.debugLineNum = 846;BA.debugLine="Case \"41401\" : sPhoneNumber = \"6006\" : sSMSBody";
mostCurrent._vvvvvvv4 = "APN Settings";
 break; }
case 1: {
 //BA.debugLineNum = 847;BA.debugLine="Case \"41406\" : sCode = \"*979*3*1*3#\"' Telenor";
mostCurrent._vvvvvvv2 = "*979*3*1*3#";
 break; }
}
;
 //BA.debugLineNum = 849;BA.debugLine="If sCode = \"\" Then";
if ((mostCurrent._vvvvvvv2).equals("")) { 
 //BA.debugLineNum = 850;BA.debugLine="ps.Send(sPhoneNumber, sSMSBody)";
mostCurrent._vvvvvvv5.Send(mostCurrent._vvvvvvv3,mostCurrent._vvvvvvv4);
 }else {
 //BA.debugLineNum = 852;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\"";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2,"UTF8"))));
 };
 //BA.debugLineNum = 854;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv0() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
 //BA.debugLineNum = 485;BA.debugLine="Sub BalanceTransfer";
 //BA.debugLineNum = 486;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 487;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 488;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 489;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"BalanceTransferAndTopMeUpDialog");
 //BA.debugLineNum = 490;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 491;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 492;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 493;BA.debugLine="Dim lblAmount As Label = DialogLayout.Views.Get(\"";
_lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
_lblamount.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblAmount")));
 //BA.debugLineNum = 494;BA.debugLine="Dim edtAmount As EditText = DialogLayout.Views.Ge";
_edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtamount.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtAmount")));
 //BA.debugLineNum = 496;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtAmoun";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtamount.getTop()+_edtamount.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 498;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405")) {
case 0: {
 //BA.debugLineNum = 499;BA.debugLine="Case \"41401\" : sCode = \"*223*\"' MPT";
mostCurrent._vvvvvvv2 = "*223*";
 break; }
case 1: {
 //BA.debugLineNum = 500;BA.debugLine="Case \"41405\" : sCode = \"*155*\"' Ooredoo";
mostCurrent._vvvvvvv2 = "*155*";
 break; }
default: {
 //BA.debugLineNum = 501;BA.debugLine="Case Else : sCode = \"*110*\"' MECTel";
mostCurrent._vvvvvvv2 = "*110*";
 break; }
}
;
 //BA.debugLineNum = 504;BA.debugLine="lblPhoneNumber.Text = \"ေငြလက္ခံမည့္သူ၏ဖုန္းနံပါတ္";
_lblphonenumber.setText((Object)("ေငြလက္ခံမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 506;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvv2.GetDrawable("17301547"));
 //BA.debugLineNum = 508;BA.debugLine="lblAmount.Text = \"လႊဲေျပာင္းေပးလိုေသာေငြပမာဏ႐ိုက္";
_lblamount.setText((Object)("လႊဲေျပာင္းေပးလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 510;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0 And edtAm";
while (!(_edtphonenumber.getText().length()>0 && _edtamount.getText().length()>0)) {
 //BA.debugLineNum = 511;BA.debugLine="If DialogLayout.Show(\"ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေ";
if (_dialoglayout.Show("ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္","ေငြလႊဲပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 512;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 513;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 514;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_edtamount.getText().length()==0) { 
 //BA.debugLineNum = 516;BA.debugLine="edtAmount.RequestFocus";
_edtamount.RequestFocus();
 //BA.debugLineNum = 517;BA.debugLine="ToastMessageShow(\"ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပ";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 520;BA.debugLine="If SIMOperator = \"41406\" Then' Telenor";
if ((_vvv0).equals("41406")) { 
 //BA.debugLineNum = 521;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtP";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtphonenumber.getText()+"*"+_edtamount.getText()+"#","UTF8"))));
 }else {
 //BA.debugLineNum = 523;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtA";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtamount.getText()+"*"+_edtphonenumber.getText()+"#","UTF8"))));
 };
 //BA.debugLineNum = 525;BA.debugLine="End Sub";
return "";
}
public static String  _banner_pagechanged(int _position) throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub Banner_PageChanged (Position As Int)";
 //BA.debugLineNum = 208;BA.debugLine="iCurrentBanner = Position";
_vvvvvv6 = _position;
 //BA.debugLineNum = 209;BA.debugLine="End Sub";
return "";
}
public static String  _btncontactpicker_click() throws Exception{
com.moribanxenia.contactpicker.ContactPicker _cp = null;
 //BA.debugLineNum = 463;BA.debugLine="Sub btnContactPicker_Click";
 //BA.debugLineNum = 464;BA.debugLine="Dim cp As ContactPicker";
_cp = new com.moribanxenia.contactpicker.ContactPicker();
 //BA.debugLineNum = 465;BA.debugLine="cp.Initialize(\"cp\")";
_cp.Initialize("cp");
 //BA.debugLineNum = 466;BA.debugLine="cp.Show";
_cp.Show(processBA);
 //BA.debugLineNum = 467;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv3(String[] _phonenumber) throws Exception{
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
int _i = 0;
 //BA.debugLineNum = 952;BA.debugLine="Sub Call(PhoneNumber() As String)";
 //BA.debugLineNum = 953;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 954;BA.debugLine="Dim iResult As Int = 0";
_iresult = (int) (0);
 //BA.debugLineNum = 956;BA.debugLine="If PhoneNumber.Length > 1 Then";
if (_phonenumber.length>1) { 
 //BA.debugLineNum = 957;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 958;BA.debugLine="Dialog.Options.Dimensions.Height = (PhoneNumber.";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Height = (int) ((_phonenumber.length*anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)))+((_phonenumber.length-1)*anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 959;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZa";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 960;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Ty";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 961;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Lo";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 962;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 964;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 965;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravit";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 966;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Colo";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 968;BA.debugLine="bd = xml.GetDrawable(\"17301645\")";
_bd.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._vvvvvvvv2.GetDrawable("17301645")));
 //BA.debugLineNum = 970;BA.debugLine="For i = 0 To PhoneNumber.Length - 1";
{
final int step14 = 1;
final int limit14 = (int) (_phonenumber.length-1);
for (_i = (int) (0) ; (step14 > 0 && _i <= limit14) || (step14 < 0 && _i >= limit14); _i = ((int)(0 + _i + step14)) ) {
 //BA.debugLineNum = 971;BA.debugLine="lvwList.AddTwoLinesAndBitmap(PhoneNumber(i), \"\"";
_lvwlist.AddTwoLinesAndBitmap(_phonenumber[_i],"",_bd.getBitmap());
 }
};
 //BA.debugLineNum = 974;BA.debugLine="iResult = DialogLayout.Show(\"ေခၚဆိုမည့္ဖုန္းနံပါ";
_iresult = _dialoglayout.Show("ေခၚဆိုမည့္ဖုန္းနံပါတ္ေရြးခ်ယ္ရန္","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 975;BA.debugLine="If iResult = DialogResponse.CANCEL Then Return";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 };
 //BA.debugLineNum = 978;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(PhoneNumber(iR";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(_phonenumber[_iresult],"UTF8"))));
 //BA.debugLineNum = 979;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv4() throws Exception{
 //BA.debugLineNum = 865;BA.debugLine="Sub CallCentre";
 //BA.debugLineNum = 866;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41404","41405","41406")) {
case 0: 
case 1: {
 //BA.debugLineNum = 867;BA.debugLine="Case \"41401\", \"41404\" : Call(Array As String(\"10";
_vvvvvvvv3(new String[]{"106"});
 break; }
case 2: {
 //BA.debugLineNum = 868;BA.debugLine="Case \"41405\" : Call(Array As String(\"234\", \"0997";
_vvvvvvvv3(new String[]{"234","09970000234"});
 break; }
case 3: {
 //BA.debugLineNum = 869;BA.debugLine="Case \"41406\" : Call(Array As String(\"979\", \"0979";
_vvvvvvvv3(new String[]{"979","09790097900"});
 break; }
default: {
 //BA.debugLineNum = 870;BA.debugLine="Case Else : Call(Array As String(\"1212\"))' MECTe";
_vvvvvvvv3(new String[]{"1212"});
 break; }
}
;
 //BA.debugLineNum = 872;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv5() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 698;BA.debugLine="Sub CallForwarding";
 //BA.debugLineNum = 699;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 701;BA.debugLine="sName = \"Call Forwarding\"";
_sname = "Call Forwarding";
 //BA.debugLineNum = 702;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံေခၚဆိုေ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံေခၚဆိုေသာ အဝင္ဖုန္းမ်ားကို မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားသည့္ အျခားဖုန္းနံပါတ္တစ္ခုသို႔ လႊဲေျပာင္းေပးႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 703;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41404")) {
case 0: {
 //BA.debugLineNum = 705;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 706;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 707;BA.debugLine="sSubscription = \"CF\"";
_ssubscription = "CF";
 //BA.debugLineNum = 708;BA.debugLine="sUnsubscription = \"CF OFF\"";
_sunsubscription = "CF OFF";
 break; }
case 1: {
 //BA.debugLineNum = 710;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၉၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 711;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 712;BA.debugLine="sSubscription = \"Orderdata CF\"";
_ssubscription = "Orderdata CF";
 //BA.debugLineNum = 713;BA.debugLine="sUnsubscription = \"Cancel CF\"";
_sunsubscription = "Cancel CF";
 break; }
}
;
 //BA.debugLineNum = 716;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv6(_sname,_sdescription,mostCurrent._vvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 717;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv7() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
 //BA.debugLineNum = 802;BA.debugLine="Sub CallMeBack";
 //BA.debugLineNum = 803;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 804;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 805;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 806;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"CallMeBackDialog");
 //BA.debugLineNum = 807;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 808;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 809;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 811;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtPhone";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtphonenumber.getTop()+_edtphonenumber.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 813;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 814;BA.debugLine="Case \"41401\" : sCode = \"*222*\"' MPT";
mostCurrent._vvvvvvv2 = "*222*";
 break; }
case 1: {
 //BA.debugLineNum = 815;BA.debugLine="Case \"41405\" : sCode = \"*122*\"' Ooredoo";
mostCurrent._vvvvvvv2 = "*122*";
 break; }
case 2: {
 //BA.debugLineNum = 816;BA.debugLine="Case \"41406\" : sCode = \"*1*\"' Telenor";
mostCurrent._vvvvvvv2 = "*1*";
 break; }
}
;
 //BA.debugLineNum = 819;BA.debugLine="lblPhoneNumber.Text = \"ျပန္ေခၚေစလိုေသာဖုန္းနံပါတ္";
_lblphonenumber.setText((Object)("ျပန္ေခၚေစလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 821;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvv2.GetDrawable("17301547"));
 //BA.debugLineNum = 823;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0";
while (!(_edtphonenumber.getText().length()>0)) {
 //BA.debugLineNum = 824;BA.debugLine="If DialogLayout.Show(\"မိမိဖုန္းအားျပန္ေခၚေပးပါရန";
if (_dialoglayout.Show("မိမိဖုန္းအားျပန္ေခၚေပးပါရန္","ပို႔ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 825;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 826;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 827;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 830;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPho";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtphonenumber.getText()+"#","UTF8"))));
 //BA.debugLineNum = 831;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv0() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 719;BA.debugLine="Sub CallWaiting";
 //BA.debugLineNum = 720;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 722;BA.debugLine="sName = \"Call Waiting\"";
_sname = "Call Waiting";
 //BA.debugLineNum = 723;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းေျပာ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းေျပာဆိုေနစဥ္အတြင္း ထပ္မံေရာက္ရွိလာေသာ အဝင္ဖုန္းကို လက္ခံေျပာဆိုႏိုင္ေစၿပီး၊ လက္ရွိေျပာဆိုေနမႈကို ေစာင့္ဆိုင္းခိုင္းထားႏိုင္ပါသည္။ ဖုန္းေခၚဆိုမႈႏွစ္ခုအၾကားတြင္လည္း ဖုန္းခ်စရာမလိုပဲ အျပန္အလွန္ေျပာဆိုႏုိင္ပါသည္။<br>";
 //BA.debugLineNum = 724;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41404")) {
case 0: {
 //BA.debugLineNum = 726;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 727;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 728;BA.debugLine="sSubscription = \"CW\"";
_ssubscription = "CW";
 //BA.debugLineNum = 729;BA.debugLine="sUnsubscription = \"CW OFF\"";
_sunsubscription = "CW OFF";
 break; }
case 1: {
 //BA.debugLineNum = 731;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 732;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 733;BA.debugLine="sSubscription = \"Orderdata CW\"";
_ssubscription = "Orderdata CW";
 //BA.debugLineNum = 734;BA.debugLine="sUnsubscription = \"Cancel CW\"";
_sunsubscription = "Cancel CW";
 break; }
}
;
 //BA.debugLineNum = 737;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv6(_sname,_sdescription,mostCurrent._vvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 738;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv1() throws Exception{
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
 //BA.debugLineNum = 341;BA.debugLine="Sub CheckBalance";
 //BA.debugLineNum = 342;BA.debugLine="Dim iResult As Int";
_iresult = 0;
 //BA.debugLineNum = 344;BA.debugLine="sCode = \"\"";
mostCurrent._vvvvvvv2 = "";
 //BA.debugLineNum = 346;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 347;BA.debugLine="Dialog.Options.Dimensions.Height = 121dip";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (121));
 //BA.debugLineNum = 348;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 349;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 350;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 351;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get(";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 353;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 354;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Typeface = SmartZ";
_lvwlist.getTwoLinesAndBitmap().Label.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 355;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravity";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 356;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Color";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 358;BA.debugLine="lvwList.AddTwoLinesAndBitmap(\"ဖုန္းလက္က်န္ေငြ\", \"";
_lvwlist.AddTwoLinesAndBitmap("ဖုန္းလက္က်န္ေငြ","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"balance.png").getObject()));
 //BA.debugLineNum = 359;BA.debugLine="lvwList.AddTwoLinesAndBitmap(\"အင္တာနက္ႏွင့္အပိုဆု";
_lvwlist.AddTwoLinesAndBitmap("အင္တာနက္ႏွင့္အပိုဆုမ်ား","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bonus.png").getObject()));
 //BA.debugLineNum = 361;BA.debugLine="iResult = DialogLayout.Show(\"ဖုန္းလက္က်န္ေငြစစ္ေဆ";
_iresult = _dialoglayout.Show("ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 362;BA.debugLine="Select iResult";
switch (BA.switchObjectToInt(_iresult,anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL,(int) (0),(int) (1))) {
case 0: {
 //BA.debugLineNum = 363;BA.debugLine="Case DialogResponse.CANCEL : Return";
if (true) return "";
 break; }
case 1: {
 //BA.debugLineNum = 365;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406","41404")) {
case 0: 
case 1: 
case 2: {
 //BA.debugLineNum = 366;BA.debugLine="Case \"41401\", \"41405\", \"41406\" : sCode = \"*124";
mostCurrent._vvvvvvv2 = "*124#";
 break; }
case 3: {
 //BA.debugLineNum = 367;BA.debugLine="Case \"41404\" : sCode = \"*162\"' MPT CDMA";
mostCurrent._vvvvvvv2 = "*162";
 break; }
default: {
 //BA.debugLineNum = 368;BA.debugLine="Case Else : sCode = \"*123#\"' MECTel";
mostCurrent._vvvvvvv2 = "*123#";
 break; }
}
;
 break; }
case 2: {
 //BA.debugLineNum = 371;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41404","41405","41406")) {
case 0: 
case 1: {
 //BA.debugLineNum = 372;BA.debugLine="Case \"41401\", \"41404\" : sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 372;BA.debugLine="Case \"41401\", \"41404\" : sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv4 = "QER";
 break; }
case 2: {
 //BA.debugLineNum = 373;BA.debugLine="Case \"41405\" : sPhoneNumber = \"2230\" : sSMSBod";
mostCurrent._vvvvvvv3 = "2230";
 //BA.debugLineNum = 373;BA.debugLine="Case \"41405\" : sPhoneNumber = \"2230\" : sSMSBod";
mostCurrent._vvvvvvv4 = "b";
 break; }
case 3: {
 //BA.debugLineNum = 374;BA.debugLine="Case \"41406\" : sCode = \"*124*1#\"' Telenor";
mostCurrent._vvvvvvv2 = "*124*1#";
 break; }
default: {
 //BA.debugLineNum = 375;BA.debugLine="Case Else : sPhoneNumber = \"233\" : sSMSBody =";
mostCurrent._vvvvvvv3 = "233";
 //BA.debugLineNum = 375;BA.debugLine="Case Else : sPhoneNumber = \"233\" : sSMSBody =";
mostCurrent._vvvvvvv4 = "BAL";
 break; }
}
;
 break; }
}
;
 //BA.debugLineNum = 378;BA.debugLine="If sCode = \"\" Then";
if ((mostCurrent._vvvvvvv2).equals("")) { 
 //BA.debugLineNum = 379;BA.debugLine="ps.Send(sPhoneNumber, sSMSBody)";
mostCurrent._vvvvvvv5.Send(mostCurrent._vvvvvvv3,mostCurrent._vvvvvvv4);
 }else {
 //BA.debugLineNum = 381;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\"";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2,"UTF8"))));
 };
 //BA.debugLineNum = 383;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv2() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 683;BA.debugLine="Sub CLIR";
 //BA.debugLineNum = 684;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 686;BA.debugLine="sName = \"CLIR\"";
_sname = "CLIR";
 //BA.debugLineNum = 687;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြေခၚဆိုမည္";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြေခၚဆိုမည့္ အျခားဖုန္းမ်ားတြင္ ေခၚဆိုသူနံပါတ္ေဖာ္ျပျခင္းကို ေရွာင္ရွားလိုပါက အသံုးျပဳႏိုင္သည္။ သတိျပဳရန္မွာ ေအာ္ပေရတာတူ ဖုန္းအခ်င္းခ်င္းသာ အျပည့္အဝအလုပ္လုပ္ေဆာင္ပါမည္။<br>";
 //BA.debugLineNum = 688;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41405")) {
case 0: {
 //BA.debugLineNum = 690;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၂၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 691;BA.debugLine="sSubscription = \"*311#\"";
_ssubscription = "*311#";
 //BA.debugLineNum = 692;BA.debugLine="sUnsubscription = \"*311*0#\"";
_sunsubscription = "*311*0#";
 break; }
}
;
 //BA.debugLineNum = 695;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv6(_sname,_sdescription,mostCurrent._vvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 696;BA.debugLine="End Sub";
return "";
}
public static String  _cp_result(boolean _success,String _displayname,String _phonenumber,int _phonetype) throws Exception{
 //BA.debugLineNum = 469;BA.debugLine="Sub cp_Result (Success As Boolean, DisplayName As";
 //BA.debugLineNum = 470;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 471;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").RequestF";
mostCurrent._vvvvvvvv1.getViews().EditText("edtPhoneNumber").RequestFocus();
 //BA.debugLineNum = 472;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Text = P";
mostCurrent._vvvvvvvv1.getViews().EditText("edtPhoneNumber").setText((Object)(_phonenumber));
 };
 //BA.debugLineNum = 474;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _vvvvv4(int _paneltype) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwprepaid = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwoffers = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwsupport = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwstores = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwabout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblappname = null;
anywheresoftware.b4a.objects.LabelWrapper _lblappversion = null;
 //BA.debugLineNum = 211;BA.debugLine="Sub CreatePanel(PanelType As Int) As Panel";
 //BA.debugLineNum = 212;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 213;BA.debugLine="Dim lvwPrepaid, lvwOffers, lvwSupport, lvwStores,";
_lvwprepaid = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwoffers = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwsupport = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwstores = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwabout = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 214;BA.debugLine="Dim lblAppName, lblAppVersion As Label";
_lblappname = new anywheresoftware.b4a.objects.LabelWrapper();
_lblappversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 216;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 217;BA.debugLine="Select PanelType";
switch (BA.switchObjectToInt(_paneltype,_type_prepaid,_type_offers,_type_support,_type_stores,_type_about)) {
case 0: {
 //BA.debugLineNum = 219;BA.debugLine="lvwPrepaid.Initialize(\"lvwPrepaid\")";
_lvwprepaid.Initialize(mostCurrent.activityBA,"lvwPrepaid");
 //BA.debugLineNum = 220;BA.debugLine="lvwPrepaid.TwoLinesAndBitmap.Label.TextColor =";
_lvwprepaid.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 221;BA.debugLine="lvwPrepaid.TwoLinesAndBitmap.SecondLabel.Typefa";
_lvwprepaid.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 222;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Check Balance";
_lvwprepaid.AddTwoLinesAndBitmap2("Check Balance","ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"checkbalance.png").getObject()),(Object)(1));
 //BA.debugLineNum = 223;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Top Up\", \"ဖုန";
_lvwprepaid.AddTwoLinesAndBitmap2("Top Up","ဖုန္းေငြျဖည့္ရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"topup.png").getObject()),(Object)(2));
 //BA.debugLineNum = 224;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 226;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Yu Htar\", \"";
_lvwprepaid.AddTwoLinesAndBitmap2("Yu Htar","ဖုန္းေငြေခ်းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"kyothone.png").getObject()),(Object)(3));
 break; }
case 1: 
case 2: {
 //BA.debugLineNum = 228;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Kyo Thone\",";
_lvwprepaid.AddTwoLinesAndBitmap2("Kyo Thone","ဖုန္းေငြေခ်းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"kyothone.png").getObject()),(Object)(3));
 break; }
}
;
 //BA.debugLineNum = 230;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"4";
if ((_vvv0).equals("41404") == false && (_vvv0).equals("41406") == false) { 
_lvwprepaid.AddTwoLinesAndBitmap2("Balance Transfer","ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"balancetransfer.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 231;BA.debugLine="If SIMOperator = \"41405\" Then lvwPrepaid.AddTwo";
if ((_vvv0).equals("41405")) { 
_lvwprepaid.AddTwoLinesAndBitmap2("Top Me Up","မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"topmeup.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 232;BA.debugLine="pnl.AddView(lvwPrepaid, 0, 0, FILL_PARENT, FILL";
_pnl.AddView((android.view.View)(_lvwprepaid.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 1: {
 //BA.debugLineNum = 234;BA.debugLine="lvwOffers.Initialize(\"lvwOffers\")";
_lvwoffers.Initialize(mostCurrent.activityBA,"lvwOffers");
 //BA.debugLineNum = 235;BA.debugLine="lvwOffers.TwoLinesAndBitmap.Label.TextColor = C";
_lvwoffers.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 236;BA.debugLine="lvwOffers.TwoLinesAndBitmap.SecondLabel.Typefac";
_lvwoffers.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 237;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Plans\", \"Plan";
_lvwoffers.AddTwoLinesAndBitmap2("Plans","Plan မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(1));
 //BA.debugLineNum = 240;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Data Packs\", \"";
_lvwoffers.AddTwoLinesAndBitmap2("Data Packs","Data Pack မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(4));
 //BA.debugLineNum = 241;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Special Packs\"";
_lvwoffers.AddTwoLinesAndBitmap2("Special Packs","Special Pack မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(5));
 //BA.debugLineNum = 242;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Value Added Se";
_lvwoffers.AddTwoLinesAndBitmap2("Value Added Services","ထပ္ေဆာင္းဝန္ေဆာင္မႈမ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(6));
 //BA.debugLineNum = 243;BA.debugLine="pnl.AddView(lvwOffers, 0, 0, FILL_PARENT, FILL_";
_pnl.AddView((android.view.View)(_lvwoffers.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 2: {
 //BA.debugLineNum = 245;BA.debugLine="lvwSupport.Initialize(\"lvwSupport\")";
_lvwsupport.Initialize(mostCurrent.activityBA,"lvwSupport");
 //BA.debugLineNum = 246;BA.debugLine="lvwSupport.TwoLinesAndBitmap.Label.TextColor =";
_lvwsupport.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 247;BA.debugLine="lvwSupport.TwoLinesAndBitmap.SecondLabel.Typefa";
_lvwsupport.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 248;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv0).equals("41401") || (_vvv0).equals("41405") || (_vvv0).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("Call Me Back","မိမိဖုန္းအားျပန္ေခၚေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"callmeback.png").getObject()),(Object)(1));};
 //BA.debugLineNum = 249;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv0).equals("41401") || (_vvv0).equals("41405") || (_vvv0).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("What's My Number?","မိမိဖုန္းနံပါတ္ကိုစစ္ေဆးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"whatismynumber.png").getObject()),(Object)(2));};
 //BA.debugLineNum = 250;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv0).equals("41401") || (_vvv0).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("APN Settings","APN Setting မ်ားရယူရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"settings.png").getObject()),(Object)(3));};
 //BA.debugLineNum = 251;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv0).equals("41401") || (_vvv0).equals("41405") || (_vvv0).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("USSD","ဝန္ေဆာင္မႈမ်ားကိုစီမံရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ussd.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 252;BA.debugLine="lvwSupport.AddTwoLinesAndBitmap2(\"Call Centre\",";
_lvwsupport.AddTwoLinesAndBitmap2("Call Centre","အေထြေထြဝန္ေဆာင္မႈမ်ားကိုစံုစမ္းေမးျမန္းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"callcentre.png").getObject()),(Object)(5));
 //BA.debugLineNum = 253;BA.debugLine="pnl.AddView(lvwSupport, 0, 0, FILL_PARENT, FILL";
_pnl.AddView((android.view.View)(_lvwsupport.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 3: {
 //BA.debugLineNum = 255;BA.debugLine="lvwStores.Initialize(\"lvwStores\")";
_lvwstores.Initialize(mostCurrent.activityBA,"lvwStores");
 //BA.debugLineNum = 256;BA.debugLine="lvwStores.TwoLinesLayout.ItemHeight = 121dip";
_lvwstores.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (121)));
 //BA.debugLineNum = 257;BA.debugLine="lvwStores.TwoLinesLayout.Label.Top = 15dip";
_lvwstores.getTwoLinesLayout().Label.setTop(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15)));
 //BA.debugLineNum = 258;BA.debugLine="lvwStores.TwoLinesLayout.Label.Typeface = Smart";
_lvwstores.getTwoLinesLayout().Label.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 259;BA.debugLine="lvwStores.TwoLinesLayout.Label.Gravity = Gravit";
_lvwstores.getTwoLinesLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 260;BA.debugLine="lvwStores.TwoLinesLayout.Label.TextColor = Colo";
_lvwstores.getTwoLinesLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 261;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Top = lvwS";
_lvwstores.getTwoLinesLayout().SecondLabel.setTop((int) (_lvwstores.getTwoLinesLayout().Label.getTop()+_lvwstores.getTwoLinesLayout().Label.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 262;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Height = 5";
_lvwstores.getTwoLinesLayout().SecondLabel.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (59)));
 //BA.debugLineNum = 263;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Typeface =";
_lvwstores.getTwoLinesLayout().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 264;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Gravity =";
_lvwstores.getTwoLinesLayout().SecondLabel.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 265;BA.debugLine="lvwStores.AddTwoLines2(\"မေကြးေရွာ့ပင္းေမာ(လ္)\",";
_lvwstores.AddTwoLines2("မေကြးေရွာ့ပင္းေမာ(လ္)","အမွတ္(၈)၊ ဗိုလ္ခ်ဳပ္လမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ပြဲႀကိဳရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၆၇၁၁၊ ၀၆၃-၂၇၂၂၃။",(Object)(1));
 //BA.debugLineNum = 266;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မေကြး-၁)\", \"အမွ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မေကြး-၁)","အမွတ္(၁၀)၊ ျပည္ေတာ္သာလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ေဈးလယ္စိုးရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၅၄၅၂၊ ၀၆၃-၂၆၁၇၁။",(Object)(2));
 //BA.debugLineNum = 267;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မေကြး-၂)\", \"အမွ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မေကြး-၂)","အမွတ္(၃၆)၊ ဗိုလ္ခ်ဳပ္လမ္း ႏွင့္ မဲထီးလမ္းေထာင့္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ရြာသစ္ရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၃၆၉၂၊ ၀၆၃-၂၈၁၉၃။",(Object)(3));
 //BA.debugLineNum = 268;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မင္းဘူး-၁)\", \"အ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မင္းဘူး-၁)","အမွတ္(၈၁၂)၊ မင္းဘူး-စကုလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၃)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၅-၂၁၅၄၇။",(Object)(4));
 //BA.debugLineNum = 269;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မင္းဘူး-၂)\", \"အ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မင္းဘူး-၂)","အမွတ္(၉၄၈)၊ ေစ်းသစ္လမ္း ႏွင့္ ယင္းမာလမ္းေထာင့္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၄)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၉-၃၀၇၄၀၁၀၁။",(Object)(5));
 //BA.debugLineNum = 270;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်";
_lvwstores.AddTwoLines2("ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်ိေရး (မေကြး)","တတိယထပ္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"မေကြးေရွာ့ပင္းေမာ(လ္)၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၈၄၂၃၊ ၀၉-၇၇၇၇၇၉၉၀၀။",(Object)(6));
 //BA.debugLineNum = 271;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်";
_lvwstores.AddTwoLines2("ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်ိေရး (မင္းဘူး)","အမွတ္(၁၂၈၁)၊ ဆင္ေဂါင္းလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၄)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၅-၂၁၅၅၈၊ ၀၉-၇၇၇၇၇၈၈၀၀။",(Object)(7));
 //BA.debugLineNum = 272;BA.debugLine="pnl.AddView(lvwStores, 0, 0, FILL_PARENT, FILL_";
_pnl.AddView((android.view.View)(_lvwstores.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 4: {
 //BA.debugLineNum = 274;BA.debugLine="ivAppIcon.Initialize(\"\")";
mostCurrent._vvvvvvvvv3.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 275;BA.debugLine="ivAppIcon.Bitmap = AppIcon.Bitmap";
mostCurrent._vvvvvvvvv3.setBitmap(mostCurrent._vvvv6.getBitmap());
 //BA.debugLineNum = 276;BA.debugLine="ivAppIcon.Gravity = Gravity.FILL";
mostCurrent._vvvvvvvvv3.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 277;BA.debugLine="pnl.AddView(ivAppIcon, 50%x - 50dip, 10dip, 100";
_pnl.AddView((android.view.View)(mostCurrent._vvvvvvvvv3.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 279;BA.debugLine="lblAppName.Initialize(\"\")";
_lblappname.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 280;BA.debugLine="lblAppName.Text = AppName";
_lblappname.setText((Object)(_vvv4));
 //BA.debugLineNum = 281;BA.debugLine="lblAppName.Gravity = Gravity.CENTER";
_lblappname.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 282;BA.debugLine="lblAppName.TextSize = 19.5";
_lblappname.setTextSize((float) (19.5));
 //BA.debugLineNum = 283;BA.debugLine="lblAppName.TextColor = Colors.DarkGray";
_lblappname.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 284;BA.debugLine="pnl.AddView(lblAppName, 0, ivAppIcon.Top + ivAp";
_pnl.AddView((android.view.View)(_lblappname.getObject()),(int) (0),(int) (mostCurrent._vvvvvvvvv3.getTop()+mostCurrent._vvvvvvvvv3.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_wrap_content);
 //BA.debugLineNum = 286;BA.debugLine="lblAppVersion.Initialize(\"\")";
_lblappversion.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 287;BA.debugLine="lblAppVersion.Text = \"Version: \" & AppVersion";
_lblappversion.setText((Object)("Version: "+_vvv5));
 //BA.debugLineNum = 288;BA.debugLine="lblAppVersion.Gravity = Gravity.CENTER";
_lblappversion.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 289;BA.debugLine="lblAppVersion.TextSize = 16.5";
_lblappversion.setTextSize((float) (16.5));
 //BA.debugLineNum = 290;BA.debugLine="lblAppVersion.TextColor = -7829368";
_lblappversion.setTextColor((int) (-7829368));
 //BA.debugLineNum = 291;BA.debugLine="pnl.AddView(lblAppVersion, 0, lblAppName.Top +";
_pnl.AddView((android.view.View)(_lblappversion.getObject()),(int) (0),(int) (_lblappname.getTop()+mostCurrent._vvvvvvv7.MeasureMultilineTextHeight((android.widget.TextView)(_lblappname.getObject()),_lblappname.getText())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_wrap_content);
 //BA.debugLineNum = 293;BA.debugLine="line.Initialize(\"\")";
mostCurrent._vvvvv7.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 294;BA.debugLine="line.Color = Colors.LightGray";
mostCurrent._vvvvv7.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 295;BA.debugLine="pnl.AddView(line, 0, lblAppVersion.Top + su.Mea";
_pnl.AddView((android.view.View)(mostCurrent._vvvvv7.getObject()),(int) (0),(int) (_lblappversion.getTop()+mostCurrent._vvvvvvv7.MeasureMultilineTextHeight((android.widget.TextView)(_lblappversion.getObject()),_lblappversion.getText())+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),_fill_parent,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)));
 //BA.debugLineNum = 297;BA.debugLine="lvwAbout.Initialize(\"lvwAbout\")";
_lvwabout.Initialize(mostCurrent.activityBA,"lvwAbout");
 //BA.debugLineNum = 298;BA.debugLine="lvwAbout.SingleLineLayout.Label.Gravity = Gravi";
_lvwabout.getSingleLineLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 299;BA.debugLine="lvwAbout.SingleLineLayout.Label.TextSize = lblA";
_lvwabout.getSingleLineLayout().Label.setTextSize(_lblappversion.getTextSize());
 //BA.debugLineNum = 300;BA.debugLine="lvwAbout.SingleLineLayout.Label.TextColor = Col";
_lvwabout.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 301;BA.debugLine="lvwAbout.AddSingleLine2(\"Developer: \" & AppPubl";
_lvwabout.AddSingleLine2("Developer: "+_vvv6,(Object)(1));
 //BA.debugLineNum = 302;BA.debugLine="lvwAbout.AddSingleLine2(\"Like Us On Facebook\",";
_lvwabout.AddSingleLine2("Like Us On Facebook",(Object)(2));
 //BA.debugLineNum = 303;BA.debugLine="lvwAbout.AddSingleLine2(\"Rate This App\", 3)";
_lvwabout.AddSingleLine2("Rate This App",(Object)(3));
 //BA.debugLineNum = 304;BA.debugLine="pnl.AddView(lvwAbout, 0, line.Top + line.Height";
_pnl.AddView((android.view.View)(_lvwabout.getObject()),(int) (0),(int) (mostCurrent._vvvvv7.getTop()+mostCurrent._vvvvv7.getHeight()),_fill_parent,_fill_parent);
 break; }
}
;
 //BA.debugLineNum = 307;BA.debugLine="Return pnl";
if (true) return _pnl;
 //BA.debugLineNum = 308;BA.debugLine="End Sub";
return null;
}
public static String  _vvvvvvvvv4() throws Exception{
String _sname = "";
String _sdescription = "";
String _sactivation = "";
String _sdeactivation = "";
 //BA.debugLineNum = 633;BA.debugLine="Sub DataService";
 //BA.debugLineNum = 634;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sActivatio";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_sactivation = "";
_sdeactivation = "";
 //BA.debugLineNum = 636;BA.debugLine="sName = \"အင္တာနက္ဝန္ေဆာင္မႈ\"";
_sname = "အင္တာနက္ဝန္ေဆာင္မႈ";
 //BA.debugLineNum = 637;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 639;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 640;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 641;BA.debugLine="sActivation = \"Orderdata service\"";
_sactivation = "Orderdata service";
 break; }
case 1: {
 //BA.debugLineNum = 643;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ ရပ္ဆိုင္းရန္ သို႔မဟုတ္ ျပန္လည္အသံုးျပဳရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 644;BA.debugLine="sPhoneNumber = \"500\"";
mostCurrent._vvvvvvv3 = "500";
 //BA.debugLineNum = 645;BA.debugLine="sActivation = \"internet on\"";
_sactivation = "internet on";
 //BA.debugLineNum = 646;BA.debugLine="sDeactivation = \"internet off\"";
_sdeactivation = "internet off";
 break; }
default: {
 //BA.debugLineNum = 648;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 649;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvvv3 = "233";
 //BA.debugLineNum = 650;BA.debugLine="sActivation = \"Open EVDO\"";
_sactivation = "Open EVDO";
 break; }
}
;
 //BA.debugLineNum = 653;BA.debugLine="Plan(sName, sDescription, sPhoneNumber, sActivati";
_vvvvvvvvv5(_sname,_sdescription,mostCurrent._vvvvvvv3,_sactivation,_sdeactivation);
 //BA.debugLineNum = 654;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv6() throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 905;BA.debugLine="Sub GetPackageName As String";
 //BA.debugLineNum = 906;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 907;BA.debugLine="Return r.GetStaticField(\"anywheresoftware.b4a.BA\"";
if (true) return BA.ObjectToString(_r.GetStaticField("anywheresoftware.b4a.BA","packageName"));
 //BA.debugLineNum = 908;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 51;BA.debugLine="Private AppIcon As BitmapDrawable = pm.GetApplica";
mostCurrent._vvvv6 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._vvvv6.setObject((android.graphics.drawable.BitmapDrawable)(_vvvvvvvvv7.GetApplicationIcon(_vvvvvvvvv6())));
 //BA.debugLineNum = 53;BA.debugLine="Private xml As XmlLayoutBuilder";
mostCurrent._vvvvvvvv2 = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 55;BA.debugLine="Private ab As AHActionBar";
mostCurrent._vvvv2 = new de.amberhome.SimpleActionBar.ActionBarWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private overflowIcon As BitmapDrawable";
mostCurrent._vvvv4 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 57;BA.debugLine="Private menu As AHPopupMenu";
mostCurrent._vvvv1 = new de.amberhome.quickaction.ICSMenu();
 //BA.debugLineNum = 59;BA.debugLine="Private pnlBanner, pnlContent As Panel";
mostCurrent._pnlbanner = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pnlcontent = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Private asBanner() As String = Array As String(\"b";
mostCurrent._vvvv0 = new String[]{"banner.jpg","banner2.jpg","banner3.jpg"};
 //BA.debugLineNum = 62;BA.debugLine="Private containerBanner, container As AHPageConta";
mostCurrent._vvvv7 = new de.amberhome.viewpager.AHPageContainer();
mostCurrent._vvvvv3 = new de.amberhome.viewpager.AHPageContainer();
 //BA.debugLineNum = 63;BA.debugLine="Private pagerBanner, pager As AHViewPager";
mostCurrent._vvvvv1 = new de.amberhome.viewpager.AHViewPager();
mostCurrent._vvvvv5 = new de.amberhome.viewpager.AHViewPager();
 //BA.debugLineNum = 64;BA.debugLine="Private tabs As AHViewPagerTabs";
mostCurrent._vvvvv6 = new de.amberhome.viewpager.AHViewPagerTabs();
 //BA.debugLineNum = 65;BA.debugLine="Private line As Panel";
mostCurrent._vvvvv7 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Private ivAppIcon As ImageView";
mostCurrent._vvvvvvvvv3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private Dialog As DialogView";
mostCurrent._vvvvvvvv1 = new com.datasteam.b4a.xtraviews.DialogView();
 //BA.debugLineNum = 71;BA.debugLine="Private sCode, sPhoneNumber, sSMSBody As String";
mostCurrent._vvvvvvv2 = "";
mostCurrent._vvvvvvv3 = "";
mostCurrent._vvvvvvv4 = "";
 //BA.debugLineNum = 72;BA.debugLine="Private su As StringUtils";
mostCurrent._vvvvvvv7 = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 73;BA.debugLine="Private pc As PhoneCalls";
mostCurrent._vvvvvvv6 = new anywheresoftware.b4a.phone.Phone.PhoneCalls();
 //BA.debugLineNum = 74;BA.debugLine="Private ps As PhoneSms";
mostCurrent._vvvvvvv5 = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 76;BA.debugLine="Private AdMobBanner As AdView";
mostCurrent._vvvvv0 = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 77;BA.debugLine="Private AdMobInterstitial As InterstitialAd";
mostCurrent._vvvvvv2 = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 78;BA.debugLine="Private IsAlreadyShowAdMobInterstitial As Boolean";
_vvvvvv4 = false;
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv2() throws Exception{
 //BA.debugLineNum = 476;BA.debugLine="Sub KyoThone";
 //BA.debugLineNum = 477;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 478;BA.debugLine="Case \"41401\" : sCode = \"*800#\"' MPT";
mostCurrent._vvvvvvv2 = "*800#";
 break; }
case 1: {
 //BA.debugLineNum = 479;BA.debugLine="Case \"41405\" : sCode = \"*125#\"' Ooredoo";
mostCurrent._vvvvvvv2 = "*125#";
 break; }
case 2: {
 //BA.debugLineNum = 480;BA.debugLine="Case \"41406\" : sCode = \"*500#\"' Telenor";
mostCurrent._vvvvvvv2 = "*500#";
 break; }
}
;
 //BA.debugLineNum = 482;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2,"UTF8"))));
 //BA.debugLineNum = 483;BA.debugLine="End Sub";
return "";
}
public static String  _lvwabout_itemclick(int _position,Object _value) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 887;BA.debugLine="Sub lvwAbout_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 888;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvvv3();
 //BA.debugLineNum = 889;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3))) {
case 0: {
 //BA.debugLineNum = 891;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 892;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"fb://profile/10000";
_i.Initialize(_i.ACTION_VIEW,"fb://profile/100005753280868");
 //BA.debugLineNum = 893;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 break; }
case 1: {
 //BA.debugLineNum = 895;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 896;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"fb://page/66040136";
_i.Initialize(_i.ACTION_VIEW,"fb://page/660401367405456");
 //BA.debugLineNum = 897;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 break; }
case 2: {
 //BA.debugLineNum = 899;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 900;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"market://details?i";
_i.Initialize(_i.ACTION_VIEW,"market://details?id="+_vvvvvvvvv6());
 //BA.debugLineNum = 901;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 break; }
}
;
 //BA.debugLineNum = 903;BA.debugLine="End Sub";
return "";
}
public static String  _lvwlist_itemclick(int _position,Object _value) throws Exception{
anywheresoftware.b4a.objects.ListViewWrapper _lvw = null;
 //BA.debugLineNum = 385;BA.debugLine="Sub lvwList_ItemClick (Position As Int, Value As O";
 //BA.debugLineNum = 386;BA.debugLine="Dim lvw As ListView = Sender";
_lvw = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvw.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 387;BA.debugLine="If lvw.Tag = \"Value\" Then";
if ((_lvw.getTag()).equals((Object)("Value"))) { 
 //BA.debugLineNum = 388;BA.debugLine="Dialog.Dismiss(Value)";
mostCurrent._vvvvvvvv1.Dismiss((int)(BA.ObjectToNumber(_value)));
 }else {
 //BA.debugLineNum = 390;BA.debugLine="Dialog.Dismiss(Position)";
mostCurrent._vvvvvvvv1.Dismiss(_position);
 };
 //BA.debugLineNum = 392;BA.debugLine="End Sub";
return "";
}
public static String  _lvwoffers_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 563;BA.debugLine="Sub lvwOffers_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 564;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvvv3();
 //BA.debugLineNum = 565;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6))) {
case 0: {
 //BA.debugLineNum = 566;BA.debugLine="Case 1 : Plans";
_vvvvvvvvvv4();
 break; }
case 1: {
 //BA.debugLineNum = 567;BA.debugLine="Case 2 : Packs(\"Voice Packs\", \"voicePacks\")";
_vvvvvvvvvv5("Voice Packs","voicePacks");
 break; }
case 2: {
 //BA.debugLineNum = 568;BA.debugLine="Case 3 : Packs(\"SMS Packs\", \"smsPacks\")";
_vvvvvvvvvv5("SMS Packs","smsPacks");
 break; }
case 3: {
 //BA.debugLineNum = 569;BA.debugLine="Case 4 : Packs(\"Data Packs\", \"dataPacks\")";
_vvvvvvvvvv5("Data Packs","dataPacks");
 break; }
case 4: {
 //BA.debugLineNum = 570;BA.debugLine="Case 5 : Packs(\"Special Packs\", \"specialPacks\")";
_vvvvvvvvvv5("Special Packs","specialPacks");
 break; }
case 5: {
 //BA.debugLineNum = 571;BA.debugLine="Case 6 : ValueAddedServices";
_vvvvvvvvvv6();
 break; }
}
;
 //BA.debugLineNum = 573;BA.debugLine="End Sub";
return "";
}
public static String  _lvwprepaid_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 330;BA.debugLine="Sub lvwPrepaid_ItemClick (Position As Int, Value A";
 //BA.debugLineNum = 331;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvvv3();
 //BA.debugLineNum = 332;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5))) {
case 0: {
 //BA.debugLineNum = 333;BA.debugLine="Case 1 : CheckBalance";
_vvvvvvvvv1();
 break; }
case 1: {
 //BA.debugLineNum = 334;BA.debugLine="Case 2 : TopUp";
_vvvvvvvvvv7();
 break; }
case 2: {
 //BA.debugLineNum = 335;BA.debugLine="Case 3 : KyoThone";
_vvvvvvvvvv2();
 break; }
case 3: {
 //BA.debugLineNum = 336;BA.debugLine="Case 4 : BalanceTransfer";
_vvvvvvv0();
 break; }
case 4: {
 //BA.debugLineNum = 337;BA.debugLine="Case 5 : TopMeUp";
_vvvvvvvvvv0();
 break; }
}
;
 //BA.debugLineNum = 339;BA.debugLine="End Sub";
return "";
}
public static String  _lvwstores_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 874;BA.debugLine="Sub lvwStores_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 875;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvvv3();
 //BA.debugLineNum = 876;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6),(Object)(7))) {
case 0: {
 //BA.debugLineNum = 877;BA.debugLine="Case 1 : Call(Array As String(\"06326711\", \"06327";
_vvvvvvvv3(new String[]{"06326711","06327223"});
 break; }
case 1: {
 //BA.debugLineNum = 878;BA.debugLine="Case 2 : Call(Array As String(\"06325452\", \"06326";
_vvvvvvvv3(new String[]{"06325452","06326171"});
 break; }
case 2: {
 //BA.debugLineNum = 879;BA.debugLine="Case 3 : Call(Array As String(\"06323692\", \"06328";
_vvvvvvvv3(new String[]{"06323692","06328193"});
 break; }
case 3: {
 //BA.debugLineNum = 880;BA.debugLine="Case 4 : Call(Array As String(\"06521547\"))";
_vvvvvvvv3(new String[]{"06521547"});
 break; }
case 4: {
 //BA.debugLineNum = 881;BA.debugLine="Case 5 : Call(Array As String(\"0930740101\"))";
_vvvvvvvv3(new String[]{"0930740101"});
 break; }
case 5: {
 //BA.debugLineNum = 882;BA.debugLine="Case 6 : Call(Array As String(\"06328423\", \"09777";
_vvvvvvvv3(new String[]{"06328423","09777779900"});
 break; }
case 6: {
 //BA.debugLineNum = 883;BA.debugLine="Case 7 : Call(Array As String(\"06521558\", \"09777";
_vvvvvvvv3(new String[]{"06521558","09777778800"});
 break; }
}
;
 //BA.debugLineNum = 885;BA.debugLine="End Sub";
return "";
}
public static String  _lvwsupport_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 791;BA.debugLine="Sub lvwSupport_ItemClick (Position As Int, Value A";
 //BA.debugLineNum = 792;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvvv3();
 //BA.debugLineNum = 793;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5))) {
case 0: {
 //BA.debugLineNum = 794;BA.debugLine="Case 1 : CallMeBack";
_vvvvvvvv7();
 break; }
case 1: {
 //BA.debugLineNum = 795;BA.debugLine="Case 2 : WhatIsMyNumber";
_vvvvvvvvvvv1();
 break; }
case 2: {
 //BA.debugLineNum = 796;BA.debugLine="Case 3 : APNSettings";
_vvvvvvv1();
 break; }
case 3: {
 //BA.debugLineNum = 797;BA.debugLine="Case 4 : USSD";
_vvvvvvvvvvv2();
 break; }
case 4: {
 //BA.debugLineNum = 798;BA.debugLine="Case 5 : CallCentre";
_vvvvvvvv4();
 break; }
}
;
 //BA.debugLineNum = 800;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv3() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 740;BA.debugLine="Sub MissedCallAlert";
 //BA.debugLineNum = 741;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 743;BA.debugLine="sName = \"Missed Call Alert\"";
_sname = "Missed Call Alert";
 //BA.debugLineNum = 744;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြ၏ဖုန္း စက";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြ၏ဖုန္း စက္ပိတ္ထားသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ ဆက္သြယ္မႈဧရိယာျပင္ပသို႔ ေရာက္ရွိေနသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ မိတ္ေဆြထံေခၚဆိုရန္ႀကိဳးစားေသာ အဝင္ဖုန္းမ်ားအေရအတြက္ကို မိတ္ေဆြ၏ဖုန္း ကြန္ရက္ေပၚသို႔ျပန္လည္ေရာက္ရွိလာေသာအခါ SMS ျဖင့္ သတိေပးခ်က္အျဖစ္ ပို႔ေပးမည္ျဖစ္သည္။<br>";
 //BA.debugLineNum = 745;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 747;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 748;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 749;BA.debugLine="sSubscription = \"MCA\"";
_ssubscription = "MCA";
 //BA.debugLineNum = 750;BA.debugLine="sUnsubscription = \"MCA OFF\"";
_sunsubscription = "MCA OFF";
 break; }
case 1: {
 //BA.debugLineNum = 752;BA.debugLine="sDescription = sDescription & \"- တစ္ပတ္စာဝန္ေဆာ";
_sdescription = _sdescription+"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 753;BA.debugLine="sPhoneNumber = \"222\"";
mostCurrent._vvvvvvv3 = "222";
 //BA.debugLineNum = 754;BA.debugLine="sSubscription = \"MCA ON\"";
_ssubscription = "MCA ON";
 //BA.debugLineNum = 755;BA.debugLine="sUnsubscription = \"MCA OFF\"";
_sunsubscription = "MCA OFF";
 break; }
default: {
 //BA.debugLineNum = 757;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 758;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvvv3 = "233";
 //BA.debugLineNum = 759;BA.debugLine="sSubscription = \"Open MCA\"";
_ssubscription = "Open MCA";
 //BA.debugLineNum = 760;BA.debugLine="sUnsubscription = \"Cancel MCA\"";
_sunsubscription = "Cancel MCA";
 break; }
}
;
 //BA.debugLineNum = 763;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv6(_sname,_sdescription,mostCurrent._vvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 764;BA.debugLine="End Sub";
return "";
}
public static String  _opttopupother_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 455;BA.debugLine="Sub optTopUpOther_CheckedChange(Checked As Boolean";
 //BA.debugLineNum = 456;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 457;BA.debugLine="Dialog.Views.Label(\"lblPhoneNumber\").Visible = T";
mostCurrent._vvvvvvvv1.getViews().Label("lblPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 458;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Visible";
mostCurrent._vvvvvvvv1.getViews().EditText("edtPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 459;BA.debugLine="Dialog.Views.Button(\"btnContactPicker\").Visible";
mostCurrent._vvvvvvvv1.getViews().Button("btnContactPicker").setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 461;BA.debugLine="End Sub";
return "";
}
public static String  _opttopupown_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 447;BA.debugLine="Sub optTopUpOwn_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 448;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 449;BA.debugLine="Dialog.Views.Label(\"lblPhoneNumber\").Visible = F";
mostCurrent._vvvvvvvv1.getViews().Label("lblPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 450;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Visible";
mostCurrent._vvvvvvvv1.getViews().EditText("edtPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 451;BA.debugLine="Dialog.Views.Button(\"btnContactPicker\").Visible";
mostCurrent._vvvvvvvv1.getViews().Button("btnContactPicker").setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 453;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv5(String _title,String _pack) throws Exception{
 //BA.debugLineNum = 581;BA.debugLine="Sub Packs(Title As String, Pack As String)";
 //BA.debugLineNum = 582;BA.debugLine="OffersActivity.Title = Title";
mostCurrent._vvvvvvvvvv1._v5 = _title;
 //BA.debugLineNum = 583;BA.debugLine="OffersActivity.Offer = Pack";
mostCurrent._vvvvvvvvvv1._v6 = _pack;
 //BA.debugLineNum = 584;BA.debugLine="StartActivity(OffersActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvv1.getObject()));
 //BA.debugLineNum = 585;BA.debugLine="End Sub";
return "";
}
public static String  _pager_pagechanged(int _position) throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _ap = null;
flm.b4a.animationplus.AnimationPlusWrapper _ap2 = null;
flm.b4a.animationplus.AnimationSet _a = null;
 //BA.debugLineNum = 310;BA.debugLine="Sub Pager_PageChanged (Position As Int)";
 //BA.debugLineNum = 311;BA.debugLine="iCurrentPage = Position";
_vvvvvv7 = _position;
 //BA.debugLineNum = 312;BA.debugLine="pager.RequestFocus";
mostCurrent._vvvvv5.RequestFocus();
 //BA.debugLineNum = 313;BA.debugLine="If Position = 4 Then";
if (_position==4) { 
 //BA.debugLineNum = 314;BA.debugLine="Dim ap, ap2 As AnimationPlus";
_ap = new flm.b4a.animationplus.AnimationPlusWrapper();
_ap2 = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 315;BA.debugLine="Dim a As AnimationSet";
_a = new flm.b4a.animationplus.AnimationSet();
 //BA.debugLineNum = 316;BA.debugLine="ap.InitializeAlpha(\"\", 0, 1)";
_ap.InitializeAlpha(mostCurrent.activityBA,"",(float) (0),(float) (1));
 //BA.debugLineNum = 317;BA.debugLine="ap.StartOffset = 500";
_ap.setStartOffset((long) (500));
 //BA.debugLineNum = 318;BA.debugLine="ap.Duration = 800";
_ap.setDuration((long) (800));
 //BA.debugLineNum = 319;BA.debugLine="ap2.InitializeScaleCenter(\"\", 0, 0, 1, 1, ivAppI";
_ap2.InitializeScaleCenter(mostCurrent.activityBA,"",(float) (0),(float) (0),(float) (1),(float) (1),(android.view.View)(mostCurrent._vvvvvvvvv3.getObject()));
 //BA.debugLineNum = 320;BA.debugLine="ap2.Duration = 1500";
_ap2.setDuration((long) (1500));
 //BA.debugLineNum = 321;BA.debugLine="ap2.SetInterpolator(ap2.INTERPOLATOR_OVERSHOOT)";
_ap2.SetInterpolator(_ap2.INTERPOLATOR_OVERSHOOT);
 //BA.debugLineNum = 322;BA.debugLine="a.Initialize(False)";
_a.Initialize(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 323;BA.debugLine="a.AddAnimation(ap)";
_a.AddAnimation(_ap);
 //BA.debugLineNum = 324;BA.debugLine="a.AddAnimation(ap2)";
_a.AddAnimation(_ap2);
 //BA.debugLineNum = 325;BA.debugLine="a.PersistAfter = True";
_a.setPersistAfter(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 326;BA.debugLine="a.Start(ivAppIcon)";
_a.Start((android.view.View)(mostCurrent._vvvvvvvvv3.getObject()));
 };
 //BA.debugLineNum = 328;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv5(String _name,String _description,String _phonenumber,String _activation,String _deactivation) throws Exception{
String _sdeactivate = "";
int _iresult = 0;
 //BA.debugLineNum = 910;BA.debugLine="Sub Plan(Name As String, Description As String, Ph";
 //BA.debugLineNum = 911;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 912;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 913;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 914;BA.debugLine="Dim sDeactivate As String";
_sdeactivate = "";
 //BA.debugLineNum = 915;BA.debugLine="If Deactivation.Length > 0 Then sDeactivate = \"De";
if (_deactivation.length()>0) { 
_sdeactivate = "Deactivate";};
 //BA.debugLineNum = 916;BA.debugLine="Dim iResult As Int = Dialog.Msgbox(Name, Descript";
_iresult = mostCurrent._vvvvvvvv1.MsgBox(mostCurrent.activityBA,_name,_description,"Activate",_sdeactivate,"ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 917;BA.debugLine="If iResult = DialogResponse.POSITIVE Then";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 918;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 919;BA.debugLine="ps.Send(PhoneNumber, Activation)";
mostCurrent._vvvvvvv5.Send(_phonenumber,_activation);
 }else {
 //BA.debugLineNum = 921;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Activation,";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(_activation,"UTF8"))));
 };
 }else if(_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 924;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 925;BA.debugLine="ps.Send(PhoneNumber, Deactivation)";
mostCurrent._vvvvvvv5.Send(_phonenumber,_deactivation);
 }else {
 //BA.debugLineNum = 927;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Deactivation";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(_deactivation,"UTF8"))));
 };
 };
 //BA.debugLineNum = 930;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv4() throws Exception{
 //BA.debugLineNum = 575;BA.debugLine="Sub Plans";
 //BA.debugLineNum = 576;BA.debugLine="OffersActivity.Title = \"Plans\"";
mostCurrent._vvvvvvvvvv1._v5 = "Plans";
 //BA.debugLineNum = 577;BA.debugLine="OffersActivity.Offer = \"plans\"";
mostCurrent._vvvvvvvvvv1._v6 = "plans";
 //BA.debugLineNum = 578;BA.debugLine="StartActivity(OffersActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvvv1.getObject()));
 //BA.debugLineNum = 579;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
offersactivity._process_globals();
welcomeactivity._process_globals();
statemanager._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private pm As PackageManager";
_vvvvvvvvv7 = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Public Const AppName As String = pm.GetApplicatio";
_vvv4 = _vvvvvvvvv7.GetApplicationLabel(_vvvvvvvvv6());
 //BA.debugLineNum = 19;BA.debugLine="Public Const AppVersion As String = pm.GetVersion";
_vvv5 = _vvvvvvvvv7.GetVersionName(_vvvvvvvvv6());
 //BA.debugLineNum = 20;BA.debugLine="Public Const AppPublisher As String = \"Kyaw Swar";
_vvv6 = BA.__b (new byte[] {26,59,42,56,96,13,32,118,33,115,0,114,32,42,33}, 33164);
 //BA.debugLineNum = 22;BA.debugLine="Private Const ID_ACTION_HOME As Int = 0";
_id_action_home = (int) (0);
 //BA.debugLineNum = 23;BA.debugLine="Private Const ID_ACTION_OVERFLOW As Int = 99";
_id_action_overflow = (int) (99);
 //BA.debugLineNum = 25;BA.debugLine="Private Const SIMOperators As Map = CreateMap(\"41";
_vvvv3 = new anywheresoftware.b4a.objects.collections.Map();
_vvvv3 = anywheresoftware.b4a.keywords.Common.createMap(new Object[] {(Object)(BA.__b (new byte[] {101,115,40,30,113}, 106406)),(Object)(BA.__b (new byte[] {28,19,3}, 470494)),(Object)(BA.__b (new byte[] {101,114,-1,59,116}, 720871)),(Object)(BA.__b (new byte[] {28,18,57,25,3,26,60,32}, 79434)),(Object)(BA.__b (new byte[] {101,114,37,-40,115}, 589806)),(Object)(BA.__b (new byte[] {28,7,70,-93,37,50}, 152496)),(Object)(BA.__b (new byte[] {101,114,-41,-46,117}, 794167)),(Object)(BA.__b (new byte[] {30,47,15,43,36,51,14}, 968581)),(Object)(BA.__b (new byte[] {101,113,108,111,118}, 916509)),(Object)(BA.__b (new byte[] {5,38,-96,18,46,48,-94}, 708917))});
 //BA.debugLineNum = 27;BA.debugLine="Private tmrBanner As Timer";
_vvvvv2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 28;BA.debugLine="Private iCurrentBanner As Int = 0";
_vvvvvv6 = (int) (0);
 //BA.debugLineNum = 30;BA.debugLine="Private Const TYPE_PREPAID As Int = 1";
_type_prepaid = (int) (1);
 //BA.debugLineNum = 31;BA.debugLine="Private Const TYPE_OFFERS As Int = 2";
_type_offers = (int) (2);
 //BA.debugLineNum = 32;BA.debugLine="Private Const TYPE_SUPPORT As Int = 3";
_type_support = (int) (3);
 //BA.debugLineNum = 33;BA.debugLine="Private Const TYPE_STORES As Int = 4";
_type_stores = (int) (4);
 //BA.debugLineNum = 34;BA.debugLine="Private Const TYPE_ABOUT As Int = 5";
_type_about = (int) (5);
 //BA.debugLineNum = 36;BA.debugLine="Public SmartZawgyi As Typeface = Typeface.LoadFro";
_vvv7 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
_vvv7.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets(BA.__b (new byte[] {2,47,103,-2,52,4,123,-93,52,42,112,-9,35,55,100}, 153654))));
 //BA.debugLineNum = 38;BA.debugLine="Public Const FILL_PARENT As Int = -1";
_fill_parent = (int) (-1);
 //BA.debugLineNum = 39;BA.debugLine="Public Const WRAP_CONTENT As Int = -2";
_wrap_content = (int) (-2);
 //BA.debugLineNum = 41;BA.debugLine="Private iCurrentPage As Int";
_vvvvvv7 = 0;
 //BA.debugLineNum = 43;BA.debugLine="Private p As Phone";
_vvvvvvvvv0 = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 44;BA.debugLine="Public SIMOperator As String = p.GetSimOperator";
_vvv0 = _vvvvvvvvv0.GetSimOperator();
 //BA.debugLineNum = 46;BA.debugLine="Private Const AdMobBannerID As String = \"ca-app-p";
_vvvvvv1 = BA.__b (new byte[] {50,35,-123,-31,48,46,-103,-88,38,49,-102,-19,102,115,-108,-7,50,109,-112,-5,126,127,-127,-28,117,106,-97,-18,116,38,-102,-24,119,98,-117,-5,104,103}, 429704);
 //BA.debugLineNum = 47;BA.debugLine="Private Const AdMobInterstitialID As String = \"ca";
_vvvvvv3 = BA.__b (new byte[] {50,35,103,-89,48,46,123,-18,38,49,120,-85,102,115,118,-65,50,109,114,-67,126,127,99,-94,117,106,125,-88,113,47,126,-85,113,106,110,-69,104,101}, 32315);
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv4() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 656;BA.debugLine="Sub RingbackTone";
 //BA.debugLineNum = 657;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 659;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံသို႔ ဖု";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ ျပန္လည္ေျဖဆိုမႈကို ေစာင့္ဆိုင္းေနစဥ္အတြင္း မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားေသာ ဂီတသံစဥ္ကို ခံစားနားဆင္ေနႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 660;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41405","41406")) {
case 0: {
 //BA.debugLineNum = 662;BA.debugLine="sName = \"FunTone\"";
_sname = "FunTone";
 //BA.debugLineNum = 663;BA.debugLine="sDescription = sDescription & \"- တစ္ရက္စာဝန္ေဆာ";
_sdescription = _sdescription+"- တစ္ရက္စာဝန္ေဆာင္ခ ၇၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ ေန႔စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ လစဥ္ ၇၅၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- FunTone IVR နံပါတ္ ၃၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ FunTone အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 664;BA.debugLine="sSubscription = \"*3333#\"";
_ssubscription = "*3333#";
 //BA.debugLineNum = 665;BA.debugLine="sUnsubscription = \"*3333*0#\"";
_sunsubscription = "*3333*0#";
 break; }
case 1: {
 //BA.debugLineNum = 667;BA.debugLine="sName = \"My Tune\"";
_sname = "My Tune";
 //BA.debugLineNum = 668;BA.debugLine="sDescription = sDescription & \"- တစ္ပတ္စာဝန္ေဆာ";
_sdescription = _sdescription+"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ႏွစ္စဥ္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- My Tune IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ My Tune အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 669;BA.debugLine="sPhoneNumber = \"333\"";
mostCurrent._vvvvvvv3 = "333";
 //BA.debugLineNum = 670;BA.debugLine="sSubscription = \"MT ON\"";
_ssubscription = "MT ON";
 //BA.debugLineNum = 671;BA.debugLine="sUnsubscription = \"MT OFF\"";
_sunsubscription = "MT OFF";
 break; }
default: {
 //BA.debugLineNum = 673;BA.debugLine="sName = \"Hello Music\"";
_sname = "Hello Music";
 //BA.debugLineNum = 674;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၂၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ဝယ္ယူႏိုင္ပါသည္။<br>- Hello Music IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Hello Music အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 675;BA.debugLine="sPhoneNumber = \"333\"";
mostCurrent._vvvvvvv3 = "333";
 //BA.debugLineNum = 676;BA.debugLine="sSubscription = \"register\"";
_ssubscription = "register";
 //BA.debugLineNum = 677;BA.debugLine="sUnsubscription = \"unregister\"";
_sunsubscription = "unregister";
 break; }
}
;
 //BA.debugLineNum = 680;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv6(_sname,_sdescription,mostCurrent._vvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 681;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv3() throws Exception{
 //BA.debugLineNum = 990;BA.debugLine="Sub ShowAdMobInterstitial";
 //BA.debugLineNum = 991;BA.debugLine="If AdMobInterstitial.Ready And IsAlreadyShowAdMob";
if (mostCurrent._vvvvvv2.getReady() && _vvvvvv4==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 992;BA.debugLine="AdMobInterstitial.Show";
mostCurrent._vvvvvv2.Show();
 //BA.debugLineNum = 993;BA.debugLine="IsAlreadyShowAdMobInterstitial = True";
_vvvvvv4 = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 995;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbanner_tick() throws Exception{
 //BA.debugLineNum = 201;BA.debugLine="Sub tmrBanner_Tick";
 //BA.debugLineNum = 202;BA.debugLine="iCurrentBanner = iCurrentBanner + 1";
_vvvvvv6 = (int) (_vvvvvv6+1);
 //BA.debugLineNum = 203;BA.debugLine="If iCurrentBanner > asBanner.Length - 1 Then iCur";
if (_vvvvvv6>mostCurrent._vvvv0.length-1) { 
_vvvvvv6 = (int) (0);};
 //BA.debugLineNum = 204;BA.debugLine="pagerBanner.GotoPage(iCurrentBanner, True)";
mostCurrent._vvvvv1.GotoPage(_vvvvvv6,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv0() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
 //BA.debugLineNum = 527;BA.debugLine="Sub TopMeUp";
 //BA.debugLineNum = 528;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 529;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 530;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 531;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"BalanceTransferAndTopMeUpDialog");
 //BA.debugLineNum = 532;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 533;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 534;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 535;BA.debugLine="Dim lblAmount As Label = DialogLayout.Views.Get(\"";
_lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
_lblamount.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblAmount")));
 //BA.debugLineNum = 536;BA.debugLine="Dim edtAmount As EditText = DialogLayout.Views.Ge";
_edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtamount.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtAmount")));
 //BA.debugLineNum = 538;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtAmoun";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtamount.getTop()+_edtamount.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 540;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41405")) {
case 0: {
 //BA.debugLineNum = 541;BA.debugLine="Case \"41405\" : sCode = \"*126*\"' Ooredoo";
mostCurrent._vvvvvvv2 = "*126*";
 break; }
}
;
 //BA.debugLineNum = 544;BA.debugLine="lblPhoneNumber.Text = \"ေငြျဖည့္ေပးမည့္သူ၏ဖုန္းနံပ";
_lblphonenumber.setText((Object)("ေငြျဖည့္ေပးမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 546;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvv2.GetDrawable("17301547"));
 //BA.debugLineNum = 548;BA.debugLine="lblAmount.Text = \"ျဖည့္ေပးေစလိုေသာေငြပမာဏ႐ိုက္ထည္";
_lblamount.setText((Object)("ျဖည့္ေပးေစလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 550;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0 And edtAm";
while (!(_edtphonenumber.getText().length()>0 && _edtamount.getText().length()>0)) {
 //BA.debugLineNum = 551;BA.debugLine="If DialogLayout.Show(\"မိမိဖုန္းအားေငြျဖည့္ေပးပါရ";
if (_dialoglayout.Show("မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္","ပို႔ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 552;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 553;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 554;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_edtamount.getText().length()==0) { 
 //BA.debugLineNum = 556;BA.debugLine="edtAmount.RequestFocus";
_edtamount.RequestFocus();
 //BA.debugLineNum = 557;BA.debugLine="ToastMessageShow(\"ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပ";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 560;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPho";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtphonenumber.getText()+"*"+_edtamount.getText()+"#","UTF8"))));
 //BA.debugLineNum = 561;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv7() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _opttopupown = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _opttopupother = null;
anywheresoftware.b4a.objects.LabelWrapper _lblpin = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtpin = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
 //BA.debugLineNum = 394;BA.debugLine="Sub TopUp";
 //BA.debugLineNum = 395;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 396;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 397;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 398;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"TopUpDialog");
 //BA.debugLineNum = 399;BA.debugLine="Dim optTopUpOwn As RadioButton = DialogLayout.Vie";
_opttopupown = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
_opttopupown.setObject((android.widget.RadioButton)(_dialoglayout.getViews().Get("optTopUpOwn")));
 //BA.debugLineNum = 400;BA.debugLine="Dim optTopUpOther As RadioButton = DialogLayout.V";
_opttopupother = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
_opttopupother.setObject((android.widget.RadioButton)(_dialoglayout.getViews().Get("optTopUpOther")));
 //BA.debugLineNum = 401;BA.debugLine="Dim lblPIN As Label = DialogLayout.Views.Get(\"lbl";
_lblpin = new anywheresoftware.b4a.objects.LabelWrapper();
_lblpin.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPIN")));
 //BA.debugLineNum = 402;BA.debugLine="Dim edtPIN As EditText = DialogLayout.Views.Get(\"";
_edtpin = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtpin.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPIN")));
 //BA.debugLineNum = 403;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 404;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 405;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 407;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtPhone";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtphonenumber.getTop()+_edtphonenumber.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 409;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406","41404")) {
case 0: 
case 1: 
case 2: {
 //BA.debugLineNum = 410;BA.debugLine="Case \"41401\", \"41405\", \"41406\" : sCode = \"*123*\"";
mostCurrent._vvvvvvv2 = "*123*";
 break; }
case 3: {
 //BA.debugLineNum = 411;BA.debugLine="Case \"41404\" : sCode = \"*166*\"' MPT CDMA";
mostCurrent._vvvvvvv2 = "*166*";
 break; }
default: {
 //BA.debugLineNum = 412;BA.debugLine="Case Else : sCode = \"*124*\"' MECTel";
mostCurrent._vvvvvvv2 = "*124*";
 break; }
}
;
 //BA.debugLineNum = 415;BA.debugLine="optTopUpOwn.Text = \"မိမိဖုန္း\"";
_opttopupown.setText((Object)("မိမိဖုန္း"));
 //BA.debugLineNum = 417;BA.debugLine="If SIMOperator <> \"41401\" And SIMOperator <> \"414";
if ((_vvv0).equals("41401") == false && (_vvv0).equals("41405") == false && (_vvv0).equals("41406") == false) { 
_opttopupother.setEnabled(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 418;BA.debugLine="optTopUpOther.Text = \"အျခားဖုန္း\"";
_opttopupother.setText((Object)("အျခားဖုန္း"));
 //BA.debugLineNum = 420;BA.debugLine="lblPIN.Text = \"Top Up ကတ္မွ PIN နံပါတ္႐ိုက္ထည့္ပါ";
_lblpin.setText((Object)("Top Up ကတ္မွ PIN နံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 422;BA.debugLine="lblPhoneNumber.Text = \"ေငြျဖည့္ေပးလိုေသာဖုန္းနံပါ";
_lblphonenumber.setText((Object)("ေငြျဖည့္ေပးလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 424;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvvv2.GetDrawable("17301547"));
 //BA.debugLineNum = 426;BA.debugLine="Do Until (optTopUpOwn.Checked = True And edtPIN.T";
while (!((_opttopupown.getChecked()==anywheresoftware.b4a.keywords.Common.True && _edtpin.getText().length()>0) || (_opttopupother.getChecked()==anywheresoftware.b4a.keywords.Common.True && (_edtpin.getText().length()>0 && _edtphonenumber.getText().length()>0)))) {
 //BA.debugLineNum = 427;BA.debugLine="If DialogLayout.Show(\"ဖုန္းေငြျဖည့္ရန္\", \"ေငြျဖည";
if (_dialoglayout.Show("ဖုန္းေငြျဖည့္ရန္","ေငြျဖည့္ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 428;BA.debugLine="If edtPIN.Text.Length = 0 Then";
if (_edtpin.getText().length()==0) { 
 //BA.debugLineNum = 429;BA.debugLine="edtPIN.RequestFocus";
_edtpin.RequestFocus();
 //BA.debugLineNum = 430;BA.debugLine="ToastMessageShow(\"PIN နံပါတ္ေနရာကို ကြက္လပ္ထား၍";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("PIN နံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_opttopupother.getChecked()==anywheresoftware.b4a.keywords.Common.True && _edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 432;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 433;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 436;BA.debugLine="If optTopUpOwn.Checked Then";
if (_opttopupown.getChecked()) { 
 //BA.debugLineNum = 437;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPI";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtpin.getText()+"#","UTF8"))));
 }else if(_opttopupother.getChecked()) { 
 //BA.debugLineNum = 439;BA.debugLine="If SIMOperator = \"41405\" Then' Ooredoo";
if ((_vvv0).equals("41405")) { 
 //BA.debugLineNum = 440;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtP";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtphonenumber.getText()+"*"+_edtpin.getText()+"*1#","UTF8"))));
 }else {
 //BA.debugLineNum = 442;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtP";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2+_edtpin.getText()+"*"+_edtphonenumber.getText()+"#","UTF8"))));
 };
 };
 //BA.debugLineNum = 445;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv2() throws Exception{
 //BA.debugLineNum = 856;BA.debugLine="Sub USSD";
 //BA.debugLineNum = 857;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 858;BA.debugLine="Case \"41401\" : sCode = \"*106#\"' MPT";
mostCurrent._vvvvvvv2 = "*106#";
 break; }
case 1: {
 //BA.debugLineNum = 859;BA.debugLine="Case \"41405\" : sCode = \"*133#\"' Ooredoo";
mostCurrent._vvvvvvv2 = "*133#";
 break; }
case 2: {
 //BA.debugLineNum = 860;BA.debugLine="Case \"41406\" : sCode = \"*979#\"' Telenor";
mostCurrent._vvvvvvv2 = "*979#";
 break; }
}
;
 //BA.debugLineNum = 862;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2,"UTF8"))));
 //BA.debugLineNum = 863;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv6() throws Exception{
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
 //BA.debugLineNum = 587;BA.debugLine="Sub ValueAddedServices";
 //BA.debugLineNum = 588;BA.debugLine="Dim iResult As Int";
_iresult = 0;
 //BA.debugLineNum = 590;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 591;BA.debugLine="Dialog.Options.Dimensions.Height = 303dip";
mostCurrent._vvvvvvvv1.getOptions().Dimensions.Height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (303));
 //BA.debugLineNum = 592;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 593;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 594;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvvv1.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 595;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get(";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 597;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 598;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Typeface = SmartZ";
_lvwlist.getTwoLinesAndBitmap().Label.setTypeface((android.graphics.Typeface)(_vvv7.getObject()));
 //BA.debugLineNum = 599;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravity";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 600;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Color";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 602;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"414";
if ((_vvv0).equals("41404") == false && (_vvv0).equals("41405") == false) { 
_lvwlist.AddTwoLinesAndBitmap2("အင္တာနက္ဝန္ေဆာင္မႈ","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(1));};
 //BA.debugLineNum = 603;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41405","41406")) {
case 0: {
 //BA.debugLineNum = 606;BA.debugLine="lvwList.AddTwoLinesAndBitmap2(\"FunTone\", \"\", Lo";
_lvwlist.AddTwoLinesAndBitmap2("FunTone","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(2));
 break; }
case 1: {
 //BA.debugLineNum = 608;BA.debugLine="lvwList.AddTwoLinesAndBitmap2(\"My Tune\", \"\", Lo";
_lvwlist.AddTwoLinesAndBitmap2("My Tune","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(2));
 break; }
default: {
 //BA.debugLineNum = 610;BA.debugLine="lvwList.AddTwoLinesAndBitmap2(\"Hello Music\", \"\"";
_lvwlist.AddTwoLinesAndBitmap2("Hello Music","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(2));
 break; }
}
;
 //BA.debugLineNum = 612;BA.debugLine="If SIMOperator = \"41405\" Then lvwList.AddTwoLines";
if ((_vvv0).equals("41405")) { 
_lvwlist.AddTwoLinesAndBitmap2("CLIR","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(3));};
 //BA.debugLineNum = 613;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"41404\"";
if ((_vvv0).equals("41401") || (_vvv0).equals("41404")) { 
_lvwlist.AddTwoLinesAndBitmap2("Call Forwarding","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 614;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"41404\"";
if ((_vvv0).equals("41401") || (_vvv0).equals("41404")) { 
_lvwlist.AddTwoLinesAndBitmap2("Call Waiting","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 615;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"414";
if ((_vvv0).equals("41404") == false && (_vvv0).equals("41405") == false) { 
_lvwlist.AddTwoLinesAndBitmap2("Missed Call Alert","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(6));};
 //BA.debugLineNum = 616;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"414";
if ((_vvv0).equals("41404") == false && (_vvv0).equals("41405") == false) { 
_lvwlist.AddTwoLinesAndBitmap2("Voice Mail","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(7));};
 //BA.debugLineNum = 618;BA.debugLine="lvwList.Tag = \"Value\"";
_lvwlist.setTag((Object)("Value"));
 //BA.debugLineNum = 620;BA.debugLine="iResult = DialogLayout.Show(\"ထပ္ေဆာင္းဝန္ေဆာင္မႈမ";
_iresult = _dialoglayout.Show("ထပ္ေဆာင္းဝန္ေဆာင္မႈမ်ား","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 621;BA.debugLine="Select iResult";
switch (BA.switchObjectToInt(_iresult,anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL,(int) (1),(int) (2),(int) (3),(int) (4),(int) (5),(int) (6),(int) (7))) {
case 0: {
 //BA.debugLineNum = 622;BA.debugLine="Case DialogResponse.CANCEL : Return";
if (true) return "";
 break; }
case 1: {
 //BA.debugLineNum = 623;BA.debugLine="Case 1 : DataService";
_vvvvvvvvv4();
 break; }
case 2: {
 //BA.debugLineNum = 624;BA.debugLine="Case 2 : RingbackTone";
_vvvvvvvvvvv4();
 break; }
case 3: {
 //BA.debugLineNum = 625;BA.debugLine="Case 3 : CLIR";
_vvvvvvvvv2();
 break; }
case 4: {
 //BA.debugLineNum = 626;BA.debugLine="Case 4 : CallForwarding";
_vvvvvvvv5();
 break; }
case 5: {
 //BA.debugLineNum = 627;BA.debugLine="Case 5 : CallWaiting";
_vvvvvvvv0();
 break; }
case 6: {
 //BA.debugLineNum = 628;BA.debugLine="Case 6 : MissedCallAlert";
_vvvvvvvvvvv3();
 break; }
case 7: {
 //BA.debugLineNum = 629;BA.debugLine="Case 7 : VoiceMail";
_vvvvvvvvvvv5();
 break; }
}
;
 //BA.debugLineNum = 631;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv6(String _name,String _description,String _phonenumber,String _subscription,String _unsubscription) throws Exception{
int _iresult = 0;
 //BA.debugLineNum = 932;BA.debugLine="Sub VAS(Name As String, Description As String, Pho";
 //BA.debugLineNum = 933;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvvv1.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 934;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 935;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvvv1.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv7.getObject());
 //BA.debugLineNum = 936;BA.debugLine="Dim iResult As Int = Dialog.Msgbox(Name, Descript";
_iresult = mostCurrent._vvvvvvvv1.MsgBox(mostCurrent.activityBA,_name,_description,"Subscribe","Unsubscribe","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 937;BA.debugLine="If iResult = DialogResponse.POSITIVE Then";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 938;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 939;BA.debugLine="ps.Send(PhoneNumber, Subscription)";
mostCurrent._vvvvvvv5.Send(_phonenumber,_subscription);
 }else {
 //BA.debugLineNum = 941;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Subscription";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(_subscription,"UTF8"))));
 };
 }else if(_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 944;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 945;BA.debugLine="ps.Send(PhoneNumber, Unsubscription)";
mostCurrent._vvvvvvv5.Send(_phonenumber,_unsubscription);
 }else {
 //BA.debugLineNum = 947;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Unsubscripti";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(_unsubscription,"UTF8"))));
 };
 };
 //BA.debugLineNum = 950;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv5() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 766;BA.debugLine="Sub VoiceMail";
 //BA.debugLineNum = 767;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvvv3 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 769;BA.debugLine="sName = \"Voice Mail\"";
_sname = "Voice Mail";
 //BA.debugLineNum = 770;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းမကို";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းမကိုင္ႏိုင္သည့္အခ်ိန္တြင္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ အသံျဖင့္ အမွာစကားခ်န္ထားခဲ့ႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 771;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 773;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။<br>- Voice Mail IVR နံပါတ္ ၁၅၅၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 774;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvvv3 = "1331";
 //BA.debugLineNum = 775;BA.debugLine="sSubscription = \"VMS\"";
_ssubscription = "VMS";
 //BA.debugLineNum = 776;BA.debugLine="sUnsubscription = \"VMS OFF\"";
_sunsubscription = "VMS OFF";
 break; }
case 1: {
 //BA.debugLineNum = 778;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၀၀ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 779;BA.debugLine="sSubscription = \"*979*2*3*1*1#\"";
_ssubscription = "*979*2*3*1*1#";
 //BA.debugLineNum = 780;BA.debugLine="sUnsubscription = \"*979*2*3*2*1#\"";
_sunsubscription = "*979*2*3*2*1#";
 break; }
default: {
 //BA.debugLineNum = 782;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၄၄၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 783;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvvv3 = "233";
 //BA.debugLineNum = 784;BA.debugLine="sSubscription = \"Open VMS\"";
_ssubscription = "Open VMS";
 //BA.debugLineNum = 785;BA.debugLine="sUnsubscription = \"Cancel VMS\"";
_sunsubscription = "Cancel VMS";
 break; }
}
;
 //BA.debugLineNum = 788;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv6(_sname,_sdescription,mostCurrent._vvvvvvv3,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 789;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvv1() throws Exception{
 //BA.debugLineNum = 833;BA.debugLine="Sub WhatIsMyNumber";
 //BA.debugLineNum = 834;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv0,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 835;BA.debugLine="Case \"41401\" : sCode = \"*88#\"' MPT";
mostCurrent._vvvvvvv2 = "*88#";
 break; }
case 1: {
 //BA.debugLineNum = 836;BA.debugLine="Case \"41405\" : sCode = \"*133*5#\"' Ooredoo";
mostCurrent._vvvvvvv2 = "*133*5#";
 break; }
case 2: {
 //BA.debugLineNum = 837;BA.debugLine="Case \"41406\" : sCode = \"*97#\"' Telenor";
mostCurrent._vvvvvvv2 = "*97#";
 break; }
}
;
 //BA.debugLineNum = 839;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv6.Call(mostCurrent._vvvvvvv7.EncodeUrl(mostCurrent._vvvvvvv2,"UTF8"))));
 //BA.debugLineNum = 840;BA.debugLine="End Sub";
return "";
}
}
