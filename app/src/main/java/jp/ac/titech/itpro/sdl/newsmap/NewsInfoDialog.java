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

import java.util.ArrayList;

/**
 * Created by tm on 2015/07/22.
 */
public class NewsInfoDialog extends DialogFragment {
    private final static String TAG = "NewsInfoDialog";
    private Uri uri;

    // number of entry at this location
    private int entryNum;
    private int currentLocalNewsID;
    private ArrayList<NewsInfo> nList;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //NewsInfo entry = getArguments().getParcelable("newsEntry");

        currentLocalNewsID = 0;
        entryNum = getArguments().getInt("newsEntryNum");
        nList = new ArrayList<>();
        for(int i = 0; i < entryNum; i++){
            NewsInfo entry = getArguments().getParcelable("newsEntry" + i);
            nList.add(entry);
        }

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.news_dialog);

        TextView locationText = (TextView) dialog.findViewById(R.id.newsDialog_location);
        TextView issueDateText = (TextView) dialog.findViewById(R.id.newsDialog_issueDate);
        TextView descText = (TextView) dialog.findViewById(R.id.newsDialog_descText);
        TextView urlText = (TextView) dialog.findViewById(R.id.newsDialog_url);

        dialog.setTitle(nList.get(currentLocalNewsID).getTitle());
        locationText.setText(nList.get(currentLocalNewsID).getLocation());
        issueDateText.setText(nList.get(currentLocalNewsID).getIssueDate().toString());
        descText.setText(nList.get(currentLocalNewsID).getContents());
        urlText.setText(nList.get(currentLocalNewsID).getURL());
        uri = Uri.parse(nList.get(currentLocalNewsID).getURL());


        Button openButton = (Button) dialog.findViewById(R.id.newsDialog_open);
        Button lookButton = (Button) dialog.findViewById(R.id.newsDialog_look);
        Button prevButton = (Button) dialog.findViewById(R.id.newsDialog_prev);
        Button nextButton = (Button) dialog.findViewById(R.id.newsDialog_next);
        Button oldButton = (Button) dialog.findViewById(R.id.newsDialog_old);
        Button newButton = (Button) dialog.findViewById(R.id.newsDialog_new);

        if(entryNum != 1){
            oldButton.setEnabled(true);
        }

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

        oldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button oldButton = (Button) getDialog().findViewById(R.id.newsDialog_old);
                Button newButton = (Button) getDialog().findViewById(R.id.newsDialog_new);

                currentLocalNewsID++;
                if(currentLocalNewsID == entryNum-1){
                    oldButton.setEnabled(false);
                }
                newButton.setEnabled(true);

                setDialogInfo();
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button oldButton = (Button) getDialog().findViewById(R.id.newsDialog_old);
                Button newButton = (Button) getDialog().findViewById(R.id.newsDialog_new);

                currentLocalNewsID--;
                if(currentLocalNewsID == 0){
                    newButton.setEnabled(false);
                }
                oldButton.setEnabled(true);

                setDialogInfo();
            }
        });

        return dialog;
    }

    public void setDialogInfo(){
        TextView locationText = (TextView) getDialog().findViewById(R.id.newsDialog_location);
        TextView issueDateText = (TextView) getDialog().findViewById(R.id.newsDialog_issueDate);
        TextView descText = (TextView) getDialog().findViewById(R.id.newsDialog_descText);
        TextView urlText = (TextView) getDialog().findViewById(R.id.newsDialog_url);

        getDialog().setTitle(nList.get(currentLocalNewsID).getTitle());
        locationText.setText(nList.get(currentLocalNewsID).getLocation());
        issueDateText.setText(nList.get(currentLocalNewsID).getIssueDate().toString());
        descText.setText(nList.get(currentLocalNewsID).getContents());
        urlText.setText(nList.get(currentLocalNewsID).getURL());
        uri = Uri.parse(nList.get(currentLocalNewsID).getURL());
    }
}
