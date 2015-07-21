package jp.ac.titech.itpro.sdl.newsmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by tm on 2015/07/19.
 */
public class NewsInfo {
    String title;
    String url;
    String location;
    Date issue_date;
    LatLng latlng;

    public NewsInfo(String _title, String _url, String _location, Date _issue_date, LatLng _latlng) {
        title = _title;
        url = _url;
        location = _location;
        issue_date = _issue_date;
        latlng = _latlng;
    }
}
