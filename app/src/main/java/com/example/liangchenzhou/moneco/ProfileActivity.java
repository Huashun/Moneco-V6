package com.example.liangchenzhou.moneco;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import Adapter.HistoryAdapter;
import Adapter.MyFavoAdapter;
import CheckUserState.Checkstate;
import Entity.Favorite;
import Entity.History;
import Entity.User;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private EditText userName, password;
    private ListView lvMyfavo, listViewHistory;
    private ArrayList<History> histories;
    private ArrayList<Favorite> favorites;
    private HistoryAdapter historyAdapter;
    private MyFavoAdapter myFavoAdapter;
    private Button saveUserButton, editUserButton;
    private int userID = -1;
    private User editUser = new User();
    private String userEmail = "";
    private String previousPassword = "";
    private Checkstate checkstate;
    private TextView logoutControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        saveUserButton = (Button) findViewById(R.id.userSave);
        saveUserButton.setOnClickListener(this);
        editUserButton = (Button) findViewById(R.id.userEdit);
        editUserButton.setOnClickListener(this);
        logoutControl = (TextView) findViewById(R.id.logoutControl);
        logoutControl.setOnClickListener(this);
        logoutControl.setText("Log out");
        password = (EditText) findViewById(R.id.profilePassword);
        userName = (EditText) findViewById(R.id.profileUserName);
        password.setEnabled(false);
        userName.setEnabled(false);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        listViewHistory = (ListView) findViewById(R.id.listViewHistory);
        listViewHistory.setOnItemClickListener(this);
        histories = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, histories);
        listViewHistory.setAdapter(historyAdapter);

        //favorite list
        favorites = new ArrayList<>();
        lvMyfavo = (ListView) findViewById(R.id.display_favo);
        lvMyfavo.setOnItemLongClickListener(this);
        myFavoAdapter = new MyFavoAdapter(this, favorites);
        lvMyfavo.setAdapter(myFavoAdapter);


        checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        User user = checkstate.handleLoggedIn();
        userName.setText(user.getUserName());
        password.setText(user.getPassword());
        previousPassword = user.getPassword();
        userID = user.getUserId();
        userEmail = user.getEmailAddress();

        String query = "http://moneco.monash.edu.au/selectHistory.php";
        new AsyncTaskHistory(this).execute(query);

        String favoriteQuery = "http://moneco.monash.edu.au/selectPreference.php";
        new AsyncTaskFavorite(this).execute(favoriteQuery);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProfileActivity.this.finish();
    }

    public void changeUsername(View view) {


    }

    public void home(View view) {
        Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
        }
    }

    public void mapFavorite(View view) {
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.userEdit) {
            password.setEnabled(true);
            userName.setEnabled(true);
        } else if (v.getId() == R.id.userSave) {
            password.setEnabled(false);
            userName.setEnabled(false);
            saveUsertoServer();
        } else if (v.getId() == R.id.logoutControl){
            if (logoutControl.getText().equals("Log out")){
                checkstate.clearLoginState();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                logoutControl.setText("Log in");
            }
        }
    }

    //change login state after change password
    public void changeLoginState(){
        if (!editUser.getPassword().equals(previousPassword)){
            checkstate.clearLoginState();
            previousPassword = editUser.getPassword();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            SharedPreferences sharedPreferences = this.getSharedPreferences("currentUser", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", editUser.getUserName()).commit();
        }
    }

    //save user information to server
    public void saveUsertoServer() {
        boolean inputState = getInputValues();
        if (inputState && editUser != null) {
            if (!editUser.getEmailAddress().equals("") && !editUser.getUserName().equals("")
                    && !editUser.getPassword().equals("")) {
                String query = "http://moneco.monash.edu.au/updateUser.php";
                new AsyncTaskEditUser(this).execute(query);
            }
        }
    }


    //get Input values and validate values
    public boolean getInputValues() {
        String passwordInput = password.getText().toString();
        String userNameInput = userName.getText().toString();
        if (!passwordInput.equals("") && !userNameInput.equals("")) {
            editUser.setUserName(userNameInput);
            editUser.setPassword(passwordInput);
            editUser.setEmailAddress(userEmail);
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "UserName and Password can not be empty", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.listViewHistory) {
            History history = histories.get(position);
            Intent intent = new Intent(ProfileActivity.this, DisplayHistory.class);
            intent.putExtra("history", history);
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int positions = position;
        if (parent.getId() == R.id.display_favo) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
            dialog.setTitle("Message")
                    .setMessage("Do you want to delete this favorite?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String deleteQuery = "http://moneco.monash.edu.au/deletePreference.php";
                            new AsyncTaskDeleteFavorite(ProfileActivity.this, favorites.get(positions).getFavoriteId(), positions).execute(deleteQuery);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        }
        return false;
    }

    //get history from server
    class AsyncTaskHistory extends AsyncTask<String, Void, String> {
        Context context;

        public AsyncTaskHistory(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (userID != -1) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String data = "user_Id=" + URLEncoder.encode(String.valueOf(userID), "UTF-8");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    InputStream inputStream = null;
//                    System.out.println(connection.getResponseCode());
//                    System.out.println(connection.getErrorStream());
                    inputStream = connection.getInputStream();
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
            if (data != null) {
                if (!data.equals("-1") && !data.equals("-3")) {
                    try {
                        JSONObject result = new JSONObject(data);
                        JSONArray arrayRsult = result.getJSONArray("result");
                        for (int i = 0; i < arrayRsult.length(); i++) {
                            JSONObject object = arrayRsult.getJSONObject(i);
                            if (!object.isNull("media_id") && !object.isNull("media_description") && !object.isNull("species_common_name")
                                    && !object.isNull("species_scientific_name") && !object.isNull("media_latitude") && !object.isNull("media_longitude")
                                    && !object.isNull("image_url") && !object.isNull("audio_url") && !object.isNull("upload_date") && !object.isNull("user_id")) {
                                History history = new History(object.getInt("media_id"), object.getString("media_description"), object.getString("species_common_name"), object.getString("species_scientific_name"),
                                        object.getDouble("media_latitude"), object.getDouble("media_longitude"), object.getString("image_url"), object.getString("audio_url"),
                                        object.getString("upload_date"), object.getInt("user_id"));
                                histories.add(history);

                            } else {
                                System.out.println("There are null values in json response");
                            }
                            historyAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    int code = Integer.parseInt(data);
                    switch (code) {
                        case -1:
                            Toast.makeText(ProfileActivity.this, "no user found, please login again", Toast.LENGTH_SHORT).show();
                            break;
                        case -3:
                            Toast.makeText(ProfileActivity.this, "server error, please login again or try late", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        }
    }


    //edit user information to server
    class AsyncTaskEditUser extends AsyncTask<String, Void, String> {
        Context context;

        public AsyncTaskEditUser(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (userID != -1) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String data = "userName=" + URLEncoder.encode(String.valueOf(editUser.getUserName()), "UTF-8")
                            + "&passwords=" + URLEncoder.encode(String.valueOf(editUser.getPassword()), "UTF-8")
                            + "&email=" + URLEncoder.encode(String.valueOf(editUser.getEmailAddress()), "UTF-8");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    InputStream inputStream = null;
//                    System.out.println(connection.getResponseCode());
//                    System.out.println(connection.getErrorStream());
                    inputStream = connection.getInputStream();
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
                JSONObject result = null;
                Integer code = null;
                try {
                    result = new JSONObject(data);
                    code = result.getInt("state");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code != null) {
                    switch (code) {
                        case 1:
                            Toast.makeText(ProfileActivity.this, "edit information successfully", Toast.LENGTH_SHORT).show();
                            changeLoginState();
                            break;
                        case 7:
                            Toast.makeText(ProfileActivity.this, "Inputs can not be empty", Toast.LENGTH_SHORT).show();
                            break;
                        case 9:
                            Toast.makeText(ProfileActivity.this, "Inputs can not be found, please try back", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        }
    }

    //get favorite species records
    class AsyncTaskFavorite extends AsyncTask<String, Void, String> {
        Context context;

        public AsyncTaskFavorite(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (userID != -1) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String data = "userId=" + URLEncoder.encode(String.valueOf(userID), "UTF-8");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    InputStream inputStream = null;
//                    System.out.println(connection.getResponseCode());
//                    System.out.println(connection.getErrorStream());
                    inputStream = connection.getInputStream();
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
            if (data != null) {
                if (!data.equals("-1") && !data.equals("-3")) {
                    try {
                        JSONObject result = new JSONObject(data);
                        JSONArray arrayRsult = result.getJSONArray("result");
                        for(int i = 0; i < arrayRsult.length(); i ++) {
                            JSONObject object = arrayRsult.getJSONObject(i);
                            int favorateId = object.getInt("preference_id");
                            String scientific = object.getString("scientific_name");
                            String commonname = object.getString("common_name");
                            double lat = object.getDouble("latitude");
                            double lng = object.getDouble("longitude");
                            String date = object.getString("date_preference");
                            String kingdom = object.getString("kingdom");
                            favorites.add(new Favorite(favorateId, scientific, commonname, kingdom, lat, lng, date));
                        }
                        myFavoAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    int code = Integer.parseInt(data);
                    switch (code) {
                        case -1:
                            Toast.makeText(ProfileActivity.this, "no user found, please login again", Toast.LENGTH_SHORT).show();
                            break;
                        case -3:
                            Toast.makeText(ProfileActivity.this, "server error, please login again or try late", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        }
    }

    //get favorite species records
    class AsyncTaskDeleteFavorite extends AsyncTask<String, Void, String> {
        Context context;
        int id;
        int recordIndex;

        public AsyncTaskDeleteFavorite(Context context, int id, int recordIndex) {
            this.context = context;
            this.id = id;
            this.recordIndex = recordIndex;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (userID != -1) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String data = "preferenceId=" + URLEncoder.encode(String.valueOf(id), "UTF-8");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    InputStream inputStream = null;
//                    System.out.println(connection.getResponseCode());
//                    System.out.println(connection.getErrorStream());
                    inputStream = connection.getInputStream();
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
                int code = Integer.parseInt(data);
                switch (code){
                    case 1:
                        Toast.makeText(ProfileActivity.this, "Delete favorite successfully", Toast.LENGTH_SHORT).show();
                        favorites.remove(recordIndex);
                        myFavoAdapter.notifyDataSetChanged();
                        break;
                    case -1:
                        Toast.makeText(ProfileActivity.this, "Fail to delete favorite", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }


}
