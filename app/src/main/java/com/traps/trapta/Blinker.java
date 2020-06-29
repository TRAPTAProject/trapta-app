package com.traps.trapta;

import java.util.Timer;
import java.util.TimerTask;

public class Blinker extends TimerTask {

	private Timer timer = new Timer();

	private int ratio0;
	private int ratio1;
	private int switch0;
	private int counter;
	private BlinkListener listener;
	private String id;
	private int index;


	
	public Blinker(BlinkListener listener, String id, int index) {
		this.listener = listener;
		this.id = id;
		this.index = index;
	}
	
	public void start(int periodInMilli, int ratio0, int ratio1) {
		this.ratio0 = ratio0;
		this.ratio1 = ratio1;
		this.switch0 = 0;
		counter = 0;
		timer = new Timer();
		timer.schedule(this, 0, periodInMilli);
	}
	
	public void stop() {
		timer.cancel();
		listener.blinkOff(id, index);
	}
	

	@Override
	public void run() {
		
		counter++;
		if ((switch0==0) && (counter==ratio0)) {
			listener.blinkOff(id, index);
			counter=0;
			switch0 = 1;
		} 
		else if ((switch0==1) && (counter==ratio1)) {
			listener.blinkOn(id, index);
			counter=0;
			switch0 = 0;
		}

	}

}
