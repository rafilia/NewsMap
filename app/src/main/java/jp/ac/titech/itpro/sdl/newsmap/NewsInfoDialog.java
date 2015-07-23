package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
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
    private Uri uri;

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
        Button prevButton = (Button) dialog.findViewById(R.id.newsDialog_prev);
        Button nextButton = (Button) dialog.findViewById(R.id.newsDialog_next);


        uri = Uri.parse(entry.getURL());

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "open button clicked");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "prev button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.openPrevDialog();
                dismiss();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "next button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.openNextDialog();
                dismiss();
            }
        });

        return dialog;
    }
}
