package com.klazen.irc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public class ZuzuBot extends MyBot {
	Map<String,Integer> zuzuMap;

	public ZuzuBot(String nick, String password, String URL, int port) throws NickAlreadyInUseException, IOException, IrcException {
		super(nick, password, URL, port);
		
		zuzuMap = new HashMap<>(100);
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		System.out.println("#"+channel+" "+sender+": "+message);
		
		modifyZuzus(sender,1);
		System.out.println("Gave "+sender+" one zuzu, he has " + getZuzus(sender) + " now OpieOP");
		
		
	}
	
	private void modifyZuzus(String user, int amount) {
		Integer getVal = zuzuMap.get(user);
		int curAmount = getVal==null?0:getVal;
		curAmount += amount;
		
		zuzuMap.put(user, curAmount);
	}
	
	private int getZuzus(String user) {
		Integer getVal = zuzuMap.get(user);
		return getVal==null?0:getVal;
	}

}
