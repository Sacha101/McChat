/**
 * MCClient.java
 *
 * details here
 *
 */
import java.net.*;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

public class MCClient
{
	public static String multicastIP = "228.0.0.1";
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
		    InetAddress address = InetAddress.getByName("localhost"); //target server address

			// Start a thread to listen and display data sent by the server
			ClientListener listener = new ClientListener(connectionSock, group);
			Thread theThread = new Thread(listener);
			theThread.start();

			int state = 0;

			String username = args[0];

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
						}						
						break;
					case 2: //send messages
					//Scanner keyboard = new Scanner(System.in);
						while (state == 2)
						{
							//message = keyboard.nextLine();
							String input = inFromUser.readLine();
							message = username + ": " + input; //append username to front so the server knows who is sending
							sendData = message.getBytes();
							sendPacket = new DatagramPacket(sendData, sendData.length, address, outboundPort);
							sendSock.send(sendPacket);
							if (input.length() >= 5 && input.startsWith("!quit"))
							{
								listener.clientRunning = false;
								state = 3;
							}
						}
						break;
					default:
						break;
				}			
			}
		    sendSock.close();
		    System.out.println("exit3");
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

