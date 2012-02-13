package net.azyobuzi.fallfavo;

import java.util.ArrayList;

import net.azyobuzi.fallfavo.util.Action;
import net.azyobuzi.fallfavo.util.StringUtil;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Setting {
	private static SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FallFavoApplication.getInstance());
	private static SharedPreferences.Editor ed = sp.edit();

	public static String getAccessToken() {
		return sp.getString("accessToken", null);
	}

	public static final ArrayList<Action> accessTokenChangedHandler = new ArrayList<Action>();
	public static void setAccessToken(String value) {
		ed.putString("accessToken", value);
		ed.commit();
		for (Action a : accessTokenChangedHandler) {
			a.Invoke();
		}
	}

	public static String getAccessTokenSecret() {
		return sp.getString("accessTokenSecret", null);
	}

	public static final ArrayList<Action> accessTokenSecretChangedHandler = new ArrayList<Action>();
	public static void setAccessTokenSecret(String value) {
		ed.putString("accessTokenSecret", value);
		ed.commit();
		for (Action a : accessTokenSecretChangedHandler) {
			a.Invoke();
		}
	}
	
	public static String getRequestToken() {
		return sp.getString("requestToken", null);
	}

	public static void setRequestToken(String value) {
		ed.putString("requestToken", value);
		ed.commit();
	}

	public static String getRequestTokenSecret() {
		return sp.getString("requestTokenSecret", null);
	}

	public static void setRequestTokenSecret(String value) {
		ed.putString("requestTokenSecret", value);
		ed.commit();
	}
	
	public static String getScreenName() {
		return sp.getString("screenName", null);
	}
	
	public static void setScreenName(String value) {
		ed.putString("screenName", value);
		ed.commit();
	}
	
	public static int getSimultaneousRunning() {
		String value = sp.getString("simultaneousRunning", null);
		if (StringUtil.isNullOrEmpty(value))
			value = "1";
		int re = Integer.parseInt(value);
		return re > 0 ? re : 1;
	}
}
