package Baku;

public class GSerial {
	private volatile int serial = 100;
	
	synchronized public int getSerial() {
		++serial;
		return serial;
	}
	
}
