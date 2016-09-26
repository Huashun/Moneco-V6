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
 * Created by lizoe on 3/09/2016.
 */
public class Waterlayer implements GoogleMap.OnPolygonClickListener  {
    KmlLayer layer;
    public static ArrayList<Garden> ponds;
    public Polygon polygon;
    public Waterlayer(){}
    Boolean isclick = true;
    public HashMap<Integer, Garden> waterpondlist;
    public HashMap<String,Integer> waterpondinfo;
    public HashMap<String,Integer> polygonmap;//String polygon id, integer index of gardenlist

    public HashMap<Integer, Garden> readGardens(GoogleMap mMap, Context context){
        try{
            waterpondlist=new HashMap<Integer, Garden>();
            waterpondinfo= new HashMap<String, Integer>();
            polygonmap = new HashMap<String, Integer>();
            layer = new KmlLayer(mMap, R.raw.monashwaterbodies,context);
            ponds = new ArrayList<Garden>();
            for (KmlContainer container : layer.getContainers()) {
                for(KmlPlacemark placemark : container.getPlacemarks()){
                    String name = "N/E";
                    String description = "N/E";
                    ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
                    if(placemark.hasProperty("name")){
                        name = placemark.getProperty("name");}

                    String result = placemark.getGeometry().toString();
                    String latlng1 =placemark.getGeometry().getGeometryObject().toString();
                    if (placemark.getGeometry().getGeometryType().equals("Polygon")) {
                        KmlPolygon k = (KmlPolygon) placemark.getGeometry();
                        latlngs = k.getOuterBoundaryCoordinates();

                    }
                    Garden garden = new Garden(name,description,latlngs);
                    ponds.add(garden);

//                    for(int i=0;i<gardens.size();i++){
//                        polygon = mMap.addPolygon(new PolygonOptions().addAll(gardens.get(i).getCoordinates())
//                                .strokeColor(0)
//                                .fillColor(Color.GREEN));
//                        polygon.setClickable(true);
//                       // gardeninfo.put(polygon.getId(),i);
//                    }
                }
            }
            for (int i=0;i<ponds.size();i++){
                waterpondlist.put(i,ponds.get(i));
            }
            return waterpondlist;
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
        Garden garden = waterpondlist.get(keyinGardenlist);
        System.out.println("Click inside of polygon key =  " + garden.getTitle());

    }


}
