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

	private void debug(String s) {
		Log.d(TAG, s);
	}

	private void debug(Exception e) {
		Log.d(TAG, e.getLocalizedMessage());
	}

	@Override
	public void onDestroy() {
		debug("onDestroy");
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		debug("onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		debug("onStart");
		urlList = (ArrayList<URL>) intent.getSerializableExtra("urls");
		debug("urls=" + urlList.toString());
		destPath = (File) intent.getSerializableExtra("destPath");
		debug("destPath=" + destPath.toString());
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		debug(receiver.toString());
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
			error(e);
		}

		if (connection != null) {
			int downloaded = 0;
			if (file.exists()) {
				downloaded = (int) file.length();
				connection.setRequestProperty("Range",
						"bytes=" + (file.length()) + "-");
			}
			connection.setDoInput(true);
			connection.setDoOutput(true);
			BufferedInputStream in = null;
			BufferedOutputStream bout = null;
			FileOutputStream fos = null;
			try {
				in = new BufferedInputStream(connection.getInputStream());
			} catch (IOException e) {
				error(e);
			}
			try {
				fos = (downloaded == 0) ? new FileOutputStream(file)
						: new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				error(e);
			}
			int fileLength = connection.getContentLength();
			bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			try {
				while ((x = in.read(data, 0, 1024)) >= 0) {
					bout.write(data, 0, x);
					downloaded += x;
					resultData.putInt("progress1",
							(int) (downloaded * 100 / fileLength));
					if (resultData != null)
						receiver.send(UPDATE_PROGRESS, resultData);
				}
			} catch (IOException e) {
				error(e);
			} finally {
				if (bout != null)
					try {
						bout.close();

					} catch (IOException e) {
						error(e);
					}
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e) {
						error(e);
					}
				if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						error(e);
					}
			}
			resultData.putInt("progress1", 100);
			if (resultData != null)
				receiver.send(UPDATE_PROGRESS, resultData);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		debug("onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		debug("onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		debug("onLowMemory");
		super.onLowMemory();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		debug("onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		debug("onRebind");
		super.onRebind(intent);
	}

	@Override
	protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
		debug("dump");
		super.dump(fd, writer, args);
	}

	@Override
	public IBinder onBind(Intent intent) {
		debug("onBind");
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		debug("onHandleIntent");
		if (urlList != null && destPath != null) {
			for (int i = 0; i < urlList.size(); i++) {
				File downloadPath;
				try {
					downloadPath = new File(destPath.getCanonicalFile() + "/"
							+ getFileName(urlList.get(i)));
					downloadFile(urlList.get(i), downloadPath);
					resultData.putInt("progress2",
							(int) ((i + 1) * 100 / urlList.size()));
					if (receiver != null)
						receiver.send(UPDATE_PROGRESS, resultData);
				} catch (IOException e) {
					error(e);
				}
			}
		} else {
			debug("URL list or rootPath is null");
		}
		resultData.putInt("progress2", 100);
		if (receiver != null)
			receiver.send(UPDATE_PROGRESS, resultData);
	}

	private void error(IOException e) {
		Log.e(TAG, e.getLocalizedMessage());

	}

	@Override
	public void setIntentRedelivery(boolean enabled) {
		debug("setIntentRedelivery");
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
