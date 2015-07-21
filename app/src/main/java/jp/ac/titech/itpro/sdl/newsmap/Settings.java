package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

/**
 * Created by tm on 2015/07/21.
 */
public class Settings extends Activity {
    final static String TAG = "settings";

    private RadioGroup radioGroupRSS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG + "/onCreate", "onCreate");

        setContentView(R.layout.settings);
        getActionBar().setTitle(R.string.app_settings);

        radioGroupRSS = (RadioGroup) findViewById(R.id.radioGroupRSS);
        radioGroupRSS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup == radioGroupRSS){
                    switch (i){
                        case R.id.radioButtonNHK:
                            break;
                        case R.id.radioButtonYahoo :
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG + "/onStart", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG + "/onStop", "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG + "/onStop", "onStop");
    }
}
