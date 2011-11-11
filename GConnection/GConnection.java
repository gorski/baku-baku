	package GConnection;

import java.io.*;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import GMessage.GMessage;
import GMessage.GMessage_disconnecting;
import GMessage.GMessage_login_failed;
import GMessage.GMessage_login_ok;
import GMessage.GMessage_message;
import GMessage.GMessage_notify_reply_60;
import GMessage.GMessage_notify_reply_60_list;
import GMessage.GMessage_status;
import GMessage.GMessage_status60;
import GMessage.GMessage_unknown;
import GMessage.GMessage_userlist;
import Baku.GUsers;

import com.sun.corba.se.impl.ior.WireObjectKeyTemplate;

public class GConnection implements GDefinitions {

	private SessionInfo session = new SessionInfo();
	private Socket sd =null;
	//private BufferedReader in = null;
	private BufferedInputStream in = null;
	private BufferedOutputStream out = null;

			   
	public GConnection(int uin, String p) {
		session.g_number = uin;
		session.g_password = p;
		session.server_info = getServerToConnect();
		if (session.server_info!=null)
			System.out.println("Server_info: ip="+session.server_info[0]+
										   " port="+session.server_info[1]);
	}
	
	/**
	 * otwieramy polaczenie tcp z serverem
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public int connect() {
		if (session.server_info==null) {
			System.out.println("ERROR: Brak danych o serwerze!");
			return -1;
		}
		try {
			sd = new Socket(session.server_info[0], Integer.parseInt(session.server_info[1]));
			sd.setSoTimeout(2500);
			in = new BufferedInputStream(sd.getInputStream());	
			out = new BufferedOutputStream(sd.getOutputStream());
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			return -2;
		}
		catch (IOException e) {
			e.printStackTrace();
			return -3;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return -4;
		}
		return 0;
	}
		
	/**
	 * lagujemy sie do serwera gadu ?
	 * @throws IOException 
	 *
	 */
	synchronized public int login(int g_status, String description) {
		session.g_status = g_status;
		if (description!=null)
			session.description = description;
		try {
			while(in.available()!=0); //czekamy na pierwsza wiadomosc
			
			if (GStreamOperations.gg_readInt(in)==msg_welcome) {
				GStreamOperations.gg_drop(in, 4);
				session.g_seed = GStreamOperations.gg_readInt(in);
				//System.out.println("-> login: seed: "+session.g_seed);
				session.g_hash = gg_login_hash(session.g_seed, session.g_password);
				//System.out.println("-> login: hash: "+session.g_hash);
				gg_send_login();
			}
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}
	/**
	 * wysylamy pakiet logowania
	 * @throws IOException
	 */
	private void gg_send_login() throws IOException {
				int len = 0x1f;
				int desc_len = 0;
				if (session.description!=null) {		// jezeli dolaczamy opis zwiekszamy wielkosc pakietu
					desc_len = session.description.length();
					if (desc_len>70) {
						session.description = session.description.substring(0, 70);
						desc_len = session.description.length();
					}
					len += desc_len;
				}
				GStreamOperations.gg_writeInt(out, msg_login);				 // typ login
				GStreamOperations.gg_writeInt(out, len);					 // wielkosc pakietu
				GStreamOperations.gg_writeInt(out, session.g_number);		 // numer gadu
				GStreamOperations.gg_writeInt(out, session.g_hash);			 // hash hasla
				GStreamOperations.gg_writeInt(out, session.g_status);		 // status na wejscie
				GStreamOperations.gg_writeInt(out, session.g_version);		 // wersja klienta gadu (zmyla ;-)
				out.write(session.unknown1);			 					 // unknown
				GStreamOperations.gg_writeInt(out, session.local_ip);		 // lokalne ip
				GStreamOperations.gg_writeShort(out, session.local_port);	 // lokalny port
				GStreamOperations.gg_writeInt(out, session.external_ip);	 // zewnetrzny ip
				GStreamOperations.gg_writeShort(out, session.external_port); // zewnetrzny port
				out.write(session.image_size);			 					 // maks obrazek
				out.write(session.unknown2);			 					 // unknown
				if (session.description!=null) {		 					 // jezeli dolaczamy opis
					GStreamOperations.gg_writeString(out, session.description); // opis
					//out.write(0);				// tekst konczymy zerem ale w login chyba nie;]
				}
				out.flush();
	}
	
	/**
	 * zmiania status
	 * @throws IOException 
	 *
	 */
	synchronized public int change_status(int g_status, String g_description) {
			int message_lenght = 4;
			int description_length = 0;
			if (g_description!=null) {
				description_length = g_description.length() +1; // bo zero na koncu
				if (description_length>70) {
					g_description = g_description.substring(0, 70);
					description_length = g_description.length();
				}
				message_lenght += description_length;
			}
			try {
				GStreamOperations.gg_writeInt(out, msg_new_status);		// typ wiadomosci
				GStreamOperations.gg_writeInt(out, message_lenght);		// rozmiar
				GStreamOperations.gg_writeInt(out, g_status);				// status
				if (g_description!=null) {
					GStreamOperations.gg_writeString(out, g_description);	// opis
					out.write(0);					// do tekstu dolaczamy zero
				}
				out.flush();
			}
			catch (IOException e) {
				return -1;
			}
			return 0;
	}
	
	/**
	 * wysylamy liste kontaktow do servera, ktory odpowiada pakietem z informacjami o userach
	 * @param list <-- lista kontaktow
	 * @return
	 * @throws IOException 
	 */
	synchronized public int send_list(ArrayList list) {
		System.out.println("ilosc userow:"+list.size());
		int len = list.size();
		
		try {
			if (len==0) { //niemamy nikogo na liscie to wysylami taki pakiet
				GStreamOperations.gg_writeInt(out, msg_list_empty);
				GStreamOperations.gg_writeInt(out, 0);
				out.flush();
				return 0;
			}
			if (len<=400) { //lista mniejsza niz 400 wpisow
				//System.out.println(getClass()+" send_list: sending userlist");
				
				
				GStreamOperations.gg_writeInt(out, msg_notify_last);
				GStreamOperations.gg_writeInt(out, len*5);				// dlugosc pakietu to 5 razy ilosc wpisow na liscie
				for(int i=0; i<len; i++) {
					ListEntry e = (ListEntry) list.get(i);
					if( e.getUin() != 0){					/* dla userow bez numeru gada */
						GStreamOperations.gg_writeInt(out, e.getUin());	// dlugosc 4
						//System.out.println("zapis: "+e.getUin());
						out.write(e.getType());		// dlugosc 1
					}
				}
				out.flush();
				
			}
			else {
				// dla listy wiekszej niz 400 wpisow jest troche inaczej to pomijamy
				System.out.println("send_list: lista > 400");
				return -1;
			}
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * true:
	 * ?eby doda? kogo? do listy w trakcie pracy
	 * false:
	 * usuwa flagi rodzaj u?ytkownika, mo?na go wykorzysta? 
	 * zarówno do usuni?cia u?ytkownika z listy kontaktów, 
	 * jak i do zmiany rodzaju.
	 * @param e
	 * @param add - dodajemy albo usuwamy
	 * @return
	 */
	synchronized public int user_notify(ListEntry entry, boolean add) {
		int notify;
		if(add)
			notify = msg_add_notify;		// wlaczamy na serveze informacje na temat danej osoby
		else
			notify = msg_remove_notify;		// wylaczamy
		try {
			GStreamOperations.gg_writeInt(out, notify);
			GStreamOperations.gg_writeInt(out, 5); 				// calkowita dl pakietu 5:
			GStreamOperations.gg_writeInt(out, entry.getUin()); 	// 	4 bajty
			out.write(entry.getType()); 	//  1 bajt
			out.flush();
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * wysylamy wiadomosc na okreslony numer
	 * tu sie trzeba zastanwoic czy obslugujemy czcionki, kolorki
	 * i obrazki chyba nie  narazie zwykly tekst 
	 * 
	 * @param rcv_number - numer odbiorcy
	 * @param message - tresc wiadomosci
	 * @param msg_seq - numer sekwencyjny, musimy zadbac z zewnatrz aby dla kazdej wiadomosci byl inny
	 * @param msg_class - klasa wiadomosci 
	 * @return
	 */
	synchronized public int send_msg(int rcv_number, String message, int msg_seq, int msg_class) {
		int len = 13;
		if (message!=null) {
			len += message.length();
		}
		try {
			GStreamOperations.gg_writeInt(out, msg_send_msg );
			GStreamOperations.gg_writeInt(out, len);
			GStreamOperations.gg_writeInt(out, rcv_number);
			GStreamOperations.gg_writeInt(out, msg_seq);
			GStreamOperations.gg_writeInt(out, msg_class);
			if (message != null) {
				GStreamOperations.gg_writeString(out, message);
			}
			out.write(0);
			out.flush();
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}

	/** 
	 * 
	 * 
	 * 
	 *            funkcyjka lekko zmieniona tak aby czytala z obiektow GUsers
	 * 
	 *            
	 *            
	 *            
	 */
	

	/**
	 * import/export listy kontaktow
	 * lista ktora przekazujemy to arraylist zawierajaca obiekty ListEntry
	 * 		import: userlist(null, false);
	 * 		export: userlist(GUserList, true);	!! ZMIANA 
	 * 		delete: userlist(null, true);
	 */
	synchronized public int userlist(GUsers gulist, boolean export) {
		try {
			GStreamOperations.gg_writeInt(out, msg_userlist_request);
			if (export) {			
				byte type = userlist_put;
				char[] s = {0x0d, 0x0a}; // do rozdzielania wpisow listy nie wiem z kad sie to bierze ale taka wartosc wyszla w ethereal i dziala
				int count = 0;
				String str = "";
				ListEntry le = null;
				if (gulist != null) {		// eksportujemy
					for(int i=0; i< gulist.getNumberOfUsers(); i++) {			// trzeba to przetestowac
						
						le = (ListEntry)gulist.get(i);
						if ( (count+(le.toString()).length()) > 2039 ) {  // gdy przekracamy wielkosc to wysylamy to co juz jest i budujemy next pakiet
							GStreamOperations.gg_writeInt(out, count+1);
							out.write(type);
							GStreamOperations.gg_writeString(out, str);
							out.flush();
							type = userlist_put_more;
							count = 0;
							str = "";
						}
						str += le.toString()+s[0]+s[1];
						count = str.length();
					}
					if (str != "") { // na koniec jezeli zostalo cos do wyslania to slemy
						GStreamOperations.gg_writeInt(out, count+1);
						out.write(type);
						GStreamOperations.gg_writeString(out, str);
						out.flush();
					}
				}
				else {		// usuwamy liste z servera
					GStreamOperations.gg_writeInt(out, 1);
					out.write(type);
					out.flush();
				}
			}
			else {			// importujemy
				GStreamOperations.gg_writeInt(out, 1);
				out.write(userlist_get);
				out.flush();
			}
			
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}	
	
	
	/**
	 * Czyta wiadomosc
	 * @throws IOException 
	 */
	public GMessage gg_read_message() throws IOException {
		//System.out.println("+trying read message");
		if(in.available()!=0) {	 

			int message_type = GStreamOperations.gg_readInt(in);		/* zgadujemy rodzaj otrzymanej wiadomosci */
			int message_lenght = GStreamOperations.gg_readInt(in);		/* drugie pole to dlugosc */
			
			//System.out.println("+read message 1");
			switch(message_type) {
				case msg_login_ok : {
					GStreamOperations.gg_drop(in, message_lenght);
					//System.out.println("+login ok");
					return new GMessage_login_ok();
				}
				
				case msg_login_failed : {
					GStreamOperations.gg_drop(in, message_lenght);
					//System.out.println("+login failed");
					return new GMessage_login_failed();
				}
			
				case msg_disconnecting : {
					GStreamOperations.gg_drop(in, message_lenght);
					System.out.println("+disconnecting");
					return new GMessage_disconnecting();
				}
			
				case msg_recv_msg : {	// wiadomosc od usera
					int tmp_sender = GStreamOperations.gg_readInt(in);
					int tmp_seq = GStreamOperations.gg_readInt(in);
					int tmp_time = GStreamOperations.gg_readInt(in);
					int tmp_class = GStreamOperations.gg_readInt(in);
					String tmp_text = GStreamOperations.gg_readString(in, message_lenght-16);
					//System.out.println("+rcv msg ["+tmp_text+"] from <"+tmp_sender+">");
					return new GMessage_message(tmp_sender,tmp_seq,tmp_time,tmp_class,tmp_text);				
				}
			
				case msg_notify_reply60 : { // lista stanow i opisow
					//System.out.println("+read message 2");
					ArrayList lst = new ArrayList();
					int package_len = message_lenght;
					/* tutaj pamietamy dlugosc pakietu, poniewaz w jednym pakiecie jest przesylane
					 * kilka struktur notify_reply60 (kazda nastepna nie ma juz pola dlugosc / typ
					 *tylko same skladniki, kolejne obieky sa doawane do arraylisty
					 */
				
					//package_len -= 8;		/* typ, dlugosc pakietu, ale te pola nie sa wliczane do dlugosci (tak mi sie zdaje)*/
				
					while(package_len > 0 ){	
						//System.out.println("+read message 3");
						int tmp_uin = GStreamOperations.gg_readInt(in) & 0x00FFFFFF; // pomijamy flagi na najstarszym bajcie
						char tmp_status = GStreamOperations.gg_readChar(in); 			 
						int tmp_remoteip = GStreamOperations.gg_readInt(in);
						short tmp_remoteport = GStreamOperations.gg_readShort(in);
						char tmp_ver = GStreamOperations.gg_readChar(in);
						char tmp_imgsize = GStreamOperations.gg_readChar(in);
						char tmp_unkn = GStreamOperations.gg_readChar(in);
				  
						int readed_len = 14;
				  
						/* nie musi wystapic */
						char tmp_dessize = 0;
						String tmp_des = "";
				  				  
						tmp_status &= 0x00FFFFFFFF; /* zerowanie maski 'tylko dla znajomych ' */
						if (tmp_status == status_not_avail_descr || tmp_status == status_avail_descr  ||
								tmp_status == status_busy_descr || tmp_status == status_invisible_descr ) {
							tmp_dessize = GStreamOperations.gg_readChar(in); 
							tmp_des = GStreamOperations.gg_readString(in,tmp_dessize);
							readed_len++;
						} 				  
						//System.out.println("<"+tmp_uin+"> status: "+(int)tmp_status+" | desc: "+tmp_des);
						lst.add(new GMessage_notify_reply_60(tmp_uin, tmp_status, tmp_remoteip, tmp_ver,
								tmp_remoteport, tmp_imgsize, tmp_unkn, tmp_dessize , tmp_des, 0));
				  
						package_len -= readed_len+tmp_dessize; // tak mi sie latwiej bylo polapac
					} // while  
					//System.out.println("+read message 4");
					return new GMessage_notify_reply_60_list(lst);
				}
			
			
				case msg_status60  : {	// zmiana stanu usera TYP 2 (zmiana stanu)
					int tmp_uin = GStreamOperations.gg_readInt(in); 
					tmp_uin &= 0x00FFFFFF; // pomijamy flagi na najstarszym bajcie
					char tmp_status = GStreamOperations.gg_readChar(in);	// nowy stan 			 
					int tmp_remoteip = GStreamOperations.gg_readInt(in);
					short tmp_remoteport = GStreamOperations.gg_readShort(in);
					char tmp_ver = GStreamOperations.gg_readChar(in);
					char tmp_imgsize = GStreamOperations.gg_readChar(in);
					char tmp_unkn = GStreamOperations.gg_readChar(in);
					int tmp_dessize = 0;
					String tmp_des = "";
				
				
					if (tmp_status == status_not_avail_descr || tmp_status == status_avail_descr  ||
							tmp_status == status_busy_descr || tmp_status == status_invisible_descr ) {
						/* status z opisem */
						tmp_dessize = ( message_lenght-14 );
						//tmp_des = GStreamOperations.gg_readString(in,tmp_dessize);								
					} 				
					//System.out.println("<"+tmp_uin+"> status: "+(int)tmp_status+" | desc: "+tmp_des);
					return new GMessage_status60 (tmp_uin,tmp_status, tmp_remoteip, tmp_remoteport, tmp_ver, 
							tmp_imgsize, tmp_unkn, tmp_dessize, tmp_des);				
				}
			
				case msg_status  : {	// zmiana stanu usera TYP 1 (odejscie z gadu)
					//System.out.println("dlug:"+message_lenght);
					int tmp_uin = GStreamOperations.gg_readInt(in); 
					tmp_uin &= 0x00FFFFFF; // pomijamy flagi na najstarszym bajcie
					char tmp_status = (char) GStreamOperations.gg_readChar(in);	// status intem (stara wersja?)
					int tmp_dessize = 0;
					String tmp_des = "";
				
					if (tmp_status == status_not_avail_descr || tmp_status == status_avail_descr  ||
							tmp_status == status_busy_descr || tmp_status == status_invisible_descr ) {
						/* status z opisem */
						tmp_dessize = ( message_lenght-5 );
						GStreamOperations.gg_readString(in,3);	// 3 nieuzywane znaki (opis jest intem!)
						tmp_dessize-=3;							
						tmp_des = GStreamOperations.gg_readString(in,tmp_dessize);
					}
					//System.out.println("<"+tmp_uin+"> status: "+(int)tmp_status+" | desc: "+tmp_des);
					return 	new GMessage_status (tmp_uin, tmp_status, tmp_dessize, tmp_des);	
				}
			
				case msg_userlist_reply : {				// odebranie listy kontaktow
					System.out.println("------------------");
					int package_len = message_lenght;			
					char[] split = {0x0d, 0x0a}; 		// z ethereala rozdzielanie pakietow ;) ;)
					
					ArrayList stringlist = new ArrayList( );
					String tempstring ="";
					
					char tmp_char, tmp_char2;			
					tmp_char =  GStreamOperations.gg_readChar(in); 
					package_len--;						// porzucamy pierwszy znak, bo tak			
					while(package_len > 0){	// do {0x0d,0x0a}
						
						tmp_char =  GStreamOperations.gg_readChar(in);	
						
						if (tmp_char != split[0]){ 					/* znak nie jest znakiem konca */
							tempstring += tmp_char;			
							package_len--;
							
						} else {									
							tmp_char2 = GStreamOperations.gg_readChar(in);
							if (tmp_char2 == split[1]){				/* koniec linii, sytuacja normalna */
								stringlist.add(tempstring);
								package_len-=2;
								tempstring="";
							} else {
								tempstring += tmp_char;				/* sytuacja nienormalna, ale jesli kolejny
																	   odczytany znak nie nalezy do sekwencji 
																	   konca wpisu to traktujemy go jako kontunuacje */
								package_len-=2;
								System.out.println("ERROR while reading userlist - illegal sequence!!!");
							}
							
						}
					}				
					return new GMessage_userlist(stringlist);	
				}
			
				default: {
					System.out.println("+msg unknown #"+message_type+"("+message_lenght+")b");
					GStreamOperations.gg_drop(in, message_lenght);
					return new GMessage_unknown(message_type);			
				}
			}
		}
		return null;
	}
	/**
	 * 
	 * @param GMessage 
	 * @return zwraca struktury, ktorych wieksza liczba znajduje sie w jednym pakiecie
	 * @throws IOException
	 */
	public GMessage gg_return_part(GMessage g) throws IOException{
		return g;
	}
	
	synchronized public int ping() {
		try {
			GStreamOperations.gg_writeInt(out, msg_ping);
			GStreamOperations.gg_writeInt(out, 0);
		}
		catch (IOException e) {
			return -1;
		}
		return 0;
	}

	public void close_connection() {
		
	}
	

// PRIVATE
	
	/**
	 * Funkcja pobiera adres i port servera do ktorego sie laczyc
	 * @param uin numer gadu
	 * @param version wersja klienta ;]
	 * @return tablica z adresem ip oraz portem
	 */
	private String[] getServerToConnect() {
		DataInputStream in;
		String[] ch,
				 ip = new String[2];
		String tmp = null,
			   query = "http://appmsg.gadu-gadu.pl/appsvc/appmsg4.asp?"
			   		   + "fmnumber="+session.g_number
			   		   + "&version=5,0,5,17"
			   		   + "&lastmsg=0";
		try {
			URL url = new URL(query);
      		URLConnection urlConnection = url.openConnection();
      		urlConnection.setConnectTimeout(2000);
      		urlConnection.connect();
      		in =  new DataInputStream(new BufferedInputStream( url.openStream() ));
      		tmp = in.readLine();
      		ch = tmp.split(" ");
    		tmp = ch[2];
    		ip = tmp.split(":");
		} 
	  	catch (MalformedURLException e) {
    		System.out.println("ERROR: getServerToConnect: " + e);
    		return null;
        }
		catch(IOException e) {
			System.out.println("ERROR: getServerToConnect: " + e);
			return null;
		}
		
		return ip;		
	}	
	
	/**
	 * obliczamy hash hasla gg
	 */
	private int gg_login_hash(int seed, String g_password) {
		int hash=0;
		long x = 0, y=0, z = 0;
		byte[] password;		
		password = g_password.getBytes();
		y = seed;
		y <<= 32;
		y >>>= 32;
		y &= 0x00000000FFFFFFFF;
		for (int i=0; i<password.length; i++) {
			x = (x & 0x00000000ffffff00) | password[i];
			x <<= 32;
			x >>>= 32;
			x &= 0x00000000FFFFFFFF;
			y ^= x;
			y <<= 32;
			y >>>= 32;
			y &= 0x00000000FFFFFFFF;
			y += x;
			y <<= 32;
			y >>>= 32;
			y &= 0x00000000FFFFFFFF;
			x <<= 8;
			x <<= 32;
			x >>>= 32;
			x &= 0x00000000FFFFFFFF;
			y ^= x;
			y <<= 32;
			y >>>= 32;
			y &= 0x00000000FFFFFFFF;
			x <<= 8;
			x <<= 32;
			x >>>= 32;
			x &= 0x00000000FFFFFFFF;
			y -= x;
			y <<= 32;
			y >>>= 32;
			y &= 0x00000000FFFFFFFF;
			x <<= 8;
			x <<= 32;
			x >>>= 32;
			x &= 0x00000000FFFFFFFF;
			y ^= x;
			y <<= 32;
			y >>>= 32;
			y &= 0x00000000FFFFFFFF;
			z = y & 0x000000000000001f;
			z <<= 32;
			z >>>= 32;
			z &= 0x00000000FFFFFFFF;
			y = (y << z) | (y >>> (32 - z));
		}
		y <<= 32;
		y >>>= 32;
		y &= 0x00000000FFFFFFFF;
		hash = (int) (hash | y);
		return hash;
	}

}
