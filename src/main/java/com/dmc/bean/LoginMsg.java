package com.dmc.bean;

public class LoginMsg {
	private String account;
	private String greeting;
	
	
	public LoginMsg(String account, String greeting) {
		super();
		this.account = account;
		this.greeting = greeting;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getGreeting() {
		return greeting;
	}
	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
	
}
