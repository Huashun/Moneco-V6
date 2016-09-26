package Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liangchenzhou on 16/08/16.
 */
public class CustomLocation implements Parcelable{
    private double cuslatitude;
    private double cuslongitude;

    public CustomLocation() {
    }

    public CustomLocation(double cuslatitude, double cuslongitude) {
        this.cuslatitude = cuslatitude;
        this.cuslongitude = cuslongitude;
    }

    public double getCuslatitude() {
        return cuslatitude;
    }

    public void setCuslatitude(double cuslatitude) {
        this.cuslatitude = cuslatitude;
    }

    public double getCuslongitude() {
        return cuslongitude;
    }

    public void setCuslongitude(double cuslongitude) {
        this.cuslongitude = cuslongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.cuslatitude);
        parcel.writeDouble(this.cuslongitude);
    }

    public static final Parcelable.Creator<CustomLocation> CREATOR = new Creator<CustomLocation>() {
        @Override
        public CustomLocation createFromParcel(Parcel source) {
            return new CustomLocation(source.readDouble(), source.readDouble());
        }

        @Override
        public CustomLocation[] newArray(int size) {
            return new CustomLocation[size];
        }
    };
}
