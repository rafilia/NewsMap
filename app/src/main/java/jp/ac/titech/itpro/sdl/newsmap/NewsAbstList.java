package jp.ac.titech.itpro.sdl.newsmap;

/**
 * Created by tm on 2015/07/23.
 */
// for NewsInfoListAdapter
// news title & issue date
public class NewsAbstList {
    String title;
    String date;

    NewsAbstList(String _title, String _date){
        title = _title;
        date = _date;
    }
    String getTitle(){return title;}
    String getDate(){return date;}

    void setTitle(String _title){title = _title;}
    void setDate(String _date){date = _date;}
}
