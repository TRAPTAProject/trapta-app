package com.trapta.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

public class JsonRequestTask extends AsyncTask<JSONObject, Integer, JSONObject> {

	private Context context;
	private ProgressDialog progressDialog;
	private InetSocketAddress socketAddress;
	private String title;
	 
	public JsonRequestTask(Context context, InetSocketAddress socketAddress, String title) {
		this.context = context;
		this.socketAddress = socketAddress;
		this.title = title;
	}
	
	@Override 
	protected void onPreExecute() {
		
		progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(title);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		
	}
	
	@Override
	protected JSONObject doInBackground(JSONObject... params) {
		Socket socket = new Socket();
		try {
			socket.setSoTimeout(10000);
			socket.connect(socketAddress, 5000);
			Log.i("SocketConnector", "Socket opened");
			// make request
			JSONObject request = params[0];

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeBytes(request.toString());
            output.writeByte(4); //EOT
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
			return jsonObject;
             
            
		} catch (Exception e) {
			e.printStackTrace();

		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		Log.i("SocketConnectorTask", "onPostExecute");
		progressDialog.dismiss();
		
	}


}
