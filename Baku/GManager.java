package Baku;

import java.util.ArrayList;
import java.util.Iterator;

import GConnection.GConnection;
import GConnection.GDefinitions;
import GConnection.GStatus;
import GConnection.ListEntry;
import GMessage.GMessage;
import GMessage.GMessage_notify_reply_60;
import GMessage.GMessage_notify_reply_60_list;
import GMessage.GMessage_status;
import GMessage.GMessage_status60;

public class GManager extends Thread implements GDefinitions {
	private MainWindow win;
	private GTaskList task_list = new GTaskList();
	private GMessage msg;
	
	public GManager(MainWindow w) {
		win = w;
		start();
	}
	
	public void run() {
		System.out.println("GMANAGER: running...");
		while(true) {
			//System.out.println("GMANAGER: listening");
			while(task_list.size()==0) // jezeli nie ma zadan to wstrzymanie
				synchronized (this) {
					try {
						System.out.println("GMANAGER: sleep");
						wait();
					}
					catch(InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			
			while(!task_list.isEmpty()) { //wykonujemy zakolejkowane zadania
				msg = task_list.removeTask();
				task(msg);
			}
			
			try { sleep(200); }
			catch (InterruptedException e) {}
			
		}
	}
	
	/**
	 * zwracamy liste zadan wykorzystywane przez GListner ktory dodaje nowe zadania
	 * @return
	 */
	public GTaskList getTaskList() {
		return task_list;
	}
	
	/**
	 * rozpoznajemy zaddanie i podejmujemy odpowiednia akcje
	 * @param m
	 */
	public void task(GMessage m) {
		switch(m.gtype) {
		case msg_recv_msg:
			rcv_msg();
			break;
		case msg_login_ok:
			login_ok();
			break;
		case msg_login_failed:
			login_failed();
			break;
		case msg_disconnecting:
			disconecting();
			break;
		case msg_notify_reply60 :
			/* tego nigdy nie dostajemy oddzielnie !!! */
			break;
		case msg_recv_msg_list:
			msg_recv_msg_list((GMessage_notify_reply_60_list) m);
			break;
		case msg_status60:
			msg_status60((GMessage_status60) m);
			 break;
		case msg_status:
			 msg_status((GMessage_status) m);
			 break;
		}
		
		// tutaj nigdy nie bedzie msg_notify_reply60 msg_notify_reply !   
	}
	
	private void rcv_msg() {
		win.chats.addMessage(msg);
	}
	
	private void login_ok() {
		//System.out.println("gmg lok");
		win.ggConnected();
		//win.showStatus(GStatus.getStatusGI(win.baku_session.g_def_status));
	}
	
	private void login_failed() {
		win.showConfigDialog();
		win.makeGConnection();
	}
	
	private void disconecting() {
		win.showStatus(GStatus.getStatusGI(status_not_avail));
		win.connection_keeper.keepConnection(null);
	}
	
	private void msg_recv_msg_list(GMessage_notify_reply_60_list mm){
		GMessage_notify_reply_60 le;	
		Iterator it = mm.list.iterator();
		
		if (!mm.list.isEmpty()){
			
			while(it.hasNext()){					   // sprawdzamy po UINie czy user jest juz na liscie 
				le = (GMessage_notify_reply_60) it.next();
				win.gusers.updateUser(le.guin, le.gstatus, le.gdescr);
				}
			}
	}

	
	private void msg_status(GMessage_status mm){
		win.gusers.updateUser((int) mm.guin, (int) mm.gstatus, mm.gdescr  );
	}	
	private void msg_status60(GMessage_status60 mm){
		win.gusers.updateUser((int) mm.guin, (int) mm.gstatus, mm.gdescr  );
	}
	
}
