package com.example.liangchenzhou.moneco;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import CheckUserState.Checkstate;
import Entity.CustomLocation;
import Entity.History;

public class DisplayHistory extends AppCompatActivity implements View.OnClickListener {
    private TextView commonDisplay, scientificDisplay, desccontentAnimalHistory, displayDateSpecies;
    private ImageView dispalyHistoryImage;
    private MediaPlayer mediaPlayer;
    private ImageButton showOnMapButton, playHistoryButton;
    private SeekBar seekBar;
    private History history;
    private Integer total = 0;
    private String audioUrl = "";
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);
        mediaPlayer = new MediaPlayer();
        displayDateSpecies = (TextView) findViewById(R.id.displayDateSpecies);
        desccontentAnimalHistory = (TextView) findViewById(R.id.desccontentAnimalHistory);
        dispalyHistoryImage = (ImageView) findViewById(R.id.imageHistoryDisplay);
        seekBar = (SeekBar) findViewById(R.id.seekBarAudioHistory);
        commonDisplay = (TextView) findViewById(R.id.commonHistoryDisplay);
        scientificDisplay = (TextView) findViewById(R.id.scientificHistoryDisplay);
        playHistoryButton = (ImageButton) findViewById(R.id.playHistoryButton);
        playHistoryButton.setOnClickListener(this);
        showOnMapButton = (ImageButton) findViewById(R.id.findOnMapHistory);
        showOnMapButton.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        Bundle bundle = null;
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
            history = bundle.getParcelable("history");
            commonDisplay.setText(history.getCommonName());
            scientificDisplay.setText(history.getScientificName());
            desccontentAnimalHistory.setText(history.getMediaDescription());
            displayDateSpecies.setText(history.getDate());
            if (history.getImageUrl() != null && !history.getImageUrl().equals("")) {
                showImage(history.getImageUrl());
            } else {
                dispalyHistoryImage.setImageResource(R.drawable.defaultimage);
            }
            if (history.getAudioUrl() != null && !history.getAudioUrl().equals("")) {
                audioUrl = history.getAudioUrl();
            } else {
                playHistoryButton.setClickable(false);
                playHistoryButton.setEnabled(false);
                seekBar.setClickable(false);
                seekBar.setEnabled(false);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        DisplayHistory.this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.findOnMapHistory) {
            showHistoryOnMap();
        } else if (v.getId() == R.id.playHistoryButton) {
            //play audio
            playVoice();
        }
    }

    //pass location on map
    public void showHistoryOnMap() {
        CustomLocation customLocation;
        if (history != null) {
            customLocation = new CustomLocation(history.getLatitude(), history.getLongitude());
            if (customLocation != null) {
                Intent intent = new Intent(DisplayHistory.this, MapsActivity.class);
                intent.putExtra("customLocation", customLocation);
                startActivity(intent);
            }
        }
    }

    //play voice
    public void playVoice() {
        try {
            if (audioUrl != null && !audioUrl.equals("")) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                } else {
                    try {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(this, Uri.parse(history.getAudioUrl()));
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                timer = new Timer();
                                mediaPlayer.start();
                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (mediaPlayer.getCurrentPosition() < total) {
                                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                            }
                                        }

                                    }, 0, 1);
                                }
                            }
                        });
                        mediaPlayer.prepare();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                timer.cancel();
                                seekBar.setProgress(0);
                                //mediaPlayer.reset();
                            }
                        });
                        total = mediaPlayer.getDuration();
                        seekBar.setMax(total);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    //display image
    public void showImage(String urlWeb) {
        new AsyncDisplayImage(this).execute(urlWeb);
    }

    class AsyncDisplayImage extends AsyncTask<String, Void, Bitmap> {
        Context context;

        AsyncDisplayImage(Context context) {
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
            if (bitmap != null) {
                dispalyHistoryImage.setImageBitmap(bitmap);
            } else {
                dispalyHistoryImage.setImageResource(R.drawable.defaultimage);
            }
        }
    }

    //media player time state when jump to another activity
    public void timerState() {
        if (mediaPlayer != null && mediaPlayer.isPlaying() && timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void home(View view) {
        timerState();
        Intent intent = new Intent(DisplayHistory.this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view) {
        timerState();
        Intent intent = new Intent(DisplayHistory.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        timerState();
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(DisplayHistory.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(DisplayHistory.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

}
