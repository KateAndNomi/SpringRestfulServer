package com.dmc.controller;

import com.dmc.database.JdbcDaoImp;

public class DatabaseMg {
	private static DatabaseMg instance = new DatabaseMg();
	private JdbcDaoImp defaultDaoImp;

	private DatabaseMg() {
	}

	public static DatabaseMg getInstance() {
		return instance;
	}

	public JdbcDaoImp getDefaultDaoImp() {
		return defaultDaoImp;
	}

	public void setDefaultDaoImp(JdbcDaoImp defaultDaoImp) {
		this.defaultDaoImp = defaultDaoImp;
	}

}
