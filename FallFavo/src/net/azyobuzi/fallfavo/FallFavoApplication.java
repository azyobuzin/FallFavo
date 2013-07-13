package net.azyobuzi.fallfavo;

import android.app.Application;

public class FallFavoApplication extends Application {
	public FallFavoApplication() {
		super();
		instance = this;
	}

	private static FallFavoApplication instance = null;
	public static FallFavoApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		System.setProperty("twitter4j.http.useSSL", "true");
		System.setProperty("twitter4j.oauth.consumerKey", "xnrEMcjtIbQWiXkOerL3gQ");
		System.setProperty("twitter4j.oauth.consumerSecret", "YV9wsZAndTrNeaQA1JYhU8hdtFcPM7i2F39s55iaZmw");
	}
}
