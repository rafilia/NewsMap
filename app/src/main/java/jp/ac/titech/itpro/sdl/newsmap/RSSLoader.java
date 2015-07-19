package jp.ac.titech.itpro.sdl.newsmap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

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

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mMapsActivity);
        mProgressDialog.setMessage("Now Loading...");
        mProgressDialog.show();
    }

    @Override
    protected ArrayList<NewsInfo> doInBackground(String... params) {
        try{
            URL feedurl = new URL(params[0]);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedurl));

            for(Object obj: feed.getEntries()){
                SyndEntry entry = (SyndEntry) obj;
                Log.i(TAG, entry.getTitle());
                Log.i(TAG, entry.getLink());
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
