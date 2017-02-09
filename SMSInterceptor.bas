Type=Service
Version=3.82
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: True
#End Region

Sub Process_Globals
	Dim si As SmsInterceptor
End Sub

Sub Service_Create
	si.Initialize2("si", 999)
End Sub

Sub Service_Start (StartingIntent As Intent)

End Sub

Sub Service_Destroy
	si.StopListening
End Sub

Sub si_MessageReceived(From As String, Body As String) As Boolean
	ToastMessageShow(From & CRLF & Body, True)
End Sub
