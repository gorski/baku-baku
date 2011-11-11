package GMessage;

import java.util.ArrayList;

import GConnection.GStatus;

/**
 * Lista zawierajaca liste userow
 */
public class GMessage_notify_reply_60_list extends GMessage{
	public ArrayList list;
	/**
	 * @param l ArrayLista zawierajaca obiekty GMessage_notify_reply_60 reprezzentujace
	 *            uzytkownikow wraz ze statusami
	 */
	
	public GMessage_notify_reply_60_list(ArrayList l){
		gtype = msg_recv_msg_list;
		list = l;
	}
	
	public String toString() {
		String str = "MESSAGE:  ";
		for (int i=0; i<list.size(); i++) {
			GMessage_notify_reply_60 gm = (GMessage_notify_reply_60) list.get(i);
			str += "\n   user: "+gm.guin+" status: "+GStatus.getStatusGS((int)gm.gstatus)+" "+gm.gdescr;
		}
		return str;	 	
	}
	
}
