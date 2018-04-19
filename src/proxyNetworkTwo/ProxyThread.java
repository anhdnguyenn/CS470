package proxyNetworkTwo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyThread extends Thread {
	private InetAddress client;
	private DatagramPacket clientPacket;
	private final DatagramSocket SOCKET;
	public ConcurrentHashMap <String, InetAddress> cache;
	public ProxyThread(InetAddress client, String clientRequest, ConcurrentHashMap <String, InetAddress> cache ) throws SocketException{
		client = client;
		clientRequest = clientRequest;
		this.SOCKET = new DatagramSocket(9999);
		cache = cache;
	}
	
	public DatagramPacket recievingPacket() throws IOException{
		byte[] inMessage = new byte[1024];
		DatagramPacket inPacket = new DatagramPacket(inMessage, inMessage.length);
		SOCKET.receive(inPacket);
		System.out.println(new String(inPacket.getData(),"US-ASCII"));
		return inPacket;
	}
	public void sendingPacket(String message, InetAddress reciever) throws IOException{
		byte[] message_ = message.getBytes();
		//Message message = new Message(myIP, providerIP,request);
		DatagramPacket sendPacket = new DatagramPacket(message_, message_.length,reciever,9999);
        SOCKET.send(sendPacket);
	}
	public boolean lookingForNewHost(String host){
		int timeout = 10;
		int count = 0;
		boolean foundHost = false;
		while(!foundHost || count <= timeout){
			count+=1;
		}
		return false;
		
	}
	public void run(){
		try {
			clientPacket = recievingPacket();
			String clientRequest= new String (clientPacket.getData(),"US-ASCII");
			if (!clientRequest.substring(0, 2).equals("get")){
				sendingPacket("Not implemented", clientPacket.getAddress());
				return;
			}
			String host = clientRequest.substring(3,5);
			if(cache.containsKey(host)){
				InetAddress hostIP = cache.get(host);
				sendingPacket(clientRequest, hostIP);
				DatagramPacket hostPacket = recievingPacket();
				byte[] m = hostPacket.getData();
				hostPacket = new DatagramPacket(m, m.length,clientPacket.getAddress(),9999);
				SOCKET.send(hostPacket);
				return;
			}
			System.out.println("host sever is down, sorry bro");
			sendingPacket("host sever is down, sorry bro", clientPacket.getAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
