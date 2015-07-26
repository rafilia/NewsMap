package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tm on 2015/07/22.
 */
// detailed news info dialog
public class NewsInfoDialog extends DialogFragment {
    private final static String TAG = "NewsInfoDialog";
    private Uri uri;

    SharedPreferences sp;
    Boolean textMode;
    // number of entry at this location
    private int entryNum;
    // current Local ID which displays information
    private int currentLocalNewsID;
    private ArrayList<NewsInfo> nList;

    private TextView descText;
    private View mProgressBar;
    private Boolean mLoadingError = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        currentLocalNewsID = 0;
        entryNum = getArguments().getInt("newsEntryNum");
        // get news entries
        nList = new ArrayList<>();
        for(int i = 0; i < entryNum; i++){
            NewsInfo entry = getArguments().getParcelable("newsEntry" + i);
            nList.add(entry);
        }

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        textMode = ((sp.getString("prefMode", "Text")).equals(getResources().getStringArray(R.array.View_mode)[0]));

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.news_dialog);

        TextView locationText = (TextView) dialog.findViewById(R.id.newsDialog_location);
        TextView issueDateText = (TextView) dialog.findViewById(R.id.newsDialog_issueDate);
        TextView urlText = (TextView) dialog.findViewById(R.id.newsDialog_url);

        descText = (TextView) dialog.findViewById(R.id.newsDialog_descText);
        WebView webView = (WebView) dialog.findViewById(R.id.newsDialog_webview);
        mProgressBar = (View) dialog.findViewById(R.id.newsDialog_progress);
        if(textMode) {
            webView.setVisibility(View.GONE);
            descText.setText(nList.get(currentLocalNewsID).getContents());
       } else {
            descText.setVisibility(View.GONE);
            webView.setWebViewClient(new WebViewClient() {
                                         @Override
                                         public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                             super.onPageStarted(view, url, favicon);
                                             mProgressBar.setVisibility(View.VISIBLE);
                                         }

                                         @Override
                                         public void onPageFinished(WebView view, String url) {
                                             super.onPageFinished(view, url);
                                             if(mLoadingError){
                                                 descText.setVisibility(View.VISIBLE);
                                                 descText.setText("web page load error\n\n" + nList.get(currentLocalNewsID).getContents());
                                             } else {
                                                 mProgressBar.setVisibility(View.GONE);
                                             }
                                         }

                                         @Override
                                         public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                             super.onReceivedError(view, errorCode, description, failingUrl);
                                             mLoadingError = true;
                                         }

                                         // disable URL click
                                         //@Override
                                         //public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         //    return true;
                                         //}
                                     }
            );
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl(nList.get(currentLocalNewsID).getURL());
        }

        dialog.setTitle(nList.get(currentLocalNewsID).getTitle());
        locationText.setText(nList.get(currentLocalNewsID).getLocation());
        issueDateText.setText(nList.get(currentLocalNewsID).getIssueDate().toString());
        urlText.setText(nList.get(currentLocalNewsID).getURL());
        uri = Uri.parse(nList.get(currentLocalNewsID).getURL());

        Button openButton = (Button) dialog.findViewById(R.id.newsDialog_open);
        Button lookButton = (Button) dialog.findViewById(R.id.newsDialog_look);
        Button prevButton = (Button) dialog.findViewById(R.id.newsDialog_prev);
        Button nextButton = (Button) dialog.findViewById(R.id.newsDialog_next);
        Button oldButton = (Button) dialog.findViewById(R.id.newsDialog_old);
        Button newButton = (Button) dialog.findViewById(R.id.newsDialog_new);



        // if there are more than two entries, enable oldButton
        if(entryNum != 1){
            oldButton.setEnabled(true);
        }

        // open URI with browser
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "open button clicked");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // zoom to the marker position
        lookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "look button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.lookCloser();
                dismiss();
            }
        });

        // show prev News Dailog
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "prev button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.openPrevDialog();
                dismiss();
            }
        });

        // show next News Dialog
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "next button clicked");
                MapsActivity activity = (MapsActivity) getActivity();
                activity.openNextDialog();
                dismiss();
            }
        });

        // show older news information at this same location
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

        // show newer news information at this same location
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
        TextView urlText = (TextView) getDialog().findViewById(R.id.newsDialog_url);

        TextView descText = (TextView) getDialog().findViewById(R.id.newsDialog_descText);
        WebView webView = (WebView) getDialog().findViewById(R.id.newsDialog_webview);
        if(textMode) {
            //webView.setVisibility(View.GONE);
            descText.setText(nList.get(currentLocalNewsID).getContents());
        } else {
            //descText.setVisibility(View.GONE);
            webView.loadUrl(nList.get(currentLocalNewsID).getURL());
        }

        getDialog().setTitle(nList.get(currentLocalNewsID).getTitle());
        locationText.setText(nList.get(currentLocalNewsID).getLocation());
        issueDateText.setText(nList.get(currentLocalNewsID).getIssueDate().toString());
        urlText.setText(nList.get(currentLocalNewsID).getURL());
        uri = Uri.parse(nList.get(currentLocalNewsID).getURL());
    }

}
