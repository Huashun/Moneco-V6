package Entity;

/**
 * Created by lizoe on 20/08/2016.
 */
public class Nestbox {
    private String nestboxID;

    private String location;
    private double lat;
    private double lng;
    public Nestbox(String newNextBoxID, String newLocation, double newlat, double newlng){
        nestboxID = newNextBoxID;
        location = newLocation;
        lat = newlat;
        lng = newlng;
    }
    public Nestbox(String newNextBoxID, double newlat, double newlng){
        nestboxID = newNextBoxID;
        lat = newlat;
        lng = newlng;
    }
    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
    public String getNestboxID(){
        return nestboxID;
    }
    public String getLocation(){return  location;}

}
