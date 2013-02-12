package com.dudak.batchdownloader;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DownloadService extends IntentService {

	public DownloadService(String name) {
		super(name);
	}

	public DownloadService() {
		super("DownloadService");
	}

	private final String TAG = "DownloadService";
	private List<URL> urlList;
	private int listPosition;
	private File destPath;
	private ProgressDialog progressDialog;
	private Looper looper;

	private class DownloadHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
	}

	private void log(String s) {
		Log.e(TAG, s);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		log("onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		log("onStart");
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		log("onStartCommand");
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		log("onConfigurationChanged");
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		log("onLowMemory");
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		log("onTrimMemory");
		// TODO Auto-generated method stub
		// super.onTrimMemory(level);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		log("onUnbind");
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		log("onRebind");
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		log("onTaskRemoved");
		// TODO Auto-generated method stub
		// super.onTaskRemoved(rootIntent);
	}

	@Override
	protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
		log("dump");
		// TODO Auto-generated method stub
		super.dump(fd, writer, args);
	}

	@Override
	public IBinder onBind(Intent intent) {
		log("onBind");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		log("onHandleIntent");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log(e.getLocalizedMessage());
		}
	}

	@Override
	public void setIntentRedelivery(boolean enabled) {
		// TODO Auto-generated method stub
		super.setIntentRedelivery(enabled);
	}

	public List<URL> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<URL> urlList) {
		this.urlList = urlList;
	}

	public int getListPosition() {
		return listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}

	public File getDestPath() {
		return destPath;
	}

	public void setDestPath(File destPath) {
		this.destPath = destPath;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

}
