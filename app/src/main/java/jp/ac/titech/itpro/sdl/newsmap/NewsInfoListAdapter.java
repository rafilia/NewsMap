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
// ListAdapter for custom Marker Infowindow
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

        TextView infoTitle, infoLocation, infoDate;
        infoTitle = (TextView) convertView.findViewById(R.id.info_title);
        infoDate = (TextView) convertView.findViewById(R.id.info_date);

        NewsAbstList item = getItem(position);
        infoTitle.setText(item.getTitle());
        infoDate.setText(item.getDate());

        return convertView;
    }
}
