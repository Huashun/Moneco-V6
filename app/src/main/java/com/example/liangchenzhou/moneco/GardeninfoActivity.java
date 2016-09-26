package com.example.liangchenzhou.moneco;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import CheckUserState.Checkstate;
import DataBase.DatabaseHelper;

public class GardeninfoActivity extends AppCompatActivity {
    public ImageView gardenimage;
    public TextView gardendesc, gardenname;
    private DatabaseHelper db;
    private Bitmap bitmap;
    private Button btnfind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gardeninfo);
        btnfind = (Button) findViewById(R.id.findentr);

        gardenimage=(ImageView)findViewById(R.id.gardenImage);
        gardendesc = (TextView)findViewById(R.id.gardendesc);
        gardenname = (TextView)findViewById(R.id.gardenname);

        //get garden name when click the map
        Intent intent= getIntent();
        String name = intent.getStringExtra("gardenname");
        if(name!=null) {
            if (!name.equals("")) {
                String desc = getDesc(name);
                gardenname.setText(name);
                if (!haveEntry(name)) {
                    btnfind.setVisibility(View.GONE);
                }
                //desc is not null
                if (desc != null) {
                    gardendesc.setText(desc);
                } else {
                    gardendesc.setText("Sorry, we do not have description for this garden.");
                }
                final String imageurl = getImage(name);
                if (imageurl == null) {


                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                //create an url object
                                URL url = new URL(imageurl);
                                //open url
                                InputStream is = url.openStream();
                                // analysi the bitmap from url
                                bitmap = BitmapFactory.decodeStream(is);
                                //  imageview.setImageBitmap(bitmap);
                                //发送消息，通知UI组件显示图片
                                handler.sendEmptyMessage(0x9527);
                                //close the stream
                                is.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }

            } else {//check whether the garden name is null, because some garden do not have a name and description
                gardenname.setText("Sorry, we do not have a name for this garden");
                gardendesc.setText("Sorry, we do not have description for this garden.");
                btnfind.setVisibility(View.GONE);

            }
        }
        else {
            String water = intent.getStringExtra("watername");
            if(water!=null) {
                if (!water.equals("")) {

                    String desc = getImageDesc(water);
                    gardenname.setText(water);
                    btnfind.setVisibility(View.GONE);
                    //desc is not null
                    if (desc != null) {
                        gardendesc.setText(desc);
                    } else {
                        gardendesc.setText("Sorry, we do not have description for this garden.");
                    }
                    final String imageurl = getwaterImage(water);
                    if (imageurl == null) {


                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    //create an url object
                                    URL url = new URL(imageurl);
                                    //open url
                                    InputStream is = url.openStream();
                                    // analysi the bitmap from url
                                    bitmap = BitmapFactory.decodeStream(is);
                                    //  imageview.setImageBitmap(bitmap);
                                    //发送消息，通知UI组件显示图片
                                    handler.sendEmptyMessage(0x9527);
                                    //close the stream
                                    is.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                    }

                }
                else {
                    gardenname.setText("Sorry, we do not have a name for this garden");
                    gardendesc.setText("Sorry, we do not have description for this garden.");
                    btnfind.setVisibility(View.GONE);

                }
            }
        }

    }

    public String getDesc(String name){
        db = new DatabaseHelper(getApplicationContext());
        String desc = db.getGardenDesc(name);
        if(desc ==null){
            return null;
        }
        else {
            return desc;
        }
    }
    public String getImage(String name){
        db = new DatabaseHelper(getApplicationContext());
        String image = db.getGardenImage(name);
        if(image==null){//no image for this garden
            return null;
        }
        else {//has miage for this garden
            return image;
        }
    }

    public String getwaterImage(String name){
        db = new DatabaseHelper(getApplicationContext());
        String image = db.getWaterimage(name);
        if(image==null){
            return null;
        }
        else {
            return image;
        }
    }

    public String getImageDesc(String name){
        db = new DatabaseHelper(getApplicationContext());
        String desc = db.getWaterDesc(name);
        if(desc==null){
            return null;
        }
        else {
            return desc;
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0x9527) {
                //显示从网上下载的图片
                gardenimage.setImageBitmap(bitmap);
            }
        }
    };


    public void findEntry(View view){
        String name = gardenname.getText().toString();
        Intent intent = new Intent(GardeninfoActivity.this, MapsActivity.class);
        intent.putExtra("gardenEntry", name);
        startActivity(intent);
    }
    public Boolean haveEntry(String gardenname){
        db = new DatabaseHelper(getApplicationContext());
        Boolean haveEntry = false;
        if(db.getEntry(gardenname).size()>0){
            haveEntry=true;
            return haveEntry;
        }
        else
            return haveEntry;
    }

    public void home(View view) {
        Intent intent = new Intent(GardeninfoActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(GardeninfoActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(GardeninfoActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(GardeninfoActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

}
