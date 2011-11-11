/*
 * komponenty gadu lista kontatkow combo do wyboru statusu toolbar itp
 * 
 */
package Baku;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GConnection.GDefinitions;
import GConnection.GStatus;


class GChooseStatus extends JComboBox implements ActionListener {
	private GStatusIcons icons = new GStatusIcons();
	private int index = -1;
	private JFrame parent;
	private BakuSession baku_session;
	private boolean isConfig;
	public volatile boolean listen = true; // jezeli chcemy wylaczyc action listener
	/**
	 * @param p - okno rodzic dla okienka dialogowego gdzie wprowadzamy status
	 * @param bk - informacje o bierzacej sesji
	 * @param offline - czy dodawac pola opisow offline do listy wyboru
	 */
	public GChooseStatus(JFrame p, BakuSession bk, boolean offline, final boolean cfg) {
		int n=0;
		int checkStatus = 0;
		parent = p;
		baku_session = bk;
		isConfig = cfg;
		if (offline)
			n = 8;
		else
			n = 6;
		for (int i=0; i<n; i++) {
			addItem(" "+GStatus.getStatusIS(i));
		}
		if (isConfig) {
			checkStatus = baku_session.g_def_status;
		}
		else
			checkStatus = baku_session.g_status;
		if (checkStatus != -1) {
			setSelectedIndex(GStatus.getStatusGI(checkStatus));
		}
		else if (offline)
			setSelectedIndex(6);
		if (isConfig)
			addActionListener(this);
	}
	
	/*
	 * wylaczamy ActionListener
	 */
	public void dontListen() {
		listen = false;
	}
	
	/*
	 * wlaczamy ActionListener
	 */
	public void Listen() {
		listen = true;
	}
	
	/**
	 * gdy dokonamy wyboru
	 */
	public void actionPerformed(ActionEvent arg0) {
		if(listen) {
			index = getSelectedIndex(); //ustawiamy index na numer statusu
			if (index%2==1 && index<8) { // jezeli potrzebny opis to wyswietlamy okienko w celu wpisania opisu
				TypeDescr td = new TypeDescr(parent, 
						parent.getX()-10, 
						parent.getY()+(int)parent.getHeight()/2-40);
				td.setVisible(true);
			}
			//else 
			//	if(isConfig)
			//		baku_session.g_def_description = null;
			//	else
			//		baku_session.g_description = null;
			
			if (isConfig)
				baku_session.g_def_status = GStatus.getStatusIG(index);
			else
				baku_session.g_status = GStatus.getStatusIG(index);
		}
	}
	
	/*
	 * okienko do wprowadzenia statusu
	 */
	class TypeDescr extends JDialog {
		public TypeDescr(JFrame parent, int x, int y) {
			super(parent, "Set description:", true);
			Container cp = getContentPane();
			cp.setLayout(null);
			
			JLabel descr_label = new JLabel("Descr:");
			descr_label.setBounds(2, 2, 50, 20);
			cp.add(descr_label);
			final JTextField descr_field = new JTextField();
			if(isConfig) {
				if (baku_session.g_def_description != null)
					descr_field.setText(baku_session.g_def_description);
			}
			else
				if (baku_session.g_description != null)
					descr_field.setText(baku_session.g_description);
				else
					descr_field.setText(baku_session.g_def_description);
			descr_field.selectAll();
			descr_field.setBounds(55, 2, 250, 20);
			cp.add(descr_field);
			
			JButton ok = new JButton("OK");
			ok.setBounds(130, 30, 60, 20);
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(isConfig)
						baku_session.g_def_description = descr_field.getText();
					else
						baku_session.g_description = descr_field.getText();
					dispose();
				}
			});
			cp.add(ok);
			setBounds(x, y, 320, 90);
		}
	}
	
}


/*
 * okienko gdzie podajemy konfiguracje
 * numer haslo status na wejscie
 */
class ConfigDialog extends JDialog {

	public ConfigDialog(JFrame parent, final BakuSession baku_session, final GConfig config, int x, int y) {
		super(parent, "Baku: Configuration", true);
		final Container cp = getContentPane();
		cp.setLayout(null);
		pack();
		//setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel("Account data:");
		label.setBounds(100,3,120,20);
		cp.add(label);
		
		// NUMER
		JLabel numer_label = new JLabel("GG number:");
		numer_label.setBounds(35, 35, 80, 20);
		cp.add(numer_label);
		final JTextField uin = new JTextField();
		uin.setBounds(145, 35, 100, 20);
		if (baku_session.g_number!=-1)
			uin.setText(Integer.toString(baku_session.g_number));
		cp.add(uin);
		
		// HASLO
		JLabel pass_label = new JLabel("Password:");
		pass_label.setBounds(35, 70, 80, 20);
		cp.add(pass_label);
		final JPasswordField pass = new JPasswordField();
		pass.setBounds(145, 70, 100, 20);
		cp.add(pass);
		if (baku_session.g_pass!=null)
			pass.setText(baku_session.g_pass);
		else if(baku_session.g_number != -1)
				pass.requestFocusInWindow();
		
		final JCheckBox check = new JCheckBox("save");
		check.setBounds(145, 90, 100, 20);
		if(baku_session.save_pass)
			check.setSelected(true);
		cp.add(check);
		
		//default status
		JLabel stat = new JLabel("Default status:");
		stat.setBounds(35, 125, 100, 20);
		cp.add(stat);
		GChooseStatus gc = new GChooseStatus(parent, baku_session, false, true);
		gc.setBounds(145, 125, 140, 20);
		cp.add(gc);
		
		JButton ok = new JButton("OK");
		ok.setBounds(120, 160, 60, 20);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Integer.parseInt(uin.getText());
				}
				catch (NumberFormatException e) {
					uin.setText("");
					return;
				}
				if (check.isSelected())
					baku_session.save_pass = true;
				else
					baku_session.save_pass = false;
				baku_session.g_number = Integer.parseInt(uin.getText());
				baku_session.g_pass = new String(pass.getPassword());
				config.saveConfig();
				dispose();
			}
		});
		cp.add(ok);
		setBounds(x, y, 320, 230);
	}
}

