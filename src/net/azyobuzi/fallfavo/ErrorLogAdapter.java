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
		View re = ctx.getLayoutInflater().inflate(R.layout.listview_item, null);
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
			((TextView)re.findViewById(R.id.tv_header)).setText(item.Item1.screenName + ": " + item.Item1.text);
			((TextView)re.findViewById(R.id.tv_content)).setText(content);
		}
		return re;
	}

}
