package com.trapta.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class UDPSyncTask extends AsyncTask<Void, Integer, Boolean> {

	
	private Context context;
	private ProgressDialog progressDialog;
	
	public UDPSyncTask(Context context) {
		this.context = context;
		

	}
	
	@Override 
	protected void onPreExecute() {
		
		progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("Connexion WIFI");
		progressDialog.setMessage("Recherche du serveur TRAPTA sur le réseau..."); 
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	
	@Override
	protected Boolean doInBackground(Void... params) {
		return UDPListener.getInstance().waitforUpdate();
		
	}

	@Override
	protected void onPostExecute(Boolean value) {
	
		progressDialog.dismiss();
		
	}


}
