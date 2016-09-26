package Entity;

/**
 * Created by liangchenzhou on 22/08/16.
 */
public class SpeciesGrid {
    private String scientific;
    private String common;
    private int id;
    private String kingdom;
    private int amount;
    private int date;

    public SpeciesGrid() {
    }

    public SpeciesGrid(String scientific, String common, int id, String kingdom, int amount, int date) {
        this.scientific = scientific;
        this.common = common;
        this.id = id;
        this.kingdom = kingdom;
        this.amount = amount;
        this.date = date;
    }

    public String getScientific() {
        return scientific;
    }

    public void setScientific(String scientific) {
        this.scientific = scientific;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }
}
