package edu.nyu.cloud.tweetmapx;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import edu.nyu.cloud.tweetmapx.tweetretrieval.TimeLineTweetsRetrievalTask;
import edu.nyu.cloud.tweetmapx.tweetretrieval.Tweet;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 */
public class TimeLineActivity extends ListActivity {

	private List<Tweet> tweetsList = new ArrayList<Tweet>();
	private TweetArrayAdapter tweetArrayAdapter;
	private TimeLineTweetsRetrievalTask timeLineTweetsRetrievalTask;
	private ProgressDialog loadTweetsProgressBar;
	private final static DateFormat FORMATTER = new SimpleDateFormat(
			"EEE MMM dd hh:mm:ss +SSSS yyyy", Locale.ENGLISH);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.timeline_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Tweets TimeLine");

		Button btnLoadMore = new Button(this);
		btnLoadMore.setText("Load More");

		// Adding button to listview at footer
		getListView().addFooterView(btnLoadMore);
		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Starting a new async task
				handleLoadMoreButtonClick();
			}
		});
		loadTweetsProgressBar = new ProgressDialog(getListView().getContext());
		loadTweetsProgressBar.setCancelable(false);
		loadTweetsProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadTweetsProgressBar.setMessage("Loading Tweets...");
		loadTweetsProgressBar.show();

		Tweet tweet = new Tweet();
		tweet.setUserID("Test");
		try {
			tweet.setTweetTime(FORMATTER
					.parse("Mon Apr 28 19:42:28 +0000 2014"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		tweetArrayAdapter = new TweetArrayAdapter(this, R.layout.rowlayout,
				this.tweetsList);
		setListAdapter(tweetArrayAdapter);

		timeLineTweetsRetrievalTask = new TimeLineTweetsRetrievalTask(this);
		timeLineTweetsRetrievalTask.execute();
	}

	public void handleLoadMoreButtonClick() {
		loadTweetsProgressBar.show();
		timeLineTweetsRetrievalTask = new TimeLineTweetsRetrievalTask(this);
		timeLineTweetsRetrievalTask.execute();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.timeline_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void updateTimeLineActivity(List<Tweet> tweetsList) {

		this.tweetsList.addAll(tweetsList);
		// Collections.sort(tweetsList);
		Collections.sort(tweetsList, new Comparator<Tweet>() {
			@Override
			public int compare(Tweet o1, Tweet o2) {
				if (o1.getTweetTime() == null || o2.getTweetTime() == null)
					return 0;
				return o1.getTweetTime().compareTo(o2.getTweetTime());
			}
		});
		// tweetArrayAdapter = new TweetArrayAdapter(this, R.layout.rowlayout,
		// this.tweetsList);
		setListAdapter(tweetArrayAdapter);
		loadTweetsProgressBar.dismiss();
	}
}
