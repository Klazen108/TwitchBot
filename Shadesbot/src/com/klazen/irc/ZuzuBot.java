package com.klazen.irc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;

public class ZuzuBot extends ListenerAdapter {
	ZuzuBot zbot;
	
	Map<String,ZUser> zuzuMap;

	String userFile;
	
	PircBotX bot;
	
	int duelAmt = 0;
	
	boolean duelOn = false;
	boolean cd_ready = true;
	ArrayList<ZUser> duelists;
	ArrayList<ZUser> top;
	ArrayList<Integer> rolls;
	ArrayList<ZUser> itemuser;
	int win = 0;
	int pot = 0;
	int lose = 0;
	ZUser winner;
	ZUser loser;
	
	boolean curseon = false;
	
	Channel channel;
	
	OutputChannel chan;
	
	
	public void setBot(PircBotX bot) {
		this.bot = bot;
		channel = bot.getUserChannelDao().getChannel("#zurairofl");
		chan = new OutputChannel(bot,channel);
	}
	
	public ZuzuBot(String userFile) throws ClassNotFoundException, IOException  {
		zbot = this;
		zuzuMap = new HashMap<>(100);
		this.userFile = userFile;
		try {
			loadUsers(userFile);
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load from file: "+userFile);
			System.out.println(e.getLocalizedMessage());
		}
		
		Timer timer = new Timer();
		timer.schedule(new ZuzuTask(), 900000, 900000);
		
		System.out.println("init");
	}
	
