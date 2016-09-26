package Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liangchenzhou on 25/08/16.
 */
public class SpeciesKingdom implements Parcelable {
    private String kingdom;
    private String scientificName;

    public SpeciesKingdom() {
    }

    public SpeciesKingdom(String kingdom, String scientificName) {
        this.kingdom = kingdom;
        this.scientificName = scientificName;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.kingdom);
        parcel.writeString(this.scientificName);
    }

    public static final Parcelable.Creator<SpeciesKingdom> CREATOR = new Creator<SpeciesKingdom>() {
        @Override
        public SpeciesKingdom createFromParcel(Parcel source) {
            return new SpeciesKingdom(source.readString(), source.readString());
        }

        @Override
        public SpeciesKingdom[] newArray(int size) {
            return new SpeciesKingdom[size];
        }
    };
}
