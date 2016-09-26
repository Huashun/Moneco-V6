package Entity;

import android.content.Context;
import android.graphics.Color;

import com.example.liangchenzhou.moneco.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;

import Entity.kml.KmlContainer;
import Entity.kml.KmlLayer;
import Entity.kml.KmlPlacemark;
import Entity.kml.KmlPolygon;

/**
 * Created by lizoe on 4/08/2016.
 */
public class Gardenlayer implements GoogleMap.OnPolygonClickListener  {

    KmlLayer layer;
    public static ArrayList<Garden> gardens;
    public Polygon polygon;
    public Gardenlayer(){}
    Boolean isclick = true;
    public HashMap<Integer, Garden> gardenlist;
    public HashMap<String,Integer> gardeninfo;
    public HashMap<String,Integer> polygonmap;//String polygon id, integer index of gardenlist

    public HashMap<Integer, Garden> readGardens(GoogleMap mMap, Context context){
        try{
            gardenlist=new HashMap<Integer, Garden>();
            gardeninfo= new HashMap<String, Integer>();
            polygonmap = new HashMap<String, Integer>();
            layer = new KmlLayer(mMap, R.raw.monashgardens,context);
           // layer.addLayerToMap();
            gardens = new ArrayList<Garden>();
            for (KmlContainer container : layer.getContainers()) {
                for(KmlPlacemark placemark : container.getPlacemarks()){
                    String name = "N/E";
                    String description = "N/E";
                    ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
                    if(placemark.hasProperty("name")){
                        name = placemark.getProperty("name");}
                    if(placemark.hasProperty("description")){
                        description = placemark.getProperty("description");
                    }
                    String result = placemark.getGeometry().toString();
                    String latlng1 =placemark.getGeometry().getGeometryObject().toString();
                    if (placemark.getGeometry().getGeometryType().equals("Polygon")) {
                        KmlPolygon k = (KmlPolygon) placemark.getGeometry();
                        latlngs = k.getOuterBoundaryCoordinates();

                    }
                    Garden garden = new Garden(name,description,latlngs);
                    gardens.add(garden);

//                    for(int i=0;i<gardens.size();i++){
//                        polygon = mMap.addPolygon(new PolygonOptions().addAll(gardens.get(i).getCoordinates())
//                                .strokeColor(0)
//                                .fillColor(Color.GREEN));
//                        polygon.setClickable(true);
//                       // gardeninfo.put(polygon.getId(),i);
//                    }
                }
            }
            for (int i=0;i<gardens.size();i++){
                gardenlist.put(i,gardens.get(i));
            }
            return gardenlist;
        }catch (Exception e){
            return null;
        }
    }

    public void drawGardens(GoogleMap mMap, Context context, HashMap<Integer, Garden> glist){
        //Polygon polygon = null;
        for(int i=0;i<glist.size();i++){
           polygon = mMap.addPolygon(new PolygonOptions().addAll(glist.get(i).getCoordinates())
                    .strokeColor(0)
                    .fillColor(Color.GREEN));
           // polygon.setClickable(true);
            // gardeninfo.put(polygon.getId(),i);
            polygon.setClickable(true);
            polygonmap.put(polygon.getId(),i);
        }
    }

    public boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }



    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        int keyinGardenlist = polygonmap.get(polygon.getId());
        Garden garden = gardenlist.get(keyinGardenlist);
        System.out.println("Click inside of polygon key =  " + garden.getTitle());

    }

    public static Polygon drawGardenByName(String gardenname, GoogleMap map){
        Polygon polygon = null;
        Garden garden = new Garden();
        for(int i= 0;i<gardens.size();i++){
            if (gardenname.equals(gardens.get(i).getTitle())){
                garden=gardens.get(i);
                break;
            }
        }
        polygon = map.addPolygon(new PolygonOptions().addAll(garden.getCoordinates()).strokeColor(0)
                .fillColor(R.color.garden)
                .clickable(false));

        return polygon;
    }

}
