Type=Activity
Version=5.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	Type Activation(PhoneNumber As String, Activation As String, Deactivation As String)
	
	Public Title, Offer As String
End Sub

Sub Globals
	Private Dialog As DialogView
	
	Private sv As ScrollView
	Private iPanelHeight As Int
	
	Private ps As PhoneSms
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Dim parser As JSONParser
	Dim root, Offers, colSIMOperator As Map
	Dim SIMOperator As List
	
	Dim pnl As Panel
	Dim lblTitle, lblMessage As Label
	Dim btnActivate, btnDeactivate As Button
	Dim cd(3) As ColorDrawable
	Dim sld As StateListDrawable
	
	Activity.Title = Title
	
	Try
		parser.Initialize(File.ReadString(File.DirAssets, "offers.json"))
		root = parser.NextObject
		Offers = root.Get(Offer)
		SIMOperator = Offers.Get(Main.SIMOperator)
		
		If Offer = "plans" Or Offer = "specialPacks" Then
			iPanelHeight = 220dip
		Else
			iPanelHeight = 180dip
		End If
		sv.Initialize((SIMOperator.Size * iPanelHeight) + 10dip)
		Activity.AddView(sv, 0, 0, 100%x, 100%y)
		
		For i = 0 To SIMOperator.Size - 1
			colSIMOperator = SIMOperator.Get(i)
			
			pnl.Initialize("")
			sv.Panel.AddView(pnl, 0, 5dip + (i * iPanelHeight), sv.Width, iPanelHeight)
			
			lblTitle.Initialize("")
			lblTitle.Text = colSIMOperator.Get("title")
			lblTitle.Typeface = Typeface.DEFAULT_BOLD
			lblTitle.TextSize = 20
			pnl.AddView(lblTitle, 10dip, 10dip, pnl.Width - 20dip, 30dip)
			
			lblMessage.Initialize("")
			If Offer = "plans" Or Offer = "specialPacks" Then
				lblMessage.Text = "ေဈးႏႈန္း: " & colSIMOperator.Get("price") & CRLF & "Voice: " & colSIMOperator.Get("voice") & CRLF & "SMS: " & colSIMOperator.Get("sms") & CRLF & "Data: " & colSIMOperator.Get("data") & CRLF & "ကန္႔သတ္ခ်က္: " & colSIMOperator.Get("limitation") & CRLF & "အက်ံဳးဝင္သည့္ကာလ: " & colSIMOperator.Get("validityPeriod")
			Else
				lblMessage.Text = "ေဈးႏႈန္း: " & colSIMOperator.Get("price") & CRLF & "ပမာဏ: " & colSIMOperator.Get("volume") & CRLF & "ကန္႔သတ္ခ်က္: " & colSIMOperator.Get("limitation") & CRLF & "အက်ံဳးဝင္သည့္ကာလ: " & colSIMOperator.Get("validityPeriod")
			End If
			lblMessage.Typeface = Main.SmartZawgyi
			lblMessage.TextSize = 16
			pnl.AddView(lblMessage, 10dip, lblTitle.Top + lblTitle.Height + 5dip, pnl.Width - 20dip, Main.WRAP_CONTENT)
			
			Dim a As Activation
			a.PhoneNumber = colSIMOperator.Get("phoneNumber")
			a.Activation = colSIMOperator.Get("activation")
			a.Deactivation = colSIMOperator.Get("deactivation")
			
			btnActivate.Initialize("btnActivate")
			btnActivate.Tag = a
			btnActivate.Text = "Activate"
			pnl.AddView(btnActivate, (pnl.Width / 2) + 5dip, pnl.Height - 50dip, (pnl.Width / 2) - 15dip, 40dip)
			
			btnDeactivate.Initialize("btnDeactivate")
			btnDeactivate.Tag = a
			btnDeactivate.Text = "Deactivate"
			If Offer = "plans" Then pnl.AddView(btnDeactivate, 10dip, pnl.Height - 50dip, (pnl.Width / 2) - 15dip, 40dip)
			
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
				btnDeactivate.TextColor = Colors.RGB(0, 162, 232)
				sld.Initialize
				sld.AddState(sld.State_Disabled, cd(1))
				sld.AddState(sld.State_Pressed, cd(2))
				sld.AddCatchAllState(cd(0))
				btnDeactivate.Background = sld
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
				btnDeactivate.TextColor = Colors.White
				sld.Initialize
				sld.AddState(sld.State_Disabled, cd(1))
				sld.AddState(sld.State_Pressed, cd(2))
				sld.AddCatchAllState(cd(0))
				btnDeactivate.Background = sld
			End If
		Next
	Catch
		Dialog.Options.Elements.Title.Typeface = Main.SmartZawgyi
		Dialog.Options.Elements.Message.Typeface = Main.SmartZawgyi
		Dialog.Options.Elements.Buttons.Default.Style.Typeface = Main.SmartZawgyi
		Dialog.Msgbox("သတိေပးခ်က္", "ဝန္ေဆာင္မႈ မရွိေသးပါ။", "အိုေက", "", "", Null)
		Activity.Finish
	End Try
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

Sub btnDeactivate_Click
	Dim btn As Button = Sender
	Dim a As Activation = btn.Tag
	ps.Send(a.PhoneNumber, a.Deactivation)
	ToastMessageShow("Deactivate လုပ္ၿပီးပါၿပီ။", True)
End Sub
