Type=Service
Version=3.2
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	
Dim AStream As AsyncStreams
Dim aSensor As List 
Dim bSensor As List
Dim sendDataTimer As Timer         
End Sub
Sub Service_Create
aSensor.Initialize
bSensor.Initialize

sendDataTimer.Initialize("sendDataTimer",10000)
sendDataTimer.Enabled = True
End Sub

Sub Service_Start (StartingIntent As Intent)
	Try
		If AStream.IsInitialized = False Then
			AStream.Initialize(Main.serial1.InputStream,  Null, "AStream")
		End If
	Catch
	End Try
	
End Sub

Sub Service_Destroy
	AStream.Close
End Sub

Sub AStream_NewData (Buffer() As Byte)
	If Buffer.Length > 0 Then
		Dim data As String= BytesToString(Buffer, 0, Buffer.Length, "US-ASCII")
		If (data.CharAt(0) = "a") Then 
			data = data.SubString(1)
			If data = "" Then 
				data = "0"
			End If
			aSensor.Add(data)
		Else If (data.CharAt(0) = "b") Then
			data = data.SubString(1)
			If data = "" Then 
				data = "0"
			End If
			bSensor.Add(data)
		End If 
	End If
End Sub

Sub sendDataTimer_tick
	ToastMessageShow(bSensor.Get(0), False)
End Sub


Sub AStream_Error
	ToastMessageShow("Connection is broken.", True)
End Sub