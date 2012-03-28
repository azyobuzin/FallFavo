package net.azyobuzi.fallfavo;

import net.azyobuzi.fallfavo.util.StringUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TweetAdapter extends BaseAdapter {
	public TweetAdapter(Activity ctx) {
		this.ctx = ctx;
	}

	private Activity ctx;

	@Override
	public int getCount() {
		return QueueManager.getQueue().size();
	}

	@Override
	public Object getItem(int arg0) {
		return getTweetItem(arg0);
	}

	public Tweet getTweetItem(int index) {
		try {
			return QueueManager.getQueue().get(getCount() - index - 1);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Tweet item = getTweetItem(arg0);
		View re = arg1 == null
			? ctx.getLayoutInflater().inflate(R.layout.listview_item, null)
			: arg1;

		ViewHolder viewHolder = (ViewHolder)re.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.header = (TextView)re.findViewById(R.id.tv_header);
			viewHolder.content = (TextView)re.findViewById(R.id.tv_content);
			re.setTag(viewHolder);
		}

		if (item != null) {
			viewHolder.header.setText(item.screenName);
			viewHolder.content.setText(StringUtil.isNullOrEmpty(item.text) ? item.id : item.text);
		}
		return re;
	}

	private static class ViewHolder {
		public TextView header;
		public TextView content;
	}

}
