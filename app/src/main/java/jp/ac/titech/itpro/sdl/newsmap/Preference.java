package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by tm on 2015/07/21.
 */
public class Preference extends Activity {
    private final static String TAG = "Preference";
    private myPrefFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = new myPrefFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mFragment).commit();
    }

    public static class myPrefFragment extends PreferenceFragment
                    implements SharedPreferences.OnSharedPreferenceChangeListener{

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preference);

            // set initial value
            ListPreference RSSFeedList = (ListPreference) findPreference ("prefRSSFeed");
            RSSFeedList.setValueIndex(0);
            RSSFeedList.setSummary(getResources().getStringArray(R.array.RSS_feeds)[0]);

            ListPreference LoadMaxValue = (ListPreference) findPreference("prefMaxLoadNum");
            LoadMaxValue.setValueIndex(2);
            LoadMaxValue.setSummary(getResources().getStringArray(R.array.Max_load_num)[2]);

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if(s.equals("prefRSSFeed")) {
                ListPreference RSSFeedList = (ListPreference) findPreference ("prefRSSFeed");
                RSSFeedList.setSummary(getResources().getStringArray(R.array.RSS_feeds)[Integer.parseInt(RSSFeedList.getValue())]);
                Log.i(TAG + "/changed", "set rss summary");
            } else if(s.equals("prefMaxLoadNum")){
                ListPreference LoadMaxValue = (ListPreference) findPreference("prefMaxLoadNum");
                LoadMaxValue.setSummary(LoadMaxValue.getValue());
                Log.i(TAG + "/changed", "set load max value summary");
            }
        }
    }
}