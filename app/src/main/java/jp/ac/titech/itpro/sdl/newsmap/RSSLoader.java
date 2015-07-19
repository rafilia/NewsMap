package jp.ac.titech.itpro.sdl.newsmap;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by tm on 2015/07/19.
 */
public class RSSLoader extends AsyncTask<String, Integer, ArrayList<NewsInfo>> {
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
            URL url = new URL(params[0]);
            InputStream is = url.openConnection().getInputStream();

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
