package Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liangchenzhou on 1/09/16.
 */
public class History implements Parcelable {
    private int mediaId;
    private String mediaDescription;
    private String commonName;
    private String scientificName;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String audioUrl;
    private String date;
    private int userId;


    public History() {
    }

    public History(int mediaId, String mediaDescription, String commonName, String scientificName, double latitude, double longitude, String imageUrl, String audioUrl, String date, int userId) {
        this.mediaId = mediaId;
        this.mediaDescription = mediaDescription;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.date = date;
        this.userId = userId;
    }


    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaDescription() {
        return mediaDescription;
    }

    public void setMediaDescription(String mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mediaId);
        parcel.writeString(this.mediaDescription);
        parcel.writeString(this.commonName);
        parcel.writeString(this.scientificName);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.audioUrl);
        parcel.writeString(this.date);
        parcel.writeInt(this.userId);
    }

    public static final Parcelable.Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel source) {
            return new History(source.readInt(), source.readString(), source.readString(), source.readString(),
                    source.readDouble(), source.readDouble(), source.readString(), source.readString(),
                    source.readString(), source.readInt());
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };}
