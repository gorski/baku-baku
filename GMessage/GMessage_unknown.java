package GMessage;

/**
 * Wiadomosc nieznanego typu
 * @param unknown_type nieznany typ wiadomosci od serwera
 */
public class GMessage_unknown extends GMessage {
	public GMessage_unknown(int unknown_type){
		super.gtype = unknown_type;
	}
	public String toString() {
		return "MESSAGE: unknown_msg";
	}	
}
