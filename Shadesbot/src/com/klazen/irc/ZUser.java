package com.klazen.irc;

import java.io.Serializable;

public class ZUser implements Serializable {
	String username;
	int zuzus;
	
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
	
	public void addZuzus(int i){
		this.zuzus += i;
	}
	
	public void chargeZuzus(int i){
		this.zuzus -= i;
	}
	
	public ZUser(String username) {
		this.username=username;
		this.zuzus=0;
	}
}
