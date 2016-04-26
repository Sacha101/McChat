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
	private ArrayList<InetAddress> socketList;
	private ArrayList<String> userList;
	private DatagramSocket serverSocket = null;

	public MCServer()
	{
		socketList = new ArrayList<InetAddress>();
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

          	String response = "";

          	if(sentence.length() >= 3 && sentence.substring(0,3).equals("ct "))
          	{
          		String newUser = sentence.substring(3);
          		response = "~~ " + newUser + " has entered the room.\n";

          		userList.add(newUser);
          		InetAddress newAddr = receivePacket.getAddress();
          		socketList.add(newAddr);
          	}
          	else if(sentence.length() >= 5 && sentence.substring(0,5).equals("!quit"))
          	{
          		InetAddress quitAddr = receivePacket.getAddress();
          		int idx = socketList.indexOf(quitAddr);
          		String quitUser = userList.get(idx);
          		response = "~~ " + quitUser + " has left the room.\n";

          		socketList.remove(idx);
          		userList.remove(idx);
          	}
          	else
          	{
          		InetAddress addr = receivePacket.getAddress();
          		int idx = socketList.indexOf(addr);
          		String user = userList.get(idx);
          		response = user + ": " + sentence + "\n";
          	}

          	System.out.println(response);

          	sendData = response.getBytes();
          	InetAddress group = InetAddress.getByName(multicastIP);
          	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
          	serverSocket.send(sendPacket);

		}
	}

	public static void main(String[] args) throws Exception
	{
		MCServer server = new MCServer();
		server.runServer();
	}
} // MTServer
