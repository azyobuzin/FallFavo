package net.azyobuzi.fallfavo;

import twitter4j.TwitterException;
import net.azyobuzi.fallfavo.util.Tuple2;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class LogActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ListView lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(new ErrorLogAdapter(this));
        registerForContextMenu(lv);
        
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			if (item.getItemId() == android.R.id.home) {
				finish();
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo)menuInfo;
    	ListView lv = (ListView)v;
    	@SuppressWarnings("unchecked")
		Tuple2<Tweet, Exception> item = (Tuple2<Tweet, Exception>)lv.getItemAtPosition(aMenuInfo.position);
    	if (item.Item2 instanceof TwitterException) {
    		final TwitterException tex = (TwitterException)item.Item2;
    		menu.setHeaderTitle(R.string.error_log);
	    	menu.add(R.string.add_ignore_setting_from_log).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuitem) {
					IgnoreSetting.add(tex.getStatusCode(), tex.getErrorMessage());
					return true;
				}
	    	});
    	}
    }
}
