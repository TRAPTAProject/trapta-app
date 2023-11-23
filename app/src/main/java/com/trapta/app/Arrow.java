package com.trapta.app;
 
public class Arrow {
	
	// codes are: 
	/*
	 * -1: not defined (value is -1)
	 * 0..10 : values 0 to 10
	 */
	      
	private int code; 
	 
	public Arrow() {
		code = -1;
	}
	
	public Arrow(int code) {
		setCode(code);
	}
	 
	public boolean isDefined() {
		if (code>-1) return true;
		return false;
	}
	
	public int getValue() {
		if (code<11) return code;
		return 10;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
		if ((code<-1) || (code>11)) code = -1;
	}
	
	public String toString() {
		if (code==11) return "10X";
		if (code==-1) return "?";
		if (code==0) return "M";
		return ""+code;
	}

}
