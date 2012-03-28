package net.azyobuzi.fallfavo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.actionbarsherlock.app.SherlockActivity;

import net.azyobuzi.fallfavo.util.StringUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AddActivity extends SherlockActivity {
	//ツイートへのPermalink
	private static Pattern uriPattern = Pattern.compile("https?://(?:www\\.|mobile\\.)?twitter\\.(?:com|jp)/(?:#!/)?([A-Za-z0-9_]+)/status(?:es)?/(\\d+)");

	//TwitRocker2 テキスト形式
	private static Pattern twitRockerPattern = Pattern.compile("^(.+)\n\n.+ / @([A-Za-z0-9_]+)\n\n\\d+/\\d+/\\d+ \\d+:\\d+:\\d+\n\nhttps://twitter\\.com/[A-Za-z0-9_]+/status/(\\d+)$", Pattern.DOTALL);

	//twicca ツイートを共有する
	private static Pattern twiccaPattern = Pattern.compile("^([A-Za-z0-9_]+)\n\n(.+)\n\n\\d+月\\d+日 \\d+時\\d+分 .+から\nhttp://twitter\\.com/[A-Za-z0-9_]+/status/(\\d+)$", Pattern.DOTALL);

	//Hamoooooon 共有
	private static Pattern hamoooooonPattern = Pattern.compile("^(.+)\n\nhttp://twitter\\.com/#!/([A-Za-z0-9_]+)/status/(\\d+)$", Pattern.DOTALL);

	//Seesmic 共有する
	private static Pattern seesmicPattern = Pattern.compile("^.+ \\(([A-Za-z0-9_]+)\\):\n(.+)\n\nhttp://twitter\\.com/[A-Za-z0-9_]+/status/(\\d+)\n\n\\(Seesmicからの送信 http://www\\.seesmic\\.com\\)$", Pattern.DOTALL);

	//TweetDeck Share
	private static Pattern tweetDeckPattern = Pattern.compile("^([A-Za-z0-9_]+): (.+)\n\nOriginal Tweet: http://twitter\\.com/[A-Za-z0-9_]+/status/(\\d+)\n\nSent via TweetDeck \\(www\\.tweetdeck\\.com\\)$", Pattern.DOTALL);

	//Plume 共有
	private static Pattern plumePattern = Pattern.compile("^(.+)\n\nhttps://twitter\\.com/#!/([A-Za-z0-9_]+)/status/(\\d+)\n\nShared via Plume\nhttps://market\\.android\\.com/details\\?id=com\\.levelup\\.touiteur$", Pattern.DOTALL);

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
        	Intent intent = getIntent();

	        if (!StringUtil.isNullOrEmpty(intent.getDataString())) {
	        	Matcher m = uriPattern.matcher(intent.getDataString());
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.screenName = m.group(1);
	        		t.id = m.group(2);
	        		add(t);
	        		return;
	        	}
	        }

	        CharSequence exData = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);

	        if (!StringUtil.isNullOrEmpty(exData)) {
	        	//TwitRocker2
	        	Matcher m = twitRockerPattern.matcher(exData);
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.text = m.group(1);
	        		t.screenName = m.group(2);
	        		t.id = m.group(3);
	        		add(t);
	        		return;
	        	}

	        	//twicca
	        	m = twiccaPattern.matcher(exData);
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.screenName = m.group(1);
	        		t.text = m.group(2);
	        		t.id = m.group(3);
	        		add(t);
	        		return;
	        	}

	        	//Hamoooooon
	        	m = hamoooooonPattern.matcher(exData);
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.text = m.group(1);
	        		t.screenName = m.group(2);
	        		t.id = m.group(3);
	        		add(t);
	        		return;
	        	}

	        	//Seesmic
	        	m = seesmicPattern.matcher(exData);
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.screenName = m.group(1);
	        		t.text = m.group(2);
	        		t.id = m.group(3);
	        		add(t);
	        		return;
	        	}

	        	//TweetDeck
	        	m = tweetDeckPattern.matcher(exData);
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.screenName = m.group(1);
	        		t.text = m.group(2);
	        		t.id = m.group(3);
	        		add(t);
	        		return;
	        	}

	        	//Plume
	        	m = plumePattern.matcher(exData);
	        	if (m.find()) {
	        		Tweet t = new Tweet();
	        		t.text = m.group(1);
	        		t.screenName = m.group(2);
	        		t.id = m.group(3);
	        		add(t);
	        		return;
	        	}

	        	//その他
	        	m = uriPattern.matcher(exData);
	        	ArrayList<Tweet> addList = new ArrayList<Tweet>();
	        	while (m.find()) {
	        		Tweet t = new Tweet();
	        		t.screenName = m.group(1);
	        		t.id = m.group(2);
	        		addList.add(t);
	        	}
	        	if (!addList.isEmpty()) {
	        		add(addList);
	        		return;
	        	}
	        }

	        Toast.makeText(this, R.string.not_supported_format, Toast.LENGTH_LONG).show();
        } finally {
        	finish();
        }
	}

	private void add(Tweet t) {
		try {
			QueueManager.add(t);
			Toast.makeText(this, R.string.success_add_fav_queue, Toast.LENGTH_LONG).show();
		} catch (Exception ex) {
			Toast.makeText(this, R.string.failed_add_fav_queue, Toast.LENGTH_LONG).show();
		}
	}
	
	private void add(ArrayList<Tweet> t) {
		try {
			QueueManager.addRange(t);
			Toast.makeText(this, R.string.success_add_fav_queue, Toast.LENGTH_LONG).show();
		} catch (Exception ex) {
			Toast.makeText(this, R.string.failed_add_fav_queue, Toast.LENGTH_LONG).show();
		}
	}
}
