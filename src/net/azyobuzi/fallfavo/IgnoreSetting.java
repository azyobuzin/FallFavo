package net.azyobuzi.fallfavo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.azyobuzi.fallfavo.util.Action;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class IgnoreSetting {
	public static class IgnoreSettingItem {
		public IgnoreSettingItem(int statusCode, String mustContainText) {
			this.statusCode = statusCode;
			this.mustContainText = mustContainText;
		}

		public int statusCode;
		public String mustContainText;
	}

	private static final String settingFile = "ignore_setting.json";

	private static ArrayList<IgnoreSettingItem> items;

	public static ArrayList<IgnoreSettingItem> getItems() {
		if (items == null) {
			items = new ArrayList<IgnoreSettingItem>();

			try {
				FileInputStream stream = FallFavoApplication.getInstance().openFileInput(settingFile);
				byte[] bs = new byte[stream.available()];
				stream.read(bs);
				stream.close();
				JSONArray jarray = new JSONArray(new String(bs, "UTF-8"));
				for (int i = 0; i < jarray.length(); i++) {
					JSONObject item = jarray.getJSONObject(i);
					items.add(new IgnoreSettingItem(item.getInt("statusCode"), item.getString("mustContainText")));
				}
			} catch (Exception ex) {
				setDefaultValue();
			}
		}
		return items;
	}

	public static void setDefaultValue() {
		if (items == null)
			items = new ArrayList<IgnoreSettingItem>();
		else
			items.clear();

		items.add(new IgnoreSettingItem(403, "You have already favorited this status"));
		items.add(new IgnoreSettingItem(404, null));
		save();
		raiseChanged();
	}

	private static void save() {
		try {
			JSONArray jarray = new JSONArray();
			for (IgnoreSettingItem item : getItems()) {
				jarray.put(
					new JSONObject()
						.put("statusCode", item.statusCode)
						.put("mustContainText", item.mustContainText)
				);
			}
			FileOutputStream stream = FallFavoApplication.getInstance().openFileOutput(settingFile, Context.MODE_PRIVATE);
			stream.write(jarray.toString().getBytes("UTF-8"));
			stream.close();
		} catch (Exception ex) { /*なかったことにしよう*/ }
	}

	public static void add(int statusCode, String mustContainText) {
		getItems().add(new IgnoreSettingItem(statusCode, mustContainText));
		save();
		raiseChanged();
	}

	public static void remove(IgnoreSettingItem item) {
		getItems().remove(item);
		save();
		raiseChanged();
	}

	public static final ArrayList<Action> changedHandler = new ArrayList<Action>();

	public static void raiseChanged() {
		for (Action a : changedHandler) {
			a.Invoke();
		}
	}
}