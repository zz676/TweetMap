package edu.nyu.cloud.tweetmapx.map;

import android.graphics.Color;
import android.view.View;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

/**
 * @author Zhisheng Zhou
 * @version 1.0
 * 
 * A demo of the Heatmaps library. Demonstrates how the HeatmapTileProvider can
 * be used to create a colored map overlay that visualises many points of
 * weighted importance/intensity, with different colors representing areas of
 * high and low concentration/combined intensity of points.
 */
public class HeatMap {

	/**
	 * Alternative radius for convolution
	 */
	private static final int ALT_HEATMAP_RADIUS = 10;

	/**
	 * Alternative opacity of heatmap overlay
	 */
	private static final double ALT_HEATMAP_OPACITY = 0.4;

	/**
	 * Alternative heatmap gradient (blue -> red) Copied from Javascript version
	 */
	private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
			Color.argb(0, 0, 255, 255),// transparent
			Color.argb(255 / 3 * 2, 0, 255, 255), Color.rgb(0, 191, 255),
			Color.rgb(0, 0, 127), Color.rgb(255, 0, 0) };

	public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = { 0.0f,
			0.10f, 0.20f, 0.60f, 1.0f };

	public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(
			ALT_HEATMAP_GRADIENT_COLORS, ALT_HEATMAP_GRADIENT_START_POINTS);

	private HeatmapTileProvider mProvider;
	private TileOverlay mOverlay;

	private boolean mDefaultGradient = true;
	private boolean mDefaultRadius = true;
	private boolean mDefaultOpacity = true;

	HeatMap(HeatmapTileProvider mProvider,TileOverlay mOverlay){
		
		this.mProvider = mProvider;
		this.mOverlay = mOverlay;
	}
	
	
	/**
	 * Maps name of data set to data (list of LatLngs) Also maps to the URL of
	 * the data set for attribution
	 */
	

	public void changeRadius(View view) {
		if (mDefaultRadius) {
			mProvider.setRadius(ALT_HEATMAP_RADIUS);
		} else {
			mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
		}
		mOverlay.clearTileCache();
		mDefaultRadius = !mDefaultRadius;
	}

	public void changeGradient(View view) {
		if (mDefaultGradient) {
			mProvider.setGradient(ALT_HEATMAP_GRADIENT);
		} else {
			mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
		}
		mOverlay.clearTileCache();
		mDefaultGradient = !mDefaultGradient;
	}

	public void changeOpacity(View view) {
		if (mDefaultOpacity) {
			mProvider.setOpacity(ALT_HEATMAP_OPACITY);
		} else {
			mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
		}
		mOverlay.clearTileCache();
		mDefaultOpacity = !mDefaultOpacity;
	}
}

