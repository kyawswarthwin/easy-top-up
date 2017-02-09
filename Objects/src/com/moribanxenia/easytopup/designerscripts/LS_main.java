package com.moribanxenia.easytopup.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_main{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 1;BA.debugLine="pnlBanner.SetTopAndBottom(48dip, 48dip + 37%x)"[main/General script]
views.get("pnlbanner").vw.setTop((int)((48d * scale)));
views.get("pnlbanner").vw.setHeight((int)((48d * scale)+(37d / 100 * width) - ((48d * scale))));
//BA.debugLineNum = 2;BA.debugLine="pnlContent.SetTopAndBottom(pnlBanner.Bottom, 100%y - 50dip)"[main/General script]
views.get("pnlcontent").vw.setTop((int)((views.get("pnlbanner").vw.getTop() + views.get("pnlbanner").vw.getHeight())));
views.get("pnlcontent").vw.setHeight((int)((100d / 100 * height)-(50d * scale) - ((views.get("pnlbanner").vw.getTop() + views.get("pnlbanner").vw.getHeight()))));

}
}