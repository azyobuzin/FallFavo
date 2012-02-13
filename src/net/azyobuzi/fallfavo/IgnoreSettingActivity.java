package net.azyobuzi.fallfavo;

import net.azyobuzi.fallfavo.IgnoreSetting.IgnoreSettingItem;
import net.azyobuzi.fallfavo.util.Action;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class IgnoreSettingActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		IgnoreSetting.changedHandler.add(settingChangedAction);

		ListView lv = (ListView)findViewById(R.id.list);
		lv.setAdapter(adapter = new IgnoreSettingAdapter(this));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				startActivity(new Intent(IgnoreSettingActivity.this, EditIgnoreSettingActivity.class).putExtra("index", arg2));
			}
		});
		registerForContextMenu(lv);

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void onDestroy() {
		IgnoreSetting.changedHandler.remove(settingChangedAction);
		super.onDestroy();
	}

	private Action settingChangedAction = new Action() {
		@Override
		public void Invoke() {
			adapter.notifyDataSetChanged();
		}
	};

	private IgnoreSettingAdapter adapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ignore_setting_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			if (item.getItemId() == android.R.id.home) {
				finish();
				return true;
			}
		}

		switch (item.getItemId()) {
			case R.id.m_add:
				startActivity(new Intent(this, EditIgnoreSettingActivity.class));
				return true;
			case R.id.m_set_default:
				IgnoreSetting.setDefaultValue();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo)menuInfo;
    	ListView lv = (ListView)v;
    	final IgnoreSettingItem item = (IgnoreSettingItem)lv.getItemAtPosition(aMenuInfo.position);
    	menu.setHeaderTitle(R.string.ignore_setting);
    	menu.add(R.string.remove).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuitem) {
				IgnoreSetting.remove(item);
				return true;
			}
    	});
    }
}
