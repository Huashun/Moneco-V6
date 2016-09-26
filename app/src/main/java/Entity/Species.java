package Entity;

/**
 * Created by lizoe on 20/08/2016.
 */
public class Species {
    private String entityID;
    private String scientific;
    private String common;
    private String kingdom;
    private double lat;
    private double lng;
    public Species(String newentityID, String newscientific, String newcommon, String newkingdom, double newlat, double newlng){
        entityID = newentityID;
        scientific = newscientific;
        common = newcommon;
        kingdom = newkingdom;
        lat = newlat;
        lng = newlng;
    }
    public Species(String newentityID, double newlat, double newlng){
        entityID = newentityID;
        lat = newlat;
        lng = newlng;
    }
    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
    public String getEntityID(){
        return entityID;
    }
    public String getScientific(){return  scientific;}
    public String getCommon(){return  common;}
    public String getKingdom(){return kingdom;}
}
