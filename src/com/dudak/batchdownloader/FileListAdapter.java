package com.dudak.batchdownloader;

import android.content.Context;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<String> {

	private static final String TAG = "BatchDownloader";

	public FileListAdapter(Context context, int resource, int textViewResourceId, String[] values) {
		super(context, resource, textViewResourceId, values);
		this.context = context;
		this.values = values;
	}

	private final Context context;
	private final String[] values;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.imglinear, parent, false);
		TextView button = (TextView) rowView.findViewById(R.id.folder_text);
		button.setFocusable(false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		button.setText(values[position]);
		// Change the icon for Windows and iPhone
		String s = values[position];
		String create_folder = context.getString(R.string.create_new_folder);
//		Log.d(TAG, "s=" + s + " | create_folder=" + create_folder);
		if (s.equals(create_folder)) {
			imageView.setImageResource(android.R.drawable.ic_input_add);
		} else if(s.equals("..")) {
//			imageView.setImageResource(android.R.drawable.ic_media_rew);
			imageView.setImageResource(android.R.drawable.ic_menu_revert);
		}
		else {
			imageView.setImageResource(android.R.drawable.checkbox_on_background);
		}

		return rowView;
	}
}
