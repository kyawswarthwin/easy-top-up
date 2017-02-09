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
	Type Subscription(PhoneNumber As String, Subscription As String, Unsubscription As String)
End Sub

Sub Globals
	Dim sv As ScrollView
	Dim iPanelHeight As Int = 160dip
	
	Dim ps As PhoneSms
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Dim parser As JSONParser
	Dim root, colSIMOperator As Map
	Dim SIMOperator As List
	
	Dim pnl As Panel
	Dim lblTitle, lblMessage As Label
	Dim btnSubscribe, btnUnsubscribe As Button
	Dim cd(3) As ColorDrawable
	Dim sld As StateListDrawable
	
	Activity.Title = "SMS Channels"
	
	parser.Initialize(File.ReadString(File.DirAssets, "smschannels.json"))
	root = parser.NextObject
	SIMOperator = root.Get(Main.SIMOperator)
	
	sv.Initialize((SIMOperator.Size * iPanelHeight) + 10dip)
	Activity.AddView(sv, 0, 0, 100%x, 100%y)
	
	For i = 0 To SIMOperator.Size - 1
		colSIMOperator = SIMOperator.Get(i)
		
		pnl.Initialize("")
		sv.Panel.AddView(pnl, 0, 5dip + (i * iPanelHeight), sv.Width, iPanelHeight)
		
		lblTitle.Initialize("")
		lblTitle.Text = colSIMOperator.Get("channel")
		lblTitle.Typeface = Typeface.DEFAULT_BOLD
		lblTitle.TextSize = 20
		pnl.AddView(lblTitle, 10dip, 10dip, pnl.Width - 20dip, 30dip)
		
		lblMessage.Initialize("")
		lblMessage.Text = "ေဈးႏႈန္း: " & colSIMOperator.Get("price") & CRLF & "အက်ံဳးဝင္သည့္ကာလ: " & colSIMOperator.Get("validityPeriod")
		lblMessage.Typeface = Main.SmartZawgyi
		lblMessage.TextSize = 16
		pnl.AddView(lblMessage, 10dip, lblTitle.Top + lblTitle.Height + 5dip, pnl.Width - 20dip, Main.WRAP_CONTENT)
		
		Dim s As Subscription
		s.PhoneNumber = colSIMOperator.Get("phoneNumber")
		s.Subscription = colSIMOperator.Get("subscription")
		s.Unsubscription = colSIMOperator.Get("unsubscription")
		
		btnSubscribe.Initialize("btnSubscribe")
		btnSubscribe.Tag = s
		btnSubscribe.Text = "Subscribe"
		pnl.AddView(btnSubscribe, (pnl.Width / 2) + 5dip, pnl.Height - 50dip, (pnl.Width / 2) - 15dip, 40dip)
		
		btnUnsubscribe.Initialize("btnUnsubscribe")
		btnUnsubscribe.Tag = s
		btnUnsubscribe.Text = "Unsubscribe"
		pnl.AddView(btnUnsubscribe, 10dip, pnl.Height - 50dip, (pnl.Width / 2) - 15dip, 40dip)
		
		If i Mod 2 = 1 Then
			pnl.Color = Colors.RGB(0, 162, 232)
			
			lblTitle.TextColor = Colors.White
			lblMessage.TextColor = Colors.White
			
			btnSubscribe.TextColor = Colors.RGB(0, 162, 232)
			cd(0).Initialize(Colors.White, 5dip)
			cd(1).Initialize(Colors.LightGray, 5dip)
			cd(2).Initialize(Colors.DarkGray, 5dip)
			sld.Initialize
			sld.AddState(sld.State_Disabled, cd(1))
			sld.AddState(sld.State_Pressed, cd(2))
			sld.AddCatchAllState(cd(0))
			btnSubscribe.Background = sld
			btnUnsubscribe.TextColor = Colors.RGB(0, 162, 232)
			sld.Initialize
			sld.AddState(sld.State_Disabled, cd(1))
			sld.AddState(sld.State_Pressed, cd(2))
			sld.AddCatchAllState(cd(0))
			btnUnsubscribe.Background = sld
		Else
			pnl.Color = Colors.White
			
			lblTitle.TextColor = Colors.RGB(0, 162, 232)
			lblMessage.TextColor = Colors.RGB(0, 162, 232)
			
			btnSubscribe.TextColor = Colors.White
			cd(0).Initialize(Colors.RGB(0, 162, 232), 5dip)
			cd(1).Initialize(Colors.LightGray, 5dip)
			cd(2).Initialize(Colors.DarkGray, 5dip)
			sld.Initialize
			sld.AddState(sld.State_Disabled, cd(1))
			sld.AddState(sld.State_Pressed, cd(2))
			sld.AddCatchAllState(cd(0))
			btnSubscribe.Background = sld
			btnUnsubscribe.TextColor = Colors.White
			sld.Initialize
			sld.AddState(sld.State_Disabled, cd(1))
			sld.AddState(sld.State_Pressed, cd(2))
			sld.AddCatchAllState(cd(0))
			btnUnsubscribe.Background = sld
		End If
	Next
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnSubscribe_Click
	Dim btn As Button = Sender
	Dim s As Subscription = btn.Tag
	ps.Send(s.PhoneNumber, s.Subscription)
	ToastMessageShow("Subscribe လုပ္ၿပီးပါၿပီ။", True)
End Sub

Sub btnUnsubscribe_Click
	Dim btn As Button = Sender
	Dim s As Subscription = btn.Tag
	ps.Send(s.PhoneNumber, s.Unsubscription)
	ToastMessageShow("Unsubscribe လုပ္ၿပီးပါၿပီ။", True)
End Sub
