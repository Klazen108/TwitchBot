package com.klazen.irc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

public class Main extends JFrame {
	ZuzuBot zbot;
	Thread ircthread;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		
	}
	
	public Main() {
		super("Pomf Pomf Kappa b Now Let's All Post FrankerZ (tm) 2014..... KID");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//Let zuzubot know we're shutting down so it can save n stuff
				zbot.onDisconnect();
				//Interrupt the IRC thread when we're closing the window
				ircthread.interrupt();
			}
		});
		

		//Create a runnable task to be passed for another thread so it doesn't lock up the main thread
    	Runnable runnable = new Runnable(){
			@Override
			public void run() {
		        try {
		        	zbot = new ZuzuBot("zuzuFile");
					
			        //Configure what we want our bot to do
			        Configuration configuration = new Configuration.Builder()
			                        .setName("Tanasinn69")
			                        .setServer("irc.twitch.tv", 80)
			                        .setServerPassword("oauth:q4n4z79oy47wbibf8edppxxzsceuga")
			                        .addAutoJoinChannel("#klazen108") 
			                        .addListener(zbot) 
			                        .buildConfiguration();
			
			        //Create our bot with the configuration
			        PircBotX bot = new PircBotX(configuration);
			        //let zuzubot know about the bot so it can use it
			        zbot.setBot(bot);
			        //Connect to the server
					bot.startBot();
		        } catch (IOException | IrcException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		        }
			}
    	};
    	
    	ircthread = new Thread(runnable);
    	ircthread.start();
	}
}
