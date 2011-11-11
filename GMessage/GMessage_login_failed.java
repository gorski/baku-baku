package GMessage;

/**
 * Blad polczaenia z serwerem
 */
public class GMessage_login_failed extends GMessage {
	public GMessage_login_failed(){
		super.gtype = msg_login_failed;
	}
	public String toString() {
		return "MESSAGE: login_failed";
	}	
}
