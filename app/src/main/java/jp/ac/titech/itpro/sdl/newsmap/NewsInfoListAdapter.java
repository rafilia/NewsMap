package jp.ac.titech.itpro.sdl.newsmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tm on 2015/07/23.
 */
public class NewsInfoListAdapter extends ArrayAdapter<NewsAbstList> {
    LayoutInflater layoutInflater;

    public NewsInfoListAdapter(Context context, int resourceID, List<NewsAbstList> nList){
        super(context, resourceID, nList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.info_window, null);
        }

        TextView infoTitle, infoLocation;
        infoTitle = (TextView) convertView.findViewById(R.id.info_title);
        infoLocation = (TextView) convertView.findViewById(R.id.info_location);

        NewsAbstList item = getItem(position);
        infoTitle.setText(item.getTitle());
        infoLocation.setText(item.getLocation());

        return convertView;
    }
}
