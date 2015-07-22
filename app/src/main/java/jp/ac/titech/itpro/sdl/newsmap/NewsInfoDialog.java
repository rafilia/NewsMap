package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tm on 2015/07/22.
 */
public class NewsInfoDialog extends DialogFragment {
//    private interface OnOpenButtonClickListener {
//        public void onClicked();
//    }
//    private interface OnLookButtonClickListener {
//        public void onClicked();
//    }
//
//    private OnOpenButtonClickListener mOpenButtonClickListener;
//    private OnLookButtonClickListener mLookButtonClickListener;
//
//    public void setOpenButtonClickListener(OnOpenButtonClickListener l){
//        mOpenButtonClickListener = l;
//    }
//    public void setLookButtonClickListener(OnLookButtonClickListener l){
//        mLookButtonClickListener = l;
//    }

    private final static String TAG = "NewsInfoDialog";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.news_dialog);

        TextView locationText = (TextView) dialog.findViewById(R.id.newsDialog_location);
        TextView descText = (TextView) dialog.findViewById(R.id.newsDialog_descText);

        dialog.setTitle(getArguments().getString("title"));
        locationText.setText(getArguments().getString("location"));
        descText.setText("detail description");

        Button openButton = (Button) dialog.findViewById(R.id.newsDialog_open);
        Button lookButton = (Button) dialog.findViewById(R.id.newsDialog_look);

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "open button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.openURL();
            }
        });

        lookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "look button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.lookCloser();
                dismiss();
            }
        });

        return dialog;
    }
}
