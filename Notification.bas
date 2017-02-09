Type=Activity
Version=4
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals

End Sub

Sub Globals
	Dim wv As WebView
	Dim wvs As WebViewSettings
End Sub

Sub Activity_Create(FirstTime As Boolean)
	wv.Initialize("")
	wv.ZoomEnabled = False
	wvs.setDefaultTextEncodingName(wv, "UTF8")
	Activity.AddView(wv, 0, 0, 100%x, 100%y)
End Sub

Sub Activity_Resume
	wv.LoadUrl("file://" & File.Combine(File.DirInternal, "index.html"))
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
