package Baku;

import java.util.Timer;
import java.util.TimerTask;

import GConnection.GConnection;

public class GConnectionKeeper {
	GConnection gg;
    Timer timer;

    public GConnectionKeeper() {
        timer = new Timer();
    }
    
    /*
     * trzyma polaczenie poprzez wysylanie pingow do servera gadu co 4 min
     * jezli jakos argument podamy null to przestaje wysylac pingi
     */
    public void keepConnection(GConnection g) {
    	gg = g;
    	if (gg != null)
    		timer.schedule(new Ping(),
    					4*60*1000,       //initial delay
    					4*60*1000); 	 //subsequent rate 3min
    }
    
    class Ping extends TimerTask {
    	int numWarningBeeps = 3;
    	public void run() {
    		if (gg == null)
    			timer.cancel();
    		else {
    			gg.ping();
    			System.out.println("ConnectionKeeper:  ping");
    		}
    	}
    }

}