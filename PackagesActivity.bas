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
	Public Title, Package As String
	
	Type Activation(PhoneNumber As String, Activation As String)
End Sub

Sub Globals
	Dim sv As ScrollView
	Dim iPanelHeight As Int = 160dip
	
	Dim ps As PhoneSms
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Dim parser As JSONParser
	Dim root, Packages, colSIMOperator As Map
	Dim SIMOperator As List
	
	Dim pnl As Panel
	Dim lblTitle, lblMessage As Label
	Dim btnActivate As Button
	Dim cd(3) As ColorDrawable
	Dim sld As StateListDrawable
	
	Activity.Title = Title
	
	parser.Initialize(File.ReadString(File.DirAssets, "packages.json"))
	root = parser.NextObject
	Packages = root.Get(Package)
	SIMOperator = Packages.Get(Main.SIMOperator)
	
	sv.Initialize((SIMOperator.Size * iPanelHeight) + 10dip)
	Activity.AddView(sv, 0, 0, 100%x, 100%y)
	
	For i = 0 To SIMOperator.Size - 1
		colSIMOperator = SIMOperator.Get(i)
		
		pnl.Initialize("")
		sv.Panel.AddView(pnl, 0, 5dip + (i * iPanelHeight), sv.Width, iPanelHeight)
		
		lblTitle.Initialize("")
		lblTitle.Text = colSIMOperator.Get("package")
		lblTitle.Typeface = Typeface.DEFAULT_BOLD
		lblTitle.TextSize = 20
		pnl.AddView(lblTitle, 10dip, 10dip, pnl.Width - 20dip, 30dip)
		
		lblMessage.Initialize("")
		lblMessage.Text = "ေဈးႏႈန္း: " & colSIMOperator.Get("price") & CRLF & "ပမာဏ: " & colSIMOperator.Get("volume") & CRLF & "အက်ံဳးဝင္သည့္ကာလ: " & colSIMOperator.Get("validityPeriod")
		lblMessage.Typeface = Main.SmartZawgyi
		lblMessage.TextSize = 16
		pnl.AddView(lblMessage, 10dip, lblTitle.Top + lblTitle.Height + 5dip, pnl.Width - 20dip, Main.WRAP_CONTENT)
		
		Dim a As Activation
		a.PhoneNumber = colSIMOperator.Get("phoneNumber")
		a.Activation = colSIMOperator.Get("activation")
		
		btnActivate.Initialize("btnActivate")
		btnActivate.Tag = a
		btnActivate.Text = "Activate"
		pnl.AddView(btnActivate, (pnl.Width / 2) + 5dip, pnl.Height - 50dip, (pnl.Width / 2) - 15dip, 40dip)
		
		If i Mod 2 = 1 Then
			pnl.Color = Colors.RGB(0, 162, 232)
			
			lblTitle.TextColor = Colors.White
			lblMessage.TextColor = Colors.White
			
			btnActivate.TextColor = Colors.RGB(0, 162, 232)
			cd(0).Initialize(Colors.White, 5dip)
			cd(1).Initialize(Colors.LightGray, 5dip)
			cd(2).Initialize(Colors.DarkGray, 5dip)
			sld.Initialize
			sld.AddState(sld.State_Disabled, cd(1))
			sld.AddState(sld.State_Pressed, cd(2))
			sld.AddCatchAllState(cd(0))
			btnActivate.Background = sld
		Else
			pnl.Color = Colors.White
			
			lblTitle.TextColor = Colors.RGB(0, 162, 232)
			lblMessage.TextColor = Colors.RGB(0, 162, 232)
			
			btnActivate.TextColor = Colors.White
			cd(0).Initialize(Colors.RGB(0, 162, 232), 5dip)
			cd(1).Initialize(Colors.LightGray, 5dip)
			cd(2).Initialize(Colors.DarkGray, 5dip)
			sld.Initialize
			sld.AddState(sld.State_Disabled, cd(1))
			sld.AddState(sld.State_Pressed, cd(2))
			sld.AddCatchAllState(cd(0))
			btnActivate.Background = sld
		End If
	Next
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnActivate_Click
	Dim btn As Button = Sender
	Dim a As Activation = btn.Tag
	ps.Send(a.PhoneNumber, a.Activation)
	ToastMessageShow("Activate လုပ္ၿပီးပါၿပီ။", True)
End Sub
