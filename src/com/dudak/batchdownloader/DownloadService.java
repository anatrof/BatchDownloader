package com.dudak.batchdownloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class DownloadService extends IntentService {

	public DownloadService(String name) {
		super(name);
	}

	public DownloadService() {
		super("DownloadService");
	}

	private final String TAG = "DownloadService";
	private int listPosition;
	private ProgressDialog progressDialog;
	private List<URL> urlList;
	private File destPath;
	private ResultReceiver receiver;
	public static final int UPDATE_PROGRESS = 8344;
	private Bundle resultData;

	private void log(String s) {
		Log.e(TAG, s);
	}

	private void log(Exception e) {
		log(e.getLocalizedMessage());
	}

	@Override
	public void onDestroy() {
		log("onDestroy");
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
		urlList = (ArrayList<URL>) intent.getSerializableExtra("urls");
		log("urls=" + urlList.toString());
		destPath = (File) intent.getSerializableExtra("destPath");
		log("destPath=" + destPath.toString());
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		log(receiver.toString());
		resultData = new Bundle();
		super.onStart(intent, startId);
	}

	private String getFileName(URL url) {
		String path = url.getPath();
		String[] parts = path.split("/");
		path = parts[parts.length - 1];
		return path;
	}

	private void downloadFile(URL url, File file) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			log(e);
		}

		if (connection != null) {
			int downloaded = 0;
			if (file.exists()) {
				downloaded = (int) file.length();
				connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-");
			}
			connection.setDoInput(true);
			connection.setDoOutput(true);
			BufferedInputStream in = null;
			BufferedOutputStream bout = null;
			FileOutputStream fos = null;
			try {
				in = new BufferedInputStream(connection.getInputStream());
			} catch (IOException e) {
				log(e);
			}
			try {
				fos = (downloaded == 0) ? new FileOutputStream(file) : new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				log(e);
			}
			int fileLength = connection.getContentLength();
			bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			try {
				while ((x = in.read(data, 0, 1024)) >= 0) {
					bout.write(data, 0, x);
					downloaded += x;
					resultData.putInt("progress1", (int) (downloaded * 100 / fileLength));
					if (resultData != null)
						receiver.send(UPDATE_PROGRESS, resultData);
				}
			} catch (IOException e) {
				log(e);
			} finally {
				if (bout != null)
					try {
						bout.close();

					} catch (IOException e) {
						log(e);
					}
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e) {
						log(e);
					}
				if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						log(e);
					}
			}
			resultData.putInt("progress1", 100);
			if (resultData != null)
				receiver.send(UPDATE_PROGRESS, resultData);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		log("onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		log("onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		log("onLowMemory");
		super.onLowMemory();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		log("onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		log("onRebind");
		super.onRebind(intent);
	}

	@Override
	protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
		log("dump");
		super.dump(fd, writer, args);
	}

	@Override
	public IBinder onBind(Intent intent) {
		log("onBind");
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		log("onHandleIntent");
		if (urlList != null && destPath != null) {
			for (int i = 0; i < urlList.size(); i++) {
				File downloadPath;
				try {
					downloadPath = new File(destPath.getCanonicalFile() + "/" + getFileName(urlList.get(i)));
					downloadFile(urlList.get(i), downloadPath);
					resultData.putInt("progress2", (int) ((i + 1) * 100 / urlList.size()));
					if (receiver != null)
						receiver.send(UPDATE_PROGRESS, resultData);
				} catch (IOException e) {
					log(e);
				}
			}
		} else {
			log("URL list or rootPath is null");
		}
		resultData.putInt("progress2", 100);
		if (receiver != null)
			receiver.send(UPDATE_PROGRESS, resultData);
	}

	@Override
	public void setIntentRedelivery(boolean enabled) {
		log("setIntentRedelivery");
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
