package Baku;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import sun.misc.ASCIICaseInsensitiveComparator;

import GConnection.*;

public class MainWindow extends JFrame implements GDefinitions {
	public GConnectionKeeper connection_keeper = new GConnectionKeeper();
	public BakuSession baku_session = new BakuSession(); //GManager uzywa tego
	public GUsers gusers;								// aktualna lista userow
	
	private JFrame parent = this; // dla okienek dialogowych
	private GConnection g;
	private GManager gmg = new GManager(this);
	private GListener gl = new GListener(gmg);
	private GConfig config = new GConfig(baku_session);
	private Container c = getContentPane();
	private Box b = Box.createVerticalBox();
	private GChooseStatus gcstatus;
	private boolean disconnected = false; //informacja czy jestesmy aktualnie poloczeni
	
	public GChats chats = new GChats(parent);
		
	public MainWindow() {
		super("Baku");
		setBounds(200, 0, 300, 500);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		addMenu();
		addComponents();
	}
	
	/**
	 * dodajemy gorne menu
	 */
	private void addMenu() {
		JMenuBar menu = new JMenuBar();
		setJMenuBar(menu);
		
		JMenu baku = new JMenu("Baku");
		menu.add(baku);
		
		JMenuItem configure = new JMenuItem("Configure");
		baku.add(configure);
		configure.setMnemonic(KeyEvent.VK_C);
		configure.setToolTipText("Konfiguracja");
		configure.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		configure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showConfigDialog();
			}
		    
		});
		
		JMenu userlista = new JMenu("Userlist...");
		
			JMenuItem userlist_add_user = new JMenuItem("Add user...");
				baku.add(userlist_add_user);
				userlist_add_user.setMnemonic(KeyEvent.VK_C);
				userlist_add_user.setToolTipText("Dodawanie uzytkownika do listy");
				userlist_add_user.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
				userlist_add_user.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						/*
						 * 
						 * 
						 *    tutaj dopisze dodawanie userow ;)
						 * 
						 * 
						 * 
						 */
					
				}    
				});
			userlist_add_user.setEnabled(false);
			userlista.add(userlist_add_user);
			
			userlista.addSeparator();

		
			JMenuItem userlist_import_server = new JMenuItem("GGServer - export");
			baku.add(userlist_import_server);
			userlist_import_server.setMnemonic(KeyEvent.VK_E);
			userlist_import_server.setToolTipText("Export z serwera");
			userlist_import_server.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
			userlist_import_server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				g.userlist(null, false);		// import listy
			}    
			});
			userlista.add(userlist_import_server);

			JMenuItem userlist_export_server = new JMenuItem("GGServer - import");
			baku.add(userlist_export_server);
			userlist_export_server.setMnemonic(KeyEvent.VK_I);
			userlist_export_server.setToolTipText("Export na serwer");
			userlist_export_server.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
			userlist_export_server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				g.userlist(gusers, true);		// export listy
			}    
			});
			userlista.add(userlist_export_server);
			
			JMenuItem userlist_export_remove = new JMenuItem("GGServer - remove...");
			baku.add(userlist_export_remove);
			userlist_export_remove.setMnemonic(KeyEvent.VK_R);
			userlist_export_remove.setToolTipText("Remove list from server");
			userlist_export_remove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
			userlist_export_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				g.userlist(null, true);		// usuniecie listy
			}    
			});
			userlista.add(userlist_export_remove);
			
			
			userlista.addSeparator();
			
			JMenuItem userlist_export_ggfile = new JMenuItem("File - export");
			baku.add(userlist_export_ggfile);
			userlist_export_ggfile.setMnemonic(KeyEvent.VK_X);
			userlist_export_ggfile.setToolTipText("Export do pliku w formacie GG");
			userlist_export_ggfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
			userlist_export_ggfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser wplik = new JFileChooser();
			    int ot = wplik.showSaveDialog(wplik);
			    if (ot == JFileChooser.APPROVE_OPTION) {
			    	gusers.saveToFileGGFormat(""+wplik.getSelectedFile(), gusers.getList());
			    	g.send_list(gusers.getListWithUINSOnly());
			    	
			    } else if (ot == JFileChooser.CANCEL_OPTION){
			    	wplik.cancelSelection();
			    }
				
			}    
			});
			
			JMenuItem userlist_import_ggfile = new JMenuItem("File - import");
			baku.add(userlist_export_ggfile);
			userlist_import_ggfile.setMnemonic(KeyEvent.VK_M);
			userlist_import_ggfile.setToolTipText("Import z pliku w formacie GG");
			userlist_import_ggfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
			userlist_import_ggfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser wplik = new JFileChooser();
			    int ot = wplik.showOpenDialog(wplik);
			    if (ot == JFileChooser.APPROVE_OPTION) {
			    	gusers.loadFromFileGGFormat(""+wplik.getSelectedFile());
			    	g.send_list(gusers.getListWithUINSOnly());
			    	gusers.saveToFile("./.gg/user-1", gusers.getList());						// po odczytaniu zapamietujemy
			    	//gusers.saveToFile("./.gg/bb-"+baku_session.g_number, gusers.getList());		// po odczytaniu zapamietujemy #2
			    	// ^^ do poprawienia 
			    } else if (ot == JFileChooser.CANCEL_OPTION){
			    	wplik.cancelSelection();
			    }
				
				
				
				
			}    
			});
			userlista.add(userlist_import_ggfile);	
			userlista.add(userlist_export_ggfile);
			
			userlista.addSeparator();

			JMenuItem userlist_clearall = new JMenuItem("Clear userlist");
			baku.add(userlist_clearall);
			userlist_clearall.setMnemonic(KeyEvent.VK_C);
			userlist_clearall.setToolTipText("Kasuje liste userow");
			userlist_clearall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
			userlist_clearall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gusers.clearList();	
			}    
			});			
			userlista.add(userlist_clearall);
		
		
		
		menu.add(userlista);
		
               
        JMenuItem exit = new JMenuItem("Exit");
        baku.add(exit);
        exit.setMnemonic(KeyEvent.VK_E);
        exit.setToolTipText("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
			    System.exit(0);
			}
		});
	}	
	
	/**
	 * pozostale elementy
	 */
	private void addComponents() {
		//combo do zmian statusow
		gcstatus = new GChooseStatus(parent, baku_session, true, false);
		gusers = new GUsers(parent, baku_session, g, true, 0);
		
		b.add(gcstatus);
		b.add(gusers);
		
		c.add(BorderLayout.SOUTH, b);
		
		gcstatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GChooseStatus gc = (GChooseStatus) arg0.getSource();
				if(gc.listen) {
					gc.actionPerformed(arg0);
					if(disconnected) { //jezeli jestesmy rozlaczeni to nalezy sie polaczyc przy zmianie statusu
						//ustawiamy defaultowe statusy z ktorych korzysta makeGConnection();
						baku_session.g_def_status = baku_session.g_status; 
						baku_session.g_def_description = baku_session.g_description;
						makeGConnection();
						disconnected = false;
					}
					else {
						//jezeli zmieniamy status na niedostepny lub na niedostepny opis
						if(baku_session.g_status == status_not_avail || baku_session.g_status == status_not_avail_descr) {
							disconnected = true;
							connection_keeper.keepConnection(null);
						}
						g.change_status(baku_session.g_status, baku_session.g_description);
					}
				}
			}
		});
	}

	
	/*
	 * numerki do testow: 9719655 6811499 8868278 9963812
	 */
	public void gg() {
		config.readConfig();
		//gusers.loadFromFile("./.gg/bb-"+baku_session.g_number);				// po odczytaniu listy userow spr zapisane kontakty

		//System.out.println(baku_session.g_def_description);
		if (baku_session.g_number==-1 || baku_session.g_pass==null) {
			showConfigDialog();
		}
		if (baku_session.g_number!=-1 && baku_session.g_pass!=null)
			makeGConnection();
	}
	
	//test w tej funkcji bedzie trzeba wstawic zmiane statusu pokazywanego tzn zeby bylo widac ze polaczony itp
	public void ggConnected() {
		//mieniamy zaznaczony na dole status zeby bylo wiadomo ze sie polaczylismy
		showStatus(GStatus.getStatusGI(baku_session.g_def_status));
		//wlanczamy podtrzymywanie polaczenia
		connection_keeper.keepConnection(g);
		
		//g.change_status(status_avail_descr, "rasta komunikator :)");
		//gusers.addUser(new ListEntry("Iza;;abellina;abellina;;;5897524;;0;;0;;0;"));
		
		g.send_list(gusers.getList());
		
		//gusers.update();
		
		//g.userlist(null, false);				// import
		//g.send_list(gusers.getList());
		

		

		
		
		chats.addChat(new ListEntry(";;ski;ski;;;4337053;;0;;0;;0;"));
		chats.addChat(new ListEntry(";;bot;bot;;;3217426;;0;;0;;0;"));
		chats.addChat(new ListEntry("misiek;;misiek;misiek;;;2690531;;0;;0;;0;"));
		
		//g.send_msg(2690531, "jooooooooooollllllll :P", 123, msgclass_chat);
		//g.userlist(al, true);
		//g.user_notify(new ListEntry(1864687), false);
		//g.change_status(status_busy_descr, "abcdefghaijklmnoprstuwxyz");
		
		//g.send_msg(2690531, "bartek <ganja>", 123, msgclass_chat);
		//g.send_msg(4337053, "<ganja> baku test, jak to czytasz to znaczy ze juz wiesz ze napisalem wysyalnie wiadomosci", 123, msgclass_chat);
		//g.send_msg(6811499,"test",123, msgclass_chat);
	}

