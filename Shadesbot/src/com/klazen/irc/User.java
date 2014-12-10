package com.klazen.irc;

import java.io.Serializable;

public class User implements Serializable {
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
	
	public User(String username) {
		this.username=username;
		this.zuzus=0;
	}
}
