package GConnection;

import java.util.HashMap;


/*
 * przetwarzanie statusu 
 */
public class GStatus {
	static String[] status_str = {
		"Online",
		"Online-descr",
		"Busy",
		"Busy-descr",
		"Invisible",
		"Invisible-descr",
		"Offline",
		"Offline-descr",
		"Blocked",
		"Friend"};
	private static int[] status = {
			GDefinitions.status_avail, 
			GDefinitions.status_avail_descr,
			GDefinitions.status_busy,
			GDefinitions.status_busy_descr,
			GDefinitions.status_invisible,
			GDefinitions.status_invisible_descr,
			GDefinitions.status_not_avail,
			GDefinitions.status_not_avail_descr,
			GDefinitions.status_blocked,
			GDefinitions.status_friends_mask};

	/**
	 * Gadu -> String
	 * dostajemy status na podstawie wartosci statusu otrzymanego z pakietu gadu
	 * @param status
	 * @return
	 */
	public static String getStatusGS(int gstatus) {
		int i;
		for(i=0; i<status.length; i++) {
			if (status[i]==gstatus) {
				return status_str[i];
			}
		}
		return null;
	}
	
	/**
	 * Gadu -> int
	 */
	public static int getStatusGI(int gstatus) {
		for(int index=0; index<status.length; index++) {
			if (status[index]==gstatus) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Index -> String
	 * status na podstawie kolejnego numerku statusy numerowane od 1-9
	 * @param i
	 * @return
	 */
	public static String getStatusIS(int index) {
		if (index>=0 && index<=9)
			return status_str[index];
		else 
			return null;
	}
	
	/**
	 * Index -> Gadu
	 * status gadu (taki do pakietu na podstawie numerku)
	 */
	public static int getStatusIG(int index) {
		return status[index];
	}
}
