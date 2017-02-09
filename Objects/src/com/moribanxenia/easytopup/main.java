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

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.phone.PackageManagerWrapper _vvvvvvvvv2 = null;
public static String _vvv3 = "";
public static String _vvv4 = "";
public static String _vvv5 = "";
public static int _id_action_home = 0;
public static int _id_action_overflow = 0;
public static anywheresoftware.b4a.objects.collections.Map _vvvv2 = null;
public static anywheresoftware.b4a.objects.Timer _vvvv0 = null;
public static int _vvvvv7 = 0;
public static int _type_prepaid = 0;
public static int _type_offers = 0;
public static int _type_support = 0;
public static int _type_stores = 0;
public static int _type_about = 0;
public static anywheresoftware.b4a.keywords.constants.TypefaceWrapper _vvv6 = null;
public static int _fill_parent = 0;
public static int _wrap_content = 0;
public static int _vvvvv0 = 0;
public static anywheresoftware.b4a.phone.Phone _vvvvvvvvv3 = null;
public static String _vvv7 = "";
public static String _vvvvvv2 = "";
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _vvvv4 = null;
public anywheresoftware.b4a.object.XmlLayoutBuilder _vvvvvvv5 = null;
public de.amberhome.SimpleActionBar.ActionBarWrapper _vvvv1 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _vvvv3 = null;
public de.amberhome.quickaction.ICSMenu _vvv0 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbanner = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlcontent = null;
public static String[] _vvvv6 = null;
public de.amberhome.viewpager.AHPageContainer _vvvv5 = null;
public de.amberhome.viewpager.AHPageContainer _vvvvv1 = null;
public de.amberhome.viewpager.AHViewPager _vvvv7 = null;
public de.amberhome.viewpager.AHViewPager _vvvvv3 = null;
public de.amberhome.viewpager.AHViewPagerTabs _vvvvv4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvv5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblfooter = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvv6 = null;
public com.datasteam.b4a.xtraviews.DialogView _vvvvvvv4 = null;
public static String _vvvvvv5 = "";
public static String _vvvvvv6 = "";
public static String _vvvvvv7 = "";
public anywheresoftware.b4a.objects.StringUtils _vvvvvvv2 = null;
public anywheresoftware.b4a.phone.Phone.PhoneCalls _vvvvvvv1 = null;
public anywheresoftware.b4a.phone.Phone.PhoneSms _vvvvvv0 = null;
public mobi.mindware.admob.interstitial.AdmobInterstitialsAds _vvvvvv1 = null;
public com.moribanxenia.easytopup.offersactivity _vvvvvvvvv4 = null;
public com.moribanxenia.easytopup.welcomeactivity _vvvvvv3 = null;
public com.moribanxenia.easytopup.statemanager _vvvvv6 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (offersactivity.mostCurrent != null);
vis = vis | (welcomeactivity.mostCurrent != null);
return vis;}
public static String  _ab_itemclicked(int _itemid) throws Exception{
 //BA.debugLineNum = 185;BA.debugLine="Sub AB_ItemClicked(ItemID As Int)";
 //BA.debugLineNum = 186;BA.debugLine="Select ItemID";
switch (BA.switchObjectToInt(_itemid,_id_action_overflow)) {
case 0: {
 //BA.debugLineNum = 188;BA.debugLine="menu.Show(ab.GetActionView(ItemID))";
mostCurrent._vvv0.Show(mostCurrent._vvvv1.GetActionView(_itemid));
 break; }
}
;
 //BA.debugLineNum = 190;BA.debugLine="End Sub";
return "";
}
public static String  _ac_click(int _position,int _actionitemid) throws Exception{
String _soldsimoperator = "";
 //BA.debugLineNum = 192;BA.debugLine="Sub AC_Click (Position As Int, ActionItemID As Int";
 //BA.debugLineNum = 193;BA.debugLine="Dim sOldSIMOperator As String = SIMOperator";
_soldsimoperator = _vvv7;
 //BA.debugLineNum = 194;BA.debugLine="SIMOperator = SIMOperators.GetKeyAt(Position)";
_vvv7 = BA.ObjectToString(_vvvv2.GetKeyAt(_position));
 //BA.debugLineNum = 195;BA.debugLine="If sOldSIMOperator <> SIMOperator Then Activity_C";
if ((_soldsimoperator).equals(_vvv7) == false) { 
_activity_create(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 196;BA.debugLine="End Sub";
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
if (_firsttime || mostCurrent._vvvv1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 88;BA.debugLine="menu.Initialize(\"AC\")";
mostCurrent._vvv0.Initialize(processBA,"AC");
 //BA.debugLineNum = 89;BA.debugLine="For i = 0 To SIMOperators.Size - 1";
{
final int step6 = 1;
final int limit6 = (int) (_vvvv2.getSize()-1);
for (_i = (int) (0) ; (step6 > 0 && _i <= limit6) || (step6 < 0 && _i >= limit6); _i = ((int)(0 + _i + step6)) ) {
 //BA.debugLineNum = 90;BA.debugLine="Dim ai As AHActionItem";
_ai = new de.amberhome.quickaction.ActionItem();
 //BA.debugLineNum = 91;BA.debugLine="ai.Initialize(i, SIMOperators.GetValueAt(i), Nu";
_ai.Initialize(_i,BA.ObjectToString(_vvvv2.GetValueAt(_i)),(android.graphics.drawable.Drawable)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 92;BA.debugLine="menu.AddActionItem(ai)";
mostCurrent._vvv0.AddActionItem(_ai);
 }
};
 };
 //BA.debugLineNum = 96;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 98;BA.debugLine="overflowIcon.Initialize(LoadBitmap(File.DirAssets";
mostCurrent._vvvv3.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ic_action_overflow.png").getObject()));
 //BA.debugLineNum = 100;BA.debugLine="ab.Initialize(\"AB\")";
mostCurrent._vvvv1.Initialize(mostCurrent.activityBA,"AB");
 //BA.debugLineNum = 101;BA.debugLine="ab.SubTitle = SIMOperators.GetDefault(SIMOperator";
mostCurrent._vvvv1.setSubTitle((java.lang.CharSequence)(_vvvv2.GetDefault((Object)(_vvv7),(Object)("MECTel"))));
 //BA.debugLineNum = 102;BA.debugLine="ab.AddHomeAction(ID_ACTION_HOME, AppIcon)";
mostCurrent._vvvv1.AddHomeAction(_id_action_home,(android.graphics.drawable.Drawable)(mostCurrent._vvvv4.getObject()));
 //BA.debugLineNum = 103;BA.debugLine="ab.AddAction(ID_ACTION_OVERFLOW, overflowIcon)";
mostCurrent._vvvv1.AddAction(_id_action_overflow,(android.graphics.drawable.Drawable)(mostCurrent._vvvv3.getObject()));
 //BA.debugLineNum = 104;BA.debugLine="Activity.AddView(ab, 0, 0, 100%x, 48dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvv1.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (48)));
 //BA.debugLineNum = 106;BA.debugLine="containerBanner.Initialize";
mostCurrent._vvvv5.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 107;BA.debugLine="For i = 0 To asBanner.Length - 1";
{
final int step20 = 1;
final int limit20 = (int) (mostCurrent._vvvv6.length-1);
for (_i = (int) (0) ; (step20 > 0 && _i <= limit20) || (step20 < 0 && _i >= limit20); _i = ((int)(0 + _i + step20)) ) {
 //BA.debugLineNum = 108;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 109;BA.debugLine="iv.Initialize(\"\")";
_iv.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 110;BA.debugLine="iv.Bitmap = LoadBitmap(File.DirAssets, asBanner(";
_iv.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),mostCurrent._vvvv6[_i]).getObject()));
 //BA.debugLineNum = 111;BA.debugLine="iv.Gravity = Gravity.FILL";
_iv.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 112;BA.debugLine="pnl.AddView(iv, 0, 0, FILL_PARENT, FILL_PARENT)";
_pnl.AddView((android.view.View)(_iv.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 //BA.debugLineNum = 113;BA.debugLine="containerBanner.AddPage(pnl, \"\")";
mostCurrent._vvvv5.AddPage((android.view.View)(_pnl.getObject()),"");
 }
};
 //BA.debugLineNum = 115;BA.debugLine="pagerBanner.Initialize(\"Banner\")";
mostCurrent._vvvv7.Initialize(mostCurrent.activityBA,"Banner");
 //BA.debugLineNum = 116;BA.debugLine="pagerBanner.PageContainer = containerBanner";
mostCurrent._vvvv7.setPageContainer(mostCurrent._vvvv5);
 //BA.debugLineNum = 117;BA.debugLine="pnlBanner.AddView(pagerBanner, 0, 0, 100%x, 37%x)";
mostCurrent._pnlbanner.AddView((android.view.View)(mostCurrent._vvvv7.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA));
 //BA.debugLineNum = 118;BA.debugLine="tmrBanner.Initialize(\"tmrBanner\", 8000)";
_vvvv0.Initialize(processBA,"tmrBanner",(long) (8000));
 //BA.debugLineNum = 119;BA.debugLine="tmrBanner.Enabled = True";
_vvvv0.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 121;BA.debugLine="container.Initialize";
mostCurrent._vvvvv1.Initialize(mostCurrent.activityBA);
 //BA.debugLineNum = 122;BA.debugLine="For i = 0 To 4";
{
final int step34 = 1;
final int limit34 = (int) (4);
for (_i = (int) (0) ; (step34 > 0 && _i <= limit34) || (step34 < 0 && _i >= limit34); _i = ((int)(0 + _i + step34)) ) {
 //BA.debugLineNum = 123;BA.debugLine="Select i";
switch (_i) {
case 0: {
 //BA.debugLineNum = 125;BA.debugLine="pnl = CreatePanel(TYPE_PREPAID)";
_pnl = _vvvvv2(_type_prepaid);
 //BA.debugLineNum = 126;BA.debugLine="container.AddPage(pnl, \"Prepaid\")";
mostCurrent._vvvvv1.AddPage((android.view.View)(_pnl.getObject()),"Prepaid");
 break; }
case 1: {
 //BA.debugLineNum = 128;BA.debugLine="pnl = CreatePanel(TYPE_OFFERS)";
_pnl = _vvvvv2(_type_offers);
 //BA.debugLineNum = 129;BA.debugLine="container.AddPage(pnl, \"Offers\")";
mostCurrent._vvvvv1.AddPage((android.view.View)(_pnl.getObject()),"Offers");
 break; }
case 2: {
 //BA.debugLineNum = 131;BA.debugLine="pnl = CreatePanel(TYPE_SUPPORT)";
_pnl = _vvvvv2(_type_support);
 //BA.debugLineNum = 132;BA.debugLine="container.AddPage(pnl, \"Support\")";
mostCurrent._vvvvv1.AddPage((android.view.View)(_pnl.getObject()),"Support");
 break; }
case 3: {
 //BA.debugLineNum = 134;BA.debugLine="pnl = CreatePanel(TYPE_STORES)";
_pnl = _vvvvv2(_type_stores);
 //BA.debugLineNum = 135;BA.debugLine="container.AddPage(pnl, \"Stores\")";
mostCurrent._vvvvv1.AddPage((android.view.View)(_pnl.getObject()),"Stores");
 break; }
case 4: {
 //BA.debugLineNum = 137;BA.debugLine="pnl = CreatePanel(TYPE_ABOUT)";
_pnl = _vvvvv2(_type_about);
 //BA.debugLineNum = 138;BA.debugLine="container.AddPage(pnl, \"About\")";
mostCurrent._vvvvv1.AddPage((android.view.View)(_pnl.getObject()),"About");
 break; }
}
;
 }
};
 //BA.debugLineNum = 142;BA.debugLine="pager.Initialize(\"Pager\")";
mostCurrent._vvvvv3.Initialize(mostCurrent.activityBA,"Pager");
 //BA.debugLineNum = 143;BA.debugLine="pager.PageContainer = container";
mostCurrent._vvvvv3.setPageContainer(mostCurrent._vvvvv1);
 //BA.debugLineNum = 145;BA.debugLine="tabs.Initialize(pager)";
mostCurrent._vvvvv4.Initialize(mostCurrent.activityBA,mostCurrent._vvvvv3);
 //BA.debugLineNum = 146;BA.debugLine="tabs.LineHeight = 5dip";
mostCurrent._vvvvv4.setLineHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (5)));
 //BA.debugLineNum = 147;BA.debugLine="tabs.UpperCaseTitle = True";
