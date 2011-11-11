package Baku;

import java.io.*;
import java.util.*;
import java.io.IOException;

import GConnection.GConnection;
import GConnection.GDefinitions;
import GMessage.GMessage;


public class GListener extends Thread {
	/**
	 * Watek nassluchajacy bufor
	 * @param delay definiuje co jaki czas (w ms) ma byc sprawdzany bufor
	 * @param actual_connection referencja do ustanowionego polaczenia
	 */
	private GConnection actual_connection;
	private GTaskList task_list;
	private GManager manager;

	public GListener(GManager gm) {
		super("GListener2");
		task_list = gm.getTaskList();
		manager = gm;
		start();
	}
	
	public void run() {
		System.out.println("GLISTENER: running...");
		int delay = 400;
		while(true){
			//System.out.println("GLISTENER: listening...");
			while(actual_connection==null) //jezeli zostanie zerwane polaczenie to wstrzymujemy nasluchiwanie
				synchronized (this) {
					try {
						System.out.println("GLISTENER: sleep");
						wait();
					}
					catch(InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			
			GMessage msg;
			try { // odczytujemy aktualna wiadomosc i poddajemy przetworzeniu	
				msg = actual_connection.gg_read_message();
				if (msg!=null) {
					delay = 100;
					//System.out.println("GLISTENER: adding task");
					task_list.addTask(msg);
					synchronized (manager) {
						//System.out.println("GLISTENER: notify manager");
						manager.notify();
					}
					//System.out.println("GLISTENER: printing and go to listening");
					System.out.println(msg);
				}
				else 
					delay = 400;
			}
			catch (IOException e) {
				System.err.println("ERROR: gg_read_message() I/O");
				e.printStackTrace();
			}
			
			try { sleep(delay); }
			catch (InterruptedException e) {
				System.out.println("GLISTENER: ERROR: sleep");
			}
			//System.out.println("GLISTENER: after sleeping");
		}
	}
	
	public void setConnection(GConnection g) {
		actual_connection = g;
		synchronized (this) {
			this.notify();
		}
	}
	
}
