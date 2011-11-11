package GMessage;

/**
 * Informacje o statusie, opisie, IP etc. danego usera 
 */
public class GMessage_notify_reply_60 extends GMessage{
	public int guin;
	public char gstatus;
	int gremoteip;
	short gremoteport;
	char gver;
	char gimgsize;
	char gunkn;
	char gdessize;
	public String gdescr = new String("");
	int gtime = 0;
	/**
	 * 
	 * @param uin numer czlowieka z listy
	 * @param status jego status
	 * @param remoteip ip albo maska : NAT / nie ma mnie w kontaktach
	 * @param remoteport zdalny port
	 * @param ver wersja gadulca
	 * @param imgsize rozmiar obrazka
	 * @param unkn ?
	 * @param dessize rozmiar opisu
	 * @param descr opis (nie musi byc)
	 * @param time czas (nie musi byc)
	 */
	
	public GMessage_notify_reply_60(int uin, char status,int remoteip,char ver,
			short remoteport, char imgsize,char unkn,char dessize, String descr,int time) {
		gtype = msg_notify_reply60;
		guin = uin;
		gstatus = status;
		gremoteip = remoteip;
		gremoteport = remoteport;
		gver = ver;
		gimgsize = imgsize;
		gunkn = unkn;
		gdessize = dessize;
		gdescr = descr;
		gtime = time;	
	}
	
	
	public String toString() {
		return "MESSAGE:  Notify60:\n   user: "+guin+" has status: "+gstatus ;	 	
	}
}
