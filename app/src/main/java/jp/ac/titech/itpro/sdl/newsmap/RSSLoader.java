package jp.ac.titech.itpro.sdl.newsmap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.jsoup.Jsoup;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by tm on 2015/07/19.
 */
public class RSSLoader extends AsyncTask<String, Integer, ArrayList<NewsInfo>> {
    private final static String TAG = "RSSLoader";
    private MapsActivity mMapsActivity;
    private ArrayList<NewsInfo> mNewsInfo;
    private ProgressDialog mProgressDialog;

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

                Log.i(TAG, title);
                Log.i(TAG, entry_url);

                // GET 'Detailed Link'
                org.jsoup.nodes.Document doc = Jsoup.connect(entry_url).get();
                String detailed_link = doc.select("a.newsLink").first().attr("href");

                Log.i(TAG, detailed_link);

                // GET main article
                org.jsoup.nodes.Document doc2 = Jsoup.connect(detailed_link).get();
                String main_text = doc2.select("p.ynDetailText").first().text();

                Log.i(TAG, main_text);

                // find location info
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsInfo> newsinfo) {
        mProgressDialog.dismiss();
    }
}
