package com.traps.trapta;

public class Security {

	private static boolean LICENSE_ACTIVATED = true;
	
	private byte[] passPhraseByteArray = {108,101,32,112,101,116,105,116,32,114,111,105,32,101,115,116,32,109,97,108,97,100,101,46,46,46};
	private String PASSPHRASE = new String(passPhraseByteArray);
	private static final int CHECK_DIGIT_COUNT = 9;
	private static final int MAX_ACTIVITY_COUNT = 20;
	private String macAddress;
	 
	private static Security instance = null;
	
	public void init(String macAdress) {
		
	}
	
	public static Security getInstance() {
		if (instance==null) instance = new Security();
		return instance;
	}
	
}

	
