package jp.ac.titech.itpro.sdl.newsmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

public class MapsActivity extends FragmentActivity {
    private final static String TAG = "MapsActivity";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    // initial location
    private final static LatLng INITIAL_LOCATION = new LatLng(38.564, 138.978);
    private final static float INITIAL_ZOOM_LEVEL = (float) 5;

    private Button centerButton, reloadButton;

    private final static String FeedURL[] = {"http://www3.nhk.or.jp/rss/news/cat1.xml",
                                             "http://rss.dailynews.yahoo.co.jp/fc/local/rss.xml"};

    private int feed_number;

    private ArrayList<NewsInfo> mNewsInfo;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        centerButton = (Button) findViewById(R.id.centerButton);
        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpMap();
            }
        });

        reloadButton = (Button) findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reload map
            }
        });

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        mNewsInfo = new ArrayList<NewsInfo>();
        RSSLoader rssLoader = new RSSLoader(this, mNewsInfo);
        rssLoader.execute(FeedURL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // set MAP configuration
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        marker.hideInfoWindow();
                    }
                });
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    //public View getInfoWindow(Marker marker) {
                    public View getInfoContents(Marker marker) {
                            View view = getLayoutInflater().inflate(R.layout.info_window, null);
                        TextView title = (TextView) view.findViewById(R.id.info_title);
                        TextView location = (TextView) view.findViewById(R.id.info_location);

                        title.setText(marker.getTitle());
                        location.setText(marker.getSnippet());

                        return view;
                    }

                    @Override
                    public View getInfoWindow(Marker marker) {
                    //public View getInfoContents(Marker marker) {
                        return null;
                    }
                });

                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LOCATION, INITIAL_ZOOM_LEVEL));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LOCATION, INITIAL_ZOOM_LEVEL));
    }


    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Log.d("menu/settings", "setting");
                Intent intent = new Intent(getApplicationContext(), Preference.class);
                startActivityForResult(intent, Consts.FROM_PREF);
                break;
            default:
                //return super.onOptionsItemSelected(item);
                return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Consts.FROM_PREF) {
            Log.i(TAG, "return from Preferences");
            if(sp.getBoolean("needRefresh", false)){
                Log.i(TAG, "to be refreshed");
                sp.edit().putBoolean("needRefresh", false).commit();
            }
        }
    }

    public void addMarker(LatLng _latlng, NewsInfo entry){
        MarkerOptions mo = new MarkerOptions();
        mo.position(_latlng);
        mo.title(entry.title);
        mo.snippet(entry.location);
        mo.draggable(false);

        // icon color
        long current = currentTimeMillis();
        long x = current - (entry.issue_date!=null? entry.issue_date.getTime() :0);
        BitmapDescriptor icon;
        if(x < 1000*3600*24) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        } else if(x < 1000*3600*24*2){
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        } else {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }
        mo.icon(icon);

        mMap.addMarker(mo);
    }
}
