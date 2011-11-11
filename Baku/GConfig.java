package Baku;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import GConnection.GStreamOperations;


/*
 * operacje na pliku konfiguracyjny czytanie zapisywanie
 */
public class GConfig {
	private BakuSession baku_session;

	public GConfig(BakuSession bk) {
		baku_session = bk;
	}
	
	/**
	 * zapisujemy numer gada i haslo w pliku haslo opcjonalnie wedle uznania ;]
	 * 
	 * @param uin
	 * @param pass
	 * @return
	 */
	public int saveConfig() {
		if (baku_session.g_number != -1) {
			try {
				int pass_len; // dlugosc hasla
				File dir = new File(".gg");
				if (!dir.isDirectory()) { // sprawdzamy czy istnieje katalog .gg jak nie to tworzymy
					dir.mkdir();
				}
				File conf = new File(".gg/config"); // bedziemy pisac do pliku .gg/config
				FileOutputStream file = new FileOutputStream(conf);
				// otwieramy bufor wyjsciowy
				BufferedOutputStream out = new BufferedOutputStream(file);
				
				// uin
				GStreamOperations.gg_writeInt(out, baku_session.g_number); // zapisujemy numer gg
				//pass
				if (baku_session.g_pass==null) // pobieramy dlugosc hasla
					pass_len = 0;
				else
					pass_len = baku_session.g_pass.length();
				if(!baku_session.save_pass)
					pass_len=0;
				GStreamOperations.gg_writeInt(out, pass_len);
				if (pass_len>0) { // jezeli haslo >0 to zapisujemy
					//baku_session.g_pass+="0";
					//zapisujemy haslo do pliku
					GStreamOperations.gg_writeString(out, encryptString(baku_session.g_pass)); 
				}
				// status
				GStreamOperations.gg_writeInt(out, baku_session.g_def_status);
				int descr_len;
				if (baku_session.g_def_description == null)
					descr_len = 0;
				else
					descr_len = baku_session.g_def_description.length();
				//zapisujemy dlugosc opisu
				GStreamOperations.gg_writeInt(out, descr_len);
				//zapisujemy opis
				if(descr_len>0)
					GStreamOperations.gg_writeString(out, encryptString(baku_session.g_def_description));
				out.close();
			}
			catch(FileNotFoundException e) {
				System.out.println("SAVE: can't save");
				return -1;
			}
			catch (UnsupportedEncodingException e) {
				System.out.println("SAVE: can't save");
				return -1;
			}
			catch (IOException e) {
				System.out.println("SAVE: can't save: I/O");
				return -1;
			}
			System.out.println("SAVE: config saved");
		}
			return 0; //operacja udana
	}
	
	/**
	 * czytamy config
	 * @return zwracamy intem czy czytanie sie powiodlo czy nie
	 */
	public int readConfig() {
		try {
			File conf = new File(".gg/config");
			if (!conf.isFile()) // jezeli plik z configiem nie istnieje to wychodzimy
				return -1;
			
			// otwieramy bufor wejsciowy
			//BufferedReader in = new BufferedReader(new InputStreamReader());
			BufferedInputStream in = new BufferedInputStream( new FileInputStream(conf));
			
			// czytamy numer gg
			baku_session.g_number = GStreamOperations.gg_readInt(in);
			// dlugosc hasla
			int pass_len = 0;
			pass_len = GStreamOperations.gg_readInt(in);
			if(pass_len>0) { // jezeli haslo >0 to czytamy je
				baku_session.g_pass = unencryptString(GStreamOperations.gg_readString(in, pass_len));
				System.out.println(baku_session.g_pass);
				baku_session.save_pass = true;
			}
			//czytamy status
			baku_session.g_def_status = GStreamOperations.gg_readInt(in);
			int descr_len = 0;
			//czytamy dlugosc opisu
			descr_len = GStreamOperations.gg_readInt(in);
			if (descr_len>0) { 
				baku_session.g_def_description = unencryptString(GStreamOperations.gg_readString(in, descr_len));
				//System.out.println(baku_session.g_def_description);
			}
			in.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("SAVE: can't save");
			return -1;
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("SAVE: can't save");
			return -1;
		}
		catch (IOException e) {
			System.out.println("SAVE: can't save: I/O");
			return -1;
		}
		return 0; //operacja udana
	}
	
	/**
	 * zapisujemy haslo tak zeby go nie bylo widac
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
	 * zamieniamy zapisany ciag na string
	 * @param str
	 * @return
	 */
	private String unencryptString(String str) {
		char [] tmp = str.toCharArray();
		for(int i=0; i<tmp.length; i++)
			tmp[i]+=30;
		return new String(tmp);
	}
	
}

