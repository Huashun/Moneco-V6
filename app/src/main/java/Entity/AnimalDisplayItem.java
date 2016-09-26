package Entity;

/**
 * Created by liangchenzhou on 13/08/16.
 */
public class AnimalDisplayItem {
    private String animalScientificName;
    private String animalCommonName;
    private String recordTimes;
    private int recordDate;
    private String kingdom;

    public AnimalDisplayItem() {
    }

    public AnimalDisplayItem(String animalScientificName, String animalCommonName, String recordTimes, int recordDate, String kingdom) {
        this.animalScientificName = animalScientificName;
        this.recordTimes = recordTimes;
        this.recordDate = recordDate;
        this.kingdom = kingdom;
    }

    public String getAnimalScientificName() {
        return animalScientificName;
    }

    public void setAnimalScientificName(String animalScientificName) {
        this.animalScientificName = animalScientificName;
    }

    public String getAnimalCommonName() {
        return animalCommonName;
    }

    public void setAnimalCommonName(String animalCommonName) {
        this.animalCommonName = animalCommonName;
    }

    public String getrecordTimes() {
        return recordTimes;
    }

    public void setrecordTimes(String recordTimes) {
        this.recordTimes = recordTimes;
    }

    public int getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(int recordDate) {
        this.recordDate = recordDate;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }
}
