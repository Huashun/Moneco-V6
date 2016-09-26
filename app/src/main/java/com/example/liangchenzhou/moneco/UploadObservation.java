package com.example.liangchenzhou.moneco;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import CheckUserState.Checkstate;
import Entity.ObservationRecord;
import GalleryFilePath.FilePath;

public class UploadObservation extends AppCompatActivity implements View.OnClickListener {
    private TextView commonNamePrompt, audioTimer, audioFileName, commonNameInput,
            scientificNameInput, descriptionInput;
    private ImageButton takePhoto, audioRecord, galleryButton, submitButton,
            uploadBack, audioPlayer, audioStop;
    private ImageView uploadImage;
    private Button buttonDismiss;
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int REQUEST_AUDIO_CAPTURE = 3;
    public static final int REQUEST_PICK_PHOTO = 4;

    private String photoFileStr = null;
    private String mCurrentPhotoPath = "";
    private String nCurrentAudioPath = "";
    private Uri uri;
    private LocationManager locationManager;
    private String urlWebPath = "";
    private String urlWebAudioPath = "";
    private ObservationRecord observationRecord = new ObservationRecord();
    private SharedPreferences sharedPreferences;
    private int userId = -1;
    private MediaPlayer player;
    private MediaRecorder recorderAudio;
    private File audioFile = null;
    private boolean stateAudioCapture = false;
    private AVLoadingIndicatorView aviUpload;
    private PopupWindow popupWindow;
    private Checkstate checkstate;
    private SeekBar seekBarAudioUpload;
    private Timer timer;
    private int total = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_observation);
        commonNamePrompt = (TextView) findViewById(R.id.commonNamePrompt);
        //audioTimer = (TextView) findViewById(R.id.audioTimer);
        commonNameInput = (TextView) findViewById(R.id.commonNameInput);
        scientificNameInput = (TextView) findViewById(R.id.scientificNameInput);
        galleryButton = (ImageButton) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(this);
        // galleryButton.setImageResource(R.drawable.gallery);
        descriptionInput = (TextView) findViewById(R.id.descriptionInput);
        takePhoto = (ImageButton) findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(this);
        // takePhoto.setImageResource(R.drawable.camera);
        audioRecord = (ImageButton) findViewById(R.id.audioRecord);
        audioRecord.setOnClickListener(this);
        audioRecord.setImageResource(R.drawable.ic_microphone_grey600_24dp);
        // audioRecord.setImageResource(R.drawable.audio_recording);
        uploadImage = (ImageView) findViewById(R.id.imageUpload);
        uploadImage.setImageResource(R.drawable.defaultimage);
        submitButton = (ImageButton) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        uploadBack = (ImageButton) findViewById(R.id.uploadBack);
        uploadBack.setOnClickListener(this);
        audioPlayer = (ImageButton) findViewById(R.id.audioPlayer);
        audioPlayer.setOnClickListener(this);
        audioStop = (ImageButton) findViewById(R.id.playStop);
        audioStop.setOnClickListener(this);
        seekBarAudioUpload = (SeekBar) findViewById(R.id.seekBarAudioUpload);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.id)

        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        this.setCommonNameColor();
        sharedPreferences = getApplicationContext().getSharedPreferences("currentUser", MODE_PRIVATE);
        checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()){
            Intent intent = new Intent(UploadObservation.this, LoginActivity.class);
            startActivity(intent);
        } else {
            userId = saveState();
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        UploadObservation.this.finish();
//    }

    //get SharePreference id
    public int saveState() {
        return checkstate.handleLoggedIn().getUserId();
    }

    //choose photos from gallery
    public void pickPhoto() {
        mCurrentPhotoPath = "";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    // open system camera method
    private void openCamera() {
        mCurrentPhotoPath = "";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                photoFileStr = photoFile.toString();
            } catch (Exception ex) {
                System.out.print(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //update and save photos to system gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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

        mCurrentPhotoPath = image.toString();
        return image;
    }

    //initial audio (mediaRecorder)
    public void initialAudio() {
        if (!stateAudioCapture) {
            recorderAudio = new MediaRecorder();
            recorderAudio.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorderAudio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorderAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            audioFile = generateAudioName();
            nCurrentAudioPath = audioFile.toString();
            recorderAudio.setOutputFile(audioFile.getAbsolutePath());
            recorderAudio.setMaxDuration(3600000);
            recorderAudio.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        recorderAudio.stop();
                        recorderAudio.release();
                        //audioTimer.setText("Voice capture successfully");
                        stateAudioCapture = false;
                        audioRecord.setImageResource(R.drawable.ic_microphone_settings_grey600_24dp);
                        if (!stateAudioCapture) {
                            //             audioRecord.setImageResource(R.drawable.audio_recording);
                        }
                    }
                }
            });
            try {
                recorderAudio.prepare();
            } catch (IOException e) {
                recorderAudio.reset();
                recorderAudio.release();
                recorderAudio = null;
            }
            recorderAudio.start();
            stateAudioCapture = true;
            audioRecord.setImageResource(R.drawable.ic_microphone_settings_grey600_24dp);
            //audioTimer.setText("");
            toastShow("Click again to finish capture, maximum 6 minutes");
            if (stateAudioCapture) {
                //    audioRecord.setImageResource(R.drawable.audio_stop);
            }
        } else {
            recorderAudio.stop();
            recorderAudio.release();
            //audioTimer.setText("Voice capture successfully and ready to upload");
            stateAudioCapture = false;
            audioRecord.setImageResource(R.drawable.ic_microphone_grey600_24dp);
            if (!stateAudioCapture) {
                //      audioRecord.setImageResource(R.drawable.audio_recording);
            }
        }

    }

    //generate audio name
    public File generateAudioName() {
        // Create an audio file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "3GP_" + timeStamp + "_";
        String fileExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = fileExternalPath + "/data/audios/";
        File fileDir = new File(filePath);
        fileDir.mkdirs();
        File fileCreate = null;
        try {
            fileCreate = File.createTempFile(audioFileName, ".3gp", fileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileCreate;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            uri = data.getData();
            Bitmap bitmap = null;
            try {
                // File file = new File(uri.toString());
                mCurrentPhotoPath = FilePath.getPath(getApplicationContext(), uri);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage.setImageBitmap(bitmap);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getExtras() == null && photoFileStr != null) {
                    Uri uris = Uri.fromFile(new File(photoFileStr));
                    uri = uris;
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadImage.setImageBitmap(bitmap);
                } else {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    uploadImage.setImageBitmap(imageBitmap);
                }
                galleryAddPic();

            } else {
                System.out.println("no data passed");

            }
        }
    }

    //upload audio AsyncTask operation
    class AsyncUploadAudio extends AsyncTask<String, Void, String> {
        Context context;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundtry = "******";
        int byteRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        AsyncUploadAudio(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... paramUrl) {
            //initialize value of urlWebPath
            urlWebAudioPath = "";
            File uploadFile = new File(nCurrentAudioPath);
//            String[] parts = mCurrentPhotoPath.split("/");
//            final String fileName = parts[parts.length - 1];
            try {
                FileInputStream fileInputStream = new FileInputStream(nCurrentAudioPath);
                URL url = new URL(paramUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multiplart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundtry);
                connection.setRequestProperty("uploadedfile", nCurrentAudioPath);
                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphens + boundtry + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data;name=\"" +
                        "uploadedfile\";filename=\"" + nCurrentAudioPath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                while ((byteRead = fileInputStream.read(buffer, 0, bufferSize)) != -1) {
                    dataOutputStream.write(buffer, 0, byteRead);
                }
                fileInputStream.close();
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundtry + twoHyphens + lineEnd);
                dataOutputStream.flush();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = bufferedReader.readLine()) != null) {
                    builder.append(str);
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (!result.equals("-1")) {
                    if (!result.equals("-2")) {
                        if (!result.equals("999")) {
                            if (!result.equals("-3")) {
                                String str[] = result.split("/");
                                String urlPath = str[str.length - 1];
                                String urlHead = "http://moneco.monash.edu.au/audios/";
                                urlWebAudioPath = urlHead + urlPath;
                                uploadObservation();
                            } else {
                                toastShow("Server error, please try again late");
                                //close popup
                                if (popupWindow != null) {
                                    aviUpload.hide();
                                    popupWindow.dismiss();
                                }
                            }
                        } else {
                            toastShow("Audio already uploaded, Thank you");
                            //close popup
                            if (popupWindow != null) {
                                aviUpload.hide();
                                popupWindow.dismiss();
                            }
                        }
                    } else {
                        toastShow("Audio type is not correct, only .3gp, .wav, .mp3, .aac, .amr are acceptable");
                        //close popup
                        if (popupWindow != null) {
                            aviUpload.hide();
                            popupWindow.dismiss();
                        }
                    }
                } else {
                    toastShow("Audio is too large, please record audio smaller than 10MB");
                    //close popup
                    if (popupWindow != null) {
                        aviUpload.hide();
                        popupWindow.dismiss();
                    }
                }
            }
        }
    }


    //upload AsyncTask operation
    class AsyncUploadImage extends AsyncTask<String, Void, String> {
        Context context;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundtry = "******";
        int byteRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        AsyncUploadImage(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... paramUrl) {
            //initialize value of urlWebPath
            urlWebPath = "";
            File uploadFile = new File(mCurrentPhotoPath);
//            String[] parts = mCurrentPhotoPath.split("/");
//            final String fileName = parts[parts.length - 1];
            try {
                FileInputStream fileInputStream = new FileInputStream(mCurrentPhotoPath);
                URL url = new URL(paramUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multiplart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundtry);
                connection.setRequestProperty("uploadedfile", mCurrentPhotoPath);
                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(twoHyphens + boundtry + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data;name=\"" +
                        "uploadedfile\";filename=\"" + mCurrentPhotoPath + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                while ((byteRead = fileInputStream.read(buffer, 0, bufferSize)) != -1) {
                    dataOutputStream.write(buffer, 0, byteRead);
                }
                fileInputStream.close();
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundtry + twoHyphens + lineEnd);
                dataOutputStream.flush();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = bufferedReader.readLine()) != null) {
                    builder.append(str);
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (!result.equals("-1")) {
                    if (!result.equals("-2")) {
                        if (!result.equals("999")) {
                            if (!result.equals("-3")) {
                                String str[] = result.split("/");
                                String urlPath = str[str.length - 1];
                                String urlHead = "http://moneco.monash.edu.au/images/";
                                urlWebPath = urlHead + urlPath;
                                if (!nCurrentAudioPath.equals("")) {
                                    uploadAudio();
                                } else {
                                    uploadObservation();
                                }
                            } else {
                                toastShow("Server error, please try again late");
                                //close popup
                                if (popupWindow != null) {
                                    aviUpload.hide();
                                    popupWindow.dismiss();
                                }
                            }
                        } else {
                            toastShow("Photo already uploaded, Thank you");
                            //close popup
                            if (popupWindow != null) {
                                aviUpload.hide();
                                popupWindow.dismiss();
                            }
                        }
                    } else {
                        toastShow("Image type is not correct, only .jpg, .gif, .png, .jpeg are acceptable");
                        //close popup
                        if (popupWindow != null) {
                            aviUpload.hide();
                            popupWindow.dismiss();
                        }
                    }
                } else {
                    toastShow("Photo is too large, please choose another photo smaller than 20MB");
                    //close popup
                    if (popupWindow != null) {
                        aviUpload.hide();
                        popupWindow.dismiss();
                    }
                }
            }
        }
    }

    //upload media record
    class AsyncUploadMediaRecord extends AsyncTask<String, Void, String> {
        Context context;
        URL url;
        HttpURLConnection connection;

        AsyncUploadMediaRecord(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                //add the AsyncUploadImage task result urlWebPath to AsyncUploadMediaRecord task
                if (!urlWebPath.equals("")) {
                    if (urlWebAudioPath.equals("")) {
                        observationRecord.setAudioUrl("");
                    } else {
                        observationRecord.setAudioUrl(urlWebAudioPath);
                    }
                    observationRecord.setImageUrl(urlWebPath);
                    url = new URL(urls[0]);
                    String dataSend = "media_description=" + URLEncoder.encode(observationRecord.getDescription(), "UTF-8")
                            + "&species_common_name=" + URLEncoder.encode(observationRecord.getCommonName(), "UTF-8")
                            + "&species_scientific_name=" + URLEncoder.encode(observationRecord.getScientificName(), "UTF-8")
                            + "&media_latitude=" + URLEncoder.encode(String.valueOf(observationRecord.getMediaLatitude()), "UTF-8")
                            + "&media_longitude=" + URLEncoder.encode(String.valueOf(observationRecord.getMediaLongitude()), "UTF-8")
                            + "&image_url=" + URLEncoder.encode(observationRecord.getImageUrl(), "UTF-8")
                            + "&audio_url=" + URLEncoder.encode(observationRecord.getAudioUrl(), "UTF-8")
                            + "&upload_date=" + URLEncoder.encode(observationRecord.getUploadDate(), "UTF-8")
                            + "&user_id=" + URLEncoder.encode(String.valueOf(observationRecord.getUserId()), "UTF-8");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(dataSend.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(dataSend.getBytes());
                    outputStream.flush();
                    InputStream inputStream = null;
                    if (connection.getResponseCode() == 200) {
                        inputStream = connection.getInputStream();
                    }
                    String re = "";
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    while ((re = bufferedReader.readLine()) != null) {
                        builder.append(re);
                    }
                    return builder.toString();
                } else {
                    toastShow("image upload failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //close popup
            if (popupWindow != null) {
                aviUpload.hide();
                popupWindow.dismiss();
            }
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int states = 0;
                    states = jsonObject.getInt("state");
                    switch (states) {
                        case 1:
                            //add record history to server
                            String queryHistory = "http://moneco.monash.edu.au/selectLastRecord_InsertHistory.php";
                            new AsyncInsertHistory(UploadObservation.this).execute(queryHistory);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toastShow("Upload observation successfully");
                                    Intent intent = new Intent(UploadObservation.this, MapsActivity.class);
                                    startActivity(intent);
                                }
                            }, 500);

                            break;
                        case 7:
                            toastShow("Upload Failed, Please open GPS and try again");
                            break;
                        case 9:
                            toastShow("Upload Failed, inputs are invalid, try again");
                            break;
                        default:
                            toastShow("Error");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //close popup
                    if (popupWindow != null) {
                        aviUpload.hide();
                        popupWindow.dismiss();
                    }
                }
            }
        }
    }

    //insert history to server
    class AsyncInsertHistory extends AsyncTask<String, Void, String>{
        Context context;
        HttpURLConnection connection;

        AsyncInsertHistory(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... urls) {
            if (userId != -1){
                try {
                    URL url = new URL(urls[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    String data = "userId=" + URLEncoder.encode(String.valueOf(userId), "UTF-8");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null){
                try {
                    JSONObject result = new JSONObject(data);
                    int code = result.getInt("state");
                    switch (code){
                        case -1:
                            Toast.makeText(UploadObservation.this, "no user found, please login again", Toast.LENGTH_SHORT).show();
                            break;
                        case -11:
                            Toast.makeText(UploadObservation.this, "no data retrieved from server, please upload again", Toast.LENGTH_SHORT).show();
                            break;
                        case 11:
                            System.out.println("Add history successfully");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UploadObservation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestTakePhotoPermissions();
                } else {
                    takePhoto.setClickable(false);
                    audioRecord.setClickable(false);
                    galleryButton.setClickable(false);
                    submitButton.setClickable(false);
                    Toast.makeText(getApplicationContext(),
                            "PERMISSION DENIED, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == PICK_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickPhoto();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UploadObservation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestTakePhotoPermissions();
                } else {
                    takePhoto.setClickable(false);
                    audioRecord.setClickable(false);
                    galleryButton.setClickable(false);
                    submitButton.setClickable(false);
                    Toast.makeText(getApplicationContext(),
                            "PERMISSION DENIED, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_AUDIO_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialAudio();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UploadObservation.this, Manifest.permission.RECORD_AUDIO)) {
                    requestCaptureAudioPermissions();
                } else {
                    takePhoto.setClickable(false);
                    audioRecord.setClickable(false);
                    galleryButton.setClickable(false);
                    submitButton.setClickable(false);
                    Toast.makeText(getApplicationContext(),
                            "PERMISSION DENIED, please go to system setting to change permission, Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //require take photo permission for camera
    public void requestTakePhotoPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The application need a permission for using camera, do you want to try again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(UploadObservation.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            takePhoto.setClickable(false);
                            audioRecord.setClickable(false);
                            galleryButton.setClickable(false);
                            submitButton.setClickable(false);
                            Toast.makeText(getApplicationContext(),
                                    "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }
    }

    //require capture audio permission
    public void requestCaptureAudioPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The application need a permission for using Audio Recorder, do you want to try again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(UploadObservation.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CAPTURE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            takePhoto.setClickable(false);
                            audioRecord.setClickable(false);
                            galleryButton.setClickable(false);
                            submitButton.setClickable(false);
                            Toast.makeText(getApplicationContext(),
                                    "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.galleryButton) {
            if (ActivityCompat.checkSelfPermission(UploadObservation.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
            } else {
                pickPhoto();
            }
        } else if (v.getId() == R.id.takePhoto) {
            if (ActivityCompat.checkSelfPermission(UploadObservation.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
            } else {
                openCamera();
            }
        } else if (v.getId() == R.id.audioRecord) {
            if (ActivityCompat.checkSelfPermission(UploadObservation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(UploadObservation.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CAPTURE);
            } else if (ActivityCompat.checkSelfPermission(UploadObservation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(UploadObservation.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_CAPTURE);
            } else if (ActivityCompat.checkSelfPermission(UploadObservation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(UploadObservation.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_CAPTURE);
            } else {
                initialAudio();
            }
        } else if (v.getId() == R.id.submitButton) {
            checkUpload();
        } else if (v.getId() == R.id.uploadBack) {
            timerState();
            Intent intent = new Intent(UploadObservation.this, MapsActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.audioPlayer) {
            playAudio();
        } else if (v.getId() == R.id.playStop) {
            stopAudio();
        } else if (v.getId() == R.id.popDismiss){
            popupWindow.dismiss();
        }
    }

    //media player time state when jump to another activity
    public void timerState() {
        if (player != null && player.isPlaying() && timer != null) {
            timer.cancel();
        }
        if (player != null){
            player.stop();
        }
    }

    //stop audio
    public void stopAudio(){
        if (player != null && player.isPlaying()){
            player.stop();
            if (timer != null) {
                timer.cancel();
            }
        }
    }


    //play audio capture
    public void playAudio() {
        if (!nCurrentAudioPath.equals("")) {
            try {
                player.reset();
                player.setDataSource(this, Uri.parse(nCurrentAudioPath));
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                        timer = new Timer();
                        player.start();
                        if (player != null && player.isPlaying()) {
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (player.getCurrentPosition() < total) {
                                        seekBarAudioUpload.setProgress(player.getCurrentPosition());
                                    }
                                }

                            }, 0, 1);
                        }
                    }
                });
                player.prepare();
                total = player.getDuration();
                seekBarAudioUpload.setMax(total);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        seekBarAudioUpload.setProgress(0);
                        player.reset();
                        timer.cancel();
                    }
                });
                seekBarAudioUpload.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (timer != null) {
                        timer.cancel();
                    } if (player != null){
                        player.stop();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }


    //upload audio
    public void uploadAudio() {
        String query = "http://moneco.monash.edu.au/uploadAudio.php";
        new AsyncUploadAudio(this).execute(query);
    }

    //upload photo
    public void uploadPhoto() {
        String query = "http://moneco.monash.edu.au/uploadImage.php";
        new AsyncUploadImage(this).execute(query);
    }

    //upload observation
    public void uploadObservation() {
        String querys = "http://moneco.monash.edu.au/insertMedia.php";
        new AsyncUploadMediaRecord(this).execute(querys);
    }

    //upload operation
    public void checkUpload() {
        if (validateTextInput() != null) {
            observationRecord.setCommonName(validateTextInput().getCommonName());
            observationRecord.setDescription(validateTextInput().getDescription());
            if (!mCurrentPhotoPath.equals("")) {
                if (userId != -1) {
                    observationRecord.setUserId(userId);
                    if (getCurrentObservation() != null) {
                        observationRecord.setMediaLatitude(getCurrentObservation().latitude);
                        observationRecord.setMediaLongitude(getCurrentObservation().longitude);
                        String scientific = scientificNameInput.getText().toString();
                        observationRecord.setScientificName(scientific);
                        observationRecord.setUploadDate(getSystemDate());
                        //show animation
                        showLoadingPop();
                        if (!nCurrentAudioPath.equals("")) {
                            //also upload image in this audio async task
                            uploadPhoto();
                        } else {
                            urlWebAudioPath = "";
                            uploadPhoto();
                        }
                    } else {
                        toastShow("Please enable the GPS server of Device and try agian");
                    }
                } else {
                    toastShow("Please Sign in Firstly");
                }
            } else {
                toastShow("Please choose a photo firstly");
            }
        } else {
            toastShow("Common Name and Description can't be empty");
        }
    }

    // get System date
    public String getSystemDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    //validate input text
    public ObservationRecord validateTextInput() {
        ObservationRecord record;
        String commonInput = commonNameInput.getText().toString();
        String descriptions = descriptionInput.getText().toString();
        if (!commonInput.equals("") && !descriptions.equals("")) {
            record = new ObservationRecord(descriptions, commonInput);
            return record;
        }
        return null;
    }

    //get current observation location
    public LatLng getCurrentObservation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER) != null) {
                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                return latLng;
            } else if (locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER) != null){
                Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                return latLng;
            } else if (locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER) != null){
                Location location = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                return latLng;
            }
        }
        return null;
    }

    //prompt information for users
    public void toastShow(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    //set last String commonNamePrompt as red color
    public void setCommonNameColor() {
        commonNamePrompt.setText(Html.fromHtml("Common Name<font color='red'>*</font>:"));
        descriptionInput.setHint(Html.fromHtml("Description<font color='red'>*</font>:"));
    }

    //show popup window
    public void showLoadingPop(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.loading_popup, (ViewGroup) findViewById(R.id.uploadObservationLayout), false);
        View parentView = findViewById(R.id.uploadObservationLayout);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        aviUpload = (AVLoadingIndicatorView) view.findViewById(R.id.aviUpload);
        buttonDismiss = (Button) view.findViewById(R.id.popDismiss);
        buttonDismiss.setOnClickListener(this);
        aviUpload.show();
    }
}
