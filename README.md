MCChat

Alex Sacharske

1814230

5/2016

-------------------------------------------------------------

Includes:
MCServer.java, 
ServerAdmin.java, 
MCClient.java, 
ClientListener.java

-------------------------------------------------------------

To compile:

(in McChat directory) javac *.java

Usage:

MCServer - java MCServer

MCClient - java MCClient serverIPAddr username

-------------------------------------------------------------

Client-side inline commands:

!quit - closes the client

!w [target] - sends a private whisper message to the target username

!log - saves a .txt file containing the chat log since the calling user has entered, and prints it to the screen

-------------------------------------------------------------

Server-side admin commands:

!mute [target] - mutes target so that their messages will not be forwarded to other users

!unmute [target] - unmutes a muted target so that their messages will be forwarded to other users
