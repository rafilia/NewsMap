package jp.ac.titech.itpro.sdl.newsmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by tm on 2015/07/22.
 */
// sort based on LatLng (longitude, latitude) and then date
public class NewsInfoComparator implements Comparator<NewsInfo> {
    @Override
    public int compare(NewsInfo newsInfo, NewsInfo newsInfo2) {
        LatLng l1 = newsInfo.getLatLng();
        LatLng l2 = newsInfo2.getLatLng();

        if(l1.longitude > l2.longitude){
            return -1;
        } else if (l1.longitude < l2.longitude) {
            return 1;
        } else if (l1.latitude < l2.latitude) {
            return -1;
        } else if (l1.latitude > l2.latitude) {
            return 1;
        } else {
            Date d1 = newsInfo.getIssueDate();
            Date d2 = newsInfo2.getIssueDate();

            return d1.compareTo(d2);
        }
    }
}
