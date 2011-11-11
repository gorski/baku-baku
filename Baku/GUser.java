package Baku;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;  
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;

import GConnection.GDefinitions;

import java.io.*;


/**
 * jeden wpis na liscie
 */
public class GUser extends JPanel {
	GStatusIcons sicons = new GStatusIcons();
	GridLayout gl2;
	Font f_nick = new Font("Tahoma", Font.BOLD,  12);
	Font f_desc = new Font("Arial",  Font.PLAIN, 10);
	
	ImageIcon ii = new ImageIcon();
	GStatusIcon gsi = new GStatusIcon();
	
	JLabel nick;
	JLabel descr;

	GUser(JLabel master, int uin, int status, String n, String d){
		
		
		ii  = gsi.getIcon(status);
		gl2 = new GridLayout(2,1);
		super.setLayout(gl2);
		
		nick = new JLabel("  "+n, (ImageIcon) ii, 2);
		nick.setFont(f_nick);
		
		
		super.add(nick);
		
		if (d != "" && d !=null){
			descr = new JLabel(" "+d);
			descr.setFont(f_desc);
			super.add(descr);
		}
	
		
	}
}

	
	class ListenToMe implements ActionListener{
		
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
