package Entity;

/**
 * Created by liangchenzhou on 25/08/16.
 */
public class SpeciesData {
    private String scientificName;
    private String commonName;
    private String kingdom;
    private String climateWatch;
    private double sLatitude;
    private double sLongitude;

    public SpeciesData() {
    }

    public SpeciesData(String scientificName, String commonName, String kingdom, String climateWatch, double sLatitude, double sLongitude) {
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.kingdom = kingdom;
        this.climateWatch = climateWatch;
        this.sLatitude = sLatitude;
        this.sLongitude = sLongitude;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getClimateWatch() {
        return climateWatch;
    }

    public void setClimateWatch(String climateWatch) {
        this.climateWatch = climateWatch;
    }

    public double getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(double sLatitude) {
        this.sLatitude = sLatitude;
    }

    public double getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(double sLongitude) {
        this.sLongitude = sLongitude;
    }
}
