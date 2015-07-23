package jp.ac.titech.itpro.sdl.newsmap;

/**
 * Created by tm on 2015/07/23.
 */
public class NewsAbstList {
    String title;
    String location;
    String date;

    NewsAbstList(String _title, String _location, String _date){
        title = _title;
        location = _location;
        date = _date;
    }
    String getTitle(){return title;}
    String getLocation(){return location;}
    String getDate(){return date;}

    void setTitle(String _title){title = _title;}
    void setLocation(String _location){location = _location;}
    void setDate(String _date){date = _date;}
}
