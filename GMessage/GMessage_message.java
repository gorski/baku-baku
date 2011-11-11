package GMessage;

/**
 * @param s numer nadawcy
 * @param sq numer sekwencyjny
 * @param t czas nadania wiadomosci
 * @param cl klasa wiadomosci
 * @param txt tresc wiadomosci
 */
public class GMessage_message extends GMessage {
	public int gsender;
	public int gseq;
	public int gtime;
	public int gclass;
	public String gmessage;			
	
	public GMessage_message(int s, int sq, int t, int cl, String msg) {
		gtype = msg_recv_msg;
		gsender = s;
		gseq = sq;
		gtime = t;
		gclass = cl;
		gmessage = msg;
	}
	
	public String toString() {
		return "MESSAGE: form_user: "+gsender+" message: "+gmessage;
	}
}
