package GMessage;

/**
 * Ustanowiono polczenie z serwerem
 */
public class GMessage_login_ok extends GMessage {
	public GMessage_login_ok(){
		super.gtype = msg_login_ok;	
	}
	public String toString() {
		return "MESSAGE: login_ok";
	}	
}
