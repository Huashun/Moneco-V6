package Entity;

/**
 * Created by liangchenzhou on 23/08/16.
 */
public class ObservationRecord {
    private String description;
    private String commonName;
    private String scientificName;
    private double mediaLatitude;
    private double mediaLongitude;
    private String imageUrl;
    private String audioUrl;
    private int userId;
    private String uploadDate;

    public ObservationRecord() {
    }

    public ObservationRecord(String description, String commonName) {
        this.description = description;
        this.commonName = commonName;
    }

    public ObservationRecord(String description, String commonName, String scientificName, double mediaLatitude, double mediaLongitude, String imageUrl, String audioUrl, int userId, String uploadDate) {
        this.description = description;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.mediaLatitude = mediaLatitude;
        this.mediaLongitude = mediaLongitude;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.userId = userId;
        this.uploadDate = uploadDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public double getMediaLatitude() {
        return mediaLatitude;
    }

    public void setMediaLatitude(double mediaLatitude) {
        this.mediaLatitude = mediaLatitude;
    }

    public double getMediaLongitude() {
        return mediaLongitude;
    }

    public void setMediaLongitude(double mediaLongitude) {
        this.mediaLongitude = mediaLongitude;
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

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
