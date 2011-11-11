package GMessage;

import GConnection.GStatus;

/**
 * zmiana statusu typ 1
 */
public class GMessage_status60 extends GMessage {
	public int guin;
	public char gstatus;
	int gremoteip;
	short gremoteport;
	char gver;
	char gimgsize;
	char gunkn;
	int gdessize = 0;
	public String gdescr = new String("");
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
	 */
	
	public GMessage_status60 (int uin,char status, int remoteip, short remoteport, char ver, char imgsize,
			char unkn, int dessize, String descr){
		gtype = msg_status60;
		guin = uin;
		gstatus = status;
		gremoteip = remoteip;
		gremoteport = remoteport;
		gver = ver;
		gimgsize = imgsize;
		gunkn = unkn;
		gdessize = dessize;
		gdescr = descr;
	}
	
	public String toString() {
		return "MESSAGE:  Status60:\n   user: "+guin+" changed status: "+GStatus.getStatusGS((int)gstatus)+" , "+gdescr ;	 	
	}
}
