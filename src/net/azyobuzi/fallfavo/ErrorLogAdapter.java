package net.azyobuzi.fallfavo;

import net.azyobuzi.fallfavo.util.StringUtil;
import net.azyobuzi.fallfavo.util.Tuple2;
import twitter4j.TwitterException;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ErrorLogAdapter extends BaseAdapter {
	public ErrorLogAdapter(Activity ctx) {
		this.ctx = ctx;
	}

	private Activity ctx;

	private final Object[] log = ErrorLog.getLog().toArray();

	@Override
	public int getCount() {
		return log.length;
	}

	@Override
	public Object getItem(int position) {
		return log[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View re = convertView == null
			? ctx.getLayoutInflater().inflate(R.layout.listview_item, null)
			: convertView;

		ViewHolder viewHolder = (ViewHolder)re.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.header = (TextView)re.findViewById(R.id.tv_header);
			viewHolder.content = (TextView)re.findViewById(R.id.tv_content);
			re.setTag(viewHolder);
		}

		@SuppressWarnings("unchecked")
		Tuple2<Tweet, Exception> item = (Tuple2<Tweet, Exception>)getItem(position);
		if (item != null) {
			String content;
			if (item.Item2 instanceof TwitterException) {
				TwitterException tex = (TwitterException)item.Item2;
				content = "TwitterException " + tex.getStatusCode() + ": "
					+ (StringUtil.isNullOrEmpty(tex.getErrorMessage()) ? tex.getMessage() : tex.getErrorMessage());
			} else {
				content = item.Item2.getClass().getSimpleName() + ": " + item.Item2.getMessage();
			}
			viewHolder.header.setText(item.Item1.screenName + ": " + item.Item1.text);
			viewHolder.content.setText(content);
		}
		return re;
	}

	private static class ViewHolder {
		public TextView header;
		public TextView content;
	}
}
