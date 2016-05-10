/**
 * MCServer.java
 *
 * Usage: java MCServer
 *
 * Takes incoming messages from clients and multicasts them to all connected clients, 
 * alerts users of client connaction and disconnection, and distributes server messages related to server commands
 *
 * Spins off a ServerAdmin thread to handle server-side commands
 */
import java.net.*;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

public class MCServer
{
	public static String multicastIP = "224.0.0.3";
	public static int inboundPort = 8887;
	public static int outboundPort = 8888;

	// Maintain list of all client sockets for broadcast
	private ArrayList<String> userList;
	private DatagramSocket serverSocket = null;
	ServerAdmin admin = null;
	InetAddress group = null;

	public MCServer()
	{
		userList = new ArrayList<String>();

	}

	private void runServer() throws Exception
	{
		try
		{
			group = InetAddress.getByName(multicastIP);
			serverSocket = new DatagramSocket(inboundPort);
			admin = new ServerAdmin(userList, serverSocket, group);
			Thread theThread = new Thread(admin);
			theThread.start();
		}
	
		catch(Exception e)
		{
			System.out.println("Failed to open socket: " + e);
			System.exit(0);
		}

		while(true)
		{
			byte[] receiveData = new byte[1024];
      		byte[] sendData  = new byte[1024];

      		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          	serverSocket.receive(receivePacket);
          	String sentence = new String(receivePacket.getData()).trim();

          	System.out.println(sentence);

          	String response = "";

          	

          	if(sentence.length() >= 3 && sentence.substring(0,3).equals("ct ")) //if it is a connection string
          	{
          		String newUser = sentence.substring(3);
          		if(!userList.contains(newUser))
          		{
          			userList.add(newUser);

          			response = "~~ " + newUser + " has entered the room.";

          			sendData = response.getBytes();
		          	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
		          	serverSocket.send(sendPacket);
          		}
          		else
          		{
          			response = "Server: !w [" + newUser + "] A client with a duplicate name has tried to connect, kicking.";
          			sendData = response.getBytes();
	          		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
	          		serverSocket.send(sendPacket);
          		}
          	}
          	else
          	{
          		String user = sentence.substring(0, sentence.indexOf(":"));
          		
          			String quitUser = ""; // In case a user inputs a quit command
	          		for (String s : userList)
	          		{
	          			if (sentence.startsWith(s))
	          			{
		          			if(sentence.length() >= 7 && sentence.startsWith(": !quit", sentence.indexOf(":")))
				          	{
				          		response = "~~ " + s + " has left the room.\n";

				          		quitUser = s;
				          	}
				          	else
				          	{
				          		response = sentence;
				         	}
	          			}
	          		}

	          		if(!quitUser.equals(""))
	          		{
	          			userList.remove(userList.indexOf(quitUser));
	          			sendData = response.getBytes();
			          	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
			          	serverSocket.send(sendPacket);
	          		}

	          		if(!admin.blacklist.contains(user))
          			{
		          		System.out.println(response);

			          	sendData = response.getBytes();
			          	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
			          	serverSocket.send(sendPacket);
          			}
          			else
          			{
          				response = "Server: !w [" + user + "] You have been muted.";
          				sendData = response.getBytes();
	          			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
	          			serverSocket.send(sendPacket);
          			}
          		
          	}
		}
	}

	public static void main(String[] args) throws Exception
	{
		MCServer server = new MCServer();
		server.runServer();
	}
} // MTServer