mostCurrent._vvvvv4.setUpperCaseTitle(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 148;BA.debugLine="tabs.TextColor = Colors.LightGray";
mostCurrent._vvvvv4.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 149;BA.debugLine="tabs.TextColorCenter = Colors.DarkGray";
mostCurrent._vvvvv4.setTextColorCenter(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 150;BA.debugLine="tabs.LineColorCenter = Colors.DarkGray";
mostCurrent._vvvvv4.setLineColorCenter(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 151;BA.debugLine="tabs.BackgroundColorPressed = Colors.RGB(51, 181,";
mostCurrent._vvvvv4.setBackgroundColorPressed(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (51),(int) (181),(int) (229)));
 //BA.debugLineNum = 152;BA.debugLine="pnlContent.AddView(tabs, 0, 0, FILL_PARENT, WRAP_";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvv4.getObject()),(int) (0),(int) (0),_fill_parent,_wrap_content);
 //BA.debugLineNum = 154;BA.debugLine="line.Initialize(\"\")";
mostCurrent._vvvvv5.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 155;BA.debugLine="line.Color = tabs.LineColorCenter";
mostCurrent._vvvvv5.setColor(mostCurrent._vvvvv4.getLineColorCenter());
 //BA.debugLineNum = 156;BA.debugLine="pnlContent.AddView(line, 0, tabs.Top + tabs.Heigh";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvv5.getObject()),(int) (0),(int) (mostCurrent._vvvvv4.getTop()+mostCurrent._vvvvv4.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (35))),mostCurrent._pnlcontent.getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2)));
 //BA.debugLineNum = 158;BA.debugLine="pnlContent.AddView(pager, 0, line.Top + line.Heig";
mostCurrent._pnlcontent.AddView((android.view.View)(mostCurrent._vvvvv3.getObject()),(int) (0),(int) (mostCurrent._vvvvv5.getTop()+mostCurrent._vvvvv5.getHeight()),mostCurrent._pnlcontent.getWidth(),(int) (mostCurrent._pnlcontent.getHeight()-(mostCurrent._vvvvv5.getTop()+mostCurrent._vvvvv5.getHeight())));
 //BA.debugLineNum = 160;BA.debugLine="lblFooter.Text = \"Developed By: \" & AppPublisher";
mostCurrent._lblfooter.setText((Object)("Developed By: "+_vvv5));
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 177;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 178;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_MENU Then AB_ItemCl";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MENU) { 
_ab_itemclicked(_id_action_overflow);};
 //BA.debugLineNum = 179;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 181;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 182;BA.debugLine="StateManager.SaveSettings";
mostCurrent._vvvvv6._vv0(mostCurrent.activityBA);
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 164;BA.debugLine="Activity.Title = AppName";
mostCurrent._activity.setTitle((Object)(_vvv3));
 //BA.debugLineNum = 166;BA.debugLine="pagerBanner.GotoPage(iCurrentBanner, False)";
mostCurrent._vvvv7.GotoPage(_vvvvv7,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 167;BA.debugLine="pager.GotoPage(iCurrentPage, False)";
mostCurrent._vvvvv3.GotoPage(_vvvvv0,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 169;BA.debugLine="AdMobInterstitial.Initialize(\"AdMobInterstitial\",";
mostCurrent._vvvvvv1.Initialize(mostCurrent.activityBA,"AdMobInterstitial",_vvvvvv2);
 //BA.debugLineNum = 170;BA.debugLine="AdMobInterstitial.LoadAd";
mostCurrent._vvvvvv1.LoadAd(mostCurrent.activityBA);
 //BA.debugLineNum = 172;BA.debugLine="If StateManager.GetSetting2(\"FirstTime\", True) Th";
if (BA.ObjectToBoolean(mostCurrent._vvvvv6._vv5(mostCurrent.activityBA,"FirstTime",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True)))) { 
 //BA.debugLineNum = 173;BA.debugLine="StartActivity(WelcomeActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvv3.getObject()));
 };
 //BA.debugLineNum = 175;BA.debugLine="End Sub";
return "";
}
public static String  _admobinterstitial_adclosed() throws Exception{
 //BA.debugLineNum = 978;BA.debugLine="Sub AdMobInterstitial_AdClosed";
 //BA.debugLineNum = 979;BA.debugLine="Log(\"Ad Closed\")";
anywheresoftware.b4a.keywords.Common.Log("Ad Closed");
 //BA.debugLineNum = 980;BA.debugLine="AdMobInterstitial.LoadAd";
mostCurrent._vvvvvv1.LoadAd(mostCurrent.activityBA);
 //BA.debugLineNum = 981;BA.debugLine="End Sub";
return "";
}
public static String  _admobinterstitial_adfailedtoload(String _errormessage) throws Exception{
 //BA.debugLineNum = 983;BA.debugLine="Sub AdMobInterstitial_AdFailedToLoad (ErrorMessage";
 //BA.debugLineNum = 984;BA.debugLine="Log(\"Ad Failed To Load: \" & ErrorMessage)";
anywheresoftware.b4a.keywords.Common.Log("Ad Failed To Load: "+_errormessage);
 //BA.debugLineNum = 985;BA.debugLine="AdMobInterstitial.LoadAd";
mostCurrent._vvvvvv1.LoadAd(mostCurrent.activityBA);
 //BA.debugLineNum = 986;BA.debugLine="End Sub";
return "";
}
public static String  _admobinterstitial_adloaded() throws Exception{
 //BA.debugLineNum = 974;BA.debugLine="Sub AdMobInterstitial_AdLoaded";
 //BA.debugLineNum = 975;BA.debugLine="Log(\"Ad Loaded\")";
anywheresoftware.b4a.keywords.Common.Log("Ad Loaded");
 //BA.debugLineNum = 976;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvv4() throws Exception{
 //BA.debugLineNum = 838;BA.debugLine="Sub APNSettings";
 //BA.debugLineNum = 839;BA.debugLine="sCode = \"\"";
mostCurrent._vvvvvv5 = "";
 //BA.debugLineNum = 841;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 842;BA.debugLine="Case \"41401\" : sPhoneNumber = \"6006\" : sSMSBody";
mostCurrent._vvvvvv6 = "6006";
 //BA.debugLineNum = 842;BA.debugLine="Case \"41401\" : sPhoneNumber = \"6006\" : sSMSBody";
mostCurrent._vvvvvv7 = "APN Settings";
 break; }
case 1: {
 //BA.debugLineNum = 843;BA.debugLine="Case \"41405\" : sCode = \"*133*6*1#\"' Ooredoo";
mostCurrent._vvvvvv5 = "*133*6*1#";
 break; }
case 2: {
 //BA.debugLineNum = 844;BA.debugLine="Case \"41406\" : sCode = \"*979*3*1*3#\"' Telenor";
mostCurrent._vvvvvv5 = "*979*3*1*3#";
 break; }
}
;
 //BA.debugLineNum = 846;BA.debugLine="If sCode = \"\" Then";
if ((mostCurrent._vvvvvv5).equals("")) { 
 //BA.debugLineNum = 847;BA.debugLine="ps.Send(sPhoneNumber, sSMSBody)";
mostCurrent._vvvvvv0.Send(mostCurrent._vvvvvv6,mostCurrent._vvvvvv7);
 }else {
 //BA.debugLineNum = 849;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\"";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5,"UTF8"))));
 };
 //BA.debugLineNum = 851;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv3() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
 //BA.debugLineNum = 481;BA.debugLine="Sub BalanceTransfer";
 //BA.debugLineNum = 482;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 483;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 484;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 485;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"BalanceTransferAndTopMeUpDialog");
 //BA.debugLineNum = 486;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 487;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 488;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 489;BA.debugLine="Dim lblAmount As Label = DialogLayout.Views.Get(\"";
_lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
_lblamount.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblAmount")));
 //BA.debugLineNum = 490;BA.debugLine="Dim edtAmount As EditText = DialogLayout.Views.Ge";
_edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtamount.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtAmount")));
 //BA.debugLineNum = 492;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtAmoun";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtamount.getTop()+_edtamount.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 494;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 495;BA.debugLine="Case \"41401\" : sCode = \"*223*\"' MPT";
mostCurrent._vvvvvv5 = "*223*";
 break; }
case 1: {
 //BA.debugLineNum = 496;BA.debugLine="Case \"41405\" : sCode = \"*155*\"' Ooredoo";
mostCurrent._vvvvvv5 = "*155*";
 break; }
case 2: {
 //BA.debugLineNum = 497;BA.debugLine="Case \"41406\" : sCode = \"*979*2*4*\"' Telenor";
mostCurrent._vvvvvv5 = "*979*2*4*";
 break; }
}
;
 //BA.debugLineNum = 500;BA.debugLine="lblPhoneNumber.Text = \"ေငြလက္ခံမည့္သူ၏ဖုန္းနံပါတ္";
