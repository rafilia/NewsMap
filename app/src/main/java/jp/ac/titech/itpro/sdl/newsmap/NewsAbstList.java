package jp.ac.titech.itpro.sdl.newsmap;

/**
 * Created by tm on 2015/07/23.
 */
public class NewsAbstList {
    String title;
    String location;

    NewsAbstList(String _title, String _location){
        title = _title;
        location = _location;
    }
    String getTitle(){return title;}
    String getLocation(){return location;}

    void setTitle(String _title){title = _title;}
    void setLocation(String _location){location = _location;}
}
