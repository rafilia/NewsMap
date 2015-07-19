package jp.ac.titech.itpro.sdl.newsmap;

import android.app.ProgressDialog;
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
import java.util.ArrayList;
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
    private ProgressDialog mProgressDialog;

    // for location search
    private final static String address_re = "((北海道|東京都|(京都|大阪)府|(鹿児島|神奈川|和歌山)県|.{2}県).{1,8}(村|町|市|区))";
    private final static Pattern address_pattern = Pattern.compile(address_re);

    public RSSLoader (MapsActivity activity, ArrayList<NewsInfo> newsinfo){
        mMapsActivity = activity;
        mNewsInfo = newsinfo;
    }

    // show Progress Bar during execution
    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mMapsActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.show();
    }

    @Override
    protected ArrayList<NewsInfo> doInBackground(String... params) {
        try{
            // Get RSS Feed
            URL feedurl = new URL(params[0]);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedurl));

            // Search Location
            for(Object obj: feed.getEntries()){
                // Get RSS entry info
                SyndEntry entry = (SyndEntry) obj;
                String title = entry.getTitle();
                String entry_url = entry.getLink();

                Log.i(TAG+"/title", title);
                Log.i(TAG+"/entry url", entry_url);

                // GET 'Detailed Link'
                org.jsoup.nodes.Document doc = Jsoup.connect(entry_url).get();
                String detailed_link = doc.select("a.newsLink").first().attr("href");

                Log.i(TAG+"/detailed link", detailed_link);

                // GET main article
                org.jsoup.nodes.Document doc2 = Jsoup.connect(detailed_link).get();
                String main_text = doc2.select("p.ynDetailText").first().text();

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

                NewsInfo newsEntry = new NewsInfo(title, entry_url, location);
                mNewsInfo.add(newsEntry);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return mNewsInfo;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsInfo> newsinfo) {
        mProgressDialog.dismiss();

        // put marker on the map
        for(NewsInfo entry : newsinfo){
            if(! "".equals(entry.location)){
                Geocoder geocoder = new Geocoder(mMapsActivity, Locale.getDefault());
                try {
                    // get 1 result
                    List<Address> addressList = geocoder.getFromLocationName(entry.location, 1);
                    Address address = addressList.get(0);
                    LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                    // add marker on the map
                    mMapsActivity.addMarker(location, entry.title);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
