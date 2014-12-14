package com.klazen.irc;

import java.io.Serializable;

public class ZUser implements Serializable {
	
	String username;
	int zuzus;
	int status = 0;
	boolean hascurse = false;
	boolean usedcurse = false;
	boolean hasr_dice = false;
	boolean usedr_dice = false;
	boolean hasshield = false;
	boolean usedshield = false;

	public int getZuzus() {
		return zuzus;
	}

	public void setZuzus(int zuzus) {
		this.zuzus = zuzus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void addZuzus(int i) {
		this.zuzus += i;
	}

	public void chargeZuzus(int i) {
		this.zuzus -= i;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatus() {
		return status == 0 ? "Normal" : status == 1 ? "Bronze"
				: status == 2 ? "Silver" : status == 3 ? "Gold"
						: status == 4 ? "Platinum" : null;
	}

	public ZUser(String username) {
		this.username = username;
		this.zuzus = 0;
	}
}
