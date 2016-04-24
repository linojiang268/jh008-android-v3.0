package com.gather.android.manager;

import android.app.ActivityManager;
import android.content.Context;

import com.gather.android.baseclass.BaseActivity;

import java.util.Stack;

public class AppManage {
	//单例模式
	private  static final AppManage instance = new AppManage();

	public static AppManage getInstance() {
		synchronized (instance){
			return  instance;
		}
	}

	private Stack<ActivityInter> activities;

	private AppManage() {
		activities = new Stack<ActivityInter>();
	}

	public void addActivity(ActivityInter activity) {
		if (activity != null) {
			activities.add(activity);
		}
	}
	

	public void removeActivity(ActivityInter activity) {
		if (activity != null) {
			activities.remove(activity);
		}
	}

	public void finishOther(){
		ActivityInter activity = getCurrentActivity();
		while (!activities.isEmpty()){
			ActivityInter act = activities.pop();
			if (act != activity && act != null){
				act.finishActivity();
			}
		}
		addActivity(activity);
	}

	public void finishActivity(ActivityInter activity) {
		if (activity != null) {
			activities.remove(activity);
		}
		activity.finishActivity();
	}

	public ActivityInter getCurrentActivity() {
		if (activities.size() > 0){
			return  activities.lastElement();
		}
		else {
			return null;
		}
	}
	

	@SuppressWarnings("deprecation")
	public void exit(Context context) {
		try {
			clearEverything();
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			manager.restartPackage(context.getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearEverything(){
		while (!activities.isEmpty()){
			ActivityInter act = activities.pop();
			if (act != null){
				act.finishActivity();
			}
		}
	}

	public interface ActivityInter {
		public void finishActivity();
	}

}
