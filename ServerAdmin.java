/**
 * ServerAdmin.java
 *
 * Takes incoming server -side commands and applies their effects to specified users
 *
 * Commands:
 * !mute [target] - mutes target so that their messages will not be forwarded to other users
 * !unmute [target] - unmutes a muted target so that their messages will be forwarded to other users
 */
import java.net.*;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class ServerAdmin implements Runnable
{
	public static int inboundPort = 8887;
	public static int outboundPort = 8888;

	public ArrayList<String> blacklist;
	public ArrayList<String> users;
	private DatagramSocket serverSock = null;
	private InetAddress group = null;


	ServerAdmin(ArrayList<String> users, DatagramSocket serverSock, InetAddress group) throws Exception
	{
		try
		{
			this.group = group;
			this.serverSock = serverSock;
			this.users = users;
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
		
	}

	public void run()
	{
		try
		{
			this.blacklist = new ArrayList<String>();
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
			while (true)
			{
				byte[] sendData  = new byte[1024];
				String adminMessage = "";
				String unMuteUser = "";
				String muteUser = "";


				String sv = serverInput.readLine();
				
				if (sv.startsWith("!mute") && sv.contains("[") && sv.contains("]"))
				{

					muteUser = sv.substring(sv.indexOf("[") + 1, sv.indexOf("]"));
					if (users.contains(muteUser))
					{
						blacklist.add(muteUser);
						adminMessage = "~~ " + muteUser + " has been muted.";
          				sendData = adminMessage.getBytes();
          				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
          				serverSock.send(sendPacket);
					}
					else
						System.out.println("Cannot find user");
					
				}
				else if (sv.startsWith("!unmute") && sv.contains("[") && sv.contains("]"))
				{
					unMuteUser = sv.substring(sv.indexOf('[') + 1, sv.indexOf(']'));
					if(blacklist.contains(unMuteUser) && users.contains(unMuteUser))
					{
						muteUser = sv.substring(sv.indexOf("[") + 1, sv.indexOf("]"));
						blacklist.remove(muteUser);
						adminMessage = "~~ " + muteUser + " has been unmuted.";
          				sendData = adminMessage.getBytes();
          				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, outboundPort);
          				serverSock.send(sendPacket);
					}
					else
						System.out.println("Cannot find user in blacklist");
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}
}
