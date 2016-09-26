package Entity;

/**
 * Created by lizoe on 27/08/2016.
 */
public class Favorite {
    int favoriteId;
    String date;
    double lat;
    double lng;
    String kingdom;
    String commonname;
    String scientificname;

    public Favorite(String scientificname, String commonname, String kingdom, double lat, double lng, String date) {
        this.scientificname = scientificname;
        this.commonname = commonname;
        this.date = date;
        this.kingdom = kingdom;
        this.lat=lat;
        this.lng=lng;
    }

    public Favorite(int favoriteId,String scientificname, String commonname, String kingdom, double lat, double lng, String date) {
        this.favoriteId = favoriteId;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.kingdom = kingdom;
        this.commonname = commonname;
        this.scientificname = scientificname;
    }

    public String getScientificname() {
        return scientificname;
    }

    public void setScientificname(String scientificname) {
        this.scientificname = scientificname;
    }

    public String getCommonname() {
        return commonname;
    }

    public void setCommonname(String commonname) {
        this.commonname = commonname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;

    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }
}
