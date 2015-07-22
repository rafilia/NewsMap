package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tm on 2015/07/22.
 */
public class NewsInfoDialog extends DialogFragment {
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

            }
        });

        lookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return dialog;

//        return new AlertDialog.Builder(getActivity())
//                .setTitle("title")
//                .setPositiveButton("open URL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .create();
    }
}
