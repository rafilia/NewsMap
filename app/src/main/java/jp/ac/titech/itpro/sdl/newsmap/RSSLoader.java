package jp.ac.titech.itpro.sdl.newsmap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tm on 2015/07/19.
 */
public class RSSLoader extends AsyncTask<String, Integer, ArrayList<NewsInfo>> {
    private final static String TAG = "RSSLoader";
    private MapsActivity mMapsActivity;
    private ArrayList<NewsInfo> mNewsInfo;
    private int mFeedNumber;
    private ProgressDialog mProgressDialog;

    private final int MAX_FEED_NUM = 75;

    // for location search
    private final static String address_re = "((北海道|東京都|(京都|大阪)府|(鹿児島|神奈川|和歌山)県|.{2}県).{1,8}(村|町|市|区))";
    private final static Pattern address_pattern = Pattern.compile(address_re);

    private final static String video_re = "videonews";
    private final static Pattern videonews_pattern = Pattern.compile(video_re);

    public RSSLoader (MapsActivity activity, ArrayList<NewsInfo> newsinfo, int feed_number){
        mMapsActivity = activity;
        mNewsInfo = newsinfo;
        mFeedNumber = feed_number;
    }

    // show Progress Bar during execution
    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mMapsActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG+"/progressbar", "canceled");
                        cancel(true);
                        mProgressDialog.cancel();
                    }
                });
        mProgressDialog.show();
    }

    @Override
    protected ArrayList<NewsInfo> doInBackground(String... params) {
        try{
            // Get RSS Feed
            Log.i(TAG+"/url", params[mFeedNumber]);
            URL feedurl = new URL(params[mFeedNumber]);
            SyndFeedInput input = new SyndFeedInput();
            mProgressDialog.setProgress(5);
            SyndFeed feed = input.build(new XmlReader(feedurl));
            mProgressDialog.setProgress(20);

            // Search Location
            int i=0;
            for(Object obj: feed.getEntries()){
                if(isCancelled()) {
                    return mNewsInfo;
                }
                mProgressDialog.setProgress(20+i);

                // Get RSS entry info
                SyndEntry entry = (SyndEntry) obj;
                String entry_title = entry.getTitle();
                String entry_url = entry.getLink();

                // GET date info
                Date entry_date = null;
                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss 'JST' yyyy", Locale.ENGLISH);
                try {
                    entry_date = df.parse(String.valueOf(entry.getPublishedDate()));
                    Log.i(TAG + "/date", String.valueOf(entry_date));
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }

                Log.i(TAG+"/title", entry_title);
                Log.i(TAG+"/entry url", entry_url);

                // GET main article
                String main_text="";
                // yahoo
                if(mFeedNumber == 0) {
                    // GET 'Detailed Link'
                    org.jsoup.nodes.Document doc = Jsoup.connect(entry_url).get();
                    String detailed_link = doc.select("a.newsLink").first().attr("href");

                    Log.i(TAG + "/detailed link", detailed_link);

                    org.jsoup.nodes.Document doc2 = Jsoup.connect(detailed_link).get();
                    Matcher md = videonews_pattern.matcher(detailed_link);

                    if (md.find()) {
                        // videonews
                        main_text = doc2.select("div.yjMt").text();
                    } else {
                        // textnews
                        main_text = doc2.select("p.ynDetailText").first().text();
                    }
                // nhk
                } else if (mFeedNumber == 1){
                    main_text = entry.getDescription().getValue();
                }
                Log.i(TAG+"/main text", main_text);

                // find location info
                Matcher m = address_pattern.matcher(main_text);
                String location = "";
                if(m.find()){
                    location = m.group();
                    Log.i(TAG+"/Location", location);
                } else {
                    Log.i(TAG+"/Location", "location cannot detect");
                }

                NewsInfo newsEntry = new NewsInfo(entry_title, entry_url, location, entry_date);
                mNewsInfo.add(newsEntry);

                if(i++ == MAX_FEED_NUM) break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return mNewsInfo;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsInfo> newsinfo) {
        // put marker on the map
        mProgressDialog.setProgress(90);
        for(NewsInfo entry : newsinfo){
            if(! "".equals(entry.location)){
                Geocoder geocoder = new Geocoder(mMapsActivity, Locale.getDefault());
                try {
                    // get 1 result
                    List<Address> addressList = geocoder.getFromLocationName(entry.location, 1);
                    if(!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                        // add marker on the map
                        mMapsActivity.addMarker(latlng, entry);
                    } else {
                        Log.i(TAG+"/onPostEx", "cannot search Location :" + entry.location);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        mProgressDialog.dismiss();

    }

    @Override
    protected void onCancelled() {
        Log.d(TAG+"/onCancelled", "cancelled");
    }
}
