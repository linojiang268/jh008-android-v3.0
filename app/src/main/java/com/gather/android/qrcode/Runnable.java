package com.gather.android.qrcode;

import android.os.AsyncTask;
import android.os.Build;


/**
 * 兼容低版本的子线程开启任务
 * 
 * @author hugo
 * 
 */
public class Runnable {

	public static void execAsync(AsyncTask<Object, Object, Object> task) {
		if (Build.VERSION.SDK_INT >= 11) {
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			task.execute();
		}
	}
}
