package com.example.liangchenzhou.moneco;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import DataBase.DatabaseHelper;
import Entity.Listimage;
import Adapter.ListimageAdapter;

public class SearchBoxActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private SQLiteDatabase speciesDB;
    private String searchBox;
    private ListView listscientific;
    private ListView listcommon;
    private ArrayList<String> scientifics,commons;
    private ListAdapter adapter1,adapter2;
    private DatabaseHelper db;

    private ArrayList<Listimage> scientificList, commonList;
    private ArrayList<String > sciname = new ArrayList<>();
    private ArrayList<Integer> sciimage = new ArrayList<>();

    private ArrayList<String > comname = new ArrayList<>();
    private ArrayList<Integer> comimage = new ArrayList<>();

    ListView lvscien,lvcommon;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);
        db = new DatabaseHelper(getApplicationContext());
        Intent intent = getIntent();
        searchBox = intent.getStringExtra("searchBox");
        scientificList= new ArrayList<Listimage>();
        commonList = new ArrayList<Listimage>();

        final int planticon = R.drawable.plant_marker;
        int animalicon = R.drawable.animal_marker;
        int fungiicon = R.drawable.fungi_marker;

        scientifics = db.searchByScientific(searchBox,"plant");
        for(int i=0;i<scientifics.size();i++){
            scientificList.add(new Listimage(scientifics.get(i),planticon));
        }
        scientifics = db.searchByScientific(searchBox,"animal");
        for(int i=0;i<scientifics.size();i++){
            scientificList.add(new Listimage(scientifics.get(i),animalicon));
        }
        scientifics = db.searchByScientific(searchBox,"fungi");
        if(scientifics.size()>0) {
            for (int i = 0; i < scientifics.size(); i++) {
                scientificList.add(new Listimage(scientifics.get(i),fungiicon));
            }
        }

        if(scientificList.size()!= 0){

            for(int i=0;i<scientificList.size();i++){
                sciname.add(scientificList.get(i).getName());
            }
            for(int i=0;i<scientificList.size();i++){
                sciimage.add(scientificList.get(i).getImage());
            }
            context=this;

            lvscien=(ListView) findViewById(R.id.lv_scientific);
            lvscien.setTag("scientific");
            lvscien.setAdapter(new ListimageAdapter(this, sciname,sciimage));
            lvscien.setOnItemClickListener(this);

        }

        commons = db.searchByScientific(searchBox,"plant");
        for(int i=0;i<commons.size();i++){
            commonList.add(new Listimage(commons.get(i),planticon));
        }
        commons = db.searchByScientific(searchBox,"animal");
        for(int i=0;i<commons.size();i++){
            commonList.add(new Listimage(commons.get(i),animalicon));
        }
        commons = db.searchByScientific(searchBox,"fungi");
        if(commons.size()>0) {
            for (int i = 0; i < commons.size(); i++) {
                commonList.add(new Listimage(commons.get(i),fungiicon));
            }
        }
        if(commonList.size()!= 0){

            for(int i=0;i<commonList.size();i++){
                comname.add(commonList.get(i).getName());
            }
            for(int i=0;i<commonList.size();i++){
                comimage.add(commonList.get(i).getImage());
            }
            context=this;

            lvcommon=(ListView) findViewById(R.id.commonlist);
            lvcommon.setAdapter(new ListimageAdapter(SearchBoxActivity.this, comname,comimage));
            lvcommon.setOnItemClickListener(this);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(this, Media.class);
//        if (view.getId() == R.id.commonlist) {
//            String common = comname.get(position);
//            Intent intent1 = new Intent(SearchBoxActivity.this, Media.class);
//            if(db.getScientific(common, ) != null){
//                intent1.putExtra("scientificSpecies", db.getScientific(common));
//                startActivity(intent);
//            } else {
//                Toast.makeText(getApplicationContext(), "Sorry, no species found", Toast.LENGTH_SHORT).show();
//            }
//
//        } else {
//            String scientific = sciname.get(position);
//            Intent intent1 = new Intent(SearchBoxActivity.this, Media.class);
//            intent1.putExtra("scientificSpecies",scientific);
//            startActivity(intent);
//        }


        Intent intent = new Intent(this, Media.class);
        if (view.getId() == R.id.commonlist) {
            String common = comname.get(position);
            int kingdomtype = comimage.get(position);
            String kingdom = "";

            switch (kingdomtype) {
                case R.drawable.plant_marker:
                    kingdom = "plant";
                    break;
                case R.drawable.animal_marker:
                    kingdom = "animal";
                    break;
                case R.drawable.fungi_marker:
                    kingdom = "fungi";
                    break;
            }

            String resultscientifi = db.getScientific(common,kingdom);
            intent.putExtra("scientificSpecies", resultscientifi);
            intent.putExtra("species",kingdom);
            context.startActivity(intent);
        } else {
            String scientific = sciname.get(position);
            int kingdomtype = sciimage.get(position);
            String kingdom = "";
            switch (kingdomtype) {
                case R.drawable.plant_marker:
                    kingdom = "plant";
                    break;
                case R.drawable.animal_marker:
                    kingdom = "animal";
                    break;
                case R.drawable.fungi_marker:
                    kingdom = "fungi";
                    break;
            }
            intent.putExtra("scientificSpecies",scientific);
            intent.putExtra("species", kingdom);
            startActivity(intent);
        }
    }



}