	//if a message was sent, this is called
	public void onMessage(MessageEvent event) {
		String message = event.getMessage();
		channel = event.getChannel();
		User sender = event.getUser();
		ZUser user = getUser(sender.getNick());
		Pattern p = Pattern.compile("!duel (\\d+)");
		Matcher m = p.matcher(event.getMessage());
		if(m.matches() && duelAmt == 0 && channel.isOp(sender) && !duelOn){
			String duelm = m.group(1);
			duelAmt = Integer.parseInt(duelm);
		}	
		
		System.out.println(channel.getName()+" "+sender.getNick()+": "+message);
		if(m.matches() &&  channel.isOp(sender) && duelAmt >= 50 && !duelOn){
				duel(event,duelAmt);
			}else if(message.contains("!duel") && channel.isOp(sender) && !duelOn){
				chan.message("The minimum amount for duel is 50!");
				duelAmt = 0;
			}
		
//		if(message.contentEquals("!top5")){
//			top = new ArrayList(zuzuMap.keySet());
//			Collections.sort(top,sorttop);
//			String s = "Top 5 Zuzu-Collectors: ";
//			for (int i = 1 ; i <= 5; ++i){
//				String candidate = outputname(top.get(i).getUsername());
//				String c_zuzu = String.valueOf(top.get(i).getZuzus());
//				String place = String.valueOf(i);
//				if(i == 5)
//					s = s.concat(place + ". " + candidate + " (" + c_zuzu +")  ");
//				else
//					s = s.concat(place + ". " + candidate + " (" + c_zuzu +") | ");
//			}
//			chan.message(s);
//		
//		}
		
		if (message.contentEquals("!shop") && channel.isOp(sender)){
			chan.message("To buy an item type '!buy *upgradename*'");
			chan.message("Permanent upgrades: *bronze*-status (5000) | *silver*-status (10000) | *gold*-status (15000) | *platinum*-status (20000)");
			chan.message("Duel items: Rigged *dice* (100) | Protective *shield* (200) | Book of *curse* (300)");
		}
		
		//lists the inventory of the user
		if(message.contentEquals("!items")){
			if(!user.hasshield && !user.hascurse && !user.hasr_dice){
				chan.message(outputname(user.getUsername()) + " don't has any items");
			} else{
				String s = outputname(user.getUsername()) + "'s" + " inventory: ";
				if(user.hasshield && user.hascurse){
					s = s.concat("Shield | ");
				}else if(user.hasshield && user.hasr_dice){
					s = s.concat("Shield | ");
				}else if(user.hasshield){
					s = s.concat("Shield");
				}
				if(user.hascurse && user.hasr_dice){
					s = s.concat("Book of curses | ");
				}else if(user.hascurse){
					s = s.concat("Book of curses");
				}
				if(user.hasr_dice){
					s = s.concat("Rigged dice");
				}
				chan.message(s);
			}
		}
		//buys a dice for the user
		if(message.contentEquals("!buy dice") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");	
			}else if(message.contentEquals("!buy dice")){
			if(user.getZuzus() >= 100){
				if (!user.hasr_dice){
					user.hasr_dice = true;
					user.chargeZuzus(100);
					chan.message(outputname(user.getUsername()) +" bought a rigged dice! OneHand");
				}else{
					chan.message(outputname(user.getUsername()) +" has already a Rigged dice.");
				}
			}else{
				chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
			}
		}
		
		//buys a book of curses for the user
		if(message.contentEquals("!buy curse") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");	
			}else if(message.contentEquals("!buy curse")){
				if(user.getZuzus() >= 300){
					if(!user.hascurse){
						user.hascurse = true;
						user.chargeZuzus(300);
						chan.message(outputname(user.getUsername()) +" bought a book of curses! KZskull");
					}else{
						chan.message(outputname(user.getUsername()) +" has already a book of curses.");
					}
				}else{
					chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
				}
			}
		
		//buys a rigged dice for the user
		if(message.contentEquals("!buy shield") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");	
			}else if(message.contentEquals("!buy shield")){
					if(user.getZuzus() >= 200){
						if(!user.hasshield){
							user.hasshield = true;
							user.chargeZuzus(200);
							chan.message(outputname(user.getUsername()) +" bought a protective shield! BloodTrail");
						}else{
							chan.message(outputname(user.getUsername()) +" has already a protective shield.");
						}	
					}else{
						chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
					}
				}	
			
		//uses the dice for the duelist;
		//adds the duelists also to the itemuser list if
		//he's not part of it yet
		if(message.contentEquals("!use dice") && duelOn && duelists.contains(user) && user.hasr_dice){
				if(!itemuser.contains(user)){
					itemuser.add(user);
				}
				user.hasr_dice = false;
				user.usedr_dice = true;
				chan.message(outputname(user.getUsername()) +" took something out of his pocket.");
		}
		//uses the curse for the duelist
		//adds the duelists also to the itemuser list if
		//he's not part of it yet
		if(message.contentEquals("!use curse") && duelOn && duelists.contains(user) && user.hascurse){
			if(!curseon){
				if(!itemuser.contains(user)){
					itemuser.add(user);
				}
				user.hascurse = false;
				user.usedcurse = true;
				curseon = true;
				chan.message(outputname(user.getUsername()) +" cursed the duel! EvilFetus");
			}else{
				chan.message("The duel is already cursed.");
			}
		}
		
		//uses the shield for the duelist
		//adds the duelists also to the itemuser list if
		//he's not part of it yet
		if(message.contentEquals("!use shield") && duelOn && duelists.contains(user) && user.hasshield){
			if(!itemuser.contains(user)){
				itemuser.add(user);
			}
//			user.hasshield = false;
			user.usedshield = true;
			chan.message(outputname(user.getUsername()) +" hides behind his shield.");
		}
		
		//buy bronze
		if(message.contentEquals("!buy bronze") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");
		}
		if(message.contentEquals("!buy bronze") && !duelOn){
			if(user.status >= 1)
				chan.message(outputname(user.getUsername()) + " has already " + user.getStatus() + 
				" status. No need to downgrade bro! FrankerZ");
			else if(user.getZuzus() < 5000)
					chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
			else{
				user.chargeZuzus(5000);
				user.setStatus(1);
				chan.message(outputname(user.getUsername()) + " has acquired " + user.getStatus() + " status! FrankerZ");
			}
		}
	
		//buy silver
		if(message.contentEquals("!buy silver") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");
		}
		if(message.contentEquals("!buy silver") && !duelOn){
			if(user.status >= 2)
				chan.message(outputname(user.getUsername()) + " has already " + user.getStatus() + 
				" status. No need to downgrade bro! FrankerZ");
			else if((user.status == 0 && user.getZuzus() < 10000) || (user.status == 1 && user.getZuzus() < 5000))
					chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
			else{
				user.chargeZuzus(user.status == 1 ? 5000 : 10000);
				user.setStatus(2);
				chan.message(outputname(user.getUsername()) + " has acquired " + user.getStatus() + " status! MVGame");
			}
		}
		
		//buy gold
		if(message.contentEquals("!buy gold") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");
		}
		
		if(message.contentEquals("!buy gold") && !duelOn){
			if(user.status >= 3)
				chan.message(outputname(user.getUsername()) + " has already " + user.getStatus() + 
				" status. No need to downgrade bro! FrankerZ");
			else if((user.status == 0 && user.getZuzus() < 15000) || (user.status == 1 && user.getZuzus() < 10000) ||
					(user.status == 2 && user.getZuzus() < 5000))
					chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
			else{
				user.chargeZuzus(user.status == 1 ? 10000 : user.status == 2 ? 5000 : 15000);
				user.setStatus(3);
				chan.message(outputname(user.getUsername()) + " has acquired " + user.getStatus() + " status! PogChamp");
			}
		}
		
		//buy platinum
		if(message.contentEquals("!buy platinum") && duelOn){
			chan.message("The shop is closed! Please wait until the duel is over.");
		}
		if(message.contentEquals("!buy platinum") && !duelOn){
			if(user.status == 4)
				chan.message(outputname(user.getUsername()) + " has already " + user.getStatus() + 
				" status. Are you drunk? DansGame");
			else if((user.status == 0 && user.getZuzus() < 20000) || (user.status == 1 && user.getZuzus() < 15000) ||
					(user.status == 2 && user.getZuzus() < 10000) || (user.status == 3 && user.getZuzus() < 5000))
					chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
			else{
				user.chargeZuzus(user.status == 1 ? 15000 : user.status == 2 ? 10000 : user.status == 3 ? 5000 : 20000);
				user.setStatus(4);
				chan.message(outputname(user.getUsername()) + " has acquired " + user.getStatus() + " status! Kreygasm");
			}
		}
		
		if(message.contentEquals("!join")){
			join(sender);
		}
		
		if(message.contentEquals("!info")){
			chan.message("Everything what you need to know about me! http://pastebin.com/FYQsegjJ");
		}
		
		if(message.contentEquals("!status")){
			chan.message(outputname(user.getUsername()) + " has " + user.getStatus() + " status.");
		}
		
		if(message.contentEquals("!zuzus")){
			chan.message(outputname(user.getUsername()) + " has " + user.getZuzus() + " Zuzus!");
		}
		
        //When someone says hello, respond with Hello World
        if (event.getMessage().startsWith("?helloworld"))
                event.respond("Hello world!");
	};
	
	
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
	ZUser getUser(String username) {
		ZUser user = zuzuMap.get(username);
		if (user == null) {
			user = new ZUser(username);
			zuzuMap.put(username, user);
		}
		return user;
	}
	
