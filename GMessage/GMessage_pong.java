package GMessage;

/**
 * Pong - odpowiedz serwera po Pingu klienta
 */
public class GMessage_pong extends GMessage {
	GMessage_pong(){
		super.gtype = msg_pong;
	}
	public String toString() {
		return "MESSAGE: pong";
	}
}
