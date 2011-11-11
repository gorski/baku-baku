package GMessage;

/**
 * Serewer zerwal polaczenie
 */
public class GMessage_disconnecting extends GMessage {
	public GMessage_disconnecting(){
		super.gtype = msg_disconnecting;
	}
	public String toString() {
		return "MESSAGE: disconnected";
	}	
}
