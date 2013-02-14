package com.dudak.batchdownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "BatchDownloader";
	private ListView listFolders;
	private ProgressDialog mProgressDialog;
	private File root;
	private ArrayAdapter<String> adapter;
	private Button btnDownload;
	private final int REQUEST_CODE = 10;
	private ArrayList<String> urlStringList;
	private TextView completePath;
	private LinkedList<String> rootStack;
	private DownloadFile downloadFile;

	private void restoreAsyncTask() {
		downloadFile = (DownloadFile) getLastNonConfigurationInstance();
		if (downloadFile != null) {
			restoreAsyncTask(downloadFile);
		}
	}

	private void restoreAsyncTask(DownloadFile downloadFile) {
		mProgressDialog = createProgress();
		root = downloadFile.getRootPath();
		updateCompletePath();
		downloadFile.setmProgressDialog(mProgressDialog);
		mProgressDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		parseIntent();
		initEnv();
		initListFolders();
		initButton();
		// restoreAsyncTask();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return downloadFile;
	}

	private void initEnv() {
		root = Environment.getExternalStorageDirectory();
		rootStack = new LinkedList<String>();
		String[] path = null;
		try {
			path = root.getCanonicalPath().split("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < path.length; i++) {
			rootStack.add("/");
			rootStack.add(path[i]);
		}

	}

	private void initButton() {
		btnDownload = (Button) findViewById(R.id.btnDownload);
		btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mProgressDialog = createProgress();
				Context context = getApplicationContext();
				Intent intent = new Intent(context, DownloadService.class);
				intent.putExtra("urls", getUrlList(urlStringList));
				intent.putExtra("destPath", root);
				intent.putExtra("receiver", new DownloadReceiver(new Handler()));
				context.startService(intent);
				mProgressDialog.show();
			}
		});
	}

	private ProgressDialog createProgress() {
		ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle("Download files");
		return mProgressDialog;
	}

	private ArrayList<URL> getUrlList(List<String> pretender) {
		ArrayList<URL> urls = new ArrayList<URL>();
		for (String s : pretender) {
			try {
				urls.add(new URL(URLDecoder.decode(s, "ISO-8859-1")));
			} catch (MalformedURLException e) {
				error(e);
			} catch (UnsupportedEncodingException e) {
				error(e);
			}
		}
		return urls;
	}

	@Override
	protected void onPause() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// restoreAsyncTask();
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (downloadFile != null)
			downloadFile.cancel(true);
		super.onStop();
	}

	@Override
	protected void onStart() {
		// restoreAsyncTask();
		super.onStart();
	}

	private void parseIntent() {
		Intent intent = getIntent();
		File urlFile = new File(intent.getDataString().split(":")[1]);
		if (isTextFile(urlFile)) {
			urlStringList = new ArrayList<String>();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(urlFile));
				String line;
				while ((line = br.readLine()) != null) {

					urlStringList.add(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException e) {
				}
			}
		} else {
			error("Wrong MIME type of url file");
		}
	}

	private void updateCompletePath() {
		completePath = (TextView) findViewById(R.id.completePathText);
		if (root != null) {
			try {
				completePath.setText(root.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			error("root is null");
		}
	}

	private void updateRootPath(String folder) {
		try {
			root = new File(root.getCanonicalPath() + "/" + folder);
		} catch (IOException e) {
			error(e.getLocalizedMessage());
		}
		updateCompletePath();
	}

	private boolean isTextFile(File file) {

		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

		if (type == null)
			return false;
		else
			return (type.toLowerCase().startsWith("text/"));
	}

	private void initListFolders() {
		updateCompletePath();
		adapter = Utilites.getAdapter(root, getApplicationContext());
		listFolders = (ListView) findViewById(R.id.folderList);

		listFolders.setAdapter(adapter);
		// listFolders.setFocusable(true);
		listFolders.setFocusable(false);
		// listFolders.setItemsCanFocus(true);
		listFolders.setItemsCanFocus(false);
		listFolders.setBackgroundColor(Color.BLACK);
		listFolders.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object o = listFolders.getAdapter().getItem(position);
				debug("clicked " + o.getClass().getName());
				String textClicked = String
						.valueOf(((TextView) ((LinearLayout) view)
								.getChildAt(1)).getText());
				debug("clicked " + textClicked);
				if (getResources().getString(R.string.create_new_folder)
						.equals(textClicked)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle(R.string.create_new_folder);
					builder.setCancelable(true);
					LayoutInflater inflater = getLayoutInflater();
					builder.setView(inflater
							.inflate(R.layout.edit_layout, null));
					builder.setPositiveButton(
							getResources().getString(R.string.ok_button),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String folderName = ((EditText) ((AlertDialog) dialog)
											.getCurrentFocus()).getText()
											.toString();
									if (folderName.length() > 0) {
										updateRootPath(folderName);
									}
									root.mkdirs();
								}

							});
					builder.setNegativeButton(
							getResources().getString(R.string.cancel_button),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					updateRootPath(textClicked);
					try {
						completePath.setText(root.getCanonicalPath());
					} catch (IOException e) {
						error(e);
					}
				}
				ArrayAdapter<String> adapter = Utilites.getAdapter(root,
						getApplicationContext());
				listFolders.setAdapter(adapter);
			}
		});
	}

	private void debug(String s) {
		Log.d(TAG, s);
	}

	private void debug(Exception e) {
		Log.d(TAG, e.getLocalizedMessage());
	}

	private void error(Exception e) {
		Log.e(TAG, e.getLocalizedMessage());
	}

	private void error(String s) {
		Log.e(TAG, s);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// outState.putSerializable("downloader", downloadFile);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// downloadFile = (DownloadFile)
		// savedInstanceState.getSerializable("downloader");
		// restoreAsyncTask(downloadFile);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			Toast.makeText(this,
					"Have root " + data.getExtras().getString("root"),
					Toast.LENGTH_SHORT).show();
			if (data.hasExtra("root")) {
				root = new File(data.getExtras().getString("root"));
				try {
					completePath.setText(root.getCanonicalPath());
				} catch (IOException e) {
					error(e);
				}
				// Toast.makeText(this, "Start download into " +
				// data.getExtras().getString("root"), Toast.LENGTH_SHORT)
				// .show();
				ArrayAdapter<String> adapter = Utilites.getAdapter(root,
						getApplicationContext());
				listFolders.setAdapter(adapter);
			}
		}
	}

	private class DownloadReceiver extends ResultReceiver {
		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == DownloadService.UPDATE_PROGRESS) {
				int progress1 = resultData.getInt("progress1");
				int progress2 = resultData.getInt("progress2");
				if (mProgressDialog != null) {
					mProgressDialog.setProgress(progress2);
					mProgressDialog.setSecondaryProgress(progress1);
					if (progress2 == 100) {
						mProgressDialog.dismiss();
					}
				}
			}
		}

	}

}
