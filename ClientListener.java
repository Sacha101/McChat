/**
 * ClientListener.java
 *
 * Handles incoming messages for the MCClient, converts whisper command text to whisper label
 *
 */
import java.net.*;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class ClientListener implements Runnable
{
	private MulticastSocket connectionSock = null;
	public String storedMessage = ""; // for connection confirmation
	private InetAddress group = null;
	public boolean clientRunning = true;
	public String username = "";
	public ArrayList<String> log; 

	ClientListener(MulticastSocket sock, InetAddress grp, String username) throws Exception
	{
		try
		{
			this.connectionSock = sock;
			this.group = grp;
			this.username = username;
			connectionSock.joinGroup(group);
			log = new ArrayList<String>();
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		
	}

	public void run()
	{
       		 // Wait for data from the server.  If received, output it.
		try
		{
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
			while (clientRunning)
			{
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				connectionSock.receive(receivePacket);
				// Get data sent from the server
				String serverText = new String(receivePacket.getData()).trim();
				if(serverText.startsWith(": !w", serverText.indexOf(":")) && serverText.contains("[") && serverText.contains("]"))
				{
					String recipient = serverText.substring(serverText.indexOf("[") + 1, serverText.indexOf("]"));
					if (recipient.equals(username))
					{
						serverText = serverText.replace(" !w", "");
						serverText = serverText.replace(("[" + recipient + "]"), "(WHISPER)"); //Uses a whiser to send an error message
						System.out.println(serverText);
						storedMessage = serverText;
						log.add(serverText);
					}
						
				}
				else
				{
					System.out.println(serverText);
					storedMessage = serverText;
					log.add(serverText);
				}
				
			}
			connectionSock.leaveGroup(group);
		    connectionSock.close();
		}
		catch (Exception e)
		{
			System.out.println("FROM LISTENER");
			System.out.println("Error: " + e.toString());
		}
	}
} // ClientListener for MTClient
