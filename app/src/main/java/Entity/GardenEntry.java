package Entity;

/**
 * Created by lizoe on 26/08/2016.
 */
public class GardenEntry {

        public String gardenname;
        public String entryname;
        public double latitude;
        public double longitude;

    public GardenEntry(String gardenname,String entryname, double latitude, double longitude) {
        this.entryname = entryname;
        this.gardenname = gardenname;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEntryname() {
            return entryname;
        }

        public void setEntryname(String entryname) {
            this.entryname = entryname;
        }

        public String getGardenname() {
            return gardenname;
        }

        public void setGardenname(String gardenname) {
            this.gardenname = gardenname;
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
}
