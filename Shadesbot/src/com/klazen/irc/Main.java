package com.klazen.irc;
import java.io.IOException;

import javax.swing.JFrame;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;


public class Main extends JFrame {
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		
	}
	
	public Main() {
		super("Pomf Pomf Kappa b Now Let's All Post FrankerZ (tm) 2014..... KID");
		
		try {
	        ZuzuBot bot = new ZuzuBot("Tanasinn69","oauth:q4n4z79oy47wbibf8edppxxzsceuga","irc.twitch.tv",6667);
	        bot.setVerbose(true);
	        bot.joinChannel("#klazen108");
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}
	}
}
