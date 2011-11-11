package Baku;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Arrays;
import java.util.Iterator;

import GConnection.GDefinitions;
import GConnection.GStreamOperations;
import GConnection.GConnection;

import GConnection.ListEntry;


/**
 * Lista wszytskich userow
 */
public class GUsers extends JLabel implements ActionListener {
	int tryb_sortowania = 0;						// 0 - w kolejnosci dodania - uzywany
													// 1 - najpierw dostepni 
	JPanel jp;			
	GridLayout gl;
	GUser su;
	
	ArrayList all_users = new ArrayList();
	BakuSession baku_sess;
	GConnection g;
	JFrame parr;
	JPanel user = new JPanel();
	JScrollPane jsp;

	
	public GUsers(){
		
	}

	/**
	 * Lista userow
	 * @param p okno - rodzic
	 * @param bk sesja baku
	 * @param gg polaczenie
	 * @param fromdisk czy ma byc odczytana lista userow z dysku (jesli jest)
	 * @param sort tryb sortowania 
	 */
	GUsers(JFrame p, BakuSession bk, GConnection gg, boolean fromdisk,  int sort){
		tryb_sortowania = sort;
		parr = p;
		baku_sess = bk;
		g = gg;
		
		if (fromdisk) {	/* czy lista ma byc odczytana z pliku */			
			loadFromFile("./.gg/user-1");

		}
		update();
		
		
	}
	
	/**
	 * Dodajemy usera !
	 * @param new_entry nowy wpis (porownujemy uin jak jest to nadpisujemy) 
	 * @param st aktualny status (na wejscie 6 == offline)
	 * @param d aktualny opis (na wejscie null)
	 * @return 0
	 */
	public int addUser(ListEntry new_entry, int st, String d){
		
		ListEntry le;	
		Iterator it = all_users.iterator();
		
		if (!all_users.isEmpty()){
			while(it.hasNext()){					   // sprawdzamy po UINie czy user jest juz na liscie 
				le = (ListEntry) it.next();
				if(le.getUin() == new_entry.getUin()){ // jezeli jest to nadpisujemy
					all_users.remove(le);
					break;
				}
			}
		}
		new_entry.actual_descr = d;	
		new_entry.actual_status = st;
		all_users.add(new_entry);
		update();
		return 0;
	}
	
	/**
	 * Zmiana statusu usera
	 * @param new_entry user podany jako ListEntry
	 * @param st nowy status
	 * @param d nowy opis 
	 * @return 0
	 */
	public int updateUser(ListEntry new_entry,int st, String d){
		ListEntry le;	
		Iterator it = all_users.iterator();
		
		if (!all_users.isEmpty()){
			while(it.hasNext()){					   // sprawdzamy po UINie czy user jest juz na liscie 
				le = (ListEntry) it.next();
				if(le.getUin() == new_entry.getUin()){ // jezeli jest to nadpisujemy
					all_users.remove(le);
					all_users.add(new_entry);
					break;
				}
			}
		}
		update();
		return 0;
	}
	
	/**
	 * Zmiana statusu
	 * @param uin uin usera
	 * @param st nowy status
	 * @param d nowy opis
	 * @return
	 */
	public int updateUser(int uin,int st, String d){
		ListEntry le;	
		Iterator it = all_users.iterator();
		
		if (!all_users.isEmpty()){
			while(it.hasNext()){					   // sprawdzamy po UINie czy user jest juz na liscie 
				le = (ListEntry) it.next();
				if(le.getUin() == uin){ // jezeli jest to nadpisujemy
					le.actual_status = st;
					le.actual_descr = d;
					break;
				}
			}
		}
		update();
		return 0;
	}
	
	

	
	
