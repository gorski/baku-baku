package GConnection;

/*
 * Wpis listy kontaktow (1 kontakt)
 * imi?;nazwisko;pseudonim;wy?wietlane;telefon_komórkowy;grupa;uin;adres_email;dost?pny;
 * ?cie?ka_dost?pny;wiadomo??;?cie?ka_wiadomo??;ukrywanie;telefon_domowy
 */

public class ListEntry {
	public String imie = "",
				  nazwisko = "",
				  pseudonim = "",
				  wyswietlane = "",
				  tel_kom = "",
				  grupa = "",
				  email = "",
				  dostepny = "0",
				  s_dostepny = "",
				  wiadomosc = "0",
				  s_wiadomosc = "",
				  ukrywanie = "0",
				  tel_dom = "";
	private int  uin = 0;		// numer gg
	private byte type = 0x03; 	// typ usera (user_normal jako default)
	
	public int actual_status = 6;
	public String actual_descr = "";

	public ListEntry(int u) {  	// tworzymy wpis numer plus typ usera
		uin = u;
	}
	
	public ListEntry(String s) {  // tworzymy wpis na podstawie stringu z pliku badz z importu;
		String[] tmp = s.split(";");
		if (tmp.length >= 13) {
			imie = tmp[0];			
			nazwisko = tmp[1];
			pseudonim = tmp[2];
			wyswietlane = tmp[3];
			tel_kom = tmp[4];
			grupa = tmp[5];
			try {	/* gadu pozwala tu wpisywac nienumeryczne wartosci */
				uin = Integer.parseInt(tmp[6]);
			} catch (NumberFormatException e){
				uin = 0;
			}
			email = tmp[7];
			dostepny = tmp[8];
			s_dostepny = tmp[9];
			wiadomosc = tmp[10];
			s_wiadomosc = tmp[11];
			ukrywanie = tmp[12];
		}
		if (tmp.length > 13)
			tel_dom = tmp[13];
		else
			tel_dom = "";
	}
	
	public String toString() {  // zwaracamy w formie gotowej do eksportu badz zapisu do pliku
		return imie+";"+nazwisko+";"+pseudonim+";"+wyswietlane+";"+tel_kom+";"+grupa+";"+uin+";"+
		  	   email+";"+dostepny+";"+s_dostepny+";"+wiadomosc+";"+s_wiadomosc+";"+ukrywanie+";"+tel_dom;
	}
	
	public int getUin() { 
		return uin; 
	}
	public byte getType() { return type; }
	public void setType(byte t) { type = t; }
	
	/**
	 *  import listy jako tablicy Stringow ( format z srednikami ) 
	 *  usuniecie starej listy i zapisanie nowej w jej miejsce
	 */
	public int replace(String[] str){
		System.out.println(" pierwszy string : "+str[0]);
		return 0;
	}
}
