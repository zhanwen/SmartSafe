package com.wolf.wolfsafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用程序信息的业务bean
 * @author Hanwen
 *
 */
public class AppInfo {

	private Drawable icon; //应用程序的图标
	private String name;
	private String packname;
	private boolean inRom; //为true说明装在了内存里面，false为装在了sd卡里
	private boolean userApp; //为true说明为用户程序，false为系统应用
	private int uid;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", name=" + name + ", packname="
				+ packname + ", inRom=" + inRom + ", userApp=" + userApp + "]";
	}
	
}