_lblphonenumber.setText((Object)("ေငြလက္ခံမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 502;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 504;BA.debugLine="lblAmount.Text = \"လႊဲေျပာင္းေပးလိုေသာေငြပမာဏ႐ိုက္";
_lblamount.setText((Object)("လႊဲေျပာင္းေပးလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 506;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0 And edtAm";
while (!(_edtphonenumber.getText().length()>0 && _edtamount.getText().length()>0)) {
 //BA.debugLineNum = 507;BA.debugLine="If DialogLayout.Show(\"ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေ";
if (_dialoglayout.Show("ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္","ေငြလႊဲပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 508;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 509;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 510;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_edtamount.getText().length()==0) { 
 //BA.debugLineNum = 512;BA.debugLine="edtAmount.RequestFocus";
_edtamount.RequestFocus();
 //BA.debugLineNum = 513;BA.debugLine="ToastMessageShow(\"ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပ";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 516;BA.debugLine="If SIMOperator = \"41406\" Then' Telenor";
if ((_vvv7).equals("41406")) { 
 //BA.debugLineNum = 517;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtP";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtphonenumber.getText()+"*"+_edtamount.getText()+"#","UTF8"))));
 }else {
 //BA.debugLineNum = 519;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtA";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtamount.getText()+"*"+_edtphonenumber.getText()+"#","UTF8"))));
 };
 //BA.debugLineNum = 521;BA.debugLine="End Sub";
return "";
}
public static String  _banner_pagechanged(int _position) throws Exception{
 //BA.debugLineNum = 204;BA.debugLine="Sub Banner_PageChanged (Position As Int)";
 //BA.debugLineNum = 205;BA.debugLine="iCurrentBanner = Position";
_vvvvv7 = _position;
 //BA.debugLineNum = 206;BA.debugLine="End Sub";
return "";
}
public static String  _btncontactpicker_click() throws Exception{
com.moribanxenia.contactpicker.ContactPicker _cp = null;
 //BA.debugLineNum = 459;BA.debugLine="Sub btnContactPicker_Click";
 //BA.debugLineNum = 460;BA.debugLine="Dim cp As ContactPicker";
_cp = new com.moribanxenia.contactpicker.ContactPicker();
 //BA.debugLineNum = 461;BA.debugLine="cp.Initialize(\"cp\")";
_cp.Initialize("cp");
 //BA.debugLineNum = 462;BA.debugLine="cp.Show";
_cp.Show(processBA);
 //BA.debugLineNum = 463;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv6(String[] _phonenumber) throws Exception{
anywheresoftware.b4a.objects.drawable.BitmapDrawable _bd = null;
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
int _i = 0;
 //BA.debugLineNum = 945;BA.debugLine="Sub Call(PhoneNumber() As String)";
 //BA.debugLineNum = 946;BA.debugLine="Dim bd As BitmapDrawable";
_bd = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 947;BA.debugLine="Dim iResult As Int = 0";
_iresult = (int) (0);
 //BA.debugLineNum = 949;BA.debugLine="If PhoneNumber.Length > 1 Then";
if (_phonenumber.length>1) { 
 //BA.debugLineNum = 950;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 951;BA.debugLine="Dialog.Options.Dimensions.Height = (PhoneNumber.";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Height = (int) ((_phonenumber.length*anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)))+((_phonenumber.length-1)*anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 952;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZa";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 953;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Ty";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 954;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Lo";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 955;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 957;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 958;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravit";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 959;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Colo";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 961;BA.debugLine="bd = xml.GetDrawable(\"17301645\")";
_bd.setObject((android.graphics.drawable.BitmapDrawable)(mostCurrent._vvvvvvv5.GetDrawable("17301645")));
 //BA.debugLineNum = 963;BA.debugLine="For i = 0 To PhoneNumber.Length - 1";
{
final int step14 = 1;
final int limit14 = (int) (_phonenumber.length-1);
for (_i = (int) (0) ; (step14 > 0 && _i <= limit14) || (step14 < 0 && _i >= limit14); _i = ((int)(0 + _i + step14)) ) {
 //BA.debugLineNum = 964;BA.debugLine="lvwList.AddTwoLinesAndBitmap(PhoneNumber(i), \"\"";
_lvwlist.AddTwoLinesAndBitmap(_phonenumber[_i],"",_bd.getBitmap());
 }
};
 //BA.debugLineNum = 967;BA.debugLine="iResult = DialogLayout.Show(\"ေခၚဆိုမည့္ဖုန္းနံပါ";
_iresult = _dialoglayout.Show("ေခၚဆိုမည့္ဖုန္းနံပါတ္ေရြးခ်ယ္ရန္","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 968;BA.debugLine="If iResult = DialogResponse.CANCEL Then Return";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 };
 //BA.debugLineNum = 971;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(PhoneNumber(iR";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(_phonenumber[_iresult],"UTF8"))));
 //BA.debugLineNum = 972;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv7() throws Exception{
 //BA.debugLineNum = 862;BA.debugLine="Sub CallCentre";
 //BA.debugLineNum = 863;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41404","41405","41406")) {
case 0: 
case 1: {
 //BA.debugLineNum = 864;BA.debugLine="Case \"41401\", \"41404\" : Call(Array As String(\"10";
_vvvvvvv6(new String[]{"106"});
 break; }
case 2: {
 //BA.debugLineNum = 865;BA.debugLine="Case \"41405\" : Call(Array As String(\"234\", \"0997";
_vvvvvvv6(new String[]{"234","09970000234"});
 break; }
case 3: {
 //BA.debugLineNum = 866;BA.debugLine="Case \"41406\" : Call(Array As String(\"979\", \"0979";
_vvvvvvv6(new String[]{"979","09790097900"});
 break; }
default: {
 //BA.debugLineNum = 867;BA.debugLine="Case Else : Call(Array As String(\"1212\"))' MECTe";
_vvvvvvv6(new String[]{"1212"});
 break; }
}
;
 //BA.debugLineNum = 869;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv0() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 694;BA.debugLine="Sub CallForwarding";
 //BA.debugLineNum = 695;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 697;BA.debugLine="sName = \"Call Forwarding\"";
_sname = "Call Forwarding";
 //BA.debugLineNum = 698;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံေခၚဆိုေ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံေခၚဆိုေသာ အဝင္ဖုန္းမ်ားကို မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားသည့္ အျခားဖုန္းနံပါတ္တစ္ခုသို႔ လႊဲေျပာင္းေပးႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 699;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41404")) {
case 0: {
 //BA.debugLineNum = 701;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 702;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 703;BA.debugLine="sSubscription = \"CF\"";
_ssubscription = "CF";
 //BA.debugLineNum = 704;BA.debugLine="sUnsubscription = \"CF OFF\"";
_sunsubscription = "CF OFF";
 break; }
case 1: {
 //BA.debugLineNum = 706;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၉၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 707;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 708;BA.debugLine="sSubscription = \"Orderdata CF\"";
_ssubscription = "Orderdata CF";
 //BA.debugLineNum = 709;BA.debugLine="sUnsubscription = \"Cancel CF\"";
_sunsubscription = "Cancel CF";
 break; }
}
;
 //BA.debugLineNum = 712;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvv6,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 713;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv2() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
 //BA.debugLineNum = 798;BA.debugLine="Sub CallMeBack";
 //BA.debugLineNum = 799;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 800;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 801;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 802;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"CallMeBackDialog");
 //BA.debugLineNum = 803;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 804;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 805;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 807;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtPhone";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtphonenumber.getTop()+_edtphonenumber.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 809;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 810;BA.debugLine="Case \"41401\" : sCode = \"*222*\"' MPT";
mostCurrent._vvvvvv5 = "*222*";
 break; }
case 1: {
 //BA.debugLineNum = 811;BA.debugLine="Case \"41405\" : sCode = \"*122*\"' Ooredoo";
mostCurrent._vvvvvv5 = "*122*";
 break; }
case 2: {
 //BA.debugLineNum = 812;BA.debugLine="Case \"41406\" : sCode = \"*979*3*2*\"' Telenor";
mostCurrent._vvvvvv5 = "*979*3*2*";
 break; }
}
;
 //BA.debugLineNum = 815;BA.debugLine="lblPhoneNumber.Text = \"ျပန္ေခၚေစလိုေသာဖုန္းနံပါတ္";
_lblphonenumber.setText((Object)("ျပန္ေခၚေစလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 817;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 819;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0";
while (!(_edtphonenumber.getText().length()>0)) {
 //BA.debugLineNum = 820;BA.debugLine="If DialogLayout.Show(\"မိမိဖုန္းအားျပန္ေခၚေပးပါရန";
if (_dialoglayout.Show("မိမိဖုန္းအားျပန္ေခၚေပးပါရန္","ပို႔ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 821;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 822;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 823;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 826;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPho";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtphonenumber.getText()+"#","UTF8"))));
 //BA.debugLineNum = 827;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv3() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 715;BA.debugLine="Sub CallWaiting";
 //BA.debugLineNum = 716;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 718;BA.debugLine="sName = \"Call Waiting\"";
_sname = "Call Waiting";
 //BA.debugLineNum = 719;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းေျပာ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းေျပာဆိုေနစဥ္အတြင္း ထပ္မံေရာက္ရွိလာေသာ အဝင္ဖုန္းကို လက္ခံေျပာဆိုႏိုင္ေစၿပီး၊ လက္ရွိေျပာဆိုေနမႈကို ေစာင့္ဆိုင္းခိုင္းထားႏိုင္ပါသည္။ ဖုန္းေခၚဆိုမႈႏွစ္ခုအၾကားတြင္လည္း ဖုန္းခ်စရာမလိုပဲ အျပန္အလွန္ေျပာဆိုႏုိင္ပါသည္။<br>";
 //BA.debugLineNum = 720;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41404")) {
case 0: {
 //BA.debugLineNum = 722;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 723;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 724;BA.debugLine="sSubscription = \"CW\"";
_ssubscription = "CW";
 //BA.debugLineNum = 725;BA.debugLine="sUnsubscription = \"CW OFF\"";
_sunsubscription = "CW OFF";
 break; }
case 1: {
 //BA.debugLineNum = 727;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 728;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 729;BA.debugLine="sSubscription = \"Orderdata CW\"";
_ssubscription = "Orderdata CW";
 //BA.debugLineNum = 730;BA.debugLine="sUnsubscription = \"Cancel CW\"";
_sunsubscription = "Cancel CW";
 break; }
}
;
 //BA.debugLineNum = 733;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvv6,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 734;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv4() throws Exception{
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
 //BA.debugLineNum = 337;BA.debugLine="Sub CheckBalance";
 //BA.debugLineNum = 338;BA.debugLine="Dim iResult As Int";
_iresult = 0;
 //BA.debugLineNum = 340;BA.debugLine="sCode = \"\"";
mostCurrent._vvvvvv5 = "";
 //BA.debugLineNum = 342;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 343;BA.debugLine="Dialog.Options.Dimensions.Height = 121dip";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (121));
 //BA.debugLineNum = 344;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 345;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 346;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 347;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get(";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 349;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 350;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Typeface = SmartZ";
_lvwlist.getTwoLinesAndBitmap().Label.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 351;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravity";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 352;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Color";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 354;BA.debugLine="lvwList.AddTwoLinesAndBitmap(\"ဖုန္းလက္က်န္ေငြ\", \"";
_lvwlist.AddTwoLinesAndBitmap("ဖုန္းလက္က်န္ေငြ","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"balance.png").getObject()));
 //BA.debugLineNum = 355;BA.debugLine="lvwList.AddTwoLinesAndBitmap(\"အင္တာနက္ႏွင့္အပိုဆု";
_lvwlist.AddTwoLinesAndBitmap("အင္တာနက္ႏွင့္အပိုဆုမ်ား","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bonus.png").getObject()));
 //BA.debugLineNum = 357;BA.debugLine="iResult = DialogLayout.Show(\"ဖုန္းလက္က်န္ေငြစစ္ေဆ";
_iresult = _dialoglayout.Show("ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 358;BA.debugLine="Select iResult";
switch (BA.switchObjectToInt(_iresult,anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL,(int) (0),(int) (1))) {
case 0: {
 //BA.debugLineNum = 359;BA.debugLine="Case DialogResponse.CANCEL : Return";
if (true) return "";
 break; }
case 1: {
 //BA.debugLineNum = 361;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406","41404")) {
case 0: 
case 1: 
case 2: {
 //BA.debugLineNum = 362;BA.debugLine="Case \"41401\", \"41405\", \"41406\" : sCode = \"*124";
mostCurrent._vvvvvv5 = "*124#";
 break; }
case 3: {
 //BA.debugLineNum = 363;BA.debugLine="Case \"41404\" : sCode = \"*162\"' MPT CDMA";
mostCurrent._vvvvvv5 = "*162";
 break; }
default: {
 //BA.debugLineNum = 364;BA.debugLine="Case Else : sCode = \"*123#\"' MECTel";
mostCurrent._vvvvvv5 = "*123#";
 break; }
}
;
 break; }
case 2: {
 //BA.debugLineNum = 367;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41404","41405","41406")) {
case 0: 
case 1: {
 //BA.debugLineNum = 368;BA.debugLine="Case \"41401\", \"41404\" : sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 368;BA.debugLine="Case \"41401\", \"41404\" : sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv7 = "QER";
 break; }
case 2: {
 //BA.debugLineNum = 369;BA.debugLine="Case \"41405\" : sPhoneNumber = \"2230\" : sSMSBod";
mostCurrent._vvvvvv6 = "2230";
 //BA.debugLineNum = 369;BA.debugLine="Case \"41405\" : sPhoneNumber = \"2230\" : sSMSBod";
mostCurrent._vvvvvv7 = "b";
 break; }
case 3: {
 //BA.debugLineNum = 370;BA.debugLine="Case \"41406\" : sCode = \"*124*1#\"' Telenor";
mostCurrent._vvvvvv5 = "*124*1#";
 break; }
default: {
 //BA.debugLineNum = 371;BA.debugLine="Case Else : sPhoneNumber = \"233\" : sSMSBody =";
mostCurrent._vvvvvv6 = "233";
 //BA.debugLineNum = 371;BA.debugLine="Case Else : sPhoneNumber = \"233\" : sSMSBody =";
mostCurrent._vvvvvv7 = "BAL";
 break; }
}
;
 break; }
}
;
 //BA.debugLineNum = 374;BA.debugLine="If sCode = \"\" Then";
if ((mostCurrent._vvvvvv5).equals("")) { 
 //BA.debugLineNum = 375;BA.debugLine="ps.Send(sPhoneNumber, sSMSBody)";
mostCurrent._vvvvvv0.Send(mostCurrent._vvvvvv6,mostCurrent._vvvvvv7);
 }else {
 //BA.debugLineNum = 377;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\"";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5,"UTF8"))));
 };
 //BA.debugLineNum = 379;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv5() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 679;BA.debugLine="Sub CLIR";
 //BA.debugLineNum = 680;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 682;BA.debugLine="sName = \"CLIR\"";
_sname = "CLIR";
 //BA.debugLineNum = 683;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြေခၚဆိုမည္";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြေခၚဆိုမည့္ အျခားဖုန္းမ်ားတြင္ ေခၚဆိုသူနံပါတ္ေဖာ္ျပျခင္းကို ေရွာင္ရွားလိုပါက အသံုးျပဳႏိုင္သည္။ သတိျပဳရန္မွာ ေအာ္ပေရတာတူ ဖုန္းအခ်င္းခ်င္းသာ အျပည့္အဝအလုပ္လုပ္ေဆာင္ပါမည္။<br>";
 //BA.debugLineNum = 684;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41405")) {
case 0: {
 //BA.debugLineNum = 686;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၂၅၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 687;BA.debugLine="sSubscription = \"*311#\"";
_ssubscription = "*311#";
 //BA.debugLineNum = 688;BA.debugLine="sUnsubscription = \"*311*0#\"";
_sunsubscription = "*311*0#";
 break; }
}
;
 //BA.debugLineNum = 691;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvv6,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 692;BA.debugLine="End Sub";
return "";
}
public static String  _cp_result(boolean _success,String _displayname,String _phonenumber,int _phonetype) throws Exception{
 //BA.debugLineNum = 465;BA.debugLine="Sub cp_Result (Success As Boolean, DisplayName As";
 //BA.debugLineNum = 466;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 467;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").RequestF";
mostCurrent._vvvvvvv4.getViews().EditText("edtPhoneNumber").RequestFocus();
 //BA.debugLineNum = 468;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Text = P";
mostCurrent._vvvvvvv4.getViews().EditText("edtPhoneNumber").setText((Object)(_phonenumber));
 };
 //BA.debugLineNum = 470;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _vvvvv2(int _paneltype) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _pnl = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwprepaid = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwoffers = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwsupport = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwstores = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwabout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblappname = null;
anywheresoftware.b4a.objects.LabelWrapper _lblappversion = null;
 //BA.debugLineNum = 208;BA.debugLine="Sub CreatePanel(PanelType As Int) As Panel";
 //BA.debugLineNum = 209;BA.debugLine="Dim pnl As Panel";
_pnl = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 210;BA.debugLine="Dim lvwPrepaid, lvwOffers, lvwSupport, lvwStores,";
_lvwprepaid = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwoffers = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwsupport = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwstores = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwabout = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 211;BA.debugLine="Dim lblAppName, lblAppVersion As Label";
_lblappname = new anywheresoftware.b4a.objects.LabelWrapper();
_lblappversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 213;BA.debugLine="pnl.Initialize(\"\")";
_pnl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 214;BA.debugLine="Select PanelType";
switch (BA.switchObjectToInt(_paneltype,_type_prepaid,_type_offers,_type_support,_type_stores,_type_about)) {
case 0: {
 //BA.debugLineNum = 216;BA.debugLine="lvwPrepaid.Initialize(\"lvwPrepaid\")";
_lvwprepaid.Initialize(mostCurrent.activityBA,"lvwPrepaid");
 //BA.debugLineNum = 217;BA.debugLine="lvwPrepaid.TwoLinesAndBitmap.Label.TextColor =";
_lvwprepaid.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 218;BA.debugLine="lvwPrepaid.TwoLinesAndBitmap.SecondLabel.Typefa";
_lvwprepaid.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 219;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Check Balance";
_lvwprepaid.AddTwoLinesAndBitmap2("Check Balance","ဖုန္းလက္က်န္ေငြစစ္ေဆးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"checkbalance.png").getObject()),(Object)(1));
 //BA.debugLineNum = 220;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Top Up\", \"ဖုန";
_lvwprepaid.AddTwoLinesAndBitmap2("Top Up","ဖုန္းေငြျဖည့္ရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"topup.png").getObject()),(Object)(2));
 //BA.debugLineNum = 221;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 223;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Yu Htar\", \"";
_lvwprepaid.AddTwoLinesAndBitmap2("Yu Htar","ဖုန္းေငြေခ်းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"kyothone.png").getObject()),(Object)(3));
 break; }
case 1: 
case 2: {
 //BA.debugLineNum = 225;BA.debugLine="lvwPrepaid.AddTwoLinesAndBitmap2(\"Kyo Thone\",";
_lvwprepaid.AddTwoLinesAndBitmap2("Kyo Thone","ဖုန္းေငြေခ်းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"kyothone.png").getObject()),(Object)(3));
 break; }
}
;
 //BA.debugLineNum = 227;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv7).equals("41401") || (_vvv7).equals("41405")) { 
_lvwprepaid.AddTwoLinesAndBitmap2("Balance Transfer","ဖုန္းလက္က်န္ေငြလႊဲေျပာင္းေပးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"balancetransfer.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 228;BA.debugLine="If SIMOperator = \"41405\" Then lvwPrepaid.AddTwo";
if ((_vvv7).equals("41405")) { 
_lvwprepaid.AddTwoLinesAndBitmap2("Top Me Up","မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"topmeup.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 229;BA.debugLine="pnl.AddView(lvwPrepaid, 0, 0, FILL_PARENT, FILL";
_pnl.AddView((android.view.View)(_lvwprepaid.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 1: {
 //BA.debugLineNum = 231;BA.debugLine="lvwOffers.Initialize(\"lvwOffers\")";
_lvwoffers.Initialize(mostCurrent.activityBA,"lvwOffers");
 //BA.debugLineNum = 232;BA.debugLine="lvwOffers.TwoLinesAndBitmap.Label.TextColor = C";
_lvwoffers.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 233;BA.debugLine="lvwOffers.TwoLinesAndBitmap.SecondLabel.Typefac";
_lvwoffers.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 234;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Plans\", \"Plan";
_lvwoffers.AddTwoLinesAndBitmap2("Plans","Plan မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(1));
 //BA.debugLineNum = 237;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Data Packs\", \"";
_lvwoffers.AddTwoLinesAndBitmap2("Data Packs","Data Pack မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(4));
 //BA.debugLineNum = 238;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Special Packs\"";
_lvwoffers.AddTwoLinesAndBitmap2("Special Packs","Special Pack မ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(5));
 //BA.debugLineNum = 239;BA.debugLine="lvwOffers.AddTwoLinesAndBitmap2(\"Value Added Se";
_lvwoffers.AddTwoLinesAndBitmap2("Value Added Services","ထပ္ေဆာင္းဝန္ေဆာင္မႈမ်ား",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(6));
 //BA.debugLineNum = 240;BA.debugLine="pnl.AddView(lvwOffers, 0, 0, FILL_PARENT, FILL_";
_pnl.AddView((android.view.View)(_lvwoffers.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 2: {
 //BA.debugLineNum = 242;BA.debugLine="lvwSupport.Initialize(\"lvwSupport\")";
_lvwsupport.Initialize(mostCurrent.activityBA,"lvwSupport");
 //BA.debugLineNum = 243;BA.debugLine="lvwSupport.TwoLinesAndBitmap.Label.TextColor =";
_lvwsupport.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 244;BA.debugLine="lvwSupport.TwoLinesAndBitmap.SecondLabel.Typefa";
_lvwsupport.getTwoLinesAndBitmap().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 245;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv7).equals("41401") || (_vvv7).equals("41405") || (_vvv7).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("Call Me Back","မိမိဖုန္းအားျပန္ေခၚေပးပါရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"callmeback.png").getObject()),(Object)(1));};
 //BA.debugLineNum = 246;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv7).equals("41401") || (_vvv7).equals("41405") || (_vvv7).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("What's My Number?","မိမိဖုန္းနံပါတ္ကိုစစ္ေဆးရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"whatismynumber.png").getObject()),(Object)(2));};
 //BA.debugLineNum = 247;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv7).equals("41401") || (_vvv7).equals("41405") || (_vvv7).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("APN Settings","APN Setting မ်ားရယူရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"settings.png").getObject()),(Object)(3));};
 //BA.debugLineNum = 248;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"4140";
if ((_vvv7).equals("41401") || (_vvv7).equals("41405") || (_vvv7).equals("41406")) { 
_lvwsupport.AddTwoLinesAndBitmap2("USSD","ဝန္ေဆာင္မႈမ်ားကိုစီမံရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ussd.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 249;BA.debugLine="lvwSupport.AddTwoLinesAndBitmap2(\"Call Centre\",";
_lvwsupport.AddTwoLinesAndBitmap2("Call Centre","အေထြေထြဝန္ေဆာင္မႈမ်ားကိုစံုစမ္းေမးျမန္းရန္",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"callcentre.png").getObject()),(Object)(5));
 //BA.debugLineNum = 250;BA.debugLine="pnl.AddView(lvwSupport, 0, 0, FILL_PARENT, FILL";
_pnl.AddView((android.view.View)(_lvwsupport.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 3: {
 //BA.debugLineNum = 252;BA.debugLine="lvwStores.Initialize(\"lvwStores\")";
_lvwstores.Initialize(mostCurrent.activityBA,"lvwStores");
 //BA.debugLineNum = 253;BA.debugLine="lvwStores.TwoLinesLayout.ItemHeight = 121dip";
_lvwstores.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (121)));
 //BA.debugLineNum = 254;BA.debugLine="lvwStores.TwoLinesLayout.Label.Top = 15dip";
_lvwstores.getTwoLinesLayout().Label.setTop(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15)));
 //BA.debugLineNum = 255;BA.debugLine="lvwStores.TwoLinesLayout.Label.Typeface = Smart";
_lvwstores.getTwoLinesLayout().Label.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 256;BA.debugLine="lvwStores.TwoLinesLayout.Label.Gravity = Gravit";
_lvwstores.getTwoLinesLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 257;BA.debugLine="lvwStores.TwoLinesLayout.Label.TextColor = Colo";
_lvwstores.getTwoLinesLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 258;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Top = lvwS";
_lvwstores.getTwoLinesLayout().SecondLabel.setTop((int) (_lvwstores.getTwoLinesLayout().Label.getTop()+_lvwstores.getTwoLinesLayout().Label.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (2))));
 //BA.debugLineNum = 259;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Height = 5";
_lvwstores.getTwoLinesLayout().SecondLabel.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (59)));
 //BA.debugLineNum = 260;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Typeface =";
_lvwstores.getTwoLinesLayout().SecondLabel.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 261;BA.debugLine="lvwStores.TwoLinesLayout.SecondLabel.Gravity =";
_lvwstores.getTwoLinesLayout().SecondLabel.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 262;BA.debugLine="lvwStores.AddTwoLines2(\"မေကြးေရွာ့ပင္းေမာ(လ္)\",";
_lvwstores.AddTwoLines2("မေကြးေရွာ့ပင္းေမာ(လ္)","အမွတ္(၈)၊ ဗိုလ္ခ်ဳပ္လမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ပြဲႀကိဳရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၆၇၁၁၊ ၀၆၃-၂၇၂၂၃။",(Object)(1));
 //BA.debugLineNum = 263;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မေကြး-၁)\", \"အမွ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မေကြး-၁)","အမွတ္(၁၀)၊ ျပည္ေတာ္သာလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ေဈးလယ္စိုးရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၅၄၅၂၊ ၀၆၃-၂၆၁၇၁။",(Object)(2));
 //BA.debugLineNum = 264;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မေကြး-၂)\", \"အမွ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မေကြး-၂)","အမွတ္(၃၆)၊ ဗိုလ္ခ်ဳပ္လမ္း ႏွင့္ မဲထီးလမ္းေထာင့္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"ရြာသစ္ရပ္ကြက္၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၃၆၉၂၊ ၀၆၃-၂၈၁၉၃။",(Object)(3));
 //BA.debugLineNum = 265;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မင္းဘူး-၁)\", \"အ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မင္းဘူး-၁)","အမွတ္(၈၁၂)၊ မင္းဘူး-စကုလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၃)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၅-၂၁၅၄၇။",(Object)(4));
 //BA.debugLineNum = 266;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ (မင္းဘူး-၂)\", \"အ";
_lvwstores.AddTwoLines2("ေရႊဧရာ (မင္းဘူး-၂)","အမွတ္(၉၄၈)၊ ေစ်းသစ္လမ္း ႏွင့္ ယင္းမာလမ္းေထာင့္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၄)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၉-၃၀၇၄၀၁၀၁။",(Object)(5));
 //BA.debugLineNum = 267;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်";
_lvwstores.AddTwoLines2("ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်ိေရး (မေကြး)","တတိယထပ္၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"မေကြးေရွာ့ပင္းေမာ(လ္)၊ မေကြးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၃-၂၈၄၂၃၊ ၀၉-၇၇၇၇၇၉၉၀၀။",(Object)(6));
 //BA.debugLineNum = 268;BA.debugLine="lvwStores.AddTwoLines2(\"ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်";
_lvwstores.AddTwoLines2("ေရႊဧရာ တယ္လီေနာ ျဖန္႔ခ်ိေရး (မင္းဘူး)","အမွတ္(၁၂၈၁)၊ ဆင္ေဂါင္းလမ္း၊ "+anywheresoftware.b4a.keywords.Common.CRLF+"အမွတ္(၄)ရပ္ကြက္၊ မင္းဘူးၿမိဳ႕။"+anywheresoftware.b4a.keywords.Common.CRLF+"ဖုန္း ၀၆၅-၂၁၅၅၈၊ ၀၉-၇၇၇၇၇၈၈၀၀။",(Object)(7));
 //BA.debugLineNum = 269;BA.debugLine="pnl.AddView(lvwStores, 0, 0, FILL_PARENT, FILL_";
_pnl.AddView((android.view.View)(_lvwstores.getObject()),(int) (0),(int) (0),_fill_parent,_fill_parent);
 break; }
case 4: {
 //BA.debugLineNum = 271;BA.debugLine="ivAppIcon.Initialize(\"\")";
mostCurrent._vvvvvvvv6.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 272;BA.debugLine="ivAppIcon.Bitmap = AppIcon.Bitmap";
mostCurrent._vvvvvvvv6.setBitmap(mostCurrent._vvvv4.getBitmap());
 //BA.debugLineNum = 273;BA.debugLine="ivAppIcon.Gravity = Gravity.FILL";
mostCurrent._vvvvvvvv6.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 274;BA.debugLine="pnl.AddView(ivAppIcon, 50%x - 50dip, 10dip, 100";
_pnl.AddView((android.view.View)(mostCurrent._vvvvvvvv6.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100)));
 //BA.debugLineNum = 276;BA.debugLine="lblAppName.Initialize(\"\")";
_lblappname.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 277;BA.debugLine="lblAppName.Text = AppName";
_lblappname.setText((Object)(_vvv3));
 //BA.debugLineNum = 278;BA.debugLine="lblAppName.Gravity = Gravity.CENTER";
_lblappname.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 279;BA.debugLine="lblAppName.TextSize = 19.5";
_lblappname.setTextSize((float) (19.5));
 //BA.debugLineNum = 280;BA.debugLine="lblAppName.TextColor = Colors.DarkGray";
_lblappname.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 281;BA.debugLine="pnl.AddView(lblAppName, 0, ivAppIcon.Top + ivAp";
_pnl.AddView((android.view.View)(_lblappname.getObject()),(int) (0),(int) (mostCurrent._vvvvvvvv6.getTop()+mostCurrent._vvvvvvvv6.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_wrap_content);
 //BA.debugLineNum = 283;BA.debugLine="lblAppVersion.Initialize(\"\")";
_lblappversion.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 284;BA.debugLine="lblAppVersion.Text = \"Version: \" & AppVersion";
_lblappversion.setText((Object)("Version: "+_vvv4));
 //BA.debugLineNum = 285;BA.debugLine="lblAppVersion.Gravity = Gravity.CENTER";
_lblappversion.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 286;BA.debugLine="lblAppVersion.TextSize = 16.5";
_lblappversion.setTextSize((float) (16.5));
 //BA.debugLineNum = 287;BA.debugLine="lblAppVersion.TextColor = -7829368";
_lblappversion.setTextColor((int) (-7829368));
 //BA.debugLineNum = 288;BA.debugLine="pnl.AddView(lblAppVersion, 0, lblAppName.Top +";
_pnl.AddView((android.view.View)(_lblappversion.getObject()),(int) (0),(int) (_lblappname.getTop()+mostCurrent._vvvvvvv2.MeasureMultilineTextHeight((android.widget.TextView)(_lblappname.getObject()),_lblappname.getText())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_wrap_content);
 //BA.debugLineNum = 290;BA.debugLine="line.Initialize(\"\")";
mostCurrent._vvvvv5.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 291;BA.debugLine="line.Color = Colors.LightGray";
mostCurrent._vvvvv5.setColor(anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 292;BA.debugLine="pnl.AddView(line, 0, lblAppVersion.Top + su.Mea";
_pnl.AddView((android.view.View)(mostCurrent._vvvvv5.getObject()),(int) (0),(int) (_lblappversion.getTop()+mostCurrent._vvvvvvv2.MeasureMultilineTextHeight((android.widget.TextView)(_lblappversion.getObject()),_lblappversion.getText())+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),_fill_parent,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1)));
 //BA.debugLineNum = 294;BA.debugLine="lvwAbout.Initialize(\"lvwAbout\")";
_lvwabout.Initialize(mostCurrent.activityBA,"lvwAbout");
 //BA.debugLineNum = 295;BA.debugLine="lvwAbout.SingleLineLayout.Label.Gravity = Gravi";
_lvwabout.getSingleLineLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 296;BA.debugLine="lvwAbout.SingleLineLayout.Label.TextSize = lblA";
_lvwabout.getSingleLineLayout().Label.setTextSize(_lblappversion.getTextSize());
 //BA.debugLineNum = 297;BA.debugLine="lvwAbout.SingleLineLayout.Label.TextColor = Col";
_lvwabout.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 298;BA.debugLine="lvwAbout.AddSingleLine2(\"Developer: \" & AppPubl";
_lvwabout.AddSingleLine2("Developer: "+_vvv5,(Object)(1));
 //BA.debugLineNum = 299;BA.debugLine="lvwAbout.AddSingleLine2(\"Like Us On Facebook\",";
_lvwabout.AddSingleLine2("Like Us On Facebook",(Object)(2));
 //BA.debugLineNum = 300;BA.debugLine="pnl.AddView(lvwAbout, 0, line.Top + line.Height";
_pnl.AddView((android.view.View)(_lvwabout.getObject()),(int) (0),(int) (mostCurrent._vvvvv5.getTop()+mostCurrent._vvvvv5.getHeight()),_fill_parent,_fill_parent);
 break; }
}
;
 //BA.debugLineNum = 303;BA.debugLine="Return pnl";
if (true) return _pnl;
 //BA.debugLineNum = 304;BA.debugLine="End Sub";
return null;
}
public static String  _vvvvvvvv7() throws Exception{
String _sname = "";
String _sdescription = "";
String _sactivation = "";
String _sdeactivation = "";
 //BA.debugLineNum = 629;BA.debugLine="Sub DataService";
 //BA.debugLineNum = 630;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sActivatio";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_sactivation = "";
_sdeactivation = "";
 //BA.debugLineNum = 632;BA.debugLine="sName = \"အင္တာနက္ဝန္ေဆာင္မႈ\"";
_sname = "အင္တာနက္ဝန္ေဆာင္မႈ";
 //BA.debugLineNum = 633;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 635;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 636;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 637;BA.debugLine="sActivation = \"Orderdata service\"";
_sactivation = "Orderdata service";
 break; }
case 1: {
 //BA.debugLineNum = 639;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ ရပ္ဆိုင္းရန္ သို႔မဟုတ္ ျပန္လည္အသံုးျပဳရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 640;BA.debugLine="sPhoneNumber = \"500\"";
mostCurrent._vvvvvv6 = "500";
 //BA.debugLineNum = 641;BA.debugLine="sActivation = \"internet on\"";
_sactivation = "internet on";
 //BA.debugLineNum = 642;BA.debugLine="sDeactivation = \"internet off\"";
_sdeactivation = "internet off";
 break; }
default: {
 //BA.debugLineNum = 644;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျ";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ အင္တာနက္အသံုးျပဳမႈ စတင္ရန္အတြက္ ျဖစ္သည္။<br>- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 645;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvv6 = "233";
 //BA.debugLineNum = 646;BA.debugLine="sActivation = \"Open EVDO\"";
_sactivation = "Open EVDO";
 break; }
}
;
 //BA.debugLineNum = 649;BA.debugLine="Plan(sName, sDescription, sPhoneNumber, sActivati";
_vvvvvvvv0(_sname,_sdescription,mostCurrent._vvvvvv6,_sactivation,_sdeactivation);
 //BA.debugLineNum = 650;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv1() throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 898;BA.debugLine="Sub GetPackageName As String";
 //BA.debugLineNum = 899;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 900;BA.debugLine="Return r.GetStaticField(\"anywheresoftware.b4a.BA\"";
if (true) return BA.ObjectToString(_r.GetStaticField("anywheresoftware.b4a.BA","packageName"));
 //BA.debugLineNum = 901;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 51;BA.debugLine="Private AppIcon As BitmapDrawable = pm.GetApplica";
mostCurrent._vvvv4 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._vvvv4.setObject((android.graphics.drawable.BitmapDrawable)(_vvvvvvvvv2.GetApplicationIcon(_vvvvvvvvv1())));
 //BA.debugLineNum = 53;BA.debugLine="Private xml As XmlLayoutBuilder";
mostCurrent._vvvvvvv5 = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 55;BA.debugLine="Private ab As AHActionBar";
mostCurrent._vvvv1 = new de.amberhome.SimpleActionBar.ActionBarWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private overflowIcon As BitmapDrawable";
mostCurrent._vvvv3 = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 57;BA.debugLine="Private menu As AHPopupMenu";
mostCurrent._vvv0 = new de.amberhome.quickaction.ICSMenu();
 //BA.debugLineNum = 59;BA.debugLine="Private pnlBanner, pnlContent As Panel";
mostCurrent._pnlbanner = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pnlcontent = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Private asBanner() As String = Array As String(\"b";
mostCurrent._vvvv6 = new String[]{"banner.jpg","banner2.jpg","banner3.jpg","banner4.jpg"};
 //BA.debugLineNum = 62;BA.debugLine="Private containerBanner, container As AHPageConta";
mostCurrent._vvvv5 = new de.amberhome.viewpager.AHPageContainer();
mostCurrent._vvvvv1 = new de.amberhome.viewpager.AHPageContainer();
 //BA.debugLineNum = 63;BA.debugLine="Private pagerBanner, pager As AHViewPager";
mostCurrent._vvvv7 = new de.amberhome.viewpager.AHViewPager();
mostCurrent._vvvvv3 = new de.amberhome.viewpager.AHViewPager();
 //BA.debugLineNum = 64;BA.debugLine="Private tabs As AHViewPagerTabs";
mostCurrent._vvvvv4 = new de.amberhome.viewpager.AHViewPagerTabs();
 //BA.debugLineNum = 65;BA.debugLine="Private line As Panel";
mostCurrent._vvvvv5 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Private lblFooter As Label";
mostCurrent._lblfooter = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private ivAppIcon As ImageView";
mostCurrent._vvvvvvvv6 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Private Dialog As DialogView";
mostCurrent._vvvvvvv4 = new com.datasteam.b4a.xtraviews.DialogView();
 //BA.debugLineNum = 73;BA.debugLine="Private sCode, sPhoneNumber, sSMSBody As String";
mostCurrent._vvvvvv5 = "";
mostCurrent._vvvvvv6 = "";
mostCurrent._vvvvvv7 = "";
 //BA.debugLineNum = 74;BA.debugLine="Private su As StringUtils";
mostCurrent._vvvvvvv2 = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 75;BA.debugLine="Private pc As PhoneCalls";
mostCurrent._vvvvvvv1 = new anywheresoftware.b4a.phone.Phone.PhoneCalls();
 //BA.debugLineNum = 76;BA.debugLine="Private ps As PhoneSms";
mostCurrent._vvvvvv0 = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 78;BA.debugLine="Private AdMobInterstitial As mwAdmobInterstitial";
mostCurrent._vvvvvv1 = new mobi.mindware.admob.interstitial.AdmobInterstitialsAds();
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 472;BA.debugLine="Sub KyoThone";
 //BA.debugLineNum = 473;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 474;BA.debugLine="Case \"41401\" : sCode = \"*800#\"' MPT";
mostCurrent._vvvvvv5 = "*800#";
 break; }
case 1: {
 //BA.debugLineNum = 475;BA.debugLine="Case \"41405\" : sCode = \"*125#\"' Ooredoo";
mostCurrent._vvvvvv5 = "*125#";
 break; }
case 2: {
 //BA.debugLineNum = 476;BA.debugLine="Case \"41406\" : sCode = \"*500#\"' Telenor";
mostCurrent._vvvvvv5 = "*500#";
 break; }
}
;
 //BA.debugLineNum = 478;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5,"UTF8"))));
 //BA.debugLineNum = 479;BA.debugLine="End Sub";
return "";
}
public static String  _lvwabout_itemclick(int _position,Object _value) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 884;BA.debugLine="Sub lvwAbout_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 885;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvv6();
 //BA.debugLineNum = 886;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2))) {
case 0: {
 //BA.debugLineNum = 888;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 889;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"fb://profile/10000";
_i.Initialize(_i.ACTION_VIEW,"fb://profile/100005753280868");
 //BA.debugLineNum = 890;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 break; }
case 1: {
 //BA.debugLineNum = 892;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 893;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"fb://page/66040136";
_i.Initialize(_i.ACTION_VIEW,"fb://page/660401367405456");
 //BA.debugLineNum = 894;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 break; }
}
;
 //BA.debugLineNum = 896;BA.debugLine="End Sub";
return "";
}
public static String  _lvwlist_itemclick(int _position,Object _value) throws Exception{
anywheresoftware.b4a.objects.ListViewWrapper _lvw = null;
 //BA.debugLineNum = 381;BA.debugLine="Sub lvwList_ItemClick (Position As Int, Value As O";
 //BA.debugLineNum = 382;BA.debugLine="Dim lvw As ListView = Sender";
_lvw = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvw.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 383;BA.debugLine="If lvw.Tag = \"Value\" Then";
if ((_lvw.getTag()).equals((Object)("Value"))) { 
 //BA.debugLineNum = 384;BA.debugLine="Dialog.Dismiss(Value)";
mostCurrent._vvvvvvv4.Dismiss((int)(BA.ObjectToNumber(_value)));
 }else {
 //BA.debugLineNum = 386;BA.debugLine="Dialog.Dismiss(Position)";
mostCurrent._vvvvvvv4.Dismiss(_position);
 };
 //BA.debugLineNum = 388;BA.debugLine="End Sub";
return "";
}
public static String  _lvwoffers_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 559;BA.debugLine="Sub lvwOffers_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 560;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvv6();
 //BA.debugLineNum = 561;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6))) {
case 0: {
 //BA.debugLineNum = 562;BA.debugLine="Case 1 : Plans";
_vvvvvvvvv7();
 break; }
case 1: {
 //BA.debugLineNum = 563;BA.debugLine="Case 2 : Packs(\"Voice Packs\", \"voicePacks\")";
_vvvvvvvvv0("Voice Packs","voicePacks");
 break; }
case 2: {
 //BA.debugLineNum = 564;BA.debugLine="Case 3 : Packs(\"SMS Packs\", \"smsPacks\")";
_vvvvvvvvv0("SMS Packs","smsPacks");
 break; }
case 3: {
 //BA.debugLineNum = 565;BA.debugLine="Case 4 : Packs(\"Data Packs\", \"dataPacks\")";
_vvvvvvvvv0("Data Packs","dataPacks");
 break; }
case 4: {
 //BA.debugLineNum = 566;BA.debugLine="Case 5 : Packs(\"Special Packs\", \"specialPacks\")";
_vvvvvvvvv0("Special Packs","specialPacks");
 break; }
case 5: {
 //BA.debugLineNum = 567;BA.debugLine="Case 6 : ValueAddedServices";
_vvvvvvvvvv1();
 break; }
}
;
 //BA.debugLineNum = 569;BA.debugLine="End Sub";
return "";
}
public static String  _lvwprepaid_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 326;BA.debugLine="Sub lvwPrepaid_ItemClick (Position As Int, Value A";
 //BA.debugLineNum = 327;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvv6();
 //BA.debugLineNum = 328;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5))) {
case 0: {
 //BA.debugLineNum = 329;BA.debugLine="Case 1 : CheckBalance";
_vvvvvvvv4();
 break; }
case 1: {
 //BA.debugLineNum = 330;BA.debugLine="Case 2 : TopUp";
_vvvvvvvvvv2();
 break; }
case 2: {
 //BA.debugLineNum = 331;BA.debugLine="Case 3 : KyoThone";
_vvvvvvvvv5();
 break; }
case 3: {
 //BA.debugLineNum = 332;BA.debugLine="Case 4 : BalanceTransfer";
_vvvvvvv3();
 break; }
case 4: {
 //BA.debugLineNum = 333;BA.debugLine="Case 5 : TopMeUp";
_vvvvvvvvvv3();
 break; }
}
;
 //BA.debugLineNum = 335;BA.debugLine="End Sub";
return "";
}
public static String  _lvwstores_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 871;BA.debugLine="Sub lvwStores_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 872;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvv6();
 //BA.debugLineNum = 873;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6),(Object)(7))) {
case 0: {
 //BA.debugLineNum = 874;BA.debugLine="Case 1 : Call(Array As String(\"06326711\", \"06327";
_vvvvvvv6(new String[]{"06326711","06327223"});
 break; }
case 1: {
 //BA.debugLineNum = 875;BA.debugLine="Case 2 : Call(Array As String(\"06325452\", \"06326";
_vvvvvvv6(new String[]{"06325452","06326171"});
 break; }
case 2: {
 //BA.debugLineNum = 876;BA.debugLine="Case 3 : Call(Array As String(\"06323692\", \"06328";
_vvvvvvv6(new String[]{"06323692","06328193"});
 break; }
case 3: {
 //BA.debugLineNum = 877;BA.debugLine="Case 4 : Call(Array As String(\"06521547\"))";
_vvvvvvv6(new String[]{"06521547"});
 break; }
case 4: {
 //BA.debugLineNum = 878;BA.debugLine="Case 5 : Call(Array As String(\"0930740101\"))";
_vvvvvvv6(new String[]{"0930740101"});
 break; }
case 5: {
 //BA.debugLineNum = 879;BA.debugLine="Case 6 : Call(Array As String(\"06328423\", \"09777";
_vvvvvvv6(new String[]{"06328423","09777779900"});
 break; }
case 6: {
 //BA.debugLineNum = 880;BA.debugLine="Case 7 : Call(Array As String(\"06521558\", \"09777";
_vvvvvvv6(new String[]{"06521558","09777778800"});
 break; }
}
;
 //BA.debugLineNum = 882;BA.debugLine="End Sub";
return "";
}
public static String  _lvwsupport_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 787;BA.debugLine="Sub lvwSupport_ItemClick (Position As Int, Value A";
 //BA.debugLineNum = 788;BA.debugLine="ShowAdMobInterstitial";
_vvvvvvvvv6();
 //BA.debugLineNum = 789;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5))) {
case 0: {
 //BA.debugLineNum = 790;BA.debugLine="Case 1 : CallMeBack";
_vvvvvvvv2();
 break; }
case 1: {
 //BA.debugLineNum = 791;BA.debugLine="Case 2 : WhatIsMyNumber";
_vvvvvvvvvv4();
 break; }
case 2: {
 //BA.debugLineNum = 792;BA.debugLine="Case 3 : APNSettings";
_vvvvvv4();
 break; }
case 3: {
 //BA.debugLineNum = 793;BA.debugLine="Case 4 : USSD";
_vvvvvvvvvv5();
 break; }
case 4: {
 //BA.debugLineNum = 794;BA.debugLine="Case 5 : CallCentre";
_vvvvvvv7();
 break; }
}
;
 //BA.debugLineNum = 796;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv6() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 736;BA.debugLine="Sub MissedCallAlert";
 //BA.debugLineNum = 737;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 739;BA.debugLine="sName = \"Missed Call Alert\"";
_sname = "Missed Call Alert";
 //BA.debugLineNum = 740;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြ၏ဖုန္း စက";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြ၏ဖုန္း စက္ပိတ္ထားသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ ဆက္သြယ္မႈဧရိယာျပင္ပသို႔ ေရာက္ရွိေနသည့္အခ်ိန္တြင္ျဖစ္ေစ၊ မိတ္ေဆြထံေခၚဆိုရန္ႀကိဳးစားေသာ အဝင္ဖုန္းမ်ားအေရအတြက္ကို မိတ္ေဆြ၏ဖုန္း ကြန္ရက္ေပၚသို႔ျပန္လည္ေရာက္ရွိလာေသာအခါ SMS ျဖင့္ သတိေပးခ်က္အျဖစ္ ပို႔ေပးမည္ျဖစ္သည္။<br>";
 //BA.debugLineNum = 741;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 743;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။";
 //BA.debugLineNum = 744;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 745;BA.debugLine="sSubscription = \"MCA\"";
_ssubscription = "MCA";
 //BA.debugLineNum = 746;BA.debugLine="sUnsubscription = \"MCA OFF\"";
_sunsubscription = "MCA OFF";
 break; }
case 1: {
 //BA.debugLineNum = 748;BA.debugLine="sDescription = sDescription & \"- တစ္ပတ္စာဝန္ေဆာ";
_sdescription = _sdescription+"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 749;BA.debugLine="sPhoneNumber = \"222\"";
mostCurrent._vvvvvv6 = "222";
 //BA.debugLineNum = 750;BA.debugLine="sSubscription = \"MCA ON\"";
_ssubscription = "MCA ON";
 //BA.debugLineNum = 751;BA.debugLine="sUnsubscription = \"MCA OFF\"";
_sunsubscription = "MCA OFF";
 break; }
default: {
 //BA.debugLineNum = 753;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။";
 //BA.debugLineNum = 754;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvv6 = "233";
 //BA.debugLineNum = 755;BA.debugLine="sSubscription = \"Open MCA\"";
_ssubscription = "Open MCA";
 //BA.debugLineNum = 756;BA.debugLine="sUnsubscription = \"Cancel MCA\"";
_sunsubscription = "Cancel MCA";
 break; }
}
;
 //BA.debugLineNum = 759;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvv6,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 760;BA.debugLine="End Sub";
