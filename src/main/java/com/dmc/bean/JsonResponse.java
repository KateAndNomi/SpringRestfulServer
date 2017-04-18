package com.dmc.bean;

public class JsonResponse {
	private int statusCode;
	private String msg="";
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public JsonResponse(int statusCode, String msg) {
		super();
		this.statusCode = statusCode;
		this.msg = msg;
	}
	
	
}
