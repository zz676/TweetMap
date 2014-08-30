package edu.nyu.cloud.tweetmapx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import edu.nyu.cloud.tweetmapx.tweetretrieval.Tweet;
import edu.nyu.cloud.tweetmapx.tweetretrieval.TweetsRetrievalTask;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 */
public class MainActivity extends FragmentActivity implements LocationListener {

	private static final String HEATMAPBUTTON_HIDESTRING_STRING = "Hide HeatMap";
	private static final String HEATMAPBUTTON_SHOWSTRING_STRING = "Show HeatMap";
	private static final String HIDETWEETSMAKERSBTNTXT = "Hide Tweets";
	private static final String SHOWTWEETSMAKERSBTNTXT = "Show Tweets";
	private static final LatLng centerOfAmerican = new LatLng(48.354389,
			99.998083);
	private final static DateFormat formatter = new SimpleDateFormat(
			"EEE MMM dd hh:mm:ss +SSSS yyyy", Locale.ENGLISH);

	private DrawerLayout mDrawerLayout;
	private EditText editTextSearch;
	private Button heatMapButton;
	private Button showTweetsButton;
	private RadioGroup radioMapGroup;
	private RadioButton normalMapRadio;
	private ProgressDialog loadHeatMapProgressBar;
	private GoogleMap mMap;
	private CharSequence mTitle;
	private HeatmapTileProvider mProvider;
	private TileOverlay mOverlay;
	private TweetsRetrievalTask tweetsRetrievalTask;
	private List<Tweet> tweetsList = new ArrayList<Tweet>();
	private List<LatLng> tweetLatLngList;
	private Map<String, Tweet> markerMap = new HashMap<String, Tweet>();
	private String searchString = "";
	private String searchTweetID;
	private Marker clickedMarker;
	private List<Marker> markerList = new ArrayList<Marker>();
	private Spinner keywordsSpinner;
	private String[] keywordsList;
	private Boolean isFirstRunBoolean = true;
	private Button button_drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loadHeatMapProgressBar = new ProgressDialog(findViewById(
				R.id.content_frame).getContext());
		loadHeatMapProgressBar.setCancelable(false);
		loadHeatMapProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadHeatMapProgressBar.setMessage("Loading HeatMap...");
		normalMapRadio = (RadioButton) findViewById(R.id.radioNormalMap);
		addListenerOnMapRadioButton();
		showTweetsButton = (Button) findViewById(R.id.tweetsmakers_button);
		showTweetsButton.setText(SHOWTWEETSMAKERSBTNTXT);
		heatMapButton = (Button) findViewById(R.id.heatmap_button);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		heatMapButton = (Button) findViewById(R.id.heatmap_button);
		editTextSearch = (EditText) findViewById(R.id.editText_SearchTxt);
		editTextSearch
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						editTextSearch.setText("Searched");
						editTextSearch.setSelection(editTextSearch.getText()
								.length());
						return false;
					}
				});

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		setUpMapIfNeeded();
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				handleMarkerClick(marker);
				return true;
			}
		});
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		} else {
			setCurrentLocation();
		}

		keywordsList = getResources().getStringArray(R.array.keywords_array);
		addItemsOnKeywordsSpinner();

		loadHeatMapProgressBar.show();
		tweetsRetrievalTask = new TweetsRetrievalTask(this);
		tweetsRetrievalTask.execute();
		// this.savedInstanceState = savedInstanceState;
	}

	public void addItemsOnKeywordsSpinner() {

		keywordsSpinner = (Spinner) findViewById(R.id.keywordsspinner);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, keywordsList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		keywordsSpinner.setAdapter(dataAdapter);
		keywordsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long id) {

				if (isFirstRunBoolean == false) {
					String searchKeyWordString = parent.getItemAtPosition(pos)
							.toString();
					handlerSpinnerItemSelected(searchKeyWordString, pos);
					Toast.makeText(parent.getContext(),
							"Search : " + searchKeyWordString,
							Toast.LENGTH_SHORT).show();
					mTitle = parent.getItemAtPosition(pos).toString();
					mDrawerLayout.closeDrawers();
				}
				isFirstRunBoolean = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void handlerSpinnerItemSelected(String searchKeyWordString,
			int position) {
		mDrawerLayout.closeDrawers();
		loadHeatMapProgressBar.setMessage("Search tweets("
				+ searchKeyWordString + ")...");
		loadHeatMapProgressBar.show();
		if (button_drawer != null) {
			if (button_drawer.getId() == R.id.heatmap_button) {
				button_drawer.setText(HEATMAPBUTTON_SHOWSTRING_STRING);
			} else if (button_drawer.getId() == R.id.tweetsmakers_button) {
				button_drawer.setText(SHOWTWEETSMAKERSBTNTXT);
			}
		}

		for (Marker marker : markerList) {
			marker.setVisible(false);
		}
		markerList.clear();

		if (mOverlay != null) {
			mOverlay.remove();
		}
		if ((searchKeyWordString.toUpperCase(Locale.ENGLISH)).equals("ALL")) {
			tweetsRetrievalTask = new TweetsRetrievalTask(this);
		} else {
			tweetsRetrievalTask = new TweetsRetrievalTask(this,
					searchKeyWordString, true);
		}

		tweetsRetrievalTask.execute();
	}

	public void addListenerOnMapRadioButton() {

		radioMapGroup = (RadioGroup) findViewById(R.id.radioMapType);
		radioMapGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedID) {
						RadioButton radiobutton = (RadioButton) findViewById(checkedID);
						if (radiobutton != null) {
							if (checkedID == normalMapRadio.getId()) {

								mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

							} else {
								mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
							}
						}
						mDrawerLayout.closeDrawers();
					}
				});
	}

	public void handleMarkerClick(Marker marker) {

		Tweet tweet = (Tweet) markerMap.get(marker.getId());
		searchTweetID = tweet.getTweetID();
		clickedMarker = marker;
		tweetsRetrievalTask = new TweetsRetrievalTask(this, searchTweetID);
		tweetsRetrievalTask.execute();
	}

	public void onButtonClicked(View view) {

		button_drawer = (Button) view;
		mDrawerLayout.closeDrawers();

		switch (view.getId()) {
		case R.id.heatmap_button:
			if (button_drawer.getText() == HEATMAPBUTTON_HIDESTRING_STRING) {
				button_drawer.setText(HEATMAPBUTTON_SHOWSTRING_STRING);

				// hide heatmap
				if (mOverlay != null)
					loadHeatMapProgressBar.setMessage("Removing HeatMap...");
				loadHeatMapProgressBar.show();
				mOverlay.remove();
				loadHeatMapProgressBar.dismiss();
			} else {
				loadHeatMapProgressBar.setMessage("Loading HeatMap...");
				loadHeatMapProgressBar.show();
				createHeatMap();
				button_drawer.setText(HEATMAPBUTTON_HIDESTRING_STRING);
				loadHeatMapProgressBar.dismiss();
			}
			break;
		case R.id.tweetsmakers_button:
			if (button_drawer.getText() == HIDETWEETSMAKERSBTNTXT) {
				button_drawer.setText(SHOWTWEETSMAKERSBTNTXT);

				loadHeatMapProgressBar.setMessage("Hiding Markers...");
				loadHeatMapProgressBar.show();

				// hide makers layer
				for (Marker marker : markerList) {
					marker.setVisible(false);
				}
				loadHeatMapProgressBar.dismiss();
			} else {

				if (markerList.size() > 0) {
					loadHeatMapProgressBar.setMessage("Showing Markers...");
					loadHeatMapProgressBar.show();
					for (Marker marker : markerList) {
						marker.setVisible(true);
					}
				} else {
					loadHeatMapProgressBar.setMessage("Creating Markers...");
					loadHeatMapProgressBar.show();
					addMarkersToMap();
				}
				button_drawer.setText(HIDETWEETSMAKERSBTNTXT);
				loadHeatMapProgressBar.dismiss();
			}
			break;
		case R.id.show_timeline:
			Intent intent = new Intent(this, TimeLineActivity.class);
			startActivity(intent);
			break;
		}

		// mDrawerLayout.closeDrawers();
	}

	public void addMarkersToMap() {
		MarkerOptions markerOptions;
		Marker marker;
		if (mMap != null) {

			for (Tweet tweet : tweetsList) {
				markerOptions = new MarkerOptions()
						.position(
								new LatLng(tweet.getLatitude(), tweet
										.getLongtitue())).flat(true)
						.snippet(tweet.getTweetContent()).anchor(0.5f, 0.5f);
				marker = mMap.addMarker(markerOptions);
				markerList.add(marker);
				markerMap.put(marker.getId(), tweet);
			}
		}
	}

	private void setUpMapIfNeeded() {

		if (mMap != null) {
			return;
		}
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		if (mMap == null) {
			return;
		}
		// Initialize map options. For example:
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setCompassEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerOfAmerican,
				2));
	}

	public void setCurrentLocation() {

		mMap.setMyLocationEnabled(true);
		// Getting LocationManager object from System Service LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {

			onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(provider, 20000, 0, this);
	}

	protected void createHeatMap() {

		buildTweetLatLngList();
		// mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25,
		// 143),3));
		if (tweetLatLngList != null) {
			// mOverlay.clearTileCache();
			mProvider = new HeatmapTileProvider.Builder().data(tweetLatLngList)
					.build();
			mProvider.setData(tweetLatLngList);
			mOverlay = mMap.addTileOverlay(new TileOverlayOptions()
					.tileProvider(mProvider));
		}
	}

	private void buildTweetLatLngList() {
		tweetLatLngList = new ArrayList<LatLng>();
		LatLng latLng;
		for (Tweet tweet : tweetsList) {

			latLng = new LatLng(tweet.getLatitude(), tweet.getLongtitue());
			tweetLatLngList.add(latLng);
		}
	}

	public void setTweetsList(List<Tweet> tweetsList) {
		this.tweetsList = tweetsList;
	}

	public String getSearchString() {
		return searchString;
	}

	public void updateMainActivity(List<Tweet> tweetsList) {

		this.tweetsList = tweetsList;
		createHeatMap();
		heatMapButton.setText(HEATMAPBUTTON_HIDESTRING_STRING);
		loadHeatMapProgressBar.dismiss();
	}

	public void updateMakerInfoWindow(Tweet resultTweet) {

		clickedMarker.setTitle("User: " + resultTweet.getUserID() + "  Time: "
				+ formatter.format(resultTweet.getTweetTime()));
		clickedMarker.setSnippet(resultTweet.getTweetContent());
		clickedMarker.showInfoWindow();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
}