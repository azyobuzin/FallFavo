package net.azyobuzi.fallfavo;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import net.azyobuzi.fallfavo.util.Action1;
import net.azyobuzi.fallfavo.util.StringUtil;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivity extends SherlockActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_page);
		findViewById(R.id.btn_login_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
				dialog.setMessage(getText(R.string.getting_token));
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				dialog.show();

				GetRequestTokenTask task = new GetRequestTokenTask();
				task.completedHandler = new Action1<RequestToken>() {
					@Override
					public void Invoke(RequestToken arg) {
						dialog.cancel();

						if (arg == null) {
							new AlertDialog.Builder(LoginActivity.this)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle(R.string.error)
								.setMessage(R.string.failed_get_request_token)
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();
							return;
						}

						Setting.setRequestToken(arg.getToken());
						Setting.setRequestTokenSecret(arg.getTokenSecret());

						Intent intent = new Intent(Intent.ACTION_VIEW)
							.setData(Uri.parse(arg.getAuthorizationURL()));
						startActivity(intent);
					}
				};
				task.execute();
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getData() != null) {
			 String verifier = intent.getData().getQueryParameter("oauth_verifier");

			if (!StringUtil.isNullOrEmpty(verifier)) {
				final ProgressDialog dialog = new ProgressDialog(this);
				dialog.setMessage(getText(R.string.getting_token));
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				dialog.show();

				GetAccessTokenTask task = new GetAccessTokenTask();
				task.completedHandler = new Action1<AccessToken>() {
					@Override
					public void Invoke(AccessToken arg) {
						dialog.cancel();

						if (arg == null) {
							new AlertDialog.Builder(LoginActivity.this)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle(R.string.error)
								.setMessage(R.string.failed_get_request_token)
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.show();
							return;
						}


						Setting.setScreenName(arg.getScreenName());
						Setting.setAccessToken(arg.getToken());
						Setting.setAccessTokenSecret(arg.getTokenSecret());

						finish();
					}
				};
				task.execute(verifier);
			 }
		 }
	 }

	private class GetRequestTokenTask extends AsyncTask<Void, Void, RequestToken> {
		@Override
		protected RequestToken doInBackground(Void... arg0) {
			try {
				Twitter tw = Factories.twitterFactory.getInstance();
				return tw.getOAuthRequestToken();
			} catch (Exception ex) {
				return null;
			}
		}

		public Action1<RequestToken> completedHandler = null;

		@Override
		protected void onPostExecute(RequestToken result) {
			if (completedHandler != null)
				completedHandler.Invoke(result);
		}
	}

	private class GetAccessTokenTask extends AsyncTask<String, Void, AccessToken> {
		@Override
		protected AccessToken doInBackground(String... verifier) {
			try {
				Twitter tw = Factories.twitterFactory.getInstance();
				return tw.getOAuthAccessToken(new RequestToken(Setting.getRequestToken(), Setting.getRequestTokenSecret()), verifier[0]);
			} catch (Exception ex) {
				return null;
			}
		}

		public Action1<AccessToken> completedHandler = null;

		@Override
		protected void onPostExecute(AccessToken result) {
			if (completedHandler != null)
				completedHandler.Invoke(result);
		}
	}
}