// metody uzywane rownierz przez GManager: 
	
	public void showConfigDialog() {
		ConfigDialog cf_dlg = new ConfigDialog(parent, baku_session, config, parent.getX()-10, parent.getY()+140);
		cf_dlg.setVisible(true);
	}
	
	public void showStatus(int index) {
		gcstatus.dontListen();
		//System.out.println("showing status "+index);
		gcstatus.setSelectedIndex(index);
		gcstatus.Listen();
	}
	
	/**
	 * tutaj nawiazujemy polaczenie w rzypadku niepowodzenia probujemy do skutku
	 *
	 */
	public void makeGConnection() {
			new Thread("ConnectionMaker") {
			public void run() {
				while (true) {
					int c;
					System.out.println("CONNECT: ");
					gl.setConnection(null);
					g = new GConnection(baku_session.g_number, baku_session.g_pass);
					if ((c=g.connect())==-1) { //jezeli nie dostalismy danych wolnego servera probujemy ponownie
						System.out.println("CONNECT:  ERROR: Can't get server to connect");
						continue;
					}
					else if(c!=0)
						for(int i=0; i<10; i++) { //10 prob poloczenia do servera jesli sie nie uda to pobieramy dane nowego servera
							if (g.connect()==0) {
								System.out.println("CONNECT:  Connected to server");
								g.login(baku_session.g_def_status, baku_session.g_def_description); //probujemy sie logowac
								gl.setConnection(g); // ozywiamy nasluchiwanie
								chats.setConnection(g);
								synchronized (gl) {
									gl.notify();
								}
								return;
							} 
							System.out.println("CONNECT:  ERROR: Can't connect to server");
						}
					else {
						System.out.println("CONNECT:  Connected to server");
						g.login(baku_session.g_def_status, baku_session.g_def_description); //probujemy sie logowac
						gl.setConnection(g); // ozywiamy nasluchiwanie
						chats.setConnection(g); // ustawiamy aktualne polaczenie menadzerowi chatow
						//synchronized (gl) {
						//	gl.notify();
						//}
						return;
					}
					//try { sleep(50); }
					//catch (InterruptedException e) {}
				}
			}
		}.start();
	}
	
}

	

class BakuSession {
	public int g_number = -1;
	public String g_pass;
	public boolean save_pass = false;
	public int g_def_status = -1;
	public int g_status = -1;
	public String g_def_description;
	public String g_description;
}

