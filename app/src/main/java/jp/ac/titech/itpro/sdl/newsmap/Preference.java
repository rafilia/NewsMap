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
// preference activity
// RSS Feed ... 0:NHK, 1:yahoo
// max RSS load num ... 25/50/75/100
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

            // set summary
            ListPreference RSSFeedList = (ListPreference) findPreference ("prefRSSFeed");
            RSSFeedList.setSummary(getResources().getStringArray(R.array.RSS_feeds)[Integer.parseInt(RSSFeedList.getValue())]);

            ListPreference LoadMaxValue = (ListPreference) findPreference("prefMaxLoadNum");
            LoadMaxValue.setSummary(LoadMaxValue.getValue());

            ListPreference ModeList = (ListPreference) findPreference("prefMode");
            ModeList.setSummary(ModeList.getValue());
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            // change summary dynamically
            if(s.equals("prefRSSFeed")) {
                ListPreference RSSFeedList = (ListPreference) findPreference ("prefRSSFeed");
                RSSFeedList.setSummary(getResources().getStringArray(R.array.RSS_feeds)[Integer.parseInt(RSSFeedList.getValue())]);
                Log.i(TAG + "/changed", "set rss summary");

                // to be reloaded RSS after return to main activity
                sharedPreferences.edit().putBoolean("needRefresh", true).commit();
            } else if(s.equals("prefMaxLoadNum")){
                ListPreference LoadMaxValue = (ListPreference) findPreference("prefMaxLoadNum");
                LoadMaxValue.setSummary(LoadMaxValue.getValue());
                Log.i(TAG + "/changed", "set load max value summary");

                // to be reloaded RSS after return to main activity
                sharedPreferences.edit().putBoolean("needRefresh", true).commit();
            } else if(s.equals("prefMode")){
                ListPreference ModeList = (ListPreference) findPreference("prefMode");
                ModeList.setSummary(ModeList.getValue());
                Log.i(TAG + "/changed", "set View Mode");
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}