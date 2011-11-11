package Baku;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import GConnection.GConnection;
import GConnection.GDefinitions;
import GConnection.ListEntry;
import GMessage.GMessage;
import GMessage.GMessage_message;

public class GChatWindow extends JDialog implements KeyListener {
	private ListEntry user;
	private String chatWith;
	private JTextArea message,
					  chat;
	private GConnection gg;
	private JCheckBox enter;
	private GSerial serial;
	
	public GChatWindow(JFrame parent, ListEntry e, GConnection g, GSerial s) {
		super(); // non modal dialog
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		user = e;
		gg = g;
		serial = s;
		makeWin();
		setVisible(true);
	}
	
	private void makeWin() {
		Box b = Box.createVerticalBox();
		Container content = getContentPane();
		
		if (user.wyswietlane != null && user.wyswietlane != "")
			chatWith = user.wyswietlane;
		else
			chatWith = String.valueOf(user.getUin());
		setTitle("Chat with: " + chatWith);
		pack();
		b.add(Box.createVerticalStrut(5));
		
		message = new JTextArea();
		message.setEditable(false);
		JScrollPane message_scroll = new JScrollPane(message, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		b.add(message_scroll);
		
		b.add(Box.createVerticalStrut(5));
		
		chat = new JTextArea();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chat.requestFocusInWindow();
			}
		});
		chat.addKeyListener(this);
		JScrollPane chat_scroll = new JScrollPane(chat, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		b.add(chat_scroll);
		b.add(Box.createVerticalStrut(1));
		
		JPanel down = new JPanel();
		down.setMaximumSize(new Dimension(2000,20));
		down.setLayout(new BorderLayout());
		
		JButton send = new JButton("Send");
		send.setMaximumSize(new Dimension(80, 15));
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send(chat.getText());
				chat.setText(null);
			}
		});
		down.add(BorderLayout.WEST, send);
		
		enter = new JCheckBox("enter sends message");
		enter.setSelected(true);
		down.add(BorderLayout.EAST, enter);
		
		b.add(down);
		b.add(Box.createVerticalStrut(1));
		
		content.add(b);
		setBounds(0, 0, 420, 450);
		
	}
	
	/**
	 * zwracamy numer gg osoby z ktora chatujemy
	 * @return
	 */
	public int getRecipient() {
		return user.getUin();
	}
	
	/**
	 * dodajemy wiadomosc w gornym okienku
	 * @param sender
	 * @param msg
	 */
	public void addMessage(String msg) {
		//message.setFont(new Font("Serif", Font.BOLD, 16));
		message.append("Me:\n");
		message.append(msg+"\n");
	}
	
	/**
	 * dodajemy przychodzaca wiadomsoc
	 * @param msg
	 */
	public void addMessage(GMessage_message msg) {
		message.append(chatWith+":\n");
		message.append(msg.gmessage+"\n");
	}

	/**
	 * wysylamy wiadomosc
	 * @param msg <-- wiadomosc do wyslania
	 */
	private void send(String msg) {
		gg.send_msg(user.getUin(), msg, serial.getSerial(), GDefinitions.msgclass_chat);
		addMessage(msg);
	}
	
	/**
	 * jezeli wcisniemy enter i jest zaznaczone wysylanie enterem to wiadosmosc jest wysylana
	 */
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER && enter.isSelected()) {
			send(chat.getText());
			chat.setText("");
		}
	}
	public void keyTyped(KeyEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}

}