return "";
}
public static String  _opttopupother_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 451;BA.debugLine="Sub optTopUpOther_CheckedChange(Checked As Boolean";
 //BA.debugLineNum = 452;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 453;BA.debugLine="Dialog.Views.Label(\"lblPhoneNumber\").Visible = T";
mostCurrent._vvvvvvv4.getViews().Label("lblPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 454;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Visible";
mostCurrent._vvvvvvv4.getViews().EditText("edtPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 455;BA.debugLine="Dialog.Views.Button(\"btnContactPicker\").Visible";
mostCurrent._vvvvvvv4.getViews().Button("btnContactPicker").setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 457;BA.debugLine="End Sub";
return "";
}
public static String  _opttopupown_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 443;BA.debugLine="Sub optTopUpOwn_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 444;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 445;BA.debugLine="Dialog.Views.Label(\"lblPhoneNumber\").Visible = F";
mostCurrent._vvvvvvv4.getViews().Label("lblPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 446;BA.debugLine="Dialog.Views.EditText(\"edtPhoneNumber\").Visible";
mostCurrent._vvvvvvv4.getViews().EditText("edtPhoneNumber").setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 447;BA.debugLine="Dialog.Views.Button(\"btnContactPicker\").Visible";
mostCurrent._vvvvvvv4.getViews().Button("btnContactPicker").setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 449;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv0(String _title,String _pack) throws Exception{
 //BA.debugLineNum = 577;BA.debugLine="Sub Packs(Title As String, Pack As String)";
 //BA.debugLineNum = 578;BA.debugLine="OffersActivity.Title = Title";
mostCurrent._vvvvvvvvv4._v5 = _title;
 //BA.debugLineNum = 579;BA.debugLine="OffersActivity.Offer = Pack";
mostCurrent._vvvvvvvvv4._v6 = _pack;
 //BA.debugLineNum = 580;BA.debugLine="StartActivity(OffersActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvv4.getObject()));
 //BA.debugLineNum = 581;BA.debugLine="End Sub";
return "";
}
public static String  _pager_pagechanged(int _position) throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _ap = null;
flm.b4a.animationplus.AnimationPlusWrapper _ap2 = null;
flm.b4a.animationplus.AnimationSet _a = null;
 //BA.debugLineNum = 306;BA.debugLine="Sub Pager_PageChanged (Position As Int)";
 //BA.debugLineNum = 307;BA.debugLine="iCurrentPage = Position";
_vvvvv0 = _position;
 //BA.debugLineNum = 308;BA.debugLine="pager.RequestFocus";
mostCurrent._vvvvv3.RequestFocus();
 //BA.debugLineNum = 309;BA.debugLine="If Position = 4 Then";
if (_position==4) { 
 //BA.debugLineNum = 310;BA.debugLine="Dim ap, ap2 As AnimationPlus";
_ap = new flm.b4a.animationplus.AnimationPlusWrapper();
_ap2 = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 311;BA.debugLine="Dim a As AnimationSet";
_a = new flm.b4a.animationplus.AnimationSet();
 //BA.debugLineNum = 312;BA.debugLine="ap.InitializeAlpha(\"\", 0, 1)";
_ap.InitializeAlpha(mostCurrent.activityBA,"",(float) (0),(float) (1));
 //BA.debugLineNum = 313;BA.debugLine="ap.StartOffset = 500";
_ap.setStartOffset((long) (500));
 //BA.debugLineNum = 314;BA.debugLine="ap.Duration = 800";
_ap.setDuration((long) (800));
 //BA.debugLineNum = 315;BA.debugLine="ap2.InitializeScaleCenter(\"\", 0, 0, 1, 1, ivAppI";
_ap2.InitializeScaleCenter(mostCurrent.activityBA,"",(float) (0),(float) (0),(float) (1),(float) (1),(android.view.View)(mostCurrent._vvvvvvvv6.getObject()));
 //BA.debugLineNum = 316;BA.debugLine="ap2.Duration = 1500";
_ap2.setDuration((long) (1500));
 //BA.debugLineNum = 317;BA.debugLine="ap2.SetInterpolator(ap2.INTERPOLATOR_OVERSHOOT)";
_ap2.SetInterpolator(_ap2.INTERPOLATOR_OVERSHOOT);
 //BA.debugLineNum = 318;BA.debugLine="a.Initialize(False)";
_a.Initialize(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 319;BA.debugLine="a.AddAnimation(ap)";
_a.AddAnimation(_ap);
 //BA.debugLineNum = 320;BA.debugLine="a.AddAnimation(ap2)";
_a.AddAnimation(_ap2);
 //BA.debugLineNum = 321;BA.debugLine="a.PersistAfter = True";
_a.setPersistAfter(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 322;BA.debugLine="a.Start(ivAppIcon)";
_a.Start((android.view.View)(mostCurrent._vvvvvvvv6.getObject()));
 };
 //BA.debugLineNum = 324;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv0(String _name,String _description,String _phonenumber,String _activation,String _deactivation) throws Exception{
String _sdeactivate = "";
int _iresult = 0;
 //BA.debugLineNum = 903;BA.debugLine="Sub Plan(Name As String, Description As String, Ph";
 //BA.debugLineNum = 904;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 905;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZ";
mostCurrent._vvvvvvv4.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 906;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 907;BA.debugLine="Dim sDeactivate As String";
_sdeactivate = "";
 //BA.debugLineNum = 908;BA.debugLine="If Deactivation.Length > 0 Then sDeactivate = \"De";
if (_deactivation.length()>0) { 
_sdeactivate = "Deactivate";};
 //BA.debugLineNum = 909;BA.debugLine="Dim iResult As Int = Dialog.Msgbox(Name, Descript";
_iresult = mostCurrent._vvvvvvv4.MsgBox(mostCurrent.activityBA,_name,_description,"Activate",_sdeactivate,"ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 910;BA.debugLine="If iResult = DialogResponse.POSITIVE Then";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 911;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 912;BA.debugLine="ps.Send(PhoneNumber, Activation)";
mostCurrent._vvvvvv0.Send(_phonenumber,_activation);
 }else {
 //BA.debugLineNum = 914;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Activation,";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(_activation,"UTF8"))));
 };
 }else if(_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 917;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 918;BA.debugLine="ps.Send(PhoneNumber, Deactivation)";
mostCurrent._vvvvvv0.Send(_phonenumber,_deactivation);
 }else {
 //BA.debugLineNum = 920;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Deactivation";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(_deactivation,"UTF8"))));
 };
 };
 //BA.debugLineNum = 923;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv7() throws Exception{
 //BA.debugLineNum = 571;BA.debugLine="Sub Plans";
 //BA.debugLineNum = 572;BA.debugLine="OffersActivity.Title = \"Plans\"";
mostCurrent._vvvvvvvvv4._v5 = "Plans";
 //BA.debugLineNum = 573;BA.debugLine="OffersActivity.Offer = \"plans\"";
mostCurrent._vvvvvvvvv4._v6 = "plans";
 //BA.debugLineNum = 574;BA.debugLine="StartActivity(OffersActivity)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvvvv4.getObject()));
 //BA.debugLineNum = 575;BA.debugLine="End Sub";
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
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private pm As PackageManager";
_vvvvvvvvv2 = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Public Const AppName As String = pm.GetApplicatio";
_vvv3 = _vvvvvvvvv2.GetApplicationLabel(_vvvvvvvvv1());
 //BA.debugLineNum = 20;BA.debugLine="Public Const AppVersion As String = pm.GetVersion";
_vvv4 = _vvvvvvvvv2.GetVersionName(_vvvvvvvvv1());
 //BA.debugLineNum = 21;BA.debugLine="Public Const AppPublisher As String = \"Kyaw Swar";
_vvv5 = BA.__b (new byte[] {27,56,33,-88,97,13,40,-26,35,112,8,-31,33,41,42}, 963985);
 //BA.debugLineNum = 23;BA.debugLine="Private Const ID_ACTION_HOME As Int = 0";
_id_action_home = (int) (0);
 //BA.debugLineNum = 24;BA.debugLine="Private Const ID_ACTION_OVERFLOW As Int = 99";
_id_action_overflow = (int) (99);
 //BA.debugLineNum = 26;BA.debugLine="Private Const SIMOperators As Map = CreateMap(\"41";
_vvvv2 = new anywheresoftware.b4a.objects.collections.Map();
_vvvv2 = anywheresoftware.b4a.keywords.Common.createMap(new Object[] {(Object)(BA.__b (new byte[] {100,114,119,123,112}, 43933)),(Object)(BA.__b (new byte[] {29,18,35}, 523660)),(Object)(BA.__b (new byte[] {100,114,-85,116,117}, 337845)),(Object)(BA.__b (new byte[] {29,17,2,65,2,26,4,120}, 924071)),(Object)(BA.__b (new byte[] {100,114,-65,-30,114}, 374508)),(Object)(BA.__b (new byte[] {29,7,45,-13,36,49}, 537438)),(Object)(BA.__b (new byte[] {100,115,12,29,116}, 633235)),(Object)(BA.__b (new byte[] {31,45,113,-124,37,50,115}, 618535)),(Object)(BA.__b (new byte[] {100,114,-55,28,119}, 283802)),(Object)(BA.__b (new byte[] {4,38,-16,13,47,51,-15}, 339834))});
 //BA.debugLineNum = 28;BA.debugLine="Private tmrBanner As Timer";
_vvvv0 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 29;BA.debugLine="Private iCurrentBanner As Int = 0";
_vvvvv7 = (int) (0);
 //BA.debugLineNum = 31;BA.debugLine="Private Const TYPE_PREPAID As Int = 1";
_type_prepaid = (int) (1);
 //BA.debugLineNum = 32;BA.debugLine="Private Const TYPE_OFFERS As Int = 2";
_type_offers = (int) (2);
 //BA.debugLineNum = 33;BA.debugLine="Private Const TYPE_SUPPORT As Int = 3";
_type_support = (int) (3);
 //BA.debugLineNum = 34;BA.debugLine="Private Const TYPE_STORES As Int = 4";
_type_stores = (int) (4);
 //BA.debugLineNum = 35;BA.debugLine="Private Const TYPE_ABOUT As Int = 5";
_type_about = (int) (5);
 //BA.debugLineNum = 37;BA.debugLine="Public SmartZawgyi As Typeface = Typeface.LoadFro";
_vvv6 = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
_vvv6.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets(BA.__b (new byte[] {3,46,37,-105,53,6,58,-54,54,43,49,-99,34,54,38}, 39696))));
 //BA.debugLineNum = 39;BA.debugLine="Public Const FILL_PARENT As Int = -1";
_fill_parent = (int) (-1);
 //BA.debugLineNum = 40;BA.debugLine="Public Const WRAP_CONTENT As Int = -2";
_wrap_content = (int) (-2);
 //BA.debugLineNum = 42;BA.debugLine="Private iCurrentPage As Int";
_vvvvv0 = 0;
 //BA.debugLineNum = 44;BA.debugLine="Private p As Phone";
_vvvvvvvvv3 = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 45;BA.debugLine="Public SIMOperator As String = p.GetSimOperator";
_vvv7 = _vvvvvvvvv3.GetSimOperator();
 //BA.debugLineNum = 47;BA.debugLine="Private Const AdMobInterstitialID As String = \"ca";
_vvvvvv2 = BA.__b (new byte[] {51,35,-6,-109,49,45,-27,-38,36,49,-26,-100,103,115,-21,-117,51,110,-20,-119,124,127,-3,-107,116,106,-32,-100,112,44,-32,-97,115,106,-16,-116,105,101}, 697292);
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv7() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 652;BA.debugLine="Sub RingbackTone";
 //BA.debugLineNum = 653;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 655;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံသို႔ ဖု";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ ျပန္လည္ေျဖဆိုမႈကို ေစာင့္ဆိုင္းေနစဥ္အတြင္း မိတ္ေဆြေရြးခ်ယ္သတ္မွတ္ထားေသာ ဂီတသံစဥ္ကို ခံစားနားဆင္ေနႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 656;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41405","41406")) {
case 0: {
 //BA.debugLineNum = 658;BA.debugLine="sName = \"FunTone\"";
_sname = "FunTone";
 //BA.debugLineNum = 659;BA.debugLine="sDescription = sDescription & \"- တစ္ရက္စာဝန္ေဆာ";
_sdescription = _sdescription+"- တစ္ရက္စာဝန္ေဆာင္ခ ၇၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ ေန႔စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ လစဥ္ ၇၅၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- FunTone IVR နံပါတ္ ၃၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ FunTone အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 660;BA.debugLine="sSubscription = \"*3333#\"";
_ssubscription = "*3333#";
 //BA.debugLineNum = 661;BA.debugLine="sUnsubscription = \"*3333*0#\"";
_sunsubscription = "*3333*0#";
 break; }
case 1: {
 //BA.debugLineNum = 663;BA.debugLine="sName = \"My Tune\"";
_sname = "My Tune";
 //BA.debugLineNum = 664;BA.debugLine="sDescription = sDescription & \"- တစ္ပတ္စာဝန္ေဆာ";
_sdescription = _sdescription+"- တစ္ပတ္စာဝန္ေဆာင္ခ ၃၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ အပတ္စဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ႏွစ္စဥ္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ငွားရမ္းႏိုင္ပါသည္။<br>- My Tune IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ My Tune အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 665;BA.debugLine="sPhoneNumber = \"333\"";
mostCurrent._vvvvvv6 = "333";
 //BA.debugLineNum = 666;BA.debugLine="sSubscription = \"MT ON\"";
_ssubscription = "MT ON";
 //BA.debugLineNum = 667;BA.debugLine="sUnsubscription = \"MT OFF\"";
_sunsubscription = "MT OFF";
 break; }
default: {
 //BA.debugLineNum = 669;BA.debugLine="sName = \"Hello Music\"";
_sname = "Hello Music";
 //BA.debugLineNum = 670;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၂၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- ေတးသြားတစ္ပုဒ္လွ်င္ ၃၀၀ က်ပ္ႏႈန္းျဖင့္ ဝယ္ယူႏိုင္ပါသည္။<br>- Hello Music IVR နံပါတ္ ၃၃၃ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Hello Music အေကာင့္အား စီမံခန္႔ခြဲႏိုင္ပါသည္။";
 //BA.debugLineNum = 671;BA.debugLine="sPhoneNumber = \"333\"";
mostCurrent._vvvvvv6 = "333";
 //BA.debugLineNum = 672;BA.debugLine="sSubscription = \"register\"";
_ssubscription = "register";
 //BA.debugLineNum = 673;BA.debugLine="sUnsubscription = \"unregister\"";
_sunsubscription = "unregister";
 break; }
}
;
 //BA.debugLineNum = 676;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvv6,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 677;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv6() throws Exception{
 //BA.debugLineNum = 988;BA.debugLine="Sub ShowAdMobInterstitial";
 //BA.debugLineNum = 989;BA.debugLine="If AdMobInterstitial.Status = AdMobInterstitial.S";
if (mostCurrent._vvvvvv1.Status==mostCurrent._vvvvvv1.Status_AdReadyToShow) { 
mostCurrent._vvvvvv1.Show(mostCurrent.activityBA);};
 //BA.debugLineNum = 990;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbanner_tick() throws Exception{
 //BA.debugLineNum = 198;BA.debugLine="Sub tmrBanner_Tick";
 //BA.debugLineNum = 199;BA.debugLine="iCurrentBanner = iCurrentBanner + 1";
_vvvvv7 = (int) (_vvvvv7+1);
 //BA.debugLineNum = 200;BA.debugLine="If iCurrentBanner > asBanner.Length - 1 Then iCur";
if (_vvvvv7>mostCurrent._vvvv6.length-1) { 
_vvvvv7 = (int) (0);};
 //BA.debugLineNum = 201;BA.debugLine="pagerBanner.GotoPage(iCurrentBanner, True)";
mostCurrent._vvvv7.GotoPage(_vvvvv7,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv3() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
 //BA.debugLineNum = 523;BA.debugLine="Sub TopMeUp";
 //BA.debugLineNum = 524;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 525;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 526;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 527;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"BalanceTransferAndTopMeUpDialog");
 //BA.debugLineNum = 528;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 529;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 530;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 531;BA.debugLine="Dim lblAmount As Label = DialogLayout.Views.Get(\"";
_lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
_lblamount.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblAmount")));
 //BA.debugLineNum = 532;BA.debugLine="Dim edtAmount As EditText = DialogLayout.Views.Ge";
_edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtamount.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtAmount")));
 //BA.debugLineNum = 534;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtAmoun";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtamount.getTop()+_edtamount.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 536;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41405")) {
case 0: {
 //BA.debugLineNum = 537;BA.debugLine="Case \"41405\" : sCode = \"*126*\"' Ooredoo";
mostCurrent._vvvvvv5 = "*126*";
 break; }
}
;
 //BA.debugLineNum = 540;BA.debugLine="lblPhoneNumber.Text = \"ေငြျဖည့္ေပးမည့္သူ၏ဖုန္းနံပ";
_lblphonenumber.setText((Object)("ေငြျဖည့္ေပးမည့္သူ၏ဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 542;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 544;BA.debugLine="lblAmount.Text = \"ျဖည့္ေပးေစလိုေသာေငြပမာဏ႐ိုက္ထည္";
_lblamount.setText((Object)("ျဖည့္ေပးေစလိုေသာေငြပမာဏ႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 546;BA.debugLine="Do Until edtPhoneNumber.Text.Length > 0 And edtAm";
while (!(_edtphonenumber.getText().length()>0 && _edtamount.getText().length()>0)) {
 //BA.debugLineNum = 547;BA.debugLine="If DialogLayout.Show(\"မိမိဖုန္းအားေငြျဖည့္ေပးပါရ";
if (_dialoglayout.Show("မိမိဖုန္းအားေငြျဖည့္ေပးပါရန္","ပို႔ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 548;BA.debugLine="If edtPhoneNumber.Text.Length = 0 Then";
if (_edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 549;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 550;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_edtamount.getText().length()==0) { 
 //BA.debugLineNum = 552;BA.debugLine="edtAmount.RequestFocus";
_edtamount.RequestFocus();
 //BA.debugLineNum = 553;BA.debugLine="ToastMessageShow(\"ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပ";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ေငြပမာဏေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 556;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPho";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtphonenumber.getText()+"*"+_edtamount.getText()+"#","UTF8"))));
 //BA.debugLineNum = 557;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv2() throws Exception{
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _opttopupown = null;
anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _opttopupother = null;
anywheresoftware.b4a.objects.LabelWrapper _lblpin = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtpin = null;
anywheresoftware.b4a.objects.LabelWrapper _lblphonenumber = null;
anywheresoftware.b4a.objects.EditTextWrapper _edtphonenumber = null;
anywheresoftware.b4a.objects.ButtonWrapper _btncontactpicker = null;
 //BA.debugLineNum = 390;BA.debugLine="Sub TopUp";
 //BA.debugLineNum = 391;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 392;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 393;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 394;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"TopUpDialog");
 //BA.debugLineNum = 395;BA.debugLine="Dim optTopUpOwn As RadioButton = DialogLayout.Vie";
_opttopupown = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
_opttopupown.setObject((android.widget.RadioButton)(_dialoglayout.getViews().Get("optTopUpOwn")));
 //BA.debugLineNum = 396;BA.debugLine="Dim optTopUpOther As RadioButton = DialogLayout.V";
_opttopupother = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
_opttopupother.setObject((android.widget.RadioButton)(_dialoglayout.getViews().Get("optTopUpOther")));
 //BA.debugLineNum = 397;BA.debugLine="Dim lblPIN As Label = DialogLayout.Views.Get(\"lbl";
_lblpin = new anywheresoftware.b4a.objects.LabelWrapper();
_lblpin.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPIN")));
 //BA.debugLineNum = 398;BA.debugLine="Dim edtPIN As EditText = DialogLayout.Views.Get(\"";
_edtpin = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtpin.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPIN")));
 //BA.debugLineNum = 399;BA.debugLine="Dim lblPhoneNumber As Label = DialogLayout.Views.";
_lblphonenumber = new anywheresoftware.b4a.objects.LabelWrapper();
_lblphonenumber.setObject((android.widget.TextView)(_dialoglayout.getViews().Get("lblPhoneNumber")));
 //BA.debugLineNum = 400;BA.debugLine="Dim edtPhoneNumber As EditText = DialogLayout.Vie";
_edtphonenumber = new anywheresoftware.b4a.objects.EditTextWrapper();
_edtphonenumber.setObject((android.widget.EditText)(_dialoglayout.getViews().Get("edtPhoneNumber")));
 //BA.debugLineNum = 401;BA.debugLine="Dim btnContactPicker As Button = DialogLayout.Vie";
_btncontactpicker = new anywheresoftware.b4a.objects.ButtonWrapper();
_btncontactpicker.setObject((android.widget.Button)(_dialoglayout.getViews().Get("btnContactPicker")));
 //BA.debugLineNum = 403;BA.debugLine="DialogLayout.Views.Panel(\"pnl\").Height = edtPhone";
_dialoglayout.getViews().Panel("pnl").setHeight((int) (_edtphonenumber.getTop()+_edtphonenumber.getHeight()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))));
 //BA.debugLineNum = 405;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406","41404")) {
case 0: 
case 1: 
case 2: {
 //BA.debugLineNum = 406;BA.debugLine="Case \"41401\", \"41405\", \"41406\" : sCode = \"*123*\"";
mostCurrent._vvvvvv5 = "*123*";
 break; }
case 3: {
 //BA.debugLineNum = 407;BA.debugLine="Case \"41404\" : sCode = \"*166*\"' MPT CDMA";
mostCurrent._vvvvvv5 = "*166*";
 break; }
default: {
 //BA.debugLineNum = 408;BA.debugLine="Case Else : sCode = \"*124*\"' MECTel";
mostCurrent._vvvvvv5 = "*124*";
 break; }
}
;
 //BA.debugLineNum = 411;BA.debugLine="optTopUpOwn.Text = \"မိမိဖုန္း\"";
_opttopupown.setText((Object)("မိမိဖုန္း"));
 //BA.debugLineNum = 413;BA.debugLine="If SIMOperator <> \"41401\" And SIMOperator <> \"414";
if ((_vvv7).equals("41401") == false && (_vvv7).equals("41405") == false && (_vvv7).equals("41406") == false) { 
_opttopupother.setEnabled(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 414;BA.debugLine="optTopUpOther.Text = \"အျခားဖုန္း\"";
_opttopupother.setText((Object)("အျခားဖုန္း"));
 //BA.debugLineNum = 416;BA.debugLine="lblPIN.Text = \"Top Up ကတ္မွ PIN နံပါတ္႐ိုက္ထည့္ပါ";
_lblpin.setText((Object)("Top Up ကတ္မွ PIN နံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 418;BA.debugLine="lblPhoneNumber.Text = \"ေငြျဖည့္ေပးလိုေသာဖုန္းနံပါ";
_lblphonenumber.setText((Object)("ေငြျဖည့္ေပးလိုေသာဖုန္းနံပါတ္႐ိုက္ထည့္ပါ။"));
 //BA.debugLineNum = 420;BA.debugLine="btnContactPicker.Background = xml.GetDrawable(\"17";
_btncontactpicker.setBackground(mostCurrent._vvvvvvv5.GetDrawable("17301547"));
 //BA.debugLineNum = 422;BA.debugLine="Do Until (optTopUpOwn.Checked = True And edtPIN.T";
while (!((_opttopupown.getChecked()==anywheresoftware.b4a.keywords.Common.True && _edtpin.getText().length()>0) || (_opttopupother.getChecked()==anywheresoftware.b4a.keywords.Common.True && (_edtpin.getText().length()>0 && _edtphonenumber.getText().length()>0)))) {
 //BA.debugLineNum = 423;BA.debugLine="If DialogLayout.Show(\"ဖုန္းေငြျဖည့္ရန္\", \"ေငြျဖည";
if (_dialoglayout.Show("ဖုန္းေငြျဖည့္ရန္","ေငြျဖည့္ပါ","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null))==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
if (true) return "";};
 //BA.debugLineNum = 424;BA.debugLine="If edtPIN.Text.Length = 0 Then";
if (_edtpin.getText().length()==0) { 
 //BA.debugLineNum = 425;BA.debugLine="edtPIN.RequestFocus";
_edtpin.RequestFocus();
 //BA.debugLineNum = 426;BA.debugLine="ToastMessageShow(\"PIN နံပါတ္ေနရာကို ကြက္လပ္ထား၍";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("PIN နံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 }else if(_opttopupother.getChecked()==anywheresoftware.b4a.keywords.Common.True && _edtphonenumber.getText().length()==0) { 
 //BA.debugLineNum = 428;BA.debugLine="edtPhoneNumber.RequestFocus";
_edtphonenumber.RequestFocus();
 //BA.debugLineNum = 429;BA.debugLine="ToastMessageShow(\"ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("ဖုန္းနံပါတ္ေနရာကို ကြက္လပ္ထား၍မရပါ။",anywheresoftware.b4a.keywords.Common.True);
 };
 }
;
 //BA.debugLineNum = 432;BA.debugLine="If optTopUpOwn.Checked Then";
if (_opttopupown.getChecked()) { 
 //BA.debugLineNum = 433;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtPI";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtpin.getText()+"#","UTF8"))));
 }else if(_opttopupother.getChecked()) { 
 //BA.debugLineNum = 435;BA.debugLine="If SIMOperator = \"41405\" Then' Ooredoo";
if ((_vvv7).equals("41405")) { 
 //BA.debugLineNum = 436;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtP";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtphonenumber.getText()+"*"+_edtpin.getText()+"*1#","UTF8"))));
 }else {
 //BA.debugLineNum = 438;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode & edtP";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5+_edtpin.getText()+"*"+_edtphonenumber.getText()+"#","UTF8"))));
 };
 };
 //BA.debugLineNum = 441;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 853;BA.debugLine="Sub USSD";
 //BA.debugLineNum = 854;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 855;BA.debugLine="Case \"41401\" : sCode = \"*106#\"' MPT";
mostCurrent._vvvvvv5 = "*106#";
 break; }
case 1: {
 //BA.debugLineNum = 856;BA.debugLine="Case \"41405\" : sCode = \"*133#\"' Ooredoo";
mostCurrent._vvvvvv5 = "*133#";
 break; }
case 2: {
 //BA.debugLineNum = 857;BA.debugLine="Case \"41406\" : sCode = \"*979#\"' Telenor";
mostCurrent._vvvvvv5 = "*979#";
 break; }
}
;
 //BA.debugLineNum = 859;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5,"UTF8"))));
 //BA.debugLineNum = 860;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv1() throws Exception{
int _iresult = 0;
com.datasteam.b4a.xtraviews.DialogViewLayout _dialoglayout = null;
anywheresoftware.b4a.objects.ListViewWrapper _lvwlist = null;
 //BA.debugLineNum = 583;BA.debugLine="Sub ValueAddedServices";
 //BA.debugLineNum = 584;BA.debugLine="Dim iResult As Int";
_iresult = 0;
 //BA.debugLineNum = 586;BA.debugLine="Dialog.Options.Dimensions.Width = 100%x";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Width = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA);
 //BA.debugLineNum = 587;BA.debugLine="Dialog.Options.Dimensions.Height = 303dip";
mostCurrent._vvvvvvv4.getOptions().Dimensions.Height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (303));
 //BA.debugLineNum = 588;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 589;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 590;BA.debugLine="Dim DialogLayout As DialogViewLayout = Dialog.Loa";
_dialoglayout = mostCurrent._vvvvvvv4.LoadLayout(mostCurrent.activityBA,"ListDialog");
 //BA.debugLineNum = 591;BA.debugLine="Dim lvwList As ListView = DialogLayout.Views.Get(";
_lvwlist = new anywheresoftware.b4a.objects.ListViewWrapper();
_lvwlist.setObject((anywheresoftware.b4a.objects.ListViewWrapper.SimpleListView)(_dialoglayout.getViews().Get("lvwList")));
 //BA.debugLineNum = 593;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Height = 60dip";
_lvwlist.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60)));
 //BA.debugLineNum = 594;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Typeface = SmartZ";
_lvwlist.getTwoLinesAndBitmap().Label.setTypeface((android.graphics.Typeface)(_vvv6.getObject()));
 //BA.debugLineNum = 595;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.Gravity = Gravity";
_lvwlist.getTwoLinesAndBitmap().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL);
 //BA.debugLineNum = 596;BA.debugLine="lvwList.TwoLinesAndBitmap.Label.TextColor = Color";
_lvwlist.getTwoLinesAndBitmap().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 598;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"414";
if ((_vvv7).equals("41404") == false && (_vvv7).equals("41405") == false) { 
_lvwlist.AddTwoLinesAndBitmap2("အင္တာနက္ဝန္ေဆာင္မႈ","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(1));};
 //BA.debugLineNum = 599;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41404","41405","41406")) {
case 0: 
case 1: {
 break; }
case 2: {
 //BA.debugLineNum = 602;BA.debugLine="lvwList.AddTwoLinesAndBitmap2(\"FunTone\", \"\", Loa";
_lvwlist.AddTwoLinesAndBitmap2("FunTone","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(2));
 break; }
case 3: {
 //BA.debugLineNum = 604;BA.debugLine="lvwList.AddTwoLinesAndBitmap2(\"My Tune\", \"\", Loa";
_lvwlist.AddTwoLinesAndBitmap2("My Tune","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(2));
 break; }
default: {
 //BA.debugLineNum = 606;BA.debugLine="lvwList.AddTwoLinesAndBitmap2(\"Hello Music\", \"\",";
_lvwlist.AddTwoLinesAndBitmap2("Hello Music","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(2));
 break; }
}
;
 //BA.debugLineNum = 608;BA.debugLine="If SIMOperator = \"41405\" Then lvwList.AddTwoLines";
if ((_vvv7).equals("41405")) { 
_lvwlist.AddTwoLinesAndBitmap2("CLIR","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(3));};
 //BA.debugLineNum = 609;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"41404\"";
if ((_vvv7).equals("41401") || (_vvv7).equals("41404")) { 
_lvwlist.AddTwoLinesAndBitmap2("Call Forwarding","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(4));};
 //BA.debugLineNum = 610;BA.debugLine="If SIMOperator = \"41401\" Or SIMOperator = \"41404\"";
if ((_vvv7).equals("41401") || (_vvv7).equals("41404")) { 
_lvwlist.AddTwoLinesAndBitmap2("Call Waiting","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(5));};
 //BA.debugLineNum = 611;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"414";
if ((_vvv7).equals("41404") == false && (_vvv7).equals("41405") == false) { 
_lvwlist.AddTwoLinesAndBitmap2("Missed Call Alert","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(6));};
 //BA.debugLineNum = 612;BA.debugLine="If SIMOperator <> \"41404\" And SIMOperator <> \"414";
if ((_vvv7).equals("41404") == false && (_vvv7).equals("41405") == false) { 
_lvwlist.AddTwoLinesAndBitmap2("Voice Mail","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"offers.png").getObject()),(Object)(7));};
 //BA.debugLineNum = 614;BA.debugLine="lvwList.Tag = \"Value\"";
_lvwlist.setTag((Object)("Value"));
 //BA.debugLineNum = 616;BA.debugLine="iResult = DialogLayout.Show(\"ထပ္ေဆာင္းဝန္ေဆာင္မႈမ";
_iresult = _dialoglayout.Show("ထပ္ေဆာင္းဝန္ေဆာင္မႈမ်ား","","","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 617;BA.debugLine="Select iResult";
switch (BA.switchObjectToInt(_iresult,anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL,(int) (1),(int) (2),(int) (3),(int) (4),(int) (5),(int) (6),(int) (7))) {
case 0: {
 //BA.debugLineNum = 618;BA.debugLine="Case DialogResponse.CANCEL : Return";
if (true) return "";
 break; }
case 1: {
 //BA.debugLineNum = 619;BA.debugLine="Case 1 : DataService";
_vvvvvvvv7();
 break; }
case 2: {
 //BA.debugLineNum = 620;BA.debugLine="Case 2 : RingbackTone";
_vvvvvvvvvv7();
 break; }
case 3: {
 //BA.debugLineNum = 621;BA.debugLine="Case 3 : CLIR";
_vvvvvvvv5();
 break; }
case 4: {
 //BA.debugLineNum = 622;BA.debugLine="Case 4 : CallForwarding";
_vvvvvvv0();
 break; }
case 5: {
 //BA.debugLineNum = 623;BA.debugLine="Case 5 : CallWaiting";
_vvvvvvvv3();
 break; }
case 6: {
 //BA.debugLineNum = 624;BA.debugLine="Case 6 : MissedCallAlert";
_vvvvvvvvvv6();
 break; }
case 7: {
 //BA.debugLineNum = 625;BA.debugLine="Case 7 : VoiceMail";
_vvvvvvvvvv0();
 break; }
}
;
 //BA.debugLineNum = 627;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv1(String _name,String _description,String _phonenumber,String _subscription,String _unsubscription) throws Exception{
int _iresult = 0;
 //BA.debugLineNum = 925;BA.debugLine="Sub VAS(Name As String, Description As String, Pho";
 //BA.debugLineNum = 926;BA.debugLine="Dialog.Options.Elements.Title.Typeface = SmartZaw";
mostCurrent._vvvvvvv4.getOptions().Elements.Title.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 927;BA.debugLine="Dialog.Options.Elements.Message.Typeface = SmartZ";
mostCurrent._vvvvvvv4.getOptions().Elements.Message.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 928;BA.debugLine="Dialog.Options.Elements.Buttons.Default.Style.Typ";
mostCurrent._vvvvvvv4.getOptions().Elements.Buttons.Default.Style.Typeface = (android.graphics.Typeface)(_vvv6.getObject());
 //BA.debugLineNum = 929;BA.debugLine="Dim iResult As Int = Dialog.Msgbox(Name, Descript";
_iresult = mostCurrent._vvvvvvv4.MsgBox(mostCurrent.activityBA,_name,_description,"Subscribe","Unsubscribe","ပယ္ဖ်က္ပါ",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 930;BA.debugLine="If iResult = DialogResponse.POSITIVE Then";
if (_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 931;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 932;BA.debugLine="ps.Send(PhoneNumber, Subscription)";
mostCurrent._vvvvvv0.Send(_phonenumber,_subscription);
 }else {
 //BA.debugLineNum = 934;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Subscription";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(_subscription,"UTF8"))));
 };
 }else if(_iresult==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 937;BA.debugLine="If PhoneNumber.Length > 0 Then";
if (_phonenumber.length()>0) { 
 //BA.debugLineNum = 938;BA.debugLine="ps.Send(PhoneNumber, Unsubscription)";
mostCurrent._vvvvvv0.Send(_phonenumber,_unsubscription);
 }else {
 //BA.debugLineNum = 940;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(Unsubscripti";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(_unsubscription,"UTF8"))));
 };
 };
 //BA.debugLineNum = 943;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv0() throws Exception{
String _sname = "";
String _sdescription = "";
String _ssubscription = "";
String _sunsubscription = "";
 //BA.debugLineNum = 762;BA.debugLine="Sub VoiceMail";
 //BA.debugLineNum = 763;BA.debugLine="Dim sName, sDescription, sPhoneNumber, sSubscript";
_sname = "";
_sdescription = "";
mostCurrent._vvvvvv6 = "";
_ssubscription = "";
_sunsubscription = "";
 //BA.debugLineNum = 765;BA.debugLine="sName = \"Voice Mail\"";
_sname = "Voice Mail";
 //BA.debugLineNum = 766;BA.debugLine="sDescription = \"- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းမကို";
_sdescription = "- ဤဝန္ေဆာင္မႈသည္ မိတ္ေဆြဖုန္းမကိုင္ႏိုင္သည့္အခ်ိန္တြင္ မိတ္ေဆြထံသို႔ ဖုန္းေခၚဆိုသူမ်ားသည္ အသံျဖင့္ အမွာစကားခ်န္ထားခဲ့ႏိုင္ပါသည္။<br>";
 //BA.debugLineNum = 767;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41406")) {
case 0: {
 //BA.debugLineNum = 769;BA.debugLine="sDescription = sDescription & \"- ဝန္ေဆာင္ခ အခမဲ";
_sdescription = _sdescription+"- ဝန္ေဆာင္ခ အခမဲ့ ျဖစ္သည္။<br>- Voice Mail IVR နံပါတ္ ၁၅၅၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 770;BA.debugLine="sPhoneNumber = \"1331\"";
mostCurrent._vvvvvv6 = "1331";
 //BA.debugLineNum = 771;BA.debugLine="sSubscription = \"VMS\"";
_ssubscription = "VMS";
 //BA.debugLineNum = 772;BA.debugLine="sUnsubscription = \"VMS OFF\"";
_sunsubscription = "VMS OFF";
 break; }
case 1: {
 //BA.debugLineNum = 774;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၀၀ သို႔ တစ္မိနစ္လွ်င္ ၅၀ က်ပ္ႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 775;BA.debugLine="sSubscription = \"*979*2*3*1*1#\"";
_ssubscription = "*979*2*3*1*1#";
 //BA.debugLineNum = 776;BA.debugLine="sUnsubscription = \"*979*2*3*2*1#\"";
_sunsubscription = "*979*2*3*2*1#";
 break; }
default: {
 //BA.debugLineNum = 778;BA.debugLine="sDescription = sDescription & \"- တစ္လစာဝန္ေဆာင္";
_sdescription = _sdescription+"- တစ္လစာဝန္ေဆာင္ခ ၁၀၀၀ က်ပ္ က်သင့္မည္ျဖစ္ၿပီး၊ ဖုန္းလက္က်န္ေငြထဲမွ လစဥ္ေကာက္ခံသြားပါလိမ့္မည္။<br>- Voice Mail IVR နံပါတ္ ၂၄၄၀ သို႔ တစ္မိနစ္လွ်င္ ပံုမွန္ဖုန္းေခၚဆိုခႏႈန္းျဖင့္ ေခၚဆို၍ Voice Mail မ်ားအား စစ္ေဆးႏိုင္ပါသည္။";
 //BA.debugLineNum = 779;BA.debugLine="sPhoneNumber = \"233\"";
mostCurrent._vvvvvv6 = "233";
 //BA.debugLineNum = 780;BA.debugLine="sSubscription = \"Open VMS\"";
_ssubscription = "Open VMS";
 //BA.debugLineNum = 781;BA.debugLine="sUnsubscription = \"Cancel VMS\"";
_sunsubscription = "Cancel VMS";
 break; }
}
;
 //BA.debugLineNum = 784;BA.debugLine="VAS(sName, sDescription, sPhoneNumber, sSubscript";
_vvvvvvvv1(_sname,_sdescription,mostCurrent._vvvvvv6,_ssubscription,_sunsubscription);
 //BA.debugLineNum = 785;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv4() throws Exception{
 //BA.debugLineNum = 829;BA.debugLine="Sub WhatIsMyNumber";
 //BA.debugLineNum = 830;BA.debugLine="Select SIMOperator";
switch (BA.switchObjectToInt(_vvv7,"41401","41405","41406")) {
case 0: {
 //BA.debugLineNum = 831;BA.debugLine="Case \"41401\" : sCode = \"*888#\"' MPT";
mostCurrent._vvvvvv5 = "*888#";
 break; }
case 1: {
 //BA.debugLineNum = 832;BA.debugLine="Case \"41405\" : sCode = \"*133*5#\"' Ooredoo";
mostCurrent._vvvvvv5 = "*133*5#";
 break; }
case 2: {
 //BA.debugLineNum = 833;BA.debugLine="Case \"41406\" : sCode = \"*97#\"' Telenor";
mostCurrent._vvvvvv5 = "*97#";
 break; }
}
;
 //BA.debugLineNum = 835;BA.debugLine="StartActivity(pc.Call(su.EncodeUrl(sCode, \"UTF8\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vvvvvvv1.Call(mostCurrent._vvvvvvv2.EncodeUrl(mostCurrent._vvvvvv5,"UTF8"))));
 //BA.debugLineNum = 836;BA.debugLine="End Sub";
return "";
}
}
