package jp.ac.titech.itpro.sdl.newsmap;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by tm on 2015/07/22.
 */
public class NewsInfoDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("title");

        TextView title = (TextView) dialog.findViewById(R.id.newsDialog_desc);
        Button ok_button = (Button) dialog.findViewById(R.id.newsDialog_ok);
        Button url_button = (Button) dialog.findViewById(R.id.newsDialog_ok);

        return dialog;
    }
}
