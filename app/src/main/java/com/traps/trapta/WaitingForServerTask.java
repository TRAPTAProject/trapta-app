package com.traps.trapta;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

public class WaitingForServerTask extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private ProgressDialog progressDialog;
	
	public WaitingForServerTask(Context context) {
		this.context = context;
	}
	
	
	@Override 
	protected void onPreExecute() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
		progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("Recherche réseau");
		progressDialog.setMessage("Recherche du serveur TRAPTA...");
		progressDialog.setCancelable(true);
		progressDialog.show();

	}

	
	@Override
	protected Boolean doInBackground(Void... params) {

		try {
            for (int loop=0; loop<4; loop++) {
                if (!UDPListener.getInstance().isInit()) {
                    Log.e("Waiting", "Waiting for TRAPTA Server: " + loop);
                    Thread.sleep(1000, 0);
                }
                else {
                    return true;
                }
            }
        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return false;

		
	}

	@Override
	protected void onPostExecute(Boolean value) {
	
		progressDialog.dismiss();
		
	}

}
