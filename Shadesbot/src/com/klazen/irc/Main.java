package com.klazen.irc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;

public class Main extends JFrame {
	ZuzuBot zbot;
	Thread ircthread;
	int add;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		
	}
	
	public Main() {
		super("Pomf Pomf Kappa b Now Let's All Post FrankerZ (tm) 2014..... KID");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(470,150);
		
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
    	JButton addz = new JButton("Add Zuzus");
    	JButton chargez = new JButton("Charge Zuzus");
    	JPanel p1 = new JPanel();
    	JPanel p2 = new JPanel();
    	final JTextField name = new JTextField("Name",15);
    	final JTextField name2 = new JTextField("Name",15);
    	final JTextField zuzuadd = new JTextField("Zuzus to add",15);
    	final JTextField zuzucharge = new JTextField("Zuzus to charge",15);
    	p1.add(name);
    	p1.add(zuzuadd);
    	p1.add(addz);
    	p2.add(name2);
    	p2.add(zuzucharge);
    	p2.add(chargez);	
    	add(p1);
    	p1.add(p2);
    	
    	addz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = zuzuadd.getText();
				int toadd = Integer.parseInt(s);
				zbot.getUser(name.getText()).addZuzus(toadd);
				ZUser user = zbot.getUser(name.getText());
				String name = zbot.outputname(user.getUsername());
				zbot.chan.message("Gifted " + name + " " + toadd + " Zuzus!");
			}
		 });
    	
    	chargez.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = zuzucharge.getText();
				int toadd = Integer.parseInt(s);
				zbot.getUser(name2.getText()).chargeZuzus(toadd);
				ZUser user = zbot.getUser(name2.getText());
				String name = zbot.outputname(user.getUsername());
				zbot.chan.message("Charged " + name + " " + toadd + " Zuzus!");
			}
		 });
    	
    	ircthread = new Thread(runnable);
    	ircthread.start();
    	
    	
	}
}
