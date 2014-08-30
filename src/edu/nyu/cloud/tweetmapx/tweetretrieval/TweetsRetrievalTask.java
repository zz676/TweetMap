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

import edu.nyu.cloud.tweetmapx.MainActivity;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 */
public class TweetsRetrievalTask extends AsyncTask<Void, Void, Void> {

	private final static String URL = "http://evident-ratio-563.appspot.com/gtweets.do";

	private List<Tweet> tweetsList = new ArrayList<Tweet>();
	private MainActivity mainActivity;
	private String searchTweetID = "";
	private Boolean isKeyWordSearch = false;

	public TweetsRetrievalTask(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public TweetsRetrievalTask(MainActivity mainActivity, String searchTweetID) {
		this.mainActivity = mainActivity;
		this.searchTweetID = searchTweetID;
	}

	public TweetsRetrievalTask(MainActivity mainActivity, String searchTweetID,
			Boolean isKeyWordSearch) {
		this.mainActivity = mainActivity;
		this.searchTweetID = searchTweetID;
		this.isKeyWordSearch = isKeyWordSearch;
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
		if (searchTweetID != "" && isKeyWordSearch == false) {
			mainActivity.updateMakerInfoWindow(tweetsList.get(0));
		} else {
			mainActivity.updateMainActivity(tweetsList);
		}
	}

	/**
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<Tweet> searchTweets() throws ClientProtocolException,
			IOException {

		// This doesn't work, wired.
		// HttpClient client = HttpClientBuilder.create().build();
		String urlString = URL;
		if (searchTweetID != "") {
			if (isKeyWordSearch == false) {
				urlString = URL + "?tweetID=" + searchTweetID + "";
			} else {

				urlString = URL + "?keyWord=" + searchTweetID + "";
			}
		}

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

		String resultString = result.toString();
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(resultString).getAsJsonArray();
		Tweet tweet;
		for (int i = 0; i < array.size(); i++) {
			tweet = new Tweet();
			tweet = gson.fromJson(array.get(i), Tweet.class);
			resultTweetsList.add(tweet);
		}
		return resultTweetsList;
	}

	public List<Tweet> getTweetsList() {
		return tweetsList;
	}

}
