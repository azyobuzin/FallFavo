package net.azyobuzi.fallfavo;

import net.azyobuzi.fallfavo.util.Action;
import net.azyobuzi.fallfavo.util.StringUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

public class MainPageActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (StringUtil.isNullOrEmpty(Setting.getAccessToken()) || StringUtil.isNullOrEmpty(Setting.getAccessTokenSecret()))
        {
        	Intent intent = new Intent(this, LoginActivity.class);
        	startActivity(intent);
        }

        QueueManager.queueChangedHandler.add(queueChangedAction);
        QueueManager.isReleasingChangedHandler.add(isReleasingChangedAction);
        Setting.accessTokenChangedHandler.add(accessTokenChangedAction);
        Setting.accessTokenSecretChangedHandler.add(accessTokenChangedAction);

        ListView lv = (ListView)findViewById(R.id.list);
        registerForContextMenu(lv);
        lv.setAdapter(adapter = new TweetAdapter(this));
    }

    @Override
    public void onDestroy() {
    	QueueManager.queueChangedHandler.remove(queueChangedAction);
    	QueueManager.isReleasingChangedHandler.remove(isReleasingChangedAction);
    	Setting.accessTokenChangedHandler.remove(accessTokenChangedAction);
        Setting.accessTokenSecretChangedHandler.remove(accessTokenChangedAction);

        super.onDestroy();
    }

    private TweetAdapter adapter;

    private Action queueChangedAction = new Action() {
		@Override
		public void Invoke() {
			adapter.notifyDataSetChanged();
		}
    };

    private Action accessTokenChangedAction = new Action() {
		@Override
		public void Invoke() {
			if (menu != null) {
				menu.findItem(R.id.m_release).setEnabled(
					!StringUtil.isNullOrEmpty(Setting.getAccessToken()) && !StringUtil.isNullOrEmpty(Setting.getAccessTokenSecret())
				);
			}
		}
    };

    private Action isReleasingChangedAction = new Action() {
		@Override
		public void Invoke() {
			if (menu != null) {
				menu.findItem(R.id.m_release).setTitle(QueueManager.getIsReleasing() ? R.string.stop_releasing : R.string.release_fav);
			}
		}
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo)menuInfo;
    	ListView lv = (ListView)v;
		final Tweet item = (Tweet)lv.getItemAtPosition(aMenuInfo.position);
		menu.setHeaderTitle(R.string.tweet);
    	menu.add(R.string.open_tweet).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(
					"http://twitter.com/" + item.screenName + "/status/" + item.id
				)));
				return true;
			}
    	});
    	menu.add(R.string.remove).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				try {
					QueueManager.remove(item);
				} catch (Exception ex) {
					Toast.makeText(MainPageActivity.this, R.string.failed_remove_tweet, Toast.LENGTH_SHORT).show();
				}
				return true;
			}
    	});
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	this.menu = menu;
    	getMenuInflater().inflate(R.menu.main_menu, menu);
    	accessTokenChangedAction.Invoke();
    	isReleasingChangedAction.Invoke();
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.m_setting:
    			startActivity(new Intent(this, SettingActivity.class));
    			return true;
    		case R.id.m_release:
    			Intent serviceIntent = new Intent(this, ReleaseService.class);
    			if (QueueManager.getIsReleasing()) {
    				stopService(serviceIntent);
    			} else {
    				if (QueueManager.getQueue().size() <= 0) {
    					Toast.makeText(this, R.string.not_fount_tweet, Toast.LENGTH_SHORT).show();
    				} else {
    					startService(serviceIntent);
    				}
    			}
    			return true;
    		case R.id.m_clear_all:
    			QueueManager.clear();
    			return true;
    		case R.id.m_about:
    			new AlertDialog.Builder(this)
    			.setIcon(android.R.drawable.ic_dialog_info)
    			.setTitle(R.string.about)
    			.setMessage(R.string.about_this_app)
    			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
    			.show();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}