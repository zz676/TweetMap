package edu.nyu.cloud.tweetmapx.tweetretrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.nyu.cloud.tweetmapx.TimeLineActivity;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 */
public class TimeLineTweetsRetrievalTask extends AsyncTask<Void, Void, Void> {

	private List<Tweet> tweetsList = new ArrayList<Tweet>();
	private final static String url = "http://evident-ratio-563.appspot.com/bgtweets.do";
	private TimeLineActivity timeLineActivity;

	public TimeLineTweetsRetrievalTask(TimeLineActivity timeLineActivity) {
		this.timeLineActivity = timeLineActivity;
		// searchString = mainActivity.getSearchString();
	}

	@Override
	protected Void doInBackground(Void... params) {
		android.os.Debug.waitForDebugger();
		try {
			tweetsList = searchTweets();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

		timeLineActivity.updateTimeLineActivity(tweetsList);
	}

	/**
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<Tweet> searchTweets() throws ClientProtocolException,
			IOException {

		String urlString = url;
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlString);
		HttpResponse response = client.execute(httpGet);
		List<Tweet> resultTweetsList = new ArrayList<Tweet>();

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		// System.out.println(result.toString());

		String resultString = result.toString();
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(resultString).getAsJsonArray();
		Tweet tweet;
		for (int i = 0; i < array.size(); i++) {
			tweet = new Tweet();
			tweet = gson.fromJson(array.get(i), Tweet.class);
			resultTweetsList.add(tweet);
			/*
			 * System.out.println("tweetID:" + tweet.getTweetID() + "; " +
			 * "userID:" + tweet.getUserID() + "; " + "tweetTime:" +
			 * tweet.getTweetTime() + "; " + "latitude:" +
			 * String.valueOf(tweet.getLatitude()) + "; " + "longtitue:" +
			 * String.valueOf(tweet.getLongtitue()) + "; " + "content:" +
			 * tweet.getTweetContent() + "; ");
			 */
		}
		return resultTweetsList;
	}

	public List<Tweet> getTweetsList() {
		return tweetsList;
	}

}
