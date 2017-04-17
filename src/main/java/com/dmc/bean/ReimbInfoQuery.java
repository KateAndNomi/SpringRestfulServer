package com.dmc.bean;

import java.util.ArrayList;

/**
 * 查询报销信息的Bean
 * 
 * @author maxx
 *
 */
public class ReimbInfoQuery {
	private String id = "", title = "", desc = "", amount = "", costTime = "", type = "", state = "", time = "";
	private ArrayList<String> imgs = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<String> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<String> imgs) {
		this.imgs = imgs;
	}

	@Override
	public String toString() {
		return "ReimbInfoQuery [id=" + id + ", title=" + title + ", desc=" + desc + ", amount=" + amount + ", costTime="
				+ costTime + ", type=" + type + ", state=" + state + ", time=" + time + ", imgs=" + imgs + "]";
	}

	

}
