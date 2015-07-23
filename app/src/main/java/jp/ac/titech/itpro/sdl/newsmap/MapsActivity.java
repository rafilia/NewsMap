package jp.ac.titech.itpro.sdl.newsmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class MapsActivity extends FragmentActivity {
    private final static String TAG = "MapsActivity";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    // initial location
    private final static LatLng INITIAL_LOCATION = new LatLng(38.564, 138.978);
    private final static float INITIAL_ZOOM_LEVEL = (float) 5;
    private final static float CLOSE_ZOOM_LEVEL = (float) 13;

    private float prevZoomLevel = INITIAL_ZOOM_LEVEL;
    private float backZoomLevel;
    private LatLng prevLatLng = INITIAL_LOCATION;
    private LatLng backLatLng;

    private final static String FeedURL[] = {"http://www3.nhk.or.jp/rss/news/cat1.xml",
            "http://rss.dailynews.yahoo.co.jp/fc/local/rss.xml"};

    private ArrayList<NewsInfo> mNewsInfo;
    private int currentNewsID = 0;
    private ArrayList<Marker> mMarkers;
    private Boolean showCurrentMarkerInfo = true;

    private SharedPreferences sp;
    private ConnectivityManager cm;

    private int backButtonVisibility = View.INVISIBLE;

    private ArrayList<LatLng> mLatLngList;
    private ArrayList<ArrayList<Integer>> mNewsAtSameLocation;
    private int currentNewsLocationID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        Button centerButton, reloadButton, backButton, prevButton, newestButton, nextButton;
        centerButton = (Button) findViewById(R.id.centerButton);
        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LOCATION, INITIAL_ZOOM_LEVEL));
            }
        });

        reloadButton = (Button) findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // force reload map
                mNewsInfo = new ArrayList<>();
                currentNewsID = 0;
                loadRSS();
            }
        });

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) findViewById(R.id.backButton);
                b.setVisibility(View.INVISIBLE);
                backButtonVisibility = View.INVISIBLE;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(backLatLng, backZoomLevel));
            }
        });

        prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrevMarker(true);
            }
        });
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextMarker(true);
            }
        });
        newestButton = (Button) findViewById(R.id.newestButton);
        newestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewestMarker(true);
            }
        });

        if (mNewsInfo == null) {
            mNewsInfo = new ArrayList<>();
        }
        mMarkers = new ArrayList<>();
        mLatLngList = new ArrayList<>();
        mNewsAtSameLocation = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG + "/onResume", "onResume");
        super.onResume();

        setUpMapIfNeeded();
        setUpMap();
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setVisibility(backButtonVisibility);

        loadRSS();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSavedInstanceState");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("NewsInfo", mNewsInfo);
        outState.putInt("currentNewsID", currentNewsID);
        outState.putInt("currentNewsLocationID", currentNewsLocationID);
        outState.putBoolean("showCurrentMarkerInfo", showCurrentMarkerInfo);


        outState.putFloat("prevZoomLevel", prevZoomLevel);
        outState.putParcelable("prevLatLng", prevLatLng);
        outState.putFloat("backZoomLevel", backZoomLevel);
        outState.putParcelable("backLatLng", backLatLng);

        outState.putInt("backButtonVisibility", backButtonVisibility);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        mNewsInfo = savedInstanceState.getParcelableArrayList("NewsInfo");
        currentNewsID = savedInstanceState.getInt("currentNewsID");
        currentNewsLocationID = savedInstanceState.getInt("currentNewsLocationID");
        showCurrentMarkerInfo = savedInstanceState.getBoolean("showCurrentMarkerInfo");

        prevZoomLevel = savedInstanceState.getFloat("prevZoomLevel");
        prevLatLng = savedInstanceState.getParcelable("prevLatLng");
        backZoomLevel = savedInstanceState.getFloat("backZoomLevel");
        backLatLng = savedInstanceState.getParcelable("backLatLng");

        backButtonVisibility = savedInstanceState.getInt("backButtonVisibility");
    }

    private void loadRSS() {
        Log.i(TAG + "/loadRSS", "loadRSS");
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            mMap.clear();
            mMarkers.clear();
            RSSLoader rssLoader = new RSSLoader(this);
            rssLoader.execute(FeedURL);
        } else {
            Toast.makeText(this, "No Network Connection! Cannot Load News Data!", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<NewsInfo> getNewsInfo() {
        return mNewsInfo;
    }

    public void setNewsInfo(ArrayList<NewsInfo> newsinfo) {
        mNewsInfo = newsinfo;
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
                        // show info window dialog
                        showNewsInfoDialog();
                    }
                });

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    //public View getInfoWindow(Marker marker) {
                    public View getInfoContents(Marker marker) {
                        currentNewsLocationID = Integer.parseInt(marker.getSnippet());
                        ArrayList<Integer> newsIDList = mNewsAtSameLocation.get(currentNewsLocationID);

                        currentNewsID = newsIDList.get(0);
                        showCurrentMarkerInfo = true;

                        View view = getLayoutInflater().inflate(R.layout.info_window_list, null);
                        ListView list = (ListView) view.findViewById(R.id.info_list);

                        List<NewsAbstList> nList = new ArrayList<>();

                        SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_PATTERN);
                        for (int i = 0; i < newsIDList.size(); i++) {
                            NewsAbstList item = new NewsAbstList(
                                    mNewsInfo.get(newsIDList.get(i)).getTitle(),
                                    //mNewsInfo.get(newsIDList.get(i)).getIssueDate().toString());
                                    sdf.format(mNewsInfo.get(newsIDList.get(i)).getIssueDate())
                            );
                            nList.add(item);
                        }
                        TextView locationText = (TextView) view.findViewById(R.id.info_location);
                        locationText.setText(mNewsInfo.get(currentNewsID).getLocation());

                        NewsInfoListAdapter adapter = new NewsInfoListAdapter(getBaseContext(), 0, nList);

                        list.setAdapter(adapter);

                        // NOT work (can't get event)
