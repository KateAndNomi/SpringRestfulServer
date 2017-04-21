package com.dmc.bean;

import java.util.ArrayList;
import java.util.List;

public class Order {
	private String order_id = "", cooperation = "", cooperation_id = "", total = "", state = "", build_time = "",
			arrived_time = "", description = "", receiver = "", receiver_phone = "";
	private List<OrderItem> items = new ArrayList<>();

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getCooperation() {
		return cooperation;
	}

	public void setCooperation(String cooperation) {
		this.cooperation = cooperation;
	}

	public String getCooperation_id() {
		return cooperation_id;
	}

	public void setCooperation_id(String cooperation_id) {
		this.cooperation_id = cooperation_id;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getBuild_time() {
		return build_time;
	}

	public void setBuild_time(String build_time) {
		this.build_time = build_time;
	}

	public String getArrived_time() {
		return arrived_time;
	}

	public void setArrived_time(String arrived_time) {
		this.arrived_time = arrived_time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiver_phone() {
		return receiver_phone;
	}

	public void setReceiver_phone(String receiver_phone) {
		this.receiver_phone = receiver_phone;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

}
