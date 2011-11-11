package GMessage;

import GConnection.GStatus;

/**
 *  zmiana stanu typ 2 (odejscie)
 */
public class GMessage_status extends GMessage{
	public int guin;
	public char gstatus;
	public int gdessize = 0;
	public String gdescr = new String("");
	/**
	 * Zmiana stanu typ 2 (odejscie)
	 * @param uin user id number
	 * @param status status
	 * @param dessize dlugosc opisu
	 * @param descr opis 
	 */
	
	public GMessage_status (int uin, char status, int dessize, String descr){
		gtype = msg_status;
		guin = uin;
		gstatus = status;
		gdessize = dessize;
		gdescr = descr;	
	}
	
	public String toString() {
		return "MESSAGE:  Status:\n   user: "+guin+" changed status: "+GStatus.getStatusGS((int)gstatus)+" , "+gdescr ;	 	
	}
	
}
