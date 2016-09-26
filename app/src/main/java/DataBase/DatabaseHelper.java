package DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import Entity.Animal;
import Entity.ClimateWatchMedia;
import Entity.GardenEntry;
import Entity.Nestbox;
import Entity.Species;
import Entity.SpeciesData;
import Entity.SpeciesGrid;

/**
 * Created by liangchenzhou on 18/07/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_PATH = "/data/data/com.example.liangchenzhou.moneco/databases/";
    private static final String DB_NAME = "moneco.db";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //create database operation
    public void createDB() {
        boolean dbExist = this.checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                this.copyDB();
                //  Toast.makeText(context, "Database Copy successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }
        //Toast.makeText(context, "Database already exist", Toast.LENGTH_SHORT).show();
    }

    //check if database exist in application
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        File file = null;
        try {
            String targetPath = DB_PATH + DB_NAME;
            file = new File(targetPath);
//                checkDB = SQLiteDatabase.openDatabase(targetPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
//        if (checkDB != null) {
//            checkDB.close();
//        }
//        return checkDB != null ? true : false;
        return file.exists() ? true : false;
    }

    //copy db file to target folder
    public void copyDB() {
        try {
            InputStream inputStream = context.getAssets().open("moneco.db");
            String outFilePath = DB_PATH + DB_NAME;
            OutputStream outputStream = new FileOutputStream(outFilePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //view species information


    //view species information
    public List<SpeciesData> viewSpecies(String scName, String tableName) {
        List<SpeciesData> listofSpecificAnimal = new ArrayList<SpeciesData>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT scientificName, commonName, Kingdom, ClimateWatch, Latitude, Longitude " +
                        "FROM " + tableName + " where scientificName = ?",
                new String[]{scName});
        if (cursor.moveToFirst()) {
            do {
                String scientifc = cursor.getString(0);
                String common = cursor.getString(1);
                String kingdom = cursor.getString(2);
                String climate = cursor.getString(3);
                double latitude = cursor.getDouble(4);
                double longitude = cursor.getDouble(5);
                listofSpecificAnimal.add(new SpeciesData(scientifc, common, kingdom, climate, latitude, longitude));
            } while (cursor.moveToNext());
        }
        return listofSpecificAnimal;
    }


    //view animal information
    public List<Animal> viewAnimal(String scName) {
        List<Animal> listofSpecificAnimal = new ArrayList<Animal>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Animal animal = new Animal();
        // SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.example.liangchenzhou.testpolygon/databases/db.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("SELECT * FROM animal where scientificName = ?",
                new String[]{scName});
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String catalogNo = cursor.getString(1);
                String taxonGUID = cursor.getString(2);
                String scientificName = cursor.getString(3);
                String commonName = cursor.getString(5);
                String climateWatch = cursor.getString(6);
                String kingdom = cursor.getString(7);
                String classAnimal = cursor.getString(9);
                String order = cursor.getString(10);
                String family = cursor.getString(11);
                String genus = cursor.getString(12);
                String species = cursor.getString(13);
                float latitude = cursor.getFloat(20);
                float longitude = cursor.getFloat(21);
                String year = cursor.getString(27);
                String month = cursor.getString(28);
                String date = cursor.getString(29);
                listofSpecificAnimal.add(new Animal(id, catalogNo, taxonGUID, scientificName, commonName, climateWatch,
                        kingdom, classAnimal, order, family, genus, species, latitude, longitude, year, month, date));
            } while (cursor.moveToNext());
        }
        return listofSpecificAnimal;
    }

    public HashMap<String, LatLng> getAnimalLocations() {
        HashMap<String, LatLng> animalLocations = new HashMap<String, LatLng>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, Latitude, Longitude FROM animal", null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                LatLng latLng = new LatLng(Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
                animalLocations.put(id, latLng);
            } while (cursor.moveToNext());
        }
        return animalLocations;
    }

    //test all grid points
    public LinkedHashMap<Integer, LatLng> viewGridPoints() {
        LinkedHashMap<Integer, LatLng> hashMapLatLng = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM grids order by _id", null);
        if (cursor.moveToFirst()) {
            do {
                LatLng latLngPoint = new LatLng(Double.parseDouble(cursor.getString(4)), Double.parseDouble(cursor.getString(5)));
                hashMapLatLng.put(Integer.parseInt(cursor.getString(0)), latLngPoint);
            } while (cursor.moveToNext());
        }
        return hashMapLatLng;
    }

    //get scientific name
    public List<SpeciesGrid> getScientificNames(int gridId, String kingdomName) {
        List<SpeciesGrid> list = new ArrayList<SpeciesGrid>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if (kingdomName.equals("Plantae")) {
            query = "select a.species_sc, a.species_co, a._id, a.kingdom, COUNT(*) as amount, a.year || a.month as Dates from spatialarea a " +
                    "where a._id = ? AND a.kingdom = ? " +
                    "Group by a.species_sc, a._id;";
        } else if (kingdomName.equals("ANIMALIA") || kingdomName.equals("Fungi")) {
            query = "select a.matched_sc, a.vernacular, a._id, a.kingdom_matched, COUNT(*) as amount, a.year || a.month as Dates from spatialarea a " +
                    "where a._id = ? AND a.kingdom_matched = ? " +
                    "Group by a.matched_sc, a._id;";
        }
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gridId), kingdomName});
        if (cursor.moveToFirst()) {
            do {
                list.add(new SpeciesGrid(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3), Integer.parseInt(cursor.getString(4)), cursor.getInt(5)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    //get climatewatch species
    public List<SpeciesGrid> getClimateWatchSpecies(int gridId){
        List<SpeciesGrid> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select matched_sc, vernacular, _id, kingdom_matched, amount, year || month as Dates from (select a.matched_sc, a.vernacular, a._id, a.kingdom_matched, COUNT(*) as amount, a.year, a.month from spatialarea a " +
                "where a.matched_sc in (select distinct a.matched_sc from spatialarea a " +
                "where a.climatewat = 'Y') AND a.kingdom_matched = 'ANIMALIA' " +
                "Group by a.matched_sc, a._id " +
                "UNION " +
                "select a.matched_sc, a.vernacular, a._id, a.kingdom_matched, COUNT(*) as amount, a.year, a.month from spatialarea a " +
                "where a.matched_sc in (select distinct a.matched_sc from spatialarea a " +
                "where a.climatewat = 'Y') AND a.kingdom_matched = 'Fungi' " +
                "Group by a.matched_sc, a._id " +
                "UNION " +
                "select a.species_sc, a.species_co, a._id, a.kingdom, COUNT(*) as amount, a.year, a.month from spatialarea a " +
                "where a.species_sc in (select distinct a.species_sc from spatialarea a " +
                "where a.climatewat = 'Y') AND a.kingdom = 'Plantae' " +
                "Group by a.species_sc, a._id) " +
                "WHERE _id = ? " +
                "Group by matched_sc, _id " +
                "ORDER BY year desc, month desc;";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gridId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new SpeciesGrid(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3), Integer.parseInt(cursor.getString(4)), cursor.getInt(5)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    //match points of grid with a grid number
    public int matchGridNo(int pointgrid1, int pointgrid2, int pointgrid3, int pointgrid4) {
        int gridNo = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT _id FROM codegrid WHERE pointgrid1 = ? AND pointgrid2 = ? " +
                "AND pointgrid3 = ? AND pointgrid4 = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(pointgrid1), String.valueOf(pointgrid2),
                String.valueOf(pointgrid3), String.valueOf(pointgrid4)});
        if (cursor.moveToFirst()) {
            do {
                gridNo = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return gridNo;
    }

    //------July
    public ArrayList<String> searchByCommon(String keyword, String tablename) {
        ArrayList<String> commonList = new ArrayList<>();
        if (keyword != null) {
            SQLiteDatabase speciesDB = this.getReadableDatabase();
            String sql = "SELECT DISTINCT commonName FROM " + tablename + " WHERE commonName LIKE '%" + keyword + "%' ORDER BY commonName";
            Cursor cursor = speciesDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String common_name = cursor.getString(0);
                commonList.add(common_name);
            }
            cursor.close();
            speciesDB.close();
            return commonList;
        } else {
            SQLiteDatabase speciesDB = this.getReadableDatabase();
            String sql = "SELECT DISTINCT commonName FROM " + tablename + " ORDER BY commonName";
            Cursor cursor = speciesDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (cursor.getString(0) != null) {
                    String common_name = cursor.getString(0);
                    if (common_name.length() > 0) {
                        commonList.add(common_name);
                    }
                }
            }
            cursor.close();
            speciesDB.close();
            return commonList;
        }
    }

    public ArrayList<Species> displayByCommon(String keyword, String tablename) {
        ArrayList<Species> commonList = new ArrayList<>();

        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id, scientificName, commonName, Kingdom," +
                "Latitude, Longitude FROM " + tablename + " WHERE commonName = '" + keyword + "'";
        Cursor cursor = speciesDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String eneityid = cursor.getString(0);
            String scientific = cursor.getString(1);
            String common = cursor.getString(2);
            String kingdom = cursor.getString(3);
            double lat = Double.parseDouble(cursor.getString(4));
            double lng = Double.parseDouble(cursor.getString(5));
            Species species = new Species(eneityid, scientific, common, kingdom, lat, lng);
            commonList.add(species);
        }
        cursor.close();
        speciesDB.close();
        return commonList;
    }

    public ArrayList<Species> displayByScien(String keyword, String tablename) {
        ArrayList<Species> commonList = new ArrayList<>();

        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id, scientificName, commonName, Kingdom," +
                "Latitude, Longitude FROM " + tablename + " WHERE scientificName = '" + keyword + "'";
        Cursor cursor = speciesDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String eneityid = cursor.getString(0);
            String scientific = cursor.getString(1);
            String common = cursor.getString(2);
            String kingdom = cursor.getString(3);
            double lat = Double.parseDouble(cursor.getString(4));
            double lng = Double.parseDouble(cursor.getString(5));
            Species species = new Species(eneityid, scientific, common, kingdom, lat, lng);
            commonList.add(species);
        }
        cursor.close();
        speciesDB.close();
        return commonList;

    }

    public ArrayList<String> searchByScientific(String keyword, String tablename) {
        ArrayList<String> commonList = new ArrayList<>();
        if (keyword != null) {
            SQLiteDatabase speciesDB = this.getReadableDatabase();
            String sql = "SELECT DISTINCT scientificName FROM " + tablename + " WHERE scientificName LIKE '%" + keyword + "%' ORDER BY scientificName";
            Cursor cursor = speciesDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String common_name = cursor.getString(0);
                commonList.add(common_name);
            }
            cursor.close();
            speciesDB.close();
            return commonList;

        } else {
            SQLiteDatabase speciesDB = this.getReadableDatabase();
            String sql = "SELECT DISTINCT scientificName FROM " + tablename + " ORDER BY scientificName";
            Cursor cursor = speciesDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String common_name = cursor.getString(0);
                commonList.add(common_name);
            }
            cursor.close();
            speciesDB.close();
            return commonList;


        }
    }

    public ArrayList<String> searchAllClimatewatech(String tablename) {
        ArrayList<String> scientificList = new ArrayList<>();
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT DISTINCT scientificName FROM " + tablename + " WHERE ClimateWatch='Y' ORDER BY scientificName";
        Cursor cursor = speciesDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String common_name = cursor.getString(0);
            scientificList.add(common_name);
        }
        cursor.close();
        speciesDB.close();
        return scientificList;
    }

    public ArrayList<Species> readSpecies(String tablename) {
        ArrayList<Species> allSpeices = new ArrayList<>();
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id, scientificName, commonName, Kingdom, Latitude, Longitude FROM " + tablename;
        Cursor cursor = speciesDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String entityId = cursor.getString(0);
            String scientificName = cursor.getString(1);
            String commonName = cursor.getString(2);
            String kingdom = cursor.getString(3);
            double latitude = Double.parseDouble(cursor.getString(4));
            double longitude = Double.parseDouble(cursor.getString(5));
            Species species = new Species(entityId, scientificName, commonName, kingdom, latitude, longitude);
            allSpeices.add(species);
        }
        cursor.close();
        speciesDB.close();
        return allSpeices;
    }

    public ArrayList<Species> getNear(double curla, double curlo, ArrayList<Species> species, double radium) {
        ArrayList<Species> results = new ArrayList<>();
        for (int i = 0; i < species.size(); i++) {
            double distance = getDistance(curla, curlo, species.get(i).getLat(), species.get(i).getLng());
            if (distance <= radium) {
                results.add(species.get(i));
            }
        }
        return results;
    }

    public double getDistance(double startla, double startlo, double endla, double endlo) {

        float[] results = new float[1];
        Location.distanceBetween(startla, startlo, endla, endlo, results);
        return results[0];
    }

    public ArrayList<Nestbox> getNestBox() {
        ArrayList<Nestbox> allBox = new ArrayList<>();
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id, LOCATION, LATITUDE, LONGITUDE FROM NESTBOX";
        Cursor cursor = speciesDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String nestboxID = cursor.getString(0);
            String location = cursor.getString(1);
            double latitude = Double.parseDouble(cursor.getString(2));
            double longitude = Double.parseDouble(cursor.getString(3));
            Nestbox nestbox = new Nestbox(nestboxID, location, latitude, longitude);
            allBox.add(nestbox);
        }
        cursor.close();
        speciesDB.close();
        return allBox;
    }

    public String getFamily(String scientific, String table) {
        String family = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT DISTINCT FAMILY FROM " + table + " WHERE scientificName = ?";
        Cursor cursor = speciesDB.rawQuery(sql, new String[]{scientific});
        if (cursor.getCount() == 0) {
            return null;
        } else {
            while (cursor.moveToNext()) {
                family = cursor.getString(0);
            }
            cursor.close();
            speciesDB.close();
            return family;
        }
    }

    public Boolean isNestBox(String keyword) {
        Boolean isnest = false;
        String nestboxid = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id FROM NESTBOX WHERE _id = ?";
        Cursor cursor = speciesDB.rawQuery(sql, new String[]{keyword});
        if (cursor.getCount() == 0) {
            return isnest;
        } else {
            isnest = true;
            return isnest;
        }

    }

    public String getGardenImage(String gardenname) {
        String image = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT IMAGE FROM GARDEN WHERE _id = ?";
        Cursor cursor = speciesDB.rawQuery(sql, new String[]{gardenname});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                image = cursor.getString(0);
            }
            cursor.close();
            speciesDB.close();
            return image;
        } else {
            return null;
        }

    }

    public String getGardenDesc(String gardenname) {
        String desc = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT DESC FROM GARDEN WHERE _id =?";
        Cursor cursor = speciesDB.rawQuery(sql, new String[]{gardenname});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                desc = cursor.getString(0);
            }
            cursor.close();
            speciesDB.close();
            return desc;
        } else {
            return null;
        }

    }

    //according to the gardenname to find all entry of the garden
    public ArrayList<GardenEntry> getEntry(String gardenname) {
        ArrayList<GardenEntry> entryList = new ArrayList<GardenEntry>();
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id, ENTRYNAME, LATITUDE, LONGITUDE FROM GARDENENTRY WHERE _id =?";
        Cursor cursor = speciesDB.rawQuery(sql, new String[]{gardenname});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String entry = cursor.getString(1);
                double lat = Double.parseDouble(cursor.getString(2));
                double lng = Double.parseDouble(cursor.getString(3));
                GardenEntry ge = new GardenEntry(name, entry, lat, lng);
                entryList.add(ge);
            }
            cursor.close();
            speciesDB.close();
            return entryList;
        } else
            return null;

    }

    public String getKingdomByScientific(String scientificname) {
        String kingdom = null;
        String tablename = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        //String sql = "SELECT DISTINCT KINGDOM FROM ? WHERE scientificName = ?";
        String plant = "SELECT DISTINCT KINGDOM FROM PLANT WHERE SCIENTIFICNAME = ?";
        String animal = "SELECT DISTINCT KINGDOM FROM ANIMAL WHERE SCIENTIFICNAME = ?";
        String fungi = "SELECT DISTINCT KINGDOM FROM FUNGI WHERE SCIENTIFICNAME = ?";
        Cursor plantresult = speciesDB.rawQuery(plant, new String[]{scientificname});
        if (plantresult.getCount() > 0)
            kingdom = "plant";
        else {
            Cursor animalresult = speciesDB.rawQuery(animal, new String[]{scientificname});
            if (animalresult.getCount() > 0)
                kingdom = "animal";
            else {
                Cursor fungiresult = speciesDB.rawQuery(fungi, new String[]{scientificname});
                if (fungiresult.getCount() > 0)
                    kingdom = "fungi";

            }
        }
        return kingdom;
    }

    public String getScientific(String commonname, String tableName) {
        String scientificname = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT DISTINCT scientificName FROM " + tableName + " WHERE commonName = ?";
        Cursor cursor = speciesDB.rawQuery(sql, new String[]{commonname});
        if (cursor.moveToFirst()) {
            scientificname = cursor.getString(0);
            return scientificname;
        }

        return null;

    }
    public String getWaterDesc(String name){
        String desc = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT DESC FROM WATERPOND WHERE WATERNAME = ?";
        Cursor cursor = speciesDB.rawQuery(sql,new String[]{name});
        if(cursor.moveToFirst()){
            desc = cursor.getString(0);
            return desc;
        }
        return null;
    }
    public String getWaterimage(String name){
        String image = null;
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT IMAGE FROM WATERPOND WHERE WATERNAME = ?";
        Cursor cursor = speciesDB.rawQuery(sql,new String[]{name});
        if(cursor.moveToFirst()){
            image = cursor.getString(0);
            return image;
        }
        return null;
    }
    public ArrayList<Species> getAllEntity(String tablename){
        ArrayList<Species> allEntity = new ArrayList<>();
        SQLiteDatabase speciesDB = this.getReadableDatabase();
        String sql = "SELECT _id, scientificName, commonName, Latitude, Longitude FROM "+tablename;
        Cursor cursor = speciesDB.rawQuery(sql, null);
        while (cursor.moveToNext()){
            String entityid = cursor.getString(0);
            String scientificname = cursor.getString(1);
            String commonname = cursor.getString(2);
            double lat = Double.parseDouble(cursor.getString(3));
            double lng = Double.parseDouble(cursor.getString(4));
            Species species = new Species(entityid,scientificname,commonname,tablename,lat,lng);
            allEntity.add(species);
        }
        return allEntity;

    }

    public ClimateWatchMedia getClimateSpecies(String scientific){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from climatewatchdata where scientific_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{scientific});
        if (cursor.moveToFirst()){
            ClimateWatchMedia climateWatchMedia = new ClimateWatchMedia(cursor.getString(0),cursor.getString(1),cursor.getString(2)
                    ,cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)
                    ,cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12)
                    ,cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17)
                    ,cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21),cursor.getString(22)
                    ,cursor.getString(23), cursor.getString(24));
            return climateWatchMedia;
        }
        return null;
    }

}
