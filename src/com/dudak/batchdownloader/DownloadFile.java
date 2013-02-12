package com.dudak.batchdownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadFile extends AsyncTask<String, Integer, Boolean> implements Serializable {

	private static final long serialVersionUID = 1387275769131431254L;
	private List<URL> urls;
	private ProgressDialog mProgressDialog;
	private final int TIMEOUT = 5000;
	private final String TAG = "BatchDownloader";
	private File rootPath;
	private Integer[] progress = new Integer[2];
	private String progressMessage = "Download ";
	private int position = 0;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public DownloadFile(List<URL> urls, ProgressDialog mProgressDialog, File rootPath) {
		super();
		this.urls = urls;
		this.mProgressDialog = mProgressDialog;
		this.rootPath = rootPath;
	}

	public DownloadFile() {
	}

	@Override
	protected Boolean doInBackground(String... sUrl) {

		if (urls != null && rootPath != null) {
			int total = urls.size();
			double step = 100.0 / total;
			double current = 0;
			for (int i = position; i < urls.size(); i++) {
				File downloadPath;
				try {
					downloadPath = new File(rootPath.getCanonicalFile() + "/" + getFileName(urls.get(i)));
					current += step;
					progress[1] = (int) current;
					downloadFile(urls.get(i), downloadPath);
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
		} else {
			Log.e(TAG, "URL list or rootPath is null");
		}
		return true;
	}

	private String getFileName(URL url) {
		String path = url.getPath();
		String[] parts = path.split("/");
		path = parts[parts.length - 1];
		return path;
	}

	private synchronized boolean downloadFile(URL url, File target) {
		URLConnection conn;
		InputStream is = null;
		OutputStream os = null;
		try {
			progressMessage = "Download \n" + getFileName(url);
			conn = url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.connect();
			int fileLength = conn.getContentLength();
			is = new BufferedInputStream(conn.getInputStream());
			os = new FileOutputStream(target);
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = is.read(data)) != -1) {
				total += count;
				progress[0] = (int) (total * 100 / fileLength);
				publishProgress(progress);
				os.write(data, 0, count);
			}
			os.flush();
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}
		return true;
	}

	@Override
	protected void onCancelled() {
		if (mProgressDialog != null) {
			mProgressDialog.hide();
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		super.onCancelled();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mProgressDialog == null)
			Log.e(TAG, "mProgressDialog is null");
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if (mProgressDialog != null) {
			mProgressDialog.setTitle(progressMessage);
			mProgressDialog.setProgress(progress[0]);
			mProgressDialog.setSecondaryProgress(progress[1]);
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mProgressDialog != null) {
			mProgressDialog.hide();
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		super.onPostExecute(result);
	}

	public List<URL> getUrls() {
		return urls;
	}

	public void setUrls(List<URL> urls) {
		this.urls = urls;
	}

	public File getRootPath() {
		return rootPath;
	}

	public void setRootPath(File rootPath) {
		this.rootPath = rootPath;
	}

	public ProgressDialog getmProgressDialog() {
		return mProgressDialog;
	}

	public void setmProgressDialog(ProgressDialog mProgressDialog) {
		this.mProgressDialog = mProgressDialog;
	}

}
