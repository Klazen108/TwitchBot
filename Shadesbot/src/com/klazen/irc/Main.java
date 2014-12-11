package com.klazen.irc;
import java.io.IOException;

import javax.swing.JFrame;

import org.jibble.pircbot.IrcException;


public class Main extends JFrame {
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		
	}
	
	ZuzuBot bot;
	Runnable ircThread;
	
	public Main() {
		super("Pomf Pomf Kappa b Now Let's All Post FrankerZ (tm) 2014..... KID");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        bot.disconnect();
		    }
		});
 
		try {
			bot = new ZuzuBot("Tanasinn69","oauth:q4n4z79oy47wbibf8edppxxzsceuga","irc.twitch.tv",443, "zuzuFile.txt");
	        //bot.setVerbose(true);
	        bot.joinChannel("#klazen108");
	        bot.sendMessage("#klazen108", "HeyGuys");
	        System.out.println("I <3 Klazen");
		} catch (IOException | IrcException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("exit constr");
	}
}
