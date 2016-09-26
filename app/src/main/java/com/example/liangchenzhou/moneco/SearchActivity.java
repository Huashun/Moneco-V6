package com.example.liangchenzhou.moneco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import CheckUserState.Checkstate;
import DataBase.DatabaseHelper;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerSci, spinnerCom, spinnerCli;
    private DatabaseHelper db;
    private ArrayAdapter<String> adapterSci, adapterCom, adapterCli;
    private ArrayList<String> scientificname, commonname, scienticlimate;
    private ImageButton plantbt, animalbt, fungibt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = new DatabaseHelper(this);

        spinnerSci = (Spinner) findViewById(R.id.spinnerSci);
        spinnerCom = (Spinner) findViewById(R.id.spinnerCom);
        spinnerCli = (Spinner) findViewById(R.id.spinnerCli);

        scientificname = new ArrayList<>();
        commonname = new ArrayList<>();
        scienticlimate = new ArrayList<>();

        plantbt = (ImageButton) findViewById(R.id.plantbt);
        plantbt.setOnClickListener(this);
        animalbt = (ImageButton) findViewById(R.id.animalbt);
        animalbt.setOnClickListener(this);
        fungibt = (ImageButton) findViewById(R.id.fungibt);
        fungibt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plantbt:
                searchplant();
                break;
            case R.id.animalbt:
                searchanimal();
                break;
            case R.id.fungibt:
                searchfungi();
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        spinnerSci.setSelection(0, true);
        spinnerCom.setSelection(0, true);
        spinnerCli.setSelection(0, true);

    }

    public void searchplant() {
        scientificname.clear();
        scientificname = db.searchByScientific(null, "plant");
        scientificname.add(0, "---- Please Choose ----");
        adapterSci = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scientificname);
        adapterSci.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerSci.setAdapter(adapterSci);
        spinnerSci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                if (position > 0) {
                    String selectSci = spinnerSci.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);
                    intent.putExtra("scientificSpecies", selectSci);
                    intent.putExtra("species", "plant");
                    startActivity(intent);
                }
                //display information by api
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        commonname.clear();
        commonname = db.searchByCommon(null, "plant");
        commonname.add(0, "---- Please Choose ----");
        adapterCom = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, commonname);
        adapterCom.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerCom.setAdapter(adapterCom);
        spinnerCom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                if (position > 0) {
                    String selectCom = spinnerCom.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);
                    if (db.getScientific(selectCom, "plant") != null) {

                        intent.putExtra("scientificSpecies", db.getScientific(selectCom, "plant"));
                        intent.putExtra("species", "plant");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, no species found", Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        scienticlimate.clear();
        scienticlimate = db.searchAllClimatewatech("plant");
        scienticlimate.add(0, "---- Please Choose ----");
        adapterCli = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scienticlimate);
        adapterCli.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerCli.setAdapter(adapterCli);
        spinnerCli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {


                if (position > 0) {
                    String selectCli = spinnerCli.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);
                    intent.putExtra("scientificSpecies", selectCli);
                    intent.putExtra("species", "plant");
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }


    public void searchanimal() {
        scientificname.clear();
        scientificname = db.searchByScientific(null, "animal");
        scientificname.add(0, "---- Please Choose ----");
        adapterSci = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scientificname);
        spinnerSci.setAdapter(adapterSci);
        spinnerSci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (position > 0) {
                    String selectSci = spinnerSci.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);

                    intent.putExtra("scientificSpecies", selectSci);
                    intent.putExtra("species", "animal");
                    startActivity(intent);
                }
                //display information by api
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        commonname.clear();
        commonname = db.searchByCommon(null, "animal");
        commonname.add(0, "---- Please Choose ----");
        adapterCom = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, commonname);
        adapterCom.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerCom.setAdapter(adapterCom);
        spinnerCom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (position > 0) {
                    String selectCom = spinnerCom.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);
                    if (db.getScientific(selectCom, "animal") != null) {
                        intent.putExtra("scientificSpecies", db.getScientific(selectCom, "animal"));
                        intent.putExtra("species", "animal");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, no species found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        scienticlimate.clear();
        scienticlimate = db.searchAllClimatewatech("animal");
        scienticlimate.add(0, "---- Please Choose ----");
        if (scienticlimate.size() == 0) {
            scienticlimate.add("No ClimateWatch Animal");
        }
        adapterCli = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scienticlimate);
        adapterCli.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerCli.setAdapter(adapterCli);
        spinnerCli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (position > 0) {
                    String selectCli = spinnerCli.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);

                    intent.putExtra("scientificSpecies", selectCli);
                    intent.putExtra("species", "animal");
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void searchfungi() {
        scientificname.clear();
        scientificname = db.searchByScientific(null, "fungi");
        scientificname.add(0, "---- Please Choose ----");
        adapterSci = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scientificname);
        adapterSci.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerSci.setAdapter(adapterSci);
        spinnerSci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (position > 0) {
                    String selectSci = spinnerSci.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);
                    intent.putExtra("scientificSpecies", selectSci);
                    intent.putExtra("species", "fungi");
                    startActivity(intent);
                }
                //display information by api
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        commonname.clear();
        commonname = db.searchByCommon(null, "fungi");
        commonname.add(0, "---- Please Choose ----");
        adapterCom = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, commonname);
        adapterCom.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerCom.setAdapter(adapterCom);
        spinnerCom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (position > 0) {
                    String selectCom = spinnerCom.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);
                    if (db.getScientific(selectCom, "fungi") != null) {

                        intent.putExtra("scientificSpecies", db.getScientific(selectCom, "fungi"));
                        intent.putExtra("species", "fungi");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, no species found", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        scienticlimate.clear();
        scienticlimate = db.searchAllClimatewatech("fungi");
        scienticlimate.add(0, "---- Please Choose ----");
        if (scienticlimate.size() == 0) {
            scienticlimate.add("No ClimateWatch Fungi");
        }
        adapterCli = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scienticlimate);
        adapterCli.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnerCli.setAdapter(adapterCli);
        spinnerCli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (position > 0) {
                    String selectCli = spinnerCli.getSelectedItem().toString();
                    Intent intent = new Intent(SearchActivity.this, Media.class);

                    intent.putExtra("scientificSpecies", selectCli);
                    intent.putExtra("species", "fungi");
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void searchWaterPonds(View view){
        Intent intent = new Intent(SearchActivity.this,MapsActivity.class);
        intent.putExtra("WaterPonds", "waterponds");
        startActivity(intent);

    }

    public void searchAllEntity(View view){
        Intent intent = new Intent(SearchActivity.this,MapsActivity.class);
        intent.putExtra("Displayall", "Displayall");
        startActivity(intent);

    }

    public void searchGarden(View view) {
        String gardens = "gardens";
        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
        intent.putExtra("gardens", gardens);
        startActivity(intent);
    }

    public void searchNestbox(View view) {
        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
        intent.putExtra("nestbox", "nestbox");
        startActivity(intent);
    }

    public void home(View view) {
        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
