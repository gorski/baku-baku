package GMessage;

/**
 * potwierdzenie wyslanie wiadomosci
 * status - stan wiadomo?ci
 * recipient - numer odbiorcy
 * seq - numer sekwencyjny
 */
public class GMessage_msg_send_ack extends GMessage {
	public int status,
			   recipient,
			   seq;
	
	public GMessage_msg_send_ack(int status, int recipient, int seq) {
		super.gtype 	= msg_send_msg_ack;
		this.status 	= status;
		this.recipient 	= recipient;
		this.seq 		= seq;
	}
	
}
