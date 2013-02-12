package com.dudak.batchdownloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	Context ctx;
	Button startServ;
	TextView tv;
	Button stopServ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startServ = (Button) findViewById(R.id.button1);
		stopServ = (Button) findViewById(R.id.button2);
		tv = (TextView) findViewById(R.id.textView1);
		ctx = getApplicationContext();
		startServ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent service = new Intent(ctx, DownloadService.class);
				ctx.startService(service);

			}
		});
		
		stopServ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent service = new Intent(ctx, DownloadService.class);
				ctx.stopService(service);

			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
