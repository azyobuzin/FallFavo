package net.azyobuzi.fallfavo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.azyobuzi.fallfavo.util.Action;

import android.content.Context;
import android.os.Handler;

public class QueueManager {
	private static ArrayList<Tweet> m_queue;

	//141文字のセパレーター
	private static final String separator = "separatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorseparatorsepara";

	private static final String queueFile = "fallfavo_favqueue.txt";

	public static ArrayList<Tweet> getQueue() {
		if (m_queue == null) {
			m_queue = new ArrayList<Tweet>();

			try {
				FileInputStream stream = FallFavoApplication.getInstance().openFileInput(queueFile);
				byte[] bs = new byte[stream.available()];
				stream.read(bs);
				stream.close();
				String content = new String(bs, "UTF-8");
				String[] split = content.split("\n" + separator + "\n");
				for (int i = 0; i < split.length - 1; i += 3) {
					Tweet t = new Tweet();
					t.id = split[i];
					t.screenName = split[i + 1];
					t.text = split[i + 2];
					m_queue.add(t);
				}
			} catch (Exception ex) { }
		}

		return m_queue;
	}

	private static String tweetToString(Tweet t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.id);
		sb.append("\n");
		sb.append(separator);
		sb.append("\n");
		sb.append(t.screenName);
		sb.append("\n");
		sb.append(separator);
		sb.append("\n");
		sb.append(t.text);
		sb.append("\n");
		sb.append(separator);
		sb.append("\n");
		return sb.toString();
	}

	public static final ArrayList<Action> queueChangedHandler = new ArrayList<Action>();

	private static Handler handler = new Handler();

	private static void raiseQueueChanged() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (Action a : queueChangedHandler) {
					a.Invoke();
				}
			}
		});
	}

	public static void add(Tweet t) throws IOException {
		FileOutputStream stream = FallFavoApplication.getInstance().openFileOutput(queueFile, Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
		stream.write(tweetToString(t).getBytes("UTF-8"));
		stream.close();

		if (m_queue != null) {
			m_queue.add(t);
			raiseQueueChanged();
		}
	}

	public static void remove(Tweet t) throws IOException {
		@SuppressWarnings("unchecked")
		ArrayList<Tweet> copied = (ArrayList<Tweet>)getQueue().clone();
		if (copied.remove(t)) {
			FileOutputStream stream = FallFavoApplication.getInstance().openFileOutput(queueFile, Context.MODE_WORLD_READABLE);
			for (Tweet item : copied) {
				stream.write(tweetToString(item).getBytes("UTF-8"));
			}
			stream.close();

			getQueue().remove(t);
			raiseQueueChanged();
		}
	}

	public static void removeRange(Collection<Tweet> tweets) throws IOException {
		@SuppressWarnings("unchecked")
		ArrayList<Tweet> copied = (ArrayList<Tweet>)getQueue().clone();

		for (Tweet t : tweets) {
			copied.remove(t);
		}

		FileOutputStream stream = FallFavoApplication.getInstance().openFileOutput(queueFile, Context.MODE_WORLD_READABLE);
		for (Tweet item : copied) {
			stream.write(tweetToString(item).getBytes("UTF-8"));
		}
		stream.close();

		for (Tweet t : tweets) {
			getQueue().remove(t);
		}
		raiseQueueChanged();
	}
	
	public static void clear() {
		FallFavoApplication.getInstance().deleteFile(queueFile);
		m_queue = null;
		raiseQueueChanged();
	}

	private static boolean isReleasing = false;
	public static final ArrayList<Action> isReleasingChangedHandler = new ArrayList<Action>();

	public static boolean getIsReleasing() {
		return isReleasing;
	}

	public static void setIsReleasing(boolean value) {
		isReleasing = value;

		handler.post(new Runnable() {
			@Override
			public void run() {
				for (Action a : isReleasingChangedHandler) {
					a.Invoke();
				}
			}
		});
	}
}
