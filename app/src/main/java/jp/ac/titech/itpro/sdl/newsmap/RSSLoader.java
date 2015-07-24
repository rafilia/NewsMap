package jp.ac.titech.itpro.sdl.newsmap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.jsoup.Jsoup;

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
public class RSSLoader extends AsyncTask<String, Integer, Void> {
    private final static String TAG = "RSSLoader";
    private MapsActivity mMapsActivity;
    private ArrayList<NewsInfo> mNewsInfo;
    private int mFeedNumber;
    private int mLoadNumber;
    private ProgressDialog mProgressDialog;

    private SharedPreferences sp;

    // for location search
    private final static String address_re = "((北海道|東京都|(京都|大阪)府|(鹿児島|神奈川|和歌山)県|.{2}県).{1,8}(村|町|市|区))";
    private final static Pattern address_pattern = Pattern.compile(address_re);

    private final static String pref_re = "(北海道|東京都*|(京都|大阪)府*|" +
            "(青森|岩手|宮城|秋田|福島|)" + "茨城|栃木|群馬|神奈川|千葉|埼玉|" +
            "新潟|富山|石川|福井|山梨|長野|岐阜|静岡|愛知|" + "三重|滋賀|兵庫|奈良|和歌山|" + "鳥取|島根|広島|岡山|山口|" +
            "徳島|高知|愛媛|香川|" + "福岡|佐賀|長崎|熊本|宮崎|大分|鹿児島|沖縄)県*";
    private final static String video_re = "videonews";
    private final static Pattern videonews_pattern = Pattern.compile(video_re);

    public RSSLoader (MapsActivity activity){
        sp = PreferenceManager.getDefaultSharedPreferences(activity);

        mMapsActivity = activity;
        mNewsInfo = mMapsActivity.getNewsInfo();
        mFeedNumber = Integer.parseInt(sp.getString("prefRSSFeed", "0"));
        mLoadNumber = Integer.parseInt(sp.getString("prefMaxLoadNum", "75"));

        Log.i(TAG+"/onCreate", "feed number :" + sp.getString("prefRSSFeed", "0"));
        Log.i(TAG+"/onCreate", "max load number :" + sp.getString("prefMaxLoadNum", "75"));
    }

    // show Progress Bar during execution
    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mMapsActivity);
        mProgressDialog.setMessage("Now Loading...");
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG + "/progressbar", "canceled");
                        cancel(true);
                        mProgressDialog.cancel();
                    }
                });
         mProgressDialog.setCancelable(false);
//        // if back button clicked ...
//        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                cancel(true);
//                mProgressDialog.cancel();
//            }
//        });
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        if(!mNewsInfo.isEmpty()) {
            return null;
        }

        try {
            // Get RSS Feed
            Log.i(TAG + "/url", params[mFeedNumber]);
            URL feedurl = new URL(params[mFeedNumber]);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedurl));
            mProgressDialog.setProgress(20);

            // Search Location
            int i = 0;
            int id= 0;
            Geocoder geocoder = new Geocoder(mMapsActivity, Locale.getDefault());
            for (Object obj : feed.getEntries()) {
                if (isCancelled()) {
                    return null;
                }
                mProgressDialog.setProgress(i + 20);

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

                Log.i(TAG + "/title", entry_title);
                Log.i(TAG + "/entry url", entry_url);

                // GET main article
                String main_text = "";
                // yahoo
                if (mFeedNumber == Consts.FEED_YAHOO) {
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
                } else if (mFeedNumber == Consts.FEED_NHK) {
                    main_text = entry.getDescription().getValue();
                }
                Log.i(TAG + "/main text", main_text);

                // find location info
                Matcher m = address_pattern.matcher(main_text);
                String entry_location = "";
                LatLng entry_latlng = new LatLng(0,0);
                if (m.find()) {
                    entry_location = m.group();
                    Log.i(TAG + "/Location", entry_location);

                    List<Address> addressList = geocoder.getFromLocationName(entry_location, 1);
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        entry_latlng = new LatLng(address.getLatitude(), address.getLongitude());

                        // add entry if it has location info
                        NewsInfo newsEntry = new NewsInfo(id++, entry_title, entry_url, entry_location, entry_date, entry_latlng, main_text);
                        mNewsInfo.add(newsEntry);
                    } else {
                        Log.i(TAG + "/onPostEx", "cannot search Location :" + entry_location);
                    }
                } else {
                    Log.i(TAG + "/Location", "location cannot detect");
                }

                // add all entry (even if location not found)
                //NewsInfo newsEntry = new NewsInfo(i, entry_title, entry_url, entry_location, entry_date, entry_latlng, main_text);
                //mNewsInfo.add(newsEntry);

                if (i++ == mLoadNumber) break;
            }
        } catch  (Exception e) {
            e.printStackTrace();
        }

        //Collections.sort(mNewsInfo, new NewsInfoComparator());
        mMapsActivity.setNewsInfo(mNewsInfo);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // put markers on the map
        mMapsActivity.addMarkers();
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG+"/onCancelled", "cancelled");
        sp.edit().putBoolean("needRefresh", true).commit();
    }
}
