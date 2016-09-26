package com.example.liangchenzhou.moneco;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import CheckUserState.Checkstate;
import DataBase.DatabaseHelper;
import Entity.Animal;
import Entity.ClimateWatchMedia;
import Entity.CustomLocation;
import Entity.SpeciesData;
import Entity.SpeciesKingdom;

public class Media extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private ArrayList<SpeciesData> arrayParseAnimal;
    private DatabaseHelper dbHelper;
    private ImageButton playButton, findOnMap;
    private ImageView animalImage;
    private TextView commonDisplay, scientificDisplay, descriptionDisplay;
    private SeekBar seekBarAudio;
    private MediaPlayer mediaPlayer;
    private int total = 0;
    private ImageButton floatingButton, climateWatchState;
    private ShareActionProvider mShareActionProvider;
    private String intentData = "";
    private String intentSpecies = "";
    private String commonToShare = "";
    private String scientificToShare = "";
    private Uri uriImage = null;
    private LinearLayout linear10, linearLayout2;
    private TextView descriptionContent, diffA, diffAContent,
            diffB, diffBContent,
            diffC, diffCContent,
            diffD, diffDContent,
            whatToObserve, whatToObserveContent,
            whenToObserve, whenToObserveContent,
            whereToObserve, whereToObserveContent,
            nativeStatus, nativeStatusContent,
            conservationStatus, conservationStatusContent,
            phylum, phylumContent, classSpecies, classSpeciesContent, order, orderContent, family, familyContent, genus, genusContent;
    private ImageButton imageHelp;
    private ImageView imageSize, imageLeaveCall, imageSeedDiet, imageFlowerBreed, imageDistribution;
    // private boolean actionItemState = true;
    private Menu menu;
    private ClimateWatchMedia climateWatchMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        linearLayout2.setVisibility(View.INVISIBLE);

        linear10 = (LinearLayout) findViewById(R.id.linear10);
        imageHelp = (ImageButton) this.findViewById(R.id.imageHelp);
        imageHelp.setOnClickListener(this);
        imageSize = (ImageView) this.findViewById(R.id.imageSize);
        imageLeaveCall = (ImageView) this.findViewById(R.id.imageLeaveCall);
        imageSeedDiet = (ImageView) this.findViewById(R.id.imageSeedDiet);
        imageFlowerBreed = (ImageView) this.findViewById(R.id.imageFlowerBreed);
        imageDistribution = (ImageView) this.findViewById(R.id.imageDistribution);
        descriptionContent = (TextView) findViewById(R.id.descriptionContent);
        diffA = (TextView) findViewById(R.id.diffA);
        diffAContent = (TextView) findViewById(R.id.diffAContent);
        diffB = (TextView) findViewById(R.id.diffB);
        diffBContent = (TextView) findViewById(R.id.diffBContent);
        diffC = (TextView) findViewById(R.id.diffC);
        diffCContent = (TextView) findViewById(R.id.diffCContent);
        diffD = (TextView) findViewById(R.id.diffD);
        diffDContent = (TextView) findViewById(R.id.diffDContent);
        whatToObserve = (TextView) findViewById(R.id.whatToObserve);
        whatToObserveContent = (TextView) findViewById(R.id.whatToObserveContent);
        whenToObserve = (TextView) findViewById(R.id.whenToObserve);
        whenToObserveContent = (TextView) findViewById(R.id.whenToObserveContent);
        whereToObserve = (TextView) findViewById(R.id.whereToObserve);
        whereToObserveContent = (TextView) findViewById(R.id.whereToObserveContent);
        nativeStatus = (TextView) findViewById(R.id.nativeStatus);
        nativeStatusContent = (TextView) findViewById(R.id.nativeStatusContent);
        conservationStatus = (TextView) findViewById(R.id.conservationStatus);
        conservationStatusContent = (TextView) findViewById(R.id.conservationStatusContent);
        phylum = (TextView) findViewById(R.id.phylum);
        phylumContent = (TextView) findViewById(R.id.phylumContent);
        classSpecies = (TextView) findViewById(R.id.classSpecies);
        classSpeciesContent = (TextView) findViewById(R.id.classSpeciesContent);
        order = (TextView) findViewById(R.id.order);
        orderContent = (TextView) findViewById(R.id.orderContent);
        family = (TextView) findViewById(R.id.family);
        familyContent = (TextView) findViewById(R.id.familyContent);
        genus = (TextView) findViewById(R.id.genus);
        genusContent = (TextView) findViewById(R.id.genusContent);



        commonDisplay = (TextView) findViewById(R.id.commonDisplay);
        scientificDisplay = (TextView) findViewById(R.id.scientificDisplay);
        descriptionDisplay = (TextView) findViewById(R.id.descriptionDisplay);

        floatingButton = (ImageButton) this.findViewById(R.id.floatingButton);
        floatingButton.setOnClickListener(this);
        climateWatchState = (ImageButton) this.findViewById(R.id.climateWatchState);
        climateWatchState.setOnClickListener(this);
        playButton = (ImageButton) this.findViewById(R.id.playButton);
        findOnMap = (ImageButton) this.findViewById(R.id.findOnMap);
        findOnMap.setOnClickListener(this);
        animalImage = (ImageView) this.findViewById(R.id.animalDisplay);
        seekBarAudio = (SeekBar) findViewById(R.id.seekBarAudio);
        playButton.setOnClickListener(this);
        dbHelper = new DatabaseHelper(getApplicationContext());

        mediaPlayer = MediaPlayer.create(this, R.raw.bird);
        total = mediaPlayer.getDuration();
        seekBarAudio.setMax(total);
        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //System.out.println(mediaPlayer.getCurrentPosition() + "--------");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        playButton.setEnabled(false);
        seekBarAudio.setEnabled(false);

        if (ActivityCompat.checkSelfPermission(Media.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Media.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
        } else {
//            actionItemState = true;
        }

        Bundle data = getIntent().getExtras();
        intentData = data.getString("scientificSpecies");
        intentSpecies = data.getString("species");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (this.getAnimalsInformation().get(0) != null && !this.getAnimalsInformation().get(0).getClimateWatch().equals("Y")) {
            imageHelp.setEnabled(false);
            linear10.setVisibility(View.INVISIBLE);
            SpeciesData data = this.getAnimalsInformation().get(0);
            String queryPrefix = "http://bie.ala.org.au/ws/search.json?q=";
            String query = queryPrefix + data.getScientificName();
            new AsyncgetInformationSpecies(this).execute(query);
            commonDisplay.setText(data.getCommonName());
            scientificDisplay.setText(data.getScientificName());
            // text to share social media
            commonToShare = data.getCommonName();
            scientificToShare = data.getScientificName();
        } else if (this.getAnimalsInformation().get(0) != null && this.getAnimalsInformation().get(0).getClimateWatch().equals("Y")) {
            SpeciesData data = this.getAnimalsInformation().get(0);
            commonDisplay.setText(data.getCommonName());
            scientificDisplay.setText(data.getScientificName());
            // text to share social media
            commonToShare = data.getCommonName();
            scientificToShare = data.getScientificName();
            String climateWatchAnimal = data.getClimateWatch();
                if (climateWatchAnimal.equals("Y")) {
                    climateWatchState.setImageResource(R.drawable.climate_logo);
                    climateWatchMedia = this.fetchClimateWatches();
                    if (climateWatchMedia != null) {
                        if (climateWatchMedia.getGeneralImageUrl() != null) {
                            if (!climateWatchMedia.getGeneralImageUrl().equals("")) {

                                new AsyncImageFromAtlas(this).execute(climateWatchMedia.getGeneralImageUrl());
                            }
                        }

                        if (climateWatchMedia.getSizeIamgeUrl() != null) {
                        if (!climateWatchMedia.getSizeIamgeUrl().equals("")) {
                            new AsyncImageSize(this).execute(climateWatchMedia.getSizeIamgeUrl());
                        }
                    }
                    if (climateWatchMedia.getDynamicA() != null) {
                        if (!climateWatchMedia.getDynamicA().equals("")) {
                            new AsyncImageLeaveCall(this).execute(climateWatchMedia.getDynamicA());
                        }
                    }
                    if (climateWatchMedia.getDynamicB() != null) {
                        if (!climateWatchMedia.getDynamicB().equals("")) {
                            new AsyncImageSeedDiet(this).execute(climateWatchMedia.getDynamicB());
                        }
                    }
                    if (climateWatchMedia.getDynamicC() != null) {
                        if (!climateWatchMedia.getDynamicC().equals("")) {
                            new AsyncImageFlowerBreed(this).execute(climateWatchMedia.getDynamicC());
                        }
                    }
                    if (climateWatchMedia.getDistributionUrl() != null) {
                        if (!climateWatchMedia.getDistributionUrl().equals("")) {
                            new AsyncImageDistribution(this).execute(climateWatchMedia.getDistributionUrl());
                        }
                    }
                    descriptionContent.setText(climateWatchMedia.getDescription());
                    if (intentSpecies.equals("plant") || intentSpecies.equals("fungi")) {
                        diffA.setText("Size");
                        diffB.setText("Leaves");
                        diffC.setText("Fruit/Seed");
                        diffD.setText("Flowers");
                    } else if (intentSpecies.equals("animal")) {
                        diffA.setText("Size/Weight");
                        diffB.setText("Movement/Call");
                        diffC.setText("Diet");
                        diffD.setText("Breeding");
                    }
                    diffAContent.setText(climateWatchMedia.getDiffA());
                    diffBContent.setText(climateWatchMedia.getDiffB());
                    diffCContent.setText(climateWatchMedia.getDiffC());
                    diffDContent.setText(climateWatchMedia.getDiffD());
                    whatToObserve.setText("What to Observe");
                    whenToObserve.setText("When to Observe");
                    whereToObserve.setText("Where to Observe");
                    whatToObserveContent.setText(climateWatchMedia.getWhatToObserve());
                    whenToObserveContent.setText(climateWatchMedia.getWhenToObserve());
                    whereToObserveContent.setText(climateWatchMedia.getWhereToObserve());
                    nativeStatus.setText("Native Status");
                    conservationStatus.setText("Conservation Status");
                    nativeStatusContent.setText(climateWatchMedia.getNativeStatus());
                    conservationStatusContent.setText(climateWatchMedia.getConservationStatus());
                    phylum.setText("Phylum: ");
                    classSpecies.setText("Class: ");
                    order.setText("Order: ");
                    family.setText("Family: ");
                    genus.setText("Genus: ");
                    phylumContent.setText(climateWatchMedia.getPhylum());
                    classSpeciesContent.setText(climateWatchMedia.getClimateClass());
                    orderContent.setText(climateWatchMedia.getOrder());
                    familyContent.setText(climateWatchMedia.getFamily());
                    genusContent.setText(climateWatchMedia.getGenus());
                }
            }
        }


    }

    public ClimateWatchMedia fetchClimateWatches(){
        return dbHelper.getClimateSpecies(intentData);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playButton) {
            mediaPlayer.start();
            seekBarAudio.setProgress(0);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mediaPlayer.getCurrentPosition() < total) {
                        seekBarAudio.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            }, 0, 1);
        } else if (v.getId() == R.id.floatingButton) {
            Checkstate checkstate = new Checkstate(this);
            if (!checkstate.checkLogin()) {
                Toast.makeText(this, "Please login firstly", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Media.this, UploadObservation.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.findOnMap) {
            this.showAnimalsOnMap();
        } else if (v.getId() == R.id.climateWatchState){
            AlertDialog.Builder dialog = new AlertDialog.Builder(Media.this);
            dialog.setTitle("Help")
                    .setMessage(climateWatchMedia.getCwIconText())
                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
        } else if (v.getId() == R.id.imageHelp){

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //       mShareActionProvider.setShareIntent(this.setShareIntent());

//        mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
//            @Override
//            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
//                Toast.makeText(getApplicationContext(), "Share successfully", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        return true;
    }

    //set shareIntent method
    public Intent setShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        if (uriImage != null && !commonToShare.equals("") && !scientificToShare.equals("")) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriImage);
            shareIntent.setType("image/*");
            String commonShare = "Common Name: " + commonToShare;
            String scientificShare = "Scientifc Name: " + scientificToShare;
            shareIntent.putExtra(Intent.EXTRA_TEXT, commonShare + "\n" + scientificShare);
        } else if (uriImage == null && !commonToShare.equals("") && !scientificToShare.equals("")) {
            shareIntent.setType("text/plain");
            String commonShare = "Common Name: " + commonToShare;
            String scientificShare = "Scientifc Name: " + scientificToShare;
            shareIntent.putExtra(Intent.EXTRA_TEXT, commonShare + "\n" + scientificShare);
        } else {
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        }
        return shareIntent;
    }

    //create the file and path to store the photos
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            System.out.println("File Create successfully!++++++++++++++++++++++++++");
        } catch (IOException e) {
            System.out.println("File Create Failed!================================");
            e.printStackTrace();
        }
        return image;
    }

    //update and save photos to system gallery
    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //get animals' information by scientific name
    public ArrayList<SpeciesData> getAnimalsInformation() {
        arrayParseAnimal = new ArrayList<SpeciesData>();
        try {
            arrayParseAnimal = (ArrayList<SpeciesData>) dbHelper.viewSpecies(intentData, intentSpecies);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No species found, back to try again", Toast.LENGTH_SHORT).show();
        }
        return arrayParseAnimal;
    }

    //extract animals' locations from arrayParseAnimal
    public ArrayList<CustomLocation> extractLocations(ArrayList<Animal> animals) {
        ArrayList<CustomLocation> arrayLatlng = new ArrayList<>();
        for (Animal animal : animals) {
            arrayLatlng.add(new CustomLocation((double) animal.getLatitudeProcessed(), (double) animal.getLongitudeProcessed()));
        }
        return arrayLatlng;
    }

    //exact species' scientificName and kingdom
    public ArrayList<SpeciesKingdom> extractKingdons(ArrayList<SpeciesData> animals) {
        ArrayList<SpeciesKingdom> arrayKingdom = new ArrayList<>();
        for (SpeciesData animal : animals) {
            arrayKingdom.add(new SpeciesKingdom(animal.getKingdom(), animal.getScientificName()));
        }
        return arrayKingdom;

    }

    //show animals on map
    public void showAnimalsOnMap() {
        if (arrayParseAnimal != null) {
            ArrayList<SpeciesKingdom> arrayLocations = extractKingdons(arrayParseAnimal);
            Intent intent = new Intent(Media.this, MapsActivity.class);
//            intent.putParcelableArrayListExtra("speciesLocations", arrayLocations);
            String tableName = "";
            switch (arrayLocations.get(0).getKingdom()) {
                case "ANIMALIA":
                    tableName = "animal";
                    break;
                case "Plantae":
                    tableName = "plant";
                    break;
                case "Fungi":
                    tableName = "fungi";
                    break;
            }
            intent.putExtra("kingdom", tableName);
            intent.putExtra("displaySci", arrayLocations.get(0).getScientificName());
            startActivity(intent);
        } else {
            Toast.makeText(this, "No Animals for displaying, please go back to map and choose again.", Toast.LENGTH_SHORT).show();
        }
    }

    //get species information from atlas API
    class AsyncgetInformationSpecies extends AsyncTask<String, Void, String> {
        private Context context;

        AsyncgetInformationSpecies(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String urlOriginal = urls[0];
                urlOriginal = urlOriginal.replaceAll(" ", "%20");
                URL url = new URL(urlOriginal);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String re = "";
                while ((re = reader.readLine()) != null) {
                    builder.append(re);
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null) {
                try {
                    JSONObject jsonResult = new JSONObject(data);
                    JSONObject searchResults = jsonResult.getJSONObject("searchResults");
                    JSONArray results = searchResults.getJSONArray("results");
                    String urlImage = "";
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject speciesJson = results.getJSONObject(i);
                        if (speciesJson.has("smallImageUrl")) {
                            urlImage = speciesJson.getString("smallImageUrl");
                            break;
                        }
                    }
                    if (!urlImage.equals("")) {
                        new AsyncImageFromAtlas(Media.this).execute(urlImage);
                    } else {
                        Toast.makeText(getApplicationContext(), "No photo record of this species", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Sorry, No photo record of this species", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //use image url to display
    class AsyncImageFromAtlas extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncImageFromAtlas(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            File file = null;
            if (bitmap != null) {
                if (ActivityCompat.checkSelfPermission(Media.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    file = createImageFile();
                    galleryAddPic(file.toString());
                    try {
                        outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        uriImage = Uri.fromFile(file);
                        mShareActionProvider.setShareIntent(setShareIntent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                animalImage.setImageBitmap(bitmap);
            }
        }
    }

    //use image leave url to display
    class AsyncImageLeaveCall extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncImageLeaveCall(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            File file = null;
            if (bitmap != null) {
                try {
                    imageLeaveCall.setImageBitmap(bitmap);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "LeaveCall Image URL format not correct", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //use image size url to display
    class AsyncImageSize extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncImageSize(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            File file = null;
            if (bitmap != null) {
                try{
                imageSize.setImageBitmap(bitmap);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Size Image URL format not correct", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //use image seekDiet url to display
    class AsyncImageSeedDiet extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncImageSeedDiet(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            File file = null;
            if (bitmap != null) {
                try {
                imageSeedDiet.setImageBitmap(bitmap);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "SeekDiet Image URL format not correct", Toast.LENGTH_SHORT).show();
            }
            }
        }
    }

    //use image FlowerBreed url to display
    class AsyncImageFlowerBreed extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncImageFlowerBreed(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            File file = null;
            if (bitmap != null) {
                try{
                imageFlowerBreed.setImageBitmap(bitmap);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "FlowerBreed Image URL format not correct", Toast.LENGTH_SHORT).show();
            }
            }
        }
    }

    //use image distribution url to display
    class AsyncImageDistribution extends AsyncTask<String, Void, Bitmap> {
        private Context context;

        AsyncImageDistribution(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            OutputStream outputStream = null;
            File file = null;
            if (bitmap != null) {
                try{
                imageDistribution.setImageBitmap(bitmap);
            } catch (Exception e){
                Toast.makeText(getApplicationContext(), "Distribution Image URL format not correct", Toast.LENGTH_SHORT).show();
            }
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Media.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPhotoPermissions();
                } else {
//                    actionItemState = false;
//                    setActionBarState();
                    Toast.makeText(getApplicationContext(),
                            "PERMISSION DENIED, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //require access photo permission for gallery
    public void requestPhotoPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The application need a permission for using camera, do you want to try again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Media.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),
                                    "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
//                            actionItemState = false;
//                            setActionBarState();
                        }
                    })
                    .create()
                    .show();
        }
    }

    //    //set actionbar state
    public void setActionBarState() {
//        if (!actionItemState){
//            //global menu item
//            getMenuInflater().inflate(R.menu.menu, menu);
//            MenuItem item = menu.findItem(R.id.action_share);
//            item.setEnabled(false);
//        }
    }

    public void home(View view) {
        Intent intent = new Intent(Media.this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(Media.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(Media.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Media.this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
