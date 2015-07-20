package jp.ac.titech.itpro.sdl.newsmap;

import java.util.Date;

/**
 * Created by tm on 2015/07/19.
 */
public class NewsInfo {
    String title;
    String url;
    String location;
    Date issue_date;

    public NewsInfo(String _title, String _url, String _location, Date _issue_date) {
        title = _title;
        url = _url;
        location = _location;
        issue_date = _issue_date;
    }
}
