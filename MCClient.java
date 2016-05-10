/**
 * MCClient.java
 *
 * Usage: java MCClient serverAddress username
 *
 * Handles incoming server commands and client-side commands, and sends user messages
 * Spins off a listener thread to handle incoming messages
 * 
 * Local commands:
 * !quit - closes the client
 * !w [target] sends a private whisper message to the target username
 */
import java.net.*;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MCClient
{
	public static String multicastIP = "224.0.0.3";
	public static int inboundPort = 8888;
	public static int outboundPort = 8887;


	public static void main(String[] args) throws Exception
	{
		try
		{
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

			MulticastSocket connectionSock = new MulticastSocket(inboundPort);
		    DatagramSocket sendSock = new DatagramSocket();

		    InetAddress group = InetAddress.getByName(multicastIP); //
		    InetAddress address = InetAddress.getByName(args[0]); //target server address

		    String username = args[1];

			// Start a thread to listen and display data sent by the server
			ClientListener listener = new ClientListener(connectionSock, group, username);
			Thread theThread = new Thread(listener);
			theThread.start();

			int state = 0;

			String message = "ct " + username;
			String response = "";

			DatagramPacket sendPacket = null;

			while (state < 3)
			{
				byte[] sendData = new byte[1024];
				switch (state)
				{
					case 0: //send initial info to server
						sendData = message.getBytes();
						sendPacket = new DatagramPacket(sendData, sendData.length, address, outboundPort);
						sendSock.send(sendPacket);
						state = 1;
						break;
					case 1: //wait for response
						if (listener.storedMessage.length() > 0)
						{
							response = listener.storedMessage;
							if(response.substring(0,2).equals("~~"))
							{
								state = 2;
							}
							else
							{
								listener.clientRunning = false;
								state = 3;
							}
						}						
						break;
					case 2: //send messages
					//Scanner keyboard = new Scanner(System.in);
						while (state == 2)
						{
							//message = keyboard.nextLine();
							String input = inFromUser.readLine();
							if (input.length() >= 5 && input.startsWith("!quit"))
							{
								
								listener.clientRunning = false;
								state = 3;
								message = username + ": " + input; //append username to front so the server knows who is sending
								sendData = message.getBytes();
								sendPacket = new DatagramPacket(sendData, sendData.length, address, outboundPort);
								sendSock.send(sendPacket);
							}
							else if (input.length() >= 4 && input.startsWith("!log"))
							{
								Date logDate = new Date();
								SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

								String logName = ("Chatlog " + dt.format(logDate) + ".txt");
								PrintWriter pw = new PrintWriter(new FileWriter(logName));
								for(String s : listener.log)
								{
									System.out.println(s);
									pw.println(s);
								}
								System.out.println("Chat log written to " + logName);
								pw.close();
							}
							else
							{
								message = username + ": " + input; //append username to front so the server knows who is sending
								sendData = message.getBytes();
								sendPacket = new DatagramPacket(sendData, sendData.length, address, outboundPort);
								sendSock.send(sendPacket);
							}
						}
						break;
					default:
						break;
				}			
			}
		    sendSock.close();
		    System.exit(0);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage()); 
		    //sendSock.close();
		    //connectionSock.leaveGroup(group);
		    //connectionSock.close();
		}
	}
} // MCClient

