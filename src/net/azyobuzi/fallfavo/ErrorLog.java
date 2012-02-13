package net.azyobuzi.fallfavo;

import java.util.ArrayList;

import net.azyobuzi.fallfavo.util.Tuple2;

public class ErrorLog {
	private static final ArrayList<Tuple2<Tweet, Exception>> log = new ArrayList<Tuple2<Tweet, Exception>>();
	
	public static ArrayList<Tuple2<Tweet, Exception>> getLog() {
		return log;
	}
	
	public static void add(Tweet tweet, Exception ex) {
		log.add(new Tuple2<Tweet, Exception>(tweet, ex));
	}
	
	public static void clear() {
		log.clear();
	}
	
	public static boolean any() {
		return !log.isEmpty();
	}
}
