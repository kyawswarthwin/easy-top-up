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

public class welcomeactivity extends Activity implements B4AActivity{
	public static welcomeactivity mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.welcomeactivity");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (welcomeactivity).");
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
		activityBA = new BA(this, layout, processBA, "com.moribanxenia.easytopup", "com.moribanxenia.easytopup.welcomeactivity");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.moribanxenia.easytopup.welcomeactivity", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (welcomeactivity) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (welcomeactivity) Resume **");
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
		return welcomeactivity.class;
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
        BA.LogInfo("** Activity (welcomeactivity) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (welcomeactivity) Resume **");
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
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvvvvvvv1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvvvvvv2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vvvvvvvvvvvv3 = null;
public flm.b4a.animationplus.AnimationPlusWrapper _vvvvvvvvvvvv4 = null;
public com.moribanxenia.easytopup.main _vvvvvvvvvvv6 = null;
public com.moribanxenia.easytopup.offersactivity _vvvvvvvvvv1 = null;
public com.moribanxenia.easytopup.statemanager _vvvvvv5 = null;
public com.moribanxenia.easytopup.starter _vvvv5 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 17;BA.debugLine="Activity.Color = Colors.ARGB(200, 0, 0, 0)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (200),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 19;BA.debugLine="lblMessage.Initialize(\"\")";
mostCurrent._vvvvvvvvvvvv1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 20;BA.debugLine="lblMessage.Text = \"ေဘးတိုက္ဆြဲ၍စာမ်က္ႏွာမ်ားေျပာင";
mostCurrent._vvvvvvvvvvvv1.setText((Object)("ေဘးတိုက္ဆြဲ၍စာမ်က္ႏွာမ်ားေျပာင္းလဲႏိုင္ပါသည္။"));
 //BA.debugLineNum = 21;BA.debugLine="lblMessage.Typeface = Main.SmartZawgyi";
mostCurrent._vvvvvvvvvvvv1.setTypeface((android.graphics.Typeface)(mostCurrent._vvvvvvvvvvv6._vvv7.getObject()));
 //BA.debugLineNum = 22;BA.debugLine="lblMessage.Gravity = Gravity.CENTER_HORIZONTAL";
mostCurrent._vvvvvvvvvvvv1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL);
 //BA.debugLineNum = 23;BA.debugLine="lblMessage.TextSize = 16.5";
mostCurrent._vvvvvvvvvvvv1.setTextSize((float) (16.5));
 //BA.debugLineNum = 24;BA.debugLine="Activity.AddView(lblMessage, 10dip, 30%y, 100%x -";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvvvvvv1.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (30),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),mostCurrent._vvvvvvvvvvv6._wrap_content);
 //BA.debugLineNum = 26;BA.debugLine="ivSwipeBackground.Initialize(\"\")";
mostCurrent._vvvvvvvvvvvv2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 27;BA.debugLine="ivSwipeBackground.Bitmap = LoadBitmap(File.DirAss";
mostCurrent._vvvvvvvvvvvv2.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"swipebackground.png").getObject()));
 //BA.debugLineNum = 28;BA.debugLine="ivSwipeBackground.Gravity = Gravity.FILL";
mostCurrent._vvvvvvvvvvvv2.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 29;BA.debugLine="Activity.AddView(ivSwipeBackground, 0, 35%y, 100%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvvvvvv2.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (35),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 31;BA.debugLine="ivSwipe.Initialize(\"\")";
mostCurrent._vvvvvvvvvvvv3.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 32;BA.debugLine="ivSwipe.Bitmap = LoadBitmap(File.DirAssets, \"swip";
mostCurrent._vvvvvvvvvvvv3.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"swipe.png").getObject()));
 //BA.debugLineNum = 33;BA.debugLine="ivSwipe.Gravity = Gravity.FILL";
mostCurrent._vvvvvvvvvvvv3.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 34;BA.debugLine="Activity.AddView(ivSwipe, 28%x, 35%y, 100%x, 100%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvvvvvv3.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (35),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 36;BA.debugLine="ap.InitializeTranslate(\"\", 0, 0, 28%x, 0)";
mostCurrent._vvvvvvvvvvvv4.InitializeTranslate(mostCurrent.activityBA,"",(float) (0),(float) (0),(float) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA)),(float) (0));
 //BA.debugLineNum = 37;BA.debugLine="ap.Duration = 3000";
mostCurrent._vvvvvvvvvvvv4.setDuration((long) (3000));
 //BA.debugLineNum = 38;BA.debugLine="ap.SetInterpolatorWithParam(ap.INTERPOLATOR_CYCLE";
mostCurrent._vvvvvvvvvvvv4.SetInterpolatorWithParam(mostCurrent._vvvvvvvvvvvv4.INTERPOLATOR_CYCLE,(float) (1));
 //BA.debugLineNum = 39;BA.debugLine="ap.RepeatCount = ap.REPEAT_INFINITE";
mostCurrent._vvvvvvvvvvvv4.setRepeatCount(mostCurrent._vvvvvvvvvvvv4.REPEAT_INFINITE);
 //BA.debugLineNum = 40;BA.debugLine="ap.Start(ivSwipe)";
mostCurrent._vvvvvvvvvvvv4.Start((android.view.View)(mostCurrent._vvvvvvvvvvvv3.getObject()));
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 52;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then Activity.";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
mostCurrent._activity.Finish();};
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 48;BA.debugLine="StateManager.SetSetting(\"FirstTime\", False)";
mostCurrent._vvvvvv5._vvv2(mostCurrent.activityBA,"FirstTime",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _activity_touch(int _action,float _x,float _y) throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub Activity_Touch (Action As Int, X As Float, Y A";
 //BA.debugLineNum = 56;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 10;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 11;BA.debugLine="Private lblMessage As Label";
mostCurrent._vvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 12;BA.debugLine="Private ivSwipeBackground, ivSwipe As ImageView";
mostCurrent._vvvvvvvvvvvv2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._vvvvvvvvvvvv3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Private ap As AnimationPlus";
mostCurrent._vvvvvvvvvvvv4 = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="End Sub";
return "";
}
}
