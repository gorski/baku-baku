package GMessage;

import java.util.ArrayList;
import java.util.Iterator;

import Baku.GUsers;
import GConnection.ListEntry;

/**
 *  Zaimportowana lista kontaktów
 */
public class GMessage_userlist extends GMessage{
	GUsers ul = new GUsers();
	
	/**
	 * Tworzymy strukture GUserList z zaimportowanych userow
	 * @param lst lista zawierajaca stringi pobrane z serwera (ze ;kami)
	 */
	public GMessage_userlist(ArrayList lst){
		Iterator i = lst.iterator();
		String entry_string;
		while(i.hasNext()){		
			entry_string = (String) i.next();
			ul.addUser(new ListEntry(entry_string),6,"");
		}
	}	
	
}
