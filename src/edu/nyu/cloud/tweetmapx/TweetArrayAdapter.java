package edu.nyu.cloud.tweetmapx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.nyu.cloud.tweetmapx.tweetretrieval.Tweet;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet> {


	private final static DateFormat FORMATTER = new SimpleDateFormat(
			"EEE MMM dd hh:mm:ss +SSSS yyyy", Locale.ENGLISH);
	private List<Tweet> tweetsList = new ArrayList<Tweet>();
	private Context context;
	private Tweet tweet;
	private TextView userName_textView;
	private TextView tweetDate_textView;
	private TextView tweetcontent_textView;
	private ImageView imageView;
	private Button showOnMapButton;

	public TweetArrayAdapter(Context context, int resource,
			List<Tweet> tweetsList) {
		super(context, resource, tweetsList);

		this.context = context;
		this.tweetsList = tweetsList;
	}

	/*
	 * public TweetArrayAdapter(Context context, List<Tweet> tweetsList) {
	 * super(context, R.layout.rowlayout, tweetsList); this.tweetsList =
	 * tweetsList; this.context = context; }
	 */

	public int getCount() {
		return tweetsList.size();
	}

	public Tweet getItem(int position) {
		return tweetsList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.rowlayout, parent, false);
		}

		showOnMapButton = (Button) convertView
				.findViewById(R.id.showonmap_button);
		showOnMapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		tweet = tweetsList.get(position);
		System.out.print(String.valueOf(position));
		userName_textView = (TextView) convertView.findViewById(R.id.username);
		tweetDate_textView = (TextView) convertView
				.findViewById(R.id.tweetdate);
		tweetcontent_textView = (TextView) convertView
				.findViewById(R.id.tweetcontent);
		imageView = (ImageView) convertView.findViewById(R.id.tweeticon);

		userName_textView.setText(tweet.getUserID());
		tweetDate_textView.setText(FORMATTER.format(tweet.getTweetTime()));
		tweetcontent_textView.setText(tweet.getTweetContent());

		return convertView;
	}
}
