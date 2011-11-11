package Baku;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import GConnection.GConnection;
import GConnection.ListEntry;
import GMessage.GMessage;
import GMessage.GMessage_message;

public class GChats {
	private ArrayList chats;
	private GConnection gg;
	private JFrame parent;
	private GSerial serial;
	
	public GChats(JFrame parent) {
		this.parent = parent;
		chats = new ArrayList();
		serial = new GSerial();
	}
	
	public void setConnection(GConnection g) {
		gg = g;
	}
	
	/**
	 * dodajemy nowy chat do listy aktualnie prowadzonych chatow
	 * @param e
	 */
	public void addChat(ListEntry e) {
		chats.add(new GChatWindow(parent, e, gg, serial));
	}
	
	/**
	 * wzucamy otrzymana wiadomosc do odpowiedniego okienka rozmowy
	 * @param recived_msg
	 */
	public void addMessage(GMessage recived_msg) {
		GMessage_message recived = (GMessage_message) recived_msg; 
		GChatWindow chat;
		Iterator chats_iter = chats.iterator();
		while(chats_iter.hasNext()) { //przeszukujemy liste w celu znalezienia odpowiedniego okienka 
			chat = (GChatWindow) chats_iter.next();
			if(!chat.isVisible()) { // jezeli okienko zostalo zamkniete to usuwamy z listy
				chats_iter.remove();
			}
			if(chat.getRecipient() == recived.gsender) {
				//jezeli otrzymana wiadomosc pasuje do okienka rozmowy to wzucamy tam wiadomosc
				chat.addMessage(recived);
			}
			else { 
				//jezeli nie pasuje otwieramy nowe okienko narazie dla testow trzeba to bedzie przerobic
				GChatWindow tmp = new GChatWindow(parent, 
						new ListEntry(";;;;;;"+recived.gsender+";;0;;0;;0;"), 
						gg, serial);
				chats.add(tmp);
				tmp.addMessage(recived);
			}
		}	
	}
	
}
