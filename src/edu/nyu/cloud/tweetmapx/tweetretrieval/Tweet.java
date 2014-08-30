package edu.nyu.cloud.tweetmapx.tweetretrieval;

import java.util.Date;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 */

public class Tweet {

	private String userID = "";
	private String tweetID;
	private Date tweetTime;
	private String tweetContent = "";
	private double longtitue;
	private double latitude;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Date getTweetTime() {
		return tweetTime;
	}

	public void setTweetTime(Date tweetTime) {
		this.tweetTime = tweetTime;
	}

	public String getTweetContent() {
		return tweetContent;
	}

	public void setTweetContent(String tweetContent) {
		this.tweetContent = tweetContent;
	}

	public double getLongtitue() {
		return longtitue;
	}

	public void setLongtitue(double longtitue) {
		this.longtitue = longtitue;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getTweetID() {
		return tweetID;
	}

	public void setTweetID(String tweetID) {
		this.tweetID = tweetID;
	}
}
