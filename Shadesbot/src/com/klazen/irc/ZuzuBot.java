package com.klazen.irc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

public class ZuzuBot extends ListenerAdapter {
	Map<String,ZUser> zuzuMap;

	String userFile;
	
	PircBotX bot;
	
	public void setBot(PircBotX bot) {
		this.bot = bot;
	}
	
	public ZuzuBot(String userFile) throws ClassNotFoundException, IOException  {
		zuzuMap = new HashMap<>(100);
		
		this.userFile = userFile;
		try {
			loadUsers(userFile);
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load from file: "+userFile);
			System.out.println(e.getLocalizedMessage());
		}
		
		Timer timer = new Timer();
		timer.schedule(new ZuzuTask(), 10000, 10000);
		
		System.out.println("init");
	}
	
	public void onMessage(MessageEvent event) {
		String message = event.getMessage();
		Channel channel = event.getChannel();
		User sender = event.getUser();
		
		System.out.println(channel.getName()+" "+sender.getNick()+": "+message);
		if(message.contentEquals("!duel") && channel.isOp(sender)){
			duel(event);
		}
		
		ZUser user = getUser(sender.getNick());
		user.setZuzus(user.getZuzus()+1);
		System.out.println("Gave "+sender.getNick()+" one zuzu, he has " + user.getZuzus() + " now OpieOP");
		
        //When someone says hello, respond with Hello World
        if (event.getMessage().startsWith("?helloworld"))
                event.respond("Hello world!");
	}
	
	
	/**
	 * Call this when the IRC bot needs to clean up and close.
	 */
	public void onDisconnect() {
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
	private ZUser getUser(String username) {
		ZUser user = zuzuMap.get(username);
		if (user == null) {
			user = new ZUser(username);
			zuzuMap.put(username, user);
		}
		return user;
	}
	
	public void loadUsers(String filename) throws IOException, ClassNotFoundException {
		System.out.println("Loading users...");
		synchronized (zuzuMap) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
				zuzuMap = (Map<String,ZUser>) ois.readObject();
			} catch (FileNotFoundException e) {
				System.out.println("Tried opening saved info, but file was not found. Ignoring.");
				//do nothing if you can't find the file, that's normal for first operation
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
	
	public void duel (MessageEvent event){
		event.respond("Duel!");
	}
	
	class ZuzuTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("Giving users zuzus...");
			for (User curUser : bot.getUserChannelDao().getUsers(bot.getUserChannelDao().getChannel("#klazen108"))) {
				ZUser user = getUser(curUser.getNick());
				user.setZuzus(user.getZuzus()+1);
			}
		}
		
	}
}
