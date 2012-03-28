package net.azyobuzi.fallfavo;

import net.azyobuzi.fallfavo.IgnoreSetting.IgnoreSettingItem;
import net.azyobuzi.fallfavo.util.StringUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IgnoreSettingAdapter extends BaseAdapter {
	public IgnoreSettingAdapter(Activity ctx) {
		this.ctx = ctx;
	}

	private Activity ctx;

	@Override
	public int getCount() {
		return IgnoreSetting.getItems().size();
	}

	public IgnoreSettingItem getIgnoreSettingItem(int index) {
		return IgnoreSetting.getItems().get(index);
	}

	@Override
	public Object getItem(int arg0) {
		return getIgnoreSettingItem(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View re = arg1 == null
			? ctx.getLayoutInflater().inflate(R.layout.ignore_setting_listview_item, null)
			: arg1;

		ViewHolder viewHolder = (ViewHolder)re.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.statusCode = (TextView)re.findViewById(R.id.tv_status_code);
			viewHolder.mustContain = (TextView)re.findViewById(R.id.tv_must_contain);
			re.setTag(viewHolder);
		}

		IgnoreSettingItem item = getIgnoreSettingItem(arg0);
		if (item != null) {
			viewHolder.statusCode.setText(String.valueOf(item.statusCode));
			viewHolder.mustContain.setText(
				StringUtil.isNullOrEmpty(item.mustContainText) ? ctx.getText(R.string.nothing) : item.mustContainText
			);
		}
		return re;
	}

	private static class ViewHolder {
		public TextView statusCode;
		public TextView mustContain;
	}
}
