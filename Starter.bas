Type=Service
Version=6
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	Public Analytics As FirebaseAnalytics
End Sub

Sub Service_Create
	Analytics.Initialize
End Sub

Sub Service_Start (StartingIntent As Intent)

End Sub

Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub
