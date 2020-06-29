package com.traps.trapta;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
 

public class TRAPTASocket extends Thread {
	
	private MainActivity mainActivity = null;
    private WifiManager wifiManager = null;
    private int failureCounter = 0;
	
	// inner class
	private class Packet {
		public int id;
		public JSONObject json;
		
		public Packet(int id, JSONObject json) {
			this.id = id;
			this.json = json;
		}

	}
	
	
	private static TRAPTASocket instance = null;
	private LinkedBlockingQueue<Packet> outputQ = new LinkedBlockingQueue<Packet>();

	public void setReference(MainActivity activity, WifiManager wifi) {
		mainActivity = activity;
        wifiManager = wifi;
	}
	
	private TRAPTASocket() {
		start();
	} 
	
	public static TRAPTASocket getInstance() {
		if (instance==null) instance = new TRAPTASocket();
		return instance;
	}
	
	private void addPacket(Packet newPacket) {
		// first remove from q any packet with same id
		for (Packet packet: outputQ) {
			if (packet.id == newPacket.id) {
				Log.i("TRAPTAThread", "Found archer or match "+packet.id+" in queue. Remove before adding new packet.");
				outputQ.remove(packet);
			}
		}
		outputQ.add(newPacket);
		Log.i("TRAPTAThread", "Queue size is "+outputQ.size());
	}
	
	private void addAfterFailure(Packet oldPacket) {
		// if another packet with same id is in the queue, then it is a new one. Do not add !
		for (Packet packet: outputQ) {
			if (packet.id == oldPacket.id) {
				Log.i("TRAPTAThread", "Found archer or match "+packet.id+" in queue. Do not add old packet.");
				return;
			}
		}
		outputQ.add(oldPacket);
	}
	
	public void sendHeat(int archerId, int heatIndex, Heat heat) {

		JSONObject object = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			object.put("command", 0);
			object.put("targetId", StaticParam.targetId);
			object.put("batteryLevel", mainActivity.getBatteryLevel());
			data.put("archerId", archerId);
			data.put("heatIndex", heatIndex);
			JSONArray array = heat.getJsonArray();
			data.put("arrowList", array);
			object.put("data", data);	
			Packet packet = new Packet(archerId, object);
			addPacket(packet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendMatch(int matchId, Match match) {
		JSONObject object = new JSONObject();

		try {
			object.put("command", 6);
			object.put("targetId", StaticParam.targetId);
			object.put("batteryLevel", mainActivity.getBatteryLevel());
			object.put("data", match.getJson());	
			Packet packet = new Packet(matchId, object);
			addPacket(packet);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	private void showNetworkFlag(boolean value) {
		if (mainActivity==null) return;
		mainActivity.showNetworkFlag(value);
	}

    private void resetWifi() throws InterruptedException {
        failureCounter++;
        if (failureCounter<3) return;
		Log.i("TRAPTAThread", "Let's reset WIFI !");
        if (wifiManager==null) {
            Log.e("TRAPTAThread", "WifiManager is null");
        }
        wifiManager.setWifiEnabled(false);
        sleep(1000);
        wifiManager.setWifiEnabled(true);
        int loop=0;
        while (loop<25) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo!=null) {
                Log.e("TRAPTAThread", "is there an IP address ?");
                if (wifiInfo.getIpAddress()!=0) break;
            }
            loop++;
            sleep(2000);
        }
        failureCounter = 0;

    }

	public void run() {
		
		Log.i("TRAPTAThread", "Starting Volley Sender...");
		while (true) {
			try {
				Packet packet = outputQ.take();
				showNetworkFlag(true);
				JSONObject json = packet.json;
				try {
					if (!UDPListener.getInstance().isInit()) {
						Log.e("VolleySender", "No connection to TRAPTA. Put it back in the queue");
						addAfterFailure(packet);
						sleep(5000); // wait for 5 sec before reading Q again
						continue;
					}
					InetSocketAddress address = UDPListener.getInstance().getAddress();
					Socket socket = new Socket(); 
					socket.setSoTimeout(10000);
					socket.connect(address, 7000);  // wait for 7 sec max
					
		            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		            output.writeBytes(json.toString());
		            output.writeByte(4);  // EOT
		            output.flush(); 
					
		            // now read answer
					
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					StringBuilder builder = new StringBuilder();
			        while (true) {
			        	String line = input.readLine();
			        	if ((line==null) || ("EOT".equals(line))) break;
			        	builder.append(line);
			        }

			        socket.close();
			        
			        JSONObject jsonObject = new JSONObject(builder.toString());
			        int response = jsonObject.getInt("response");
			        if (response<0) {
			        	Log.e("TRAPTASocket", "Error while sending volley to TRAPTA");
			        	addAfterFailure(packet);
                        resetWifi();
						sleep(3000); // wait for 3 sec before reading Q again
			        }
			        else {
			        	showNetworkFlag(false);
			        }
                    failureCounter = 0;
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("TRAPTAThread", "Cannot post heat, put it back in queue");
					addAfterFailure(packet);
                    resetWifi();
					sleep(3000); // wait for 3 sec before reading Q again
				}
				
				
			} catch (Exception e) {
				Log.e("TRAPTAThread", e.getMessage());

			}
			
			
		}
		
	}
	
	
}