//                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                Log.i(TAG+"/marker", "news list item clicked");
//                                // show info window dialog
//                                Bundle bundle = new Bundle();
//                                bundle.putParcelable("newsEntry", mNewsInfo.get(currentNewsID));
//
//                                NewsInfoDialog nid = new NewsInfoDialog();
//                                nid.setArguments(bundle);
//                                nid.show(getFragmentManager(), "newsInfoDialog");
//                            }
//                        });

                        return view;
                    }

                    @Override
                    public View getInfoWindow(Marker marker) {
                        //public View getInfoContents(Marker marker) {
                        return null;
                    }
                });


                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        prevLatLng = cameraPosition.target;
                        prevZoomLevel = cameraPosition.zoom;
                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        showCurrentMarkerInfo = false;
                    }
                });
                //setUpMap();
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
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LOCATION, INITIAL_ZOOM_LEVEL))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prevLatLng, prevZoomLevel));
    }

    public void showNewsInfoDialog() {
        Bundle bundle = new Bundle();
        ArrayList<Integer> newsIDList = mNewsAtSameLocation.get(currentNewsLocationID);
        bundle.putInt("newsEntryNum",newsIDList.size());
        for(int i = 0; i<newsIDList.size(); i++){
            //bundle.putParcelable("newsEntry", mNewsInfo.get(currentNewsID));
            bundle.putParcelable("newsEntry" + i, mNewsInfo.get(newsIDList.get(i)));
        }

        NewsInfoDialog nid = new NewsInfoDialog();
        nid.setArguments(bundle);
        nid.show(

        getFragmentManager(),            "newsInfoDialog");
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
                // delete current news info
                mNewsInfo.clear();
                currentNewsID=0;
                loadRSS();
                sp.edit().putBoolean("needRefresh", false).commit();
            }
        }
    }

    public void addMarkers(){
        if(mNewsInfo.isEmpty()){
            Log.i(TAG, "mNewsInfo is empty!");
            return;
        }

        MakeMarkers();
    }

    public void MakeMarkers(){

        mLatLngList.clear();
        mNewsAtSameLocation.clear();

        // search News entries which have same latlng info
        for(NewsInfo entry : mNewsInfo){
            LatLng l = entry.getLatLng();
            int index = mLatLngList.indexOf(l);
            if(index == -1){
                mLatLngList.add(l);

                ArrayList<Integer> list = new ArrayList<>();
                list.add(entry.getID());
                mNewsAtSameLocation.add(list);
            } else {
                ArrayList<Integer> list = mNewsAtSameLocation.get(index);
                list.add(entry.getID());
                mNewsAtSameLocation.set(index, list);
            }
        }

        // add marker per location
        for(int i = 0; i < mNewsAtSameLocation.size(); i++){
            NewsInfo entry =  mNewsInfo.get(mNewsAtSameLocation.get(i).get(0));

            MarkerOptions mo = new MarkerOptions();
            mo.position(entry.getLatLng());
            mo.title(entry.getTitle());
            // use snippet filed to hold the id
            mo.snippet(String.valueOf(i));
            mo.draggable(false);

            // set icon color depending on its issue_date
            long current = currentTimeMillis();
            long x = current - (entry.getIssueDate()!=null? entry.getIssueDate().getTime() :0);
            BitmapDescriptor icon;
            if(x < 1000*3600*24) {
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            } else if(x < 1000*3600*24*2){
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            } else {
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            }
            mo.icon(icon);

            Marker m = mMap.addMarker(mo);
            mMarkers.add(m);
        }

        if(showCurrentMarkerInfo){
            mMarkers.get(currentNewsLocationID).showInfoWindow();
        }
    }

    // move to prev/next/newest marker and show info window
    public void showPrevMarker(Boolean anime){
        if(mNewsInfo.isEmpty()) return;

        if(--currentNewsLocationID < 0) currentNewsLocationID=mNewsAtSameLocation.size()-1;
        currentNewsID = mNewsAtSameLocation.get(currentNewsLocationID).get(0);

        showCurrentIDMarker(anime);
    }
    public void showNextMarker(Boolean anime){
        if(mNewsInfo.isEmpty()) return;

        if(++currentNewsLocationID == mNewsAtSameLocation.size()) currentNewsLocationID=0;
        currentNewsID = mNewsAtSameLocation.get(currentNewsLocationID).get(0);

        showCurrentIDMarker(anime);
    }
    public void showNewestMarker(Boolean anime){
        if(mNewsInfo.isEmpty()) return;

        currentNewsID=0;
        currentNewsLocationID=0;

        showCurrentIDMarker(anime);
    }

    public void showCurrentIDMarker(Boolean anime){
        showCurrentMarkerInfo = true;
        mMarkers.get(currentNewsLocationID).showInfoWindow();
        if(anime) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLngList.get(currentNewsLocationID), mMap.getCameraPosition().zoom));
        else mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngList.get(currentNewsLocationID), mMap.getCameraPosition().zoom));
    }

    // zoom to selected marker
    public void lookCloser(){
        backZoomLevel = mMap.getCameraPosition().zoom;
        backLatLng = mMap.getCameraPosition().target;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLngList.get(currentNewsLocationID), CLOSE_ZOOM_LEVEL));

        Button b = (Button) findViewById(R.id.backButton);
        b.setVisibility(View.VISIBLE);
        backButtonVisibility = View.VISIBLE;
    }

    // show next/prev dialog
    public void openNextDialog(){
        showNextMarker(false);
        showNewsInfoDialog();
    }
    public void openPrevDialog(){
        showPrevMarker(false);
        showNewsInfoDialog();
    }
}
