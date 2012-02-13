package net.azyobuzi.fallfavo;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import net.azyobuzi.fallfavo.IgnoreSetting.IgnoreSettingItem;
import net.azyobuzi.fallfavo.util.Action2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ReleaseService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private Notification notif;
	private RemoteViews contentView;

	private final ArrayList<CreateFavRangeTask> taskList = new ArrayList<CreateFavRangeTask>();

	private Object removeLockObj = new Object();

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		QueueManager.setIsReleasing(true);

		notif = new Notification(R.drawable.ic_stat_main, getText(R.string.releasing), System.currentTimeMillis());
		notif.flags |= Notification.FLAG_ONGOING_EVENT;
		contentView = new RemoteViews(getPackageName(), R.layout.running_notification);
		notif.contentView = contentView;
		notif.contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainPageActivity.class), 0);
		((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(R.string.app_name, notif);

		completedCount = 0;
		successCount = 0;
		ErrorLog.clear();

		ArrayList<ArrayList<Tweet>> parallelQueue = new ArrayList<ArrayList<Tweet>>();
		for (int i = 0; i < Setting.getSimultaneousRunning(); i++) {
			parallelQueue.add(new ArrayList<Tweet>());
		}

		ArrayList<Tweet> tweets = QueueManager.getQueue();
		allCount = tweets.size();

		for (int c = 0; c < tweets.size(); c += Setting.getSimultaneousRunning()) {
			for (int i = 0; i < Setting.getSimultaneousRunning(); i++) {
				int getIndex = c + i;
				if (getIndex < tweets.size())
					parallelQueue.get(i).add(tweets.get(getIndex));
			}
		}

		for (ArrayList<Tweet> al : parallelQueue) {
			CreateFavRangeTask task = new CreateFavRangeTask();
			task.completed = new Action2<CreateFavRangeTask, ArrayList<Tweet>>() {
				@Override
				public void Invoke(CreateFavRangeTask arg0, ArrayList<Tweet> arg1) {
					synchronized (removeLockObj) {
						taskList.remove(arg0);
						try {
							QueueManager.removeRange(arg1);
						} catch (Exception e) { }

						if (taskList.size() == 0) {
							Notification notif = new Notification(R.drawable.ic_stat_main, getText(R.string.completed_releasing), System.currentTimeMillis());
							notif.flags |= Notification.FLAG_AUTO_CANCEL;
							notif.setLatestEventInfo(
								ReleaseService.this,
								getText(R.string.completed_releasing),
								"成功：" + successCount + "/" + allCount,
								PendingIntent.getActivity(
									ReleaseService.this,
									0,
									new Intent(ReleaseService.this, ErrorLog.any() ? LogActivity.class : MainPageActivity.class),
									0
								)
							);
							((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(R.string.app_name, notif);
							QueueManager.setIsReleasing(false);
							stopSelf();
						}
					}
				}
			};
			task.execute(al);
			taskList.add(task);
		}

		return START_STICKY;
	}

	private Object completedCountLockObj = new Object();
	private int allCount;
	private int completedCount;
	private int successCount;

	private void addCompletedCount(boolean success) {
		synchronized (completedCountLockObj) {
			completedCount++;
			contentView.setProgressBar(R.id.pb_release, allCount, completedCount, false);
			((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(R.string.app_name, notif);

			if (success)
				successCount++;
		}
	}

	@Override
	public void onDestroy() {
		for (CreateFavRangeTask task : taskList) {
			task.cancel(false);
		}

		super.onDestroy();
	}

	private class CreateFavRangeTask extends AsyncTask<ArrayList<Tweet>, Void, Void> {
		private ArrayList<Tweet> success = new ArrayList<Tweet>();

		@Override
		protected Void doInBackground(ArrayList<Tweet>... arg0) {
			Twitter tw = Factories.twitterFactory.getInstance(new AccessToken(Setting.getAccessToken(), Setting.getAccessTokenSecret()));

			for (Tweet t : arg0[0]) {
				if (isCancelled()) {
					completed.Invoke(this, success);
					return null;
				}

				try {
					tw.createFavorite(Long.parseLong(t.id));
					success.add(t);
					addCompletedCount(true);
				} catch (TwitterException ex) {
					boolean ignore = false;
					for (IgnoreSettingItem item : IgnoreSetting.getItems()) {
						if (ex.getStatusCode() == item.statusCode && ex.getErrorMessage().contains(item.mustContainText)) {
							ignore = true;
							break;
						}
					}

					if (ignore)
						success.add(t);
					else
						ErrorLog.add(t, ex);
					addCompletedCount(ignore);
				} catch (Exception ex) {
					ErrorLog.add(t, ex);
					addCompletedCount(false);
				}
			}

			completed.Invoke(this, success);

			return null;
		}

		public Action2<CreateFavRangeTask, ArrayList<Tweet>> completed;
	}
}
