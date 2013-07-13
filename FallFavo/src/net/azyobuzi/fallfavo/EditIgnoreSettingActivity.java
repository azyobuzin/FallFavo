package net.azyobuzi.fallfavo;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import net.azyobuzi.fallfavo.IgnoreSetting.IgnoreSettingItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class EditIgnoreSettingActivity extends SherlockActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_ignore_setting_page);

		final EditText statusCode = (EditText)findViewById(R.id.txt_status_code);
		final EditText content = (EditText)findViewById(R.id.txt_content);

		int index = getIntent().getIntExtra("index", -1);
		if (index >= 0) {
			target = IgnoreSetting.getItems().get(index);
			statusCode.setText(String.valueOf(target.statusCode));
			content.setText(target.mustContainText);
		}

		findViewById(R.id.btn_edit_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					if (target == null) {
						IgnoreSetting.add(Integer.parseInt(statusCode.getText().toString()), content.getText().toString());
					} else {
						target.statusCode = Integer.parseInt(statusCode.getText().toString());
						target.mustContainText = content.getText().toString();
						IgnoreSetting.raiseChanged();
					}
					finish();
				} catch (Exception ex) {
					new AlertDialog.Builder(EditIgnoreSettingActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.error)
						.setMessage(R.string.failed_edit)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) { }
						})
						.show();
				}
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private IgnoreSettingItem target;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
