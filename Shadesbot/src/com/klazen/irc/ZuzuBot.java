package com.klazen.irc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;

public class ZuzuBot extends ListenerAdapter {
	Map<String,ZUser> zuzuMap;

	String userFile;
	
	PircBotX bot;
	
	int duelAmt;
	
	boolean duelOn = false;
	boolean cd_ready = true;
	ArrayList<ZUser> duelists;
	ArrayList<Integer> rolls;
	int win = 0;
	int pot = 0;
	ZUser winner;
	
	Channel channel;
	
	OutputChannel chan;
	
	
	public void setBot(PircBotX bot) {
		this.bot = bot;
		channel = bot.getUserChannelDao().getChannel("#klazen108");
		chan = new OutputChannel(bot,channel);
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
//		timer.schedule(new ZuzuTask(), 10000, 10000);
		
		System.out.println("init");
	}
	
	public void onMessage(MessageEvent event) {
		String message = event.getMessage();
		channel = event.getChannel();
		User sender = event.getUser();
		Pattern p = Pattern.compile("!duel (\\d+)");
		Matcher m = p.matcher(event.getMessage());
		if(m.matches()){
			String duelm = m.group(1);
			duelAmt = Integer.parseInt(duelm);
		}	
		
		System.out.println(channel.getName()+" "+sender.getNick()+": "+message);
		if(m.matches() &&  channel.isOp(sender)){
				duel(event,duelAmt);
			}
		
		if(message.contentEquals("!join")){
			join(sender);
		}
		
		ZUser user = getUser(sender.getNick());
		if(message.contentEquals("!zuzus")){
			chan.message(outputname(user.getUsername()) + " has " + user.getZuzus() + " Zuzus!");
		}
		user.addZuzus(1);
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
	
	public void duel (MessageEvent event, int number){
		if(!duelOn && cd_ready){
			duelists = new ArrayList<ZUser>();
			rolls = new ArrayList<Integer>();
			chan.message("Starting a duel for " + number + "!");
			duelOn = true;
			cd_ready = false;
			Timer timer = new Timer();
			timer.schedule(new Duel(), 20000);
			Timer timer2 = new Timer();
			timer2.schedule(new Cooldown(), 90000);
		}	
	}
	
	public void join (User i_user){
		if(duelOn){
			ZUser user = getUser(i_user.getNick());
			if(!duelists.contains(user) && user.getZuzus() >= duelAmt){
				duelists.add(user);
				chan.message(outputname(user.getUsername()) + " joined the duel!");
			}else if(user.getZuzus()< duelAmt){
				chan.message(outputname(user.getUsername()) + " don't has enough Zuzus BibleThump");
			}
		}
	}
	
	public String outputname(String name){
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
		
	class ZuzuTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("Giving users zuzus...");
			for (User curUser : bot.getUserChannelDao().getUsers(bot.getUserChannelDao().getChannel("#klazen108"))) {
				ZUser user = getUser(curUser.getNick());
				user.addZuzus(1);
			}
		}
		
	}
	
	class Cooldown extends TimerTask{
		
		public void run(){
			cd_ready = true;
			chan.message("Ready for next duel!");
		}
	}
	
	class Duel extends TimerTask {

		@Override
		public void run() {
			//checks if there a more than 1 duelists
			if(duelists.size() > 1){
				duelOn = false;
				String s = "Rolls: ";
				//calculates rolls for every user and saves them in ArrayList "rolls" and creates one long String for the output
				for(int i = 0; i < duelists.size(); ++i ){
					Random r = new Random();
					int roll = r.nextInt(duelAmt-1) + 1;
					rolls.add(roll);
					String s_roll = String.valueOf(roll);
					if(i != duelists.size()-1)
						s = s.concat(outputname(duelists.get(i).getUsername()) + " (" + s_roll + "), " );
					else
						s = s.concat(outputname(duelists.get(i).getUsername()) + " (" + s_roll + ") " );
						
				}
				chan.message(s);
				
				//checks the highest roll
				for(int i = 0; i < rolls.size(); ++i ){
					 win = rolls.get(i) > win ? rolls.get(i) : win;
				}
				
				//check who had the highest roll
				if(rolls.contains(win)){
					int win_ind = rolls.indexOf(win);
					winner = duelists.get(win_ind);
					chan.message(outputname(winner.getUsername()) + " won with a " + win);
					duelists.remove(win_ind);
					rolls.remove(win_ind);
				}
				
				//calculates the lost Zuzus for every duelist, charges them and creates a long String again for the output
				s = "Lost Zuzus: ";
				for(int i = 0; i < duelists.size(); ++i){
					int charge = win - rolls.get(i);
					pot += charge;
					duelists.get(i).chargeZuzus(charge);
					String s_charge = String.valueOf(charge);
					if(i != duelists.size()-1)
						s = s.concat(outputname(duelists.get(i).getUsername()) + " (-" + s_charge + "), " );
					else
						s = s.concat(outputname(duelists.get(i).getUsername()) + " (-" + s_charge + ") " );
				}
				chan.message(s);
				winner.addZuzus(pot);
				chan.message(outputname(winner.getUsername()) + " won " + pot + " zuzus!");
			}
			//resets all variables for the next duel
			duelOn = false;
			duelists.removeAll(duelists);
			rolls.removeAll(rolls);
			winner = null;
			pot = 0;
			win = 0;
		}
		
	}
}