	/**
	 * odrysowanie listy - wolane po kazdej zmianie
	 */
	public void update(){
		jp = new JPanel();
		gl = new GridLayout(all_users.size(),1);  // liczbe wierszy == ilosci userow, 1 kolumna
		jp.setLayout(gl);
		if (jsp != null){
			parr.remove(jsp);
		}
		ListEntry le;	
		GUser us;
		Iterator it = all_users.iterator();

		if (!all_users.isEmpty()){
		switch (tryb_sortowania){
		
		case 0: {                                     /* nie sortujemy */
			while(it.hasNext()){
				le = (ListEntry) it.next();
				us = new GUser(this, le.getUin(), le.actual_status , le.wyswietlane, le.actual_descr);
				jp.add(us);
				}	
			break;
			}
		case 2: {									/* sortujemy, aktywni na poczatku */
			while(it.hasNext()){
				le = (ListEntry) it.next();
				if ( (le.actual_status == 0) || (le.actual_status == 1) || (le.actual_status == 2)
						|| (le.actual_status == 3)){ /* dostepni, zaraz wracam */
					/**
					 * 
					 * 
					 *  dorobic !
					 * 
					 * 
					 */
					
				//a_active.asList( all_users.toArray() );
					
				} else { /* niewidoczni, niedostepni */
					
					
				}
			}
		
		}
		}	
		} // if not empty
		jsp = new JScrollPane(jp, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		parr.add(jsp);
		parr.setVisible(true);			// bez tego sie nie dalo kliknac po updacie
			
		} 
	
	 /**
	 * "szyfrowanie"
	 * @param str
	 * @return
	 */
	private String encryptString(String str) {
		char[] p = str.toCharArray();
		for(int i=0; i<p.length; i++)
			p[i]-=30;
		return new String(p);
	}

	/**
	 * "deszyfrowanie"
	 * @param str
	 * @return
	 */
	private String unencryptString(String str) {
		char [] tmp = str.toCharArray();
		for(int i=0; i<tmp.length; i++)
			tmp[i]+=30;
		return new String(tmp);
	}
	
	
	/**
	 * Zapis do pliku w formacie baku (szyfrowanym)
	 * @param path sciezka
	 * @param lista lista do zapisu
	 * @return kontrola bledu
	 */
	public int saveToFile(String path, ArrayList lista){
		
		if(lista==null)		/* bez parametru - bierzemy aktualna liste */ 
			lista = all_users;
		
		
		Iterator i = lista.iterator();
		ListEntry le;
		
		if (! lista.isEmpty() ) {
			try {
				File dir = new File(".gg");
				if (!dir.isDirectory()) { // sprawdzamy czy istnieje katalog .gg jak nie to tworzymy
					dir.mkdir();
				}
				File conf = new File(path);				 // zapisujemy pod wybrana sciezka
				FileOutputStream file = new FileOutputStream(conf);
				BufferedOutputStream out = new BufferedOutputStream(file);
				
				String chck = "baku01";							// ciag kontrolny + format pliku
				GStreamOperations.gg_writeString(out,chck);		// bez szyforwania
				
				
																// miedzy kolejnymi wpisami wyzsze wartosci bo
																// cofamy o 30 (encrypt) 
				while(i.hasNext()){
					le = (ListEntry) i.next();
					GStreamOperations.gg_writeString(out, encryptString(le.toString()));
					GStreamOperations.gg_writeString(out, encryptString("|&$^|"));		// podzial miedzy kontaktami
				}	
				out.close();
				
			}
			catch(FileNotFoundException e) {
				System.out.println("GUSERLIST: can't save");
				return -1;
			}
			catch (UnsupportedEncodingException e) {
				System.out.println("GUSERLIST: can't save");
				return -1;
			}
			catch (IOException e) {
				System.out.println("GUSERLIST: can't save: I/O");
				return -1;
			}
			System.out.println("GUSERLIST: userlist saved to file");
		}
		else {
			System.out.println("GUSERLIST: nothing to save!");
			return 1;
		}
		return 0; // ok
	}
	
	/**
	 * zapis do pliku w formacie listy gadu
	 * @param path sciezka
	 * @param lista lista
	 * @return chck
	 */
	public int saveToFileGGFormat(String path, ArrayList lista){
		System.out.println("GUSERS: Saved to file !");
		
		if(lista==null)		/* bez parametru - bierzemy aktualna liste */ 
			lista = all_users;
		
		
		Iterator i = lista.iterator();
		ListEntry le;
		
		if (! lista.isEmpty() ) {
			try {
				File conf = new File(path);				 			// zapisujemy pod wybrana sciezka
				FileOutputStream file = new FileOutputStream(conf);
				BufferedOutputStream out = new BufferedOutputStream(file);
				
				byte split2 = 0x0a;
				byte split1 = 0x0d;
				
				while(i.hasNext()){
					le = (ListEntry) i.next();
					GStreamOperations.gg_writeString(out,le.toString());
					GStreamOperations.gg_writeByte(out,split1);		// podzial miedzy kontaktami 0x0d
					GStreamOperations.gg_writeByte(out,split2);		// podzial miedzy kontaktami 0x0a
				}
				GStreamOperations.gg_writeByte(out,split1);		// podzial miedzy kontaktami 0x0d
				GStreamOperations.gg_writeByte(out,split2);		// podzial miedzy kontaktami 0x0a
				out.close();
				
			}
			catch(FileNotFoundException e) {
				System.out.println("GUSERLIST: can't save");
				return -1;
			}
			catch (UnsupportedEncodingException e) {
				System.out.println("GUSERLIST: can't save");
				return -1;
			}
			catch (IOException e) {
				System.out.println("GUSERLIST: can't save: I/O");
				return -1;
			}
			System.out.println("GUSERLIST: userlist saved to file");
		}
		else {
			System.out.println("GUSERLIST: nothing to save!");
			return 1;
		}
		return 0; // ok
	}
		
	
	/**
	 * Odczyta z pliku "szyforwanego"
	 * @param path sciezka
	 * @return chck
	 */
	public ArrayList loadFromFile(String path){
		System.out.println("GUSER: Load from file!");
		ArrayList lista_userow = new ArrayList();			// userlist from file
		
		try {
			
			File userlistfile = new File(path);
			if (!userlistfile.isFile()) 				// jezeli plik z configiem nie istnieje to wychodzimy
				return null;
			
			BufferedInputStream in = new BufferedInputStream( new FileInputStream(userlistfile));
			
			if (GStreamOperations.gg_readInt(in) != 1969971554){			// sprawdzenie formatu pliku
				System.out.println("GUSERLIST: File "+path+" is NOT Baku userlist!");
				return null;
			}
			
			short format = GStreamOperations.gg_readShort(in);
			/* 
			 * kolejne dwa bajty to format zapisu ksiazki (obecnie 01), 
			 * dodalem to pole aby kolejne wersje Baku (np ta z HiSQLem) mialy mozliwosc
			 * poprawnego interpretowania pliku z lista kontaktow
			 *  
			 */
			switch(format){
			
			case 12592: {	// v01 | 6 bajtow + | kontakt + {0x0d, 0x0a} + kontakt.... 
				String tempstring ="";
				int len = (int)(userlistfile.length() - 6);
				
				tempstring = unencryptString(GStreamOperations.gg_readString(in, len)); // zczytujemy caly plik
				in.close();
			
				int splitplace;												// miejsce ciecia strinfa
				String podstring = "";										// kolejne wpisy
				
				while (len >=30 ){
					splitplace = tempstring.indexOf("|&$^|");
					
					podstring =  tempstring.substring(0, splitplace);
					lista_userow.add(new ListEntry(podstring));								// zapamietujemy dla returna
					addUser(new ListEntry(podstring),GDefinitions.status_not_avail,"");		// dodajmey do listy
					
					
					tempstring = tempstring.substring((splitplace+5), len);	// odcinamy zapisany fragment
					
					len -= (splitplace + 5); 							
				}
				break;
			}
			
			default: {
				System.out.println("GUSERLIST: UserList format NOT supported in this version!");
				return null;
			}
			}
		}
		catch(FileNotFoundException e) {
			System.out.println("GUSERLIST: cannot read");
			return null;
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("GUSERLIST: cannot read");
			return null;
		}
		catch (IOException e) {
			System.out.println("GUSERLIST: cannot read");
			return null;
		}
		
		//g.send_list(getListWithUINSOnly());							// i sprawdzamy kto jest na liscie
		return lista_userow; 
	}
	
	
	/**
	 * Odczyt z pliku kontaktow Gadu 
	 * @param path sciekza
	 * @return chck
	 */
	public ArrayList loadFromFileGGFormat(String path){
		System.out.println("GUSER: Load from file!");
		ArrayList lista_userow = new ArrayList();			// userlist from file
		
		try {
			
			File userlistfile = new File(path);
			if (!userlistfile.isFile()) 				// jezeli plik z configiem nie istnieje to wychodzimy
				return null;
			
			BufferedInputStream in = new BufferedInputStream( new FileInputStream(userlistfile));
			String tempstring ="";
			int len = (int)(userlistfile.length());
				
			tempstring = GStreamOperations.gg_readString(in, len); 		// zczytujemy caly plik
			in.close();
			char literka;
			String podstring ="";
			
			for (int i=0; i<len-1; i++){
				literka = tempstring.charAt(i);
				
				if (literka != 0x0a && literka != 0x0d){				// podzial kontaktow wg gadu
					podstring += literka;
				} else {
					if(podstring.length() >= 2){
					    lista_userow.add(new ListEntry(podstring));
						addUser(new ListEntry(podstring),GDefinitions.status_not_avail,"");
					}
					podstring="";
				}
			}

		}
		catch(FileNotFoundException e) {
			System.out.println("GUSERLIST: cannot read");
			return null;
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("GUSERLIST: cannot read");
			return null;
		}
		catch (IOException e) {
			System.out.println("GUSERLIST: cannot read");
			return null;
		}
	
		return lista_userow; 
	}
	
	
	
	
	public ArrayList getList(){
		return all_users;
	}
	
	/**
	 * Zwraca tylko userow ktorzy maja wpisane pole UIN
	 * (gadu przechowuje tez takich co maja tylko nr telefonu itd...)
	 * @return lista bez uinow
	 */
	public ArrayList getListWithUINSOnly(){
		ArrayList uins_only = new ArrayList();
		
		Iterator i = all_users.iterator();
		ListEntry le;
		
		while(i.hasNext()){
			le = (ListEntry) i.next();
			if (le.getUin() != 0){
				uins_only.add(le);
			}
		}
		return uins_only;
	}
	
	
	
	public void clearList(){
		all_users.clear();
		update();
	}
	
	
	public int getNumberOfUsers(){
		return all_users.size();
	}
	
	public ListEntry get(int i){
		return (ListEntry) all_users.get(i);
	}
	
	
	
	public void actionPerformed(ActionEvent e){
		System.out.println("action!");

		
	}
	

	
	
	
	
	
	
	
	
	
	
}



