/**
 * ClientListener.java
 *
 * Details here
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

	ClientListener(MulticastSocket sock, InetAddress grp) throws Exception
	{
		try
		{
			this.connectionSock = sock;
			this.group = grp;
			connectionSock.joinGroup(group);
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
			while (true)
			{
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				connectionSock.receive(receivePacket);
				// Get data sent from the server
				String serverText = new String(receivePacket.getData()).trim();
				System.out.println(serverText);
				storedMessage = serverText;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}
} // ClientListener for MTClient