	@SuppressWarnings("unchecked")
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
	
	void saveUsers(String filename) throws IOException {
		System.out.println("Saving users...");
		synchronized (zuzuMap) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))  {
				oos.writeObject(zuzuMap);
			}
		}
		System.out.println("Save completed.");
	}
	
	//if a duel is started, it creates an ArrayList for duelists, for rolls and for itemusers
	//which will be filled later on;
	//start the duel in 30 seconds and resets the duel cooldown in 2:30 minutes
	public void duel (MessageEvent event, int number){
		if(!duelOn && cd_ready){
			duelists = new ArrayList<ZUser>();
			rolls = new ArrayList<Integer>();
			itemuser = new ArrayList<ZUser>();
			chan.message("Starting a duel for " + number + "!");
			duelOn = true;
			cd_ready = false;
			Timer timer = new Timer();
			timer.schedule(new Duelstart(), 30000);
//			if(curseon){
//				timer.schedule(new CursedDuel(zbot), 30000);
//			}else{
//				timer.schedule(new Duel(zbot), 30000);
//			}
			Timer timer2 = new Timer();
			timer2.schedule(new Cooldown(), 150000);
		}	
	}
	
	//if a curse was used during the 30 second preparation
	//start a cursed-duel, otherwise a normal one
	class Duelstart extends TimerTask{

		@Override
		public void run() {
			if(curseon){
				new CursedDuel(zbot);
			}else{
				new Duel(zbot);	
			}		
		}	
	}
	
	//inserts the user into the duelists list if he has enough zuzus and is not part of the duel yet
	public void join (User i_user){
		if(duelOn){
			ZUser user = getUser(i_user.getNick());
			if(!duelists.contains(user) && user.getZuzus() >= duelAmt){
				duelists.add(user);
//				chan.message(outputname(user.getUsername()) + " joined the duel!");
			}else if(user.getZuzus()< duelAmt){
				chan.message(outputname(user.getUsername()) + " doesn't have enough Zuzus BibleThump");
			}
		}
	}
	
	//capitalizes the first letter of the string
	public String outputname(String name){
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
		
	//give every user all 15 minutes 5 zuzu or more (depending on his status-upgrade) 
	//and saves the list
	class ZuzuTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("Giving users zuzus...");
			for (User curUser : bot.getUserChannelDao().getUsers(bot.getUserChannelDao().getChannel("#zurairofl"))) {
				ZUser user = getUser(curUser.getNick());
				user.addZuzus(user.status == 1 ? 7 : user.status == 2 ? 9 :
					user.status == 3 ? 12 : user.status == 4 ? 15 : 5);
			}
		
			try {
				if (userFile != null) saveUsers(userFile);
			} 	catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//resets the cooldown for the next duel, clears the itemuser list and and
	//resets the cursed-duel modifier
	class Cooldown extends TimerTask{
		
		public void run(){
			curseon = false;
			itemuser.removeAll(itemuser);
			cd_ready = true;
			chan.message("Ready for next duel!");
		}
	}
	
	Comparator<ZUser> sorttop = new Comparator<ZUser>() {

		public int compare(ZUser zuzu1, ZUser zuzu2) {

			return zuzu2.getZuzus() - zuzu1.getZuzus();
		}
	};
}
