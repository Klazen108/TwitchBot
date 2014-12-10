package com.klazen.irc;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;


public class Main extends JFrame {
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		
	}
	
	ZuzuBot bot;
	
	public Main() {
		super("Pomf Pomf Kappa b Now Let's All Post FrankerZ (tm) 2014..... KID");
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        bot.onClose();
		    }
		});
		
		try {
	         bot = new ZuzuBot("Tanasinn69","oauth:q4n4z79oy47wbibf8edppxxzsceuga","irc.twitch.tv",6667, "C:\\zuzuFile.txt");
	        bot.setVerbose(true);
	        bot.joinChannel("#klazen108");
	        bot.sendMessage("#klazen108", "HeyGuys");
		} catch (IOException | IrcException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
