Type=Activity
Version=4
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals

End Sub

Sub Globals
	Dim lblMessage As Label
	Dim ivSwipeBackground, ivSwipe As ImageView
	Dim ap As AnimationPlus
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.Color = Colors.ARGB(200, 0, 0, 0)
	
	lblMessage.Initialize("")
	lblMessage.Text = "ေဘးတိုက္ဆြဲ၍စာမ်က္ႏွာမ်ားေျပာင္းလဲႏိုင္ပါသည္။"
	lblMessage.Typeface = Main.SmartZawgyi
	lblMessage.Gravity = Gravity.CENTER_HORIZONTAL
	lblMessage.TextSize = 16.5
	Activity.AddView(lblMessage, 10dip, 30%y, 100%x - 20dip, Main.WRAP_CONTENT)
	
	ivSwipeBackground.Initialize("")
	ivSwipeBackground.Bitmap = LoadBitmap(File.DirAssets, "swipebackground.png")
	ivSwipeBackground.Gravity = Gravity.FILL
	Activity.AddView(ivSwipeBackground, 0, 35%y, 100%x, 100%x)
	
	ivSwipe.Initialize("")
	ivSwipe.Bitmap = LoadBitmap(File.DirAssets, "swipe.png")
	ivSwipe.Gravity = Gravity.FILL
	Activity.AddView(ivSwipe, 28%x, 35%y, 100%x, 100%x)
	
	ap.InitializeTranslate("", 0, 0, 28%x, 0)
	ap.Duration = 3000
	ap.SetInterpolatorWithParam(ap.INTERPOLATOR_CYCLE, 1)
	ap.RepeatCount = ap.REPEAT_INFINITE
	ap.Start(ivSwipe)
	
	SaveSMSToInbox(Main.AppName, "မဂၤလာပါ " & Main.AppName & " မွ ႀကိဳဆိုပါတယ္။")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)
	StateManager.SetSetting("FirstTime", False)
End Sub

Sub Activity_KeyPress (KeyCode As Int) As Boolean 'Return True to consume the event
	If KeyCode = KeyCodes.KEYCODE_BACK Then Activity.Finish
End Sub

Sub Activity_Touch (Action As Int, X As Float, Y As Float)
	Activity.Finish
End Sub

Sub SaveSMSToInbox(Address As String, Body As String)
	Dim resolver As ContentResolver
	Dim uri1 As Uri : uri1.Parse("content://sms/inbox")
	Dim values As ContentValues
	
	resolver.Initialize("")
	values.Initialize
	values.PutString("address", Address)
	values.PutString("body", Body)
	resolver.Insert(uri1, values)
End Sub
