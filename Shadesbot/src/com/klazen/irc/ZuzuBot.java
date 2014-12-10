package com.klazen.irc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public class ZuzuBot extends MyBot {
	Map<String,User> zuzuMap;

	String userFile;
	
	public ZuzuBot(String nick, String password, String URL, int port) throws NickAlreadyInUseException, IOException, IrcException {
		super(nick, password, URL, port);
		
		System.out.println("Init");
		zuzuMap = new HashMap<>(100);
		userFile = null;
	}
	
	public ZuzuBot(String nick, String password, String URL, int port, String userFile) throws NickAlreadyInUseException, IOException, IrcException, ClassNotFoundException {
		this(nick, password, URL, port);
		
		try {
			loadUsers(userFile);
			this.userFile = userFile;
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load from file: "+userFile);
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		System.out.println("#"+channel+" "+sender+": "+message);
		
		User user = getUser(sender);
		user.setZuzus(user.getZuzus()+1);
		System.out.println("Gave "+sender+" one zuzu, he has " + user.getZuzus() + " now OpieOP");
	}
	
	/**
	 * Call this when the IRC bot needs to clean up and close.
	 */
	public void onClose() {
		System.out.println("Closing...");
		try {
			if (userFile != null) saveUsers(userFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a user from the list
	 * @param username
	 * @return
	 */
	private User getUser(String username) {
		User user = zuzuMap.get(username);
		if (user==null) user = User.createEmpty(username);
		return user;
	}
	
	public void loadUsers(String filename) throws IOException, ClassNotFoundException {
		System.out.println("Loading users...");
		synchronized (zuzuMap) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename)))  {
				zuzuMap = (Map<String,User>) ois.readObject();
			}
		}
		System.out.println("Load completed.");
	}
	
	private void saveUsers(String filename) throws IOException {
		System.out.println("Saving users...");
		synchronized (zuzuMap) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))  {
				oos.writeObject(zuzuMap);
			}
		}
		System.out.println("Save completed.");
	}

}
