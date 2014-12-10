package com.klazen.irc;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;


public class MyBot extends PircBot {
	
	public MyBot(String nick, String password, String URL, int port) throws NickAlreadyInUseException, IOException, IrcException {
		setName(nick);
        connect(URL,port,password);
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		System.out.println("#"+channel+" "+sender+": "+message);
	}
}
