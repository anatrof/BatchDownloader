package com.dudak.batchdownloader;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;

public class Utilites {
	private static String[] arr = { "" };

	public static String getName(String path) {
		String[] tmp = path.split("/");
		return tmp[tmp.length - 1];
	}

	public static String getFolder(String path) {
		String[] tmp = path.split("/");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tmp.length - 1; i++) {
			sb.append(tmp[i]).append("/");
		}
		return sb.toString();
	}

	public static ArrayAdapter<String> getAdapter(File file, Context ctx) {
		File[] values = file.listFiles();
		ArrayList<String> as = new ArrayList<String>();
		as.add("..");
		for (File f : values) {
			if (f.isDirectory())
				as.add(f.getName());
		}
		as.add(ctx.getResources().getString(R.string.create_new_folder));
		// return new ArrayAdapter<String>(ctx,
		// R.layout.imglinear,R.id.folder_text,
		// as.toArray(arr));
		return new FileListAdapter(ctx, R.layout.imglinear, R.id.folder_text, as.toArray(arr));
	}

}
