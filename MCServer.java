/**
 * MTServer.java
 *
 * Details here
 *
 */
import java.net.*;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

public class MCServer
{
	public static String multicastIP = "228.0.0.1";
	public static int inboundPort = 8887;
	public static int outboundPort = 8888;

	// Maintain list of all client sockets for broadcast
	private ArrayList<String> userList;
	private DatagramSocket serverSocket = null;

	public MCServer()
	{
		userList = new ArrayList<String>();
	}

	private void runServer() throws Exception
	{
		try
		{
			serverSocket = new DatagramSocket(inboundPort);
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

          	InetAddress group = InetAddress.getByName(multicastIP);

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
          			//TODO:
          			//Private message server rejection back to sender
          		}
          	}
          	else
          	{
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
          		}

          		System.out.println(response);

	          	sendData = response.getBytes();
	          	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
	          	serverSocket.send(sendPacket);
          	}
		}
	}

	public static void main(String[] args) throws Exception
	{
		MCServer server = new MCServer();
		server.runServer();
	}
} // MTServer
