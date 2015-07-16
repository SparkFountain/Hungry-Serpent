;HUNGRY SERPENT
;==============
;© by Spark Fountain, 2013.

AppTitle "Hungry Serpent"

;Screen resolution (in square fields, each square 32x32 pixels)
Global screenX = 20
Global screenY = 15

Graphics screenX*32,screenY*32,16,2
SetBuffer BackBuffer()
Global frameTimer = CreateTimer(60)
SeedRnd MilliSecs()

ClsColor 20,50,150
HidePointer()

;GLOBALS
Global snakeX = screenX-4
Global snakeY = screenY/2
Global snakeLength = 0

Global lastMillis = 0
Global speed = 150

Global points = 0

;create head of snake
s.Snake = New Snake
s\x = snakeX
s\y = snakeY
s\direction = "left"
snakeLength=snakeLength+1

;at the beginning, snake consists of 4 parts (1 head, 3 tail)
For i=0 To 2
	ElongateSnake()
	snakeLength=snakeLength+1
Next

;create first apple
CreateApple()

;MAIN LOOP
Repeat
	
	WaitTimer frameTimer
	Cls()
	
	DrawApple()
	DrawBarrier()
	MoveSnake()
	DrawSnake()
	SnakeCollides()
	
	GUI()
	
	;DebugStats()
	
	Flip 0
	
	
Until KeyHit(1)
End



Function CreateApple()
	
	a.Apple = New Apple
	a\x = Rnd(1,screenX-1)
	a\y = Rnd(1,screenY-1)
	
End Function
	


Function DrawApple()
	
	SetColor("apple")
	For a.Apple = Each Apple
		Oval a\x*32,a\y*32,32,32,1
	Next
	
End Function 



Function CreateBarrier()
	
	b.Barrier = New Barrier
	b\x = Rnd(screenX-1)
	b\y = Rnd(screenY-1)
	
End Function



Function DrawBarrier()
	
	SetColor("barrier")
	For b.Barrier = Each Barrier
		Oval b\x*32,b\y*32,32,32,1
	Next
	
End Function



Function MoveSnake()
	
	If (MilliSecs()-lastMillis) > speed Then
		
		lastMillis = MilliSecs()	;set back timer
		head.Snake = First Snake
	
		If KeyHit(200)
			If (head\direction$ <> "down" And head\direction$ <> "up") Then
				w.Waypoint = New Waypoint
				w\x = head\x
				w\y = head\y
				w\passed = 1
				w\direction$ = "up"
				head\direction$ = "up"
			EndIf
		ElseIf KeyHit(208)
			If (head\direction$ <> "up" And head\direction$ <> "down") Then
				w.Waypoint = New Waypoint
				w\x = head\x
				w\y = head\y
				w\passed = 1
				w\direction$ = "down"
				head\direction$ = "down"
			EndIf
		ElseIf KeyHit(203)
			If (head\direction$ <> "right" And head\direction$ <> "left") Then
				w.Waypoint = New Waypoint
				w\x = head\x
				w\y = head\y
				w\passed = 1
				w\direction$ = "left"
				head\direction$ = "left"
			EndIf
		ElseIf KeyHit(205)
			If (head\direction$ <> "left" And head\direction$ <> "right") Then
				w.Waypoint = New Waypoint
				w\x = head\x
				w\y = head\y
				w\passed = 1
				w\direction$ = "right"
				head\direction$ = "right"
			EndIf
		EndIf
		
		For s.Snake = Each Snake
			Select s\direction
				Case "up"
					s\y=s\y-1
				Case "down"
					s\y=s\y+1
				Case "left"
					s\x=s\x-1
				Case "right"
					s\x=s\x+1
			End Select
		Next
		
		CheckWaypoints()
		
	EndIf
	
End Function



Function ElongateSnake()
	
	;get tail of snake to add a new part behind
	tail.Snake = Last Snake
	
	s.Snake = New Snake
	Select tail\direction
		Case "up"
			s\x = tail\x
			s\y = tail\y+1
			s\direction$ = "up"
		Case "down"
			s\x = tail\x
			s\y = tail\y-1
			s\direction$ = "down"
		Case "left"
			s\x = tail\x+1
			s\y = tail\y
			s\direction$ = "left"
		Case "right"
			s\x = tail\x-1
			s\y = tail\y
			s\direction$ = "right"
	End Select
	
End Function



Function DrawSnake()
	
	SetColor("snake")
	For s.Snake = Each Snake
		Rect s\x*32,s\y*32,32,32,1
	Next
	
End Function 



Function SnakeCollides()
	
	For s.Snake = Each Snake
		
		;bite yourself
		For s2.Snake = Each Snake
			If (s <> s2 And s\x=s2\x And s\y=s2\y) Then
				RuntimeError "Du hast dich selbst gebissen! Deine Punkte: "+points
			EndIf
		Next
		
		
	Next
	
	head.Snake = First Snake
	;eat an apple
	For a.Apple = Each Apple
		If (head\x=a\x And head\y=a\y) Then
			snakeLength=snakeLength+1
			points=points+100
			Delete a
			For b.Barrier = Each Barrier
				Delete b
			Next
			ElongateSnake()
			CreateApple()
			If (snakeLength>4) Then
				For i=0 To Rnd(1,3)
					CreateBarrier()
				Next
			EndIf
			speed=speed-1	;decreasing value => lower interval
		EndIf
	Next
		
	;collide with a barrier
	For b.Barrier = Each Barrier
		If (head\x=b\x And head\y=b\y) Then
			RuntimeError "Du bist gegen einen Stein gerannt! Deine Punkte: "+points
		EndIf
	Next
	
	If (head\x<0 Or head\y<0 Or head\x>screenX-1 Or head\y>screenY-1) Then
		RuntimeError "Du hast den Rand berührt! Deine Punkte: "+points
	EndIf
	
End Function



Function CheckWaypoints()
	
	For s.Snake = Each Snake
		For w.Waypoint = Each Waypoint
			If (s\x=w\x And s\y=w\y) Then
				s\direction$ = w\direction$
				w\passed=w\passed+1
			EndIf
			
			If (w\passed = snakeLength) Then
				Delete w
			EndIf
		Next
	Next
	
End Function



Function SetColor(c$)
	
	Select c$
		Case "apple"
			Color 255,0,0
		Case "barrier"
			Color 128,128,128
		Case "snake"
			Color 0,255,0
		Case "debug"
			Color 255,255,0
	End Select
	
End Function



Function GUI()
	
	Text 0,0,"Punkte: "+points
	
End Function



Function DebugStats()
	
	SetColor("debug")
	For w.Waypoint = Each Waypoint
		Text w\x*32,w\y*32,"X: "+w\passed
	Next
	
	Text 0,20,"Länge der Schlange: "+snakeLength
	
End Function



Type Apple
	
	Field x,y
	
End Type



Type Barrier
	
	Field x,y
	
End Type



Type Snake
	
	Field x,y
	Field direction$
	
End Type



Type Waypoint
	
	Field x,y
	Field direction$
	Field passed	;how often this waypoint has already been passed by the snake
	
End Type
;~IDEal Editor Parameters:
;~C#Blitz3D