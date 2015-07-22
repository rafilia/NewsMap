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
    private final static String TAG = "NewsInfoDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        NewsInfo entry = getArguments().getParcelable("newsEntry");

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.news_dialog);

        TextView locationText = (TextView) dialog.findViewById(R.id.newsDialog_location);
        TextView issueDateText = (TextView) dialog.findViewById(R.id.newsDialog_issueDate);
        TextView descText = (TextView) dialog.findViewById(R.id.newsDialog_descText);
        TextView urlText = (TextView) dialog.findViewById(R.id.newsDialog_url);

        dialog.setTitle(entry.getTitle());
        locationText.setText(entry.getLocation());
        issueDateText.setText(entry.getIssueDate().toString());
        descText.setText(entry.getContents());
        urlText.setText(entry.getURL());

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
