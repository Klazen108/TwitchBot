package com.klazen.irc;

import java.io.IOException;
import java.util.Random;

public class Duel{
	
	ZuzuBot zbot;
	private boolean r_roll = false;
	
	public Duel(ZuzuBot zbot){
		
		this.zbot = zbot;
		run();
	}
	
	public void run() {
		//checks if there are more than 1 duelists
		if(zbot.duelists.size() > 1){
			zbot.duelOn = false;
			String s = "Rolls: ";
			//calculates rolls for every user and saves them in ArrayList "rolls" and creates one long String for the output
			for(int i = 0; i < zbot.duelists.size(); ++i ){
				Random r = new Random();
				int roll = 0;
				//checks if the duelist used a rigged dice and calculates his roll + 20%
				//and sets the global boolean on true
				if(zbot.duelists.get(i).usedr_dice){
					roll = r.nextInt(zbot.duelAmt-((zbot.duelAmt * 20) / 100)) + 1;
					r_roll = true;
				}else{
					roll = r.nextInt(zbot.duelAmt-1) + 1;
				}
				int bonus = zbot.duelists.get(i).status == 1 ? (roll * 5) / 100 : zbot.duelists.get(i).status == 2 ? (roll * 10) / 100 :
					zbot.duelists.get(i).status == 3 ? (roll * 15) / 100 : zbot.duelists.get(i).status == 4 ? (roll * 20) / 100 : 0;
				roll += bonus;
				
				//makes sure the combined roll of normal + bonus can't be higher than the "duel amount"
				if(roll > zbot.duelAmt)
					roll = zbot.duelAmt;
				
				zbot.rolls.add(roll);
				String s_roll = String.valueOf(roll-bonus);
				
				//checks if the duelists as a rigged dice and adds symbols to the output;
				//resets the global boolean
				if(r_roll){
					s_roll = s_roll.concat("R)");
					r_roll = false;
				}
				String s_bonus = String.valueOf(bonus);
				if (bonus > 0){
					if(i != zbot.duelists.size()-1)
						s = s.concat(zbot.outputname(zbot.duelists.get(i).getUsername()) + " (" + s_roll + "+" + s_bonus + "), " );
					else
						s = s.concat(zbot.outputname(zbot.duelists.get(i).getUsername()) + " (" + s_roll + "+" + s_bonus + ") " );
				}else{
					if(i != zbot.duelists.size()-1)
						s = s.concat(zbot.outputname(zbot.duelists.get(i).getUsername()) + " (" + s_roll + "), " );
					else
						s = s.concat(zbot.outputname(zbot.duelists.get(i).getUsername()) + " (" + s_roll + ") " );
					
					}
			}
			zbot.chan.message(s);
			
			//checks the highest roll
			for(int i = 0; i < zbot.rolls.size(); ++i ){
				zbot.win = zbot.rolls.get(i) > zbot.win ? zbot.rolls.get(i) : zbot.win;
			}
			
			//check who had the highest roll
			if(zbot.rolls.contains(zbot.win)){
				int win_ind = zbot.rolls.indexOf(zbot.win);
				zbot.winner = zbot.duelists.get(win_ind);
				zbot.chan.message(zbot.outputname(zbot.winner.getUsername()) + " won with a " + zbot.win);
				zbot.duelists.remove(win_ind);
				zbot.rolls.remove(win_ind);
			}
			
			//checks the lowest roll
			zbot.lose = zbot.duelAmt;
			for(int i = 0; i < zbot.rolls.size(); ++i ){
				zbot.lose = zbot.rolls.get(i) < zbot.lose ? zbot.rolls.get(i) : zbot.lose;
			}
			
			//checks who had the lowest roll
			if(zbot.rolls.contains(zbot.lose)){
				int lose_ind = zbot.rolls.indexOf(zbot.lose);
				zbot.loser = zbot.duelists.get(lose_ind);
				if(zbot.loser.usedshield){
					zbot.chan.message(zbot.outputname(zbot.loser.getUsername()) + " lost with a " + zbot.lose + " but wields a shield!");
				}else{
					zbot.chan.message(zbot.outputname(zbot.loser.getUsername()) + " lost with a " + zbot.lose);
				}
			}
			//checks if the loser has a shield in usage
			if(zbot.loser.usedshield){
				zbot.pot = ((zbot.win - zbot.lose) * 50) / 100;
				zbot.loser.chargeZuzus(zbot.pot);
				zbot.winner.addZuzus(zbot.pot);
				zbot.chan.message(zbot.outputname(zbot.winner.getUsername()) + " won " + zbot.pot + " zuzus!");
				zbot.loser.hasshield = false;
			}else{
			zbot.pot = zbot.win - zbot.lose;
			zbot.loser.chargeZuzus(zbot.pot);
			zbot.winner.addZuzus(zbot.pot);
			zbot.chan.message(zbot.outputname(zbot.winner.getUsername()) + " won " + zbot.pot + " zuzus!");
			}
		}
		//resets all variables for the next duel
		zbot.duelOn = false;
		zbot.duelists.removeAll(zbot.duelists);
		zbot.rolls.removeAll(zbot.rolls);
		zbot.winner = null;
		zbot.loser = null;
		zbot.pot = 0;
		zbot.win = 0;
		zbot.lose = 0;
		zbot.duelAmt = 0;
		//resets the "use-variables" for every itemuser
		for(int i = 0; i < zbot.itemuser.size(); ++i){
			zbot.itemuser.get(i).usedr_dice = false;
			zbot.itemuser.get(i).usedcurse = false;
			zbot.itemuser.get(i).usedshield = false;
		}
		try {
			if (zbot.userFile != null) zbot.saveUsers(zbot.userFile);
		} 	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


