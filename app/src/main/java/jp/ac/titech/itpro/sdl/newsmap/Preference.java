package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by tm on 2015/07/21.
 */
public class Preference extends Activity {
    private PreferenceFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = new PreferenceFragment() {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.layout.preference);
            }
        };

        getFragmentManager().beginTransaction().replace(android.R.id.content, mFragment).commit();
    }
}
