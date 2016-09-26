package Entity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by lizoe on 4/08/2016.
 */
public class Garden {
    private String name;
    private String description;
    private ArrayList<LatLng> coordinates;
    public Garden(String newTitle, String newDes, ArrayList<LatLng> newCoor) {
        name = newTitle;
        description = newDes;
        coordinates = newCoor;
    }

    public Garden() {
    }

    public String getTitle() {
        return name;
    }
    public void setTitle(String title) {
        this.name = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

}
