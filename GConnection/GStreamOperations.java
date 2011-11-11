package GConnection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

public class GStreamOperations {
	
	/**
	 * Czytamy z bufora len bajtow i zwracamy jako string
	 * @param len
	 * @return
	 * @throws IOException 
	 */
	public static String gg_readString(BufferedInputStream in, int len) throws IOException {
		byte[] tmp = new byte[len];
		for (int i=0; i<len; i++) {
			int tmp2 = in.read() & 0x000000FF;
			tmp[i] |= tmp2; 
		}
		return new String(tmp);
	}
	/**
	 * Funkcja czyta z bufora bajt i zwraca go jak znak
	 * @param in bufor wejsciowy
	 * @return czyta jedna literke
	 * @throws IOException
	 */
	public static char gg_readChar(BufferedInputStream in) throws IOException {
		return (char) in.read();
	}
	/**
	 * Funkcja odczytuje z budora zmienne typu short
	 * @param in bufor wejsciowy
	 * @return czyta dwa bajty otrzymaja liczbe zwraca jako szorta
	 * @throws IOException
	 */
	
	public static short gg_readShort(BufferedInputStream in) throws IOException {
		short sh = (short)in.read();
		short sh2 = (short)in.read();
		return (short)( sh2 <<8 | sh );
	}
	
	/**
	 * Funkcja czyta kolejne 4 bajty ze strumienia i przeksztaca na inta
	 * @param in bufor wejsciowy
	 * @param offset przesuniecie z bierzacej pozycji w buforze
	 * @return zmienna int czytana z bufora odwrotnie 
	 * @throws IOException 
	 */
	public static int gg_readInt(BufferedInputStream in) throws IOException {
			int seed;
			int[] ibuf = new int[4];
			
			for(int i=0; i<4; i++) {
				ibuf[i] = in.read();
			}
			seed =  (ibuf[3] << 24)  |  (ibuf[2] << 16) | (ibuf[1] << 8) | ibuf[0];			
			return seed;
	}
	
	/**
	 * dropujemy len bajtow
	 * @param in
	 * @param len
	 * @throws IOException
	 */
	public static void gg_drop(BufferedInputStream in, int len) throws IOException {
		int tmp;
		for(int i=0; i<len; i++)
			tmp=in.read();
	}
	
	/**
	 * piszemy do bufora inta wodwrotnej koejnosci
	 * @param out
	 * @throws IOException 
	 */
	public static void gg_writeInt(OutputStream out, int parm) throws IOException {
		int[] a = new int[4];
		int tmp = 0;
		a[0] = parm & 0x000000FF;
		a[1] = ((parm & 0x0000FF00) >>> 8) & 0x000000FF;
		a[2] = ((parm & 0x00FF0000) >>> 16) & 0x000000FF;
		a[3] = ((parm & 0xFF000000) >>> 24) & 0x000000FF;
		for(int i=0; i<4; i++)
			out.write(a[i]);
		//out.flush();
	}
	
	public static void gg_writeShort(OutputStream out, short parm) throws IOException {
		short[] a = new short[2];
		int tmp = 0;
		a[0] = (short) (parm & 0x00FF);
		a[1] = (short) ((parm & 0xFF00) >>> 8);
		for(int i=0; i<2; i++)
			out.write(a[i]);
		//out.flush();
	}
	
	public static void gg_writeByte(OutputStream out, byte parm) throws IOException{
		out.write(parm);
	}
	
	/**
	 * wpisujemy string do bufora
	 * @throws IOException 
	 */ 
	public static void gg_writeString(OutputStream out, String str) throws IOException {
		int len = str.length();
		byte[] c = str.getBytes();
		out.write(c);
	}
}
