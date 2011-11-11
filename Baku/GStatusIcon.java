package Baku;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import GConnection.GDefinitions;

/**
 * Ikony statusow
 *
 */
public class GStatusIcon {
	
	GStatusIcon(){}
	
	/**
	 * @param i int statusu
	 * @return Zwraca ikone wybranego statusu
	 */
	
	public ImageIcon getIcon(int i) {	// inne case'y aby sie trzymac jednej numeracji statusow
		switch(i){
		case GDefinitions.status_avail: 		return new ImageIcon("status_img/online.png"); 
		case GDefinitions.status_avail_descr: 	return new ImageIcon("status_img/online_d.png");
		case GDefinitions.status_busy: 			return new ImageIcon("status_img/busy.png");
		case GDefinitions.status_busy_descr: 	return new ImageIcon("status_img/busy_d.png");
		case GDefinitions.status_invisible: 	return new ImageIcon("status_img/invisible.png");
		case GDefinitions.status_invisible_descr: 	return new ImageIcon("status_img/invisible_d.png");
		case GDefinitions.status_not_avail: 	return new ImageIcon("status_img/offline.png");
		case GDefinitions.status_not_avail_descr: 	return new ImageIcon("status_img/offline_d.png");
		}
		return null;
	}
	
	
}