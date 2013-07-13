package net.azyobuzi.fallfavo;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import net.azyobuzi.fallfavo.util.Action;
import net.azyobuzi.fallfavo.util.StringUtil;
import android.os.Bundle;
import android.preference.Preference;

public class SettingActivity extends SherlockPreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Setting.accessTokenChangedHandler.add(accessTokenChanged);
		Setting.accessTokenSecretChangedHandler.add(accessTokenChanged);
		accessTokenChanged.Invoke();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onDestroy() {
		Setting.accessTokenChangedHandler.remove(accessTokenChanged);
		Setting.accessTokenSecretChangedHandler.remove(accessTokenChanged);
		
		super.onDestroy();
	}
	
	private Action accessTokenChanged = new Action() {
		@Override
		public void Invoke() {
			Preference account = findPreference("account");
			if (StringUtil.isNullOrEmpty(Setting.getAccessToken()) || StringUtil.isNullOrEmpty(Setting.getAccessTokenSecret())) {
				account.setTitle(R.string.login);
				account.setSummary(R.string.didnt_logged_in);
			} else {
				account.setTitle(R.string.login_other);
				account.setSummary(Setting.getScreenName() + getText(R.string.logged_in_by));
			}
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
