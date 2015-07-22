package jp.ac.titech.itpro.sdl.newsmap;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by tm on 2015/07/19.
 */
public class NewsInfo implements Parcelable{
    private int id;
    private String title;
    private String url;
    private String location;
    private Date issue_date;
    private LatLng latlng;

    public int getID(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getURL(){
        return url;
    }
    public String getLocation(){
        return location;
    }
    public Date getIssueDate(){
        return issue_date;
    }
    public LatLng getLatLng(){
        return latlng;
    }

    public NewsInfo(int _id ,String _title, String _url, String _location, Date _issue_date, LatLng _latlng) {
        id = _id;
        title = _title;
        url = _url;
        location = _location;
        issue_date = _issue_date;
        latlng = _latlng;
    }

    public NewsInfo (Parcel parcel){
        id = parcel.readInt();
        title = parcel.readString();
        url = parcel.readString();
        location = parcel.readString();
        issue_date = new Date(parcel.readLong());
        latlng = new LatLng(parcel.readDouble(), parcel.readDouble());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(url);
        parcel.writeString(location);
        parcel.writeLong(issue_date.getTime());
        parcel.writeDouble(latlng.latitude);
        parcel.writeDouble(latlng.longitude);
    }

    public static final Parcelable.Creator<NewsInfo> CREATOR
            = new Parcelable.Creator<NewsInfo>(){
        @Override
        public NewsInfo createFromParcel(Parcel parcel) {
            return new NewsInfo(parcel);
        }

        @Override
        public NewsInfo[] newArray(int i) {
            return new NewsInfo[i];
        }
    };
}
