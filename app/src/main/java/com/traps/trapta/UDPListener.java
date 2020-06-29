package com.traps.trapta;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UDPListener extends Thread {

	private static UDPListener instance = null;
	 
	private DatagramSocket  datagramSocket = null;
	private long timestamp = 0;
	private Semaphore semaphore;
	private boolean init = false;
	private String address;
	private int port;
	private Watchdog watchdog = null;
  
	private UDPListener() {

		try {
			datagramSocket = new DatagramSocket(5433);
			start();
		} catch (SocketException e) {
			Log.i("UDP", "It looks like the UDP listener is already started. Or cannot init on port 5433");
		}
		
		semaphore = new Semaphore(0);
	}
	
	public static UDPListener getInstance() {
		if (instance==null) instance = new UDPListener();
		return instance;
	}

	public void register(Watchdog watchdog) {
		this.watchdog = watchdog;
	}

	public synchronized boolean isInit() {
		return init;
	}
	
	public synchronized void reset() {
		init = false;
	}
	
	public synchronized InetSocketAddress getAddress() {
		return new InetSocketAddress(address, port);
	}
	
	
	public boolean waitforUpdate() {
		boolean updated = false;
		try {
			updated = semaphore.tryAcquire(6, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
		return updated;
	}
	
	private void replyHello() throws IOException, JSONException {
		if (!isInit()) return;
		Log.i("HELLO", "Say HELLO to TRAPTA");
		InetSocketAddress socketAddress = getAddress();
		Socket socket = new Socket(); 
		socket.setSoTimeout(20000);
		socket.connect(socketAddress, 10000);  // wait for 10 sec max
		
		JSONObject json = new JSONObject();
		json.put("command", 3);
		json.put("targetId", StaticParam.targetId);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeBytes(json.toString());
        output.writeByte(4); // End Of Transmision
        //output.writeChar('\n');
        output.flush(); 
	}
	
	
	private synchronized void setValues(long timestamp, String address, int port) {
		init = true;
		this.timestamp = timestamp;
		this.address = address;
		this.port = port;
	}
	
	public void run() {
		
		if (datagramSocket==null) return;
		
		Log.i("UDPListener","Starting UDP server...");
		byte[] data = new byte[1024];
		
		while(true) {
			try {
				DatagramPacket packet = new DatagramPacket(data,data.length);
				datagramSocket.receive(packet);
				String dataString = new String(packet.getData(),0,packet.getLength());

		        String[] array = dataString.split(",");
		        if (array.length<3) {
		        	Log.e("UDP", "Data packet corrupted");
		        	continue;
		        }
		        		        
		        String address = array[3];
			    int port = Integer.parseInt(array[4]);
			    if (port<1) {
					Log.e("UDP", "Port is "+port);
					continue;
				}
				long thistimestamp = Long.parseLong(array[0]);
			    if (thistimestamp<1) {
					Log.e("UDP", "Timestamp is "+thistimestamp);
					continue;
				}
			    setValues(thistimestamp, address, port);
				if (watchdog!=null) watchdog.onWatchdog(dataString);
		        Log.i("UDP", "Address="+address+":"+port);
		        // release thread waiting for update
		        if (semaphore.hasQueuedThreads()) semaphore.release();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
           
        }
	}
}
