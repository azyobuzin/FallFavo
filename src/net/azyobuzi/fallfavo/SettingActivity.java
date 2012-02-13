package net.azyobuzi.fallfavo;

import net.azyobuzi.fallfavo.util.Action;
import net.azyobuzi.fallfavo.util.StringUtil;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Setting.accessTokenChangedHandler.add(accessTokenChanged);
		Setting.accessTokenSecretChangedHandler.add(accessTokenChanged);
		accessTokenChanged.Invoke();
		
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			if (item.getItemId() == android.R.id.home) {
				finish();
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
}
