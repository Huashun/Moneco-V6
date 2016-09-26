package com.example.liangchenzhou.moneco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import CheckUserState.Checkstate;
import Entity.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public EditText useremail, password;
    public Button loginbt;
    private User userInsert = new User();
    private SharedPreferences sharedPreferences;
    private ActionProcessButton btnSignIn, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        useremail = (EditText) findViewById(R.id.edit_userName);
        password = (EditText) findViewById(R.id.edit_password);
        //loginbt = (Button) findViewById(R.id.button_login);

        buttonRegister = (ActionProcessButton) findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(this);
        btnSignIn = (ActionProcessButton) findViewById(R.id.button_login);
        btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        btnSignIn.setOnClickListener(this);
        btnSignIn.setProgress(0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LoginActivity.this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_login) {
            attemptLogin();
        } else if (v.getId() == R.id.button_register) {
            goReegister();
        }
    }


    public void attemptLogin() {
        try {
            if (this.getInput() != null) {
                final User logUser = getInput();
                if (isEmailValid(logUser.getEmailAddress())) {
                    btnSignIn.setProgress(1);
                    String queryUrl = "http://moneco.monash.edu.au/loginCheck.php";
                    //pass valid 'regUser' to global variable 'userInsert'
                    userInsert = logUser;
                    new AsyncLogin(this).execute(queryUrl);
                } else {
                    Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Email address and password cannot be emtpy.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }


    }

    public User getInput() {
        String newEmail = useremail.getText().toString();
        String newPassword = password.getText().toString();
        if (!newEmail.equals("") && !newPassword.equals("")) {
            return new User(newEmail, newPassword);
        }
        return null;
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." +
                        "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
                        "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]" +
                        "(?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|" +
                        "[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
                        "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    class AsyncLogin extends AsyncTask<String, Void, String> {
        private Context context;

        AsyncLogin(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... insertUrl) {
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(insertUrl[0]);
                String data = "email=" + URLEncoder.encode(userInsert.getEmailAddress(), "UTF-8")
                        + "&password=" + URLEncoder.encode(userInsert.getPassword(), "UTF-8");
                connection = (HttpURLConnection) url.openConnection();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            //-1 bianliang shi null
            //-2 bianliang youyige shi kong
            //-3 fuwuqiduande cuowu
            //
            final Handler handler = new Handler();
            if (result != null) {
                btnSignIn.setProgress(25);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!result.equals("-1")) {
                            btnSignIn.setProgress(50);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!result.equals("-2")) {
                                        btnSignIn.setProgress(75);
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!result.equals("-3")) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(result);
                                                        JSONArray array = jsonObject.getJSONArray("result");
                                                        JSONObject test = array.getJSONObject(0);
                                                        String email = test.getString("email");
                                                        final String username = test.getString("user_name");
                                                        String password = test.getString("passwords");
                                                        int userid = Integer.parseInt(test.getString("user_id"));
                                                        sharedPreferences = getApplicationContext().getSharedPreferences("currentUser", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("username", username);
                                                        editor.putString("password", password);
                                                        editor.putString("email", email);
                                                        editor.putInt("userid", userid);
                                                        editor.putBoolean("state", true);
                                                        editor.commit();
                                                        btnSignIn.setProgress(99);
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                btnSignIn.setProgress(100);
                                                                toastShow("Login Successfully, Hello " + username);
                                                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }, 100);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        btnSignIn.setProgress(0);
                                                        btnSignIn.setText("SIGN IN");
                                                    }
                                                } else {
                                                    btnSignIn.setProgress(0);
                                                    btnSignIn.setText("SIGN IN");
                                                    toastShow("Sorry, your password is not correct.");
                                                }
                                            }
                                        }, 300);

                                    } else {
                                        btnSignIn.setProgress(0);
                                        btnSignIn.setText("SIGN IN");
                                        toastShow("Email address and password cannot be empty");
                                    }
                                }
                            }, 300);

                        } else {
                            btnSignIn.setProgress(0);
                            btnSignIn.setText("SIGN IN");
                            toastShow("Email address and password are not filled");
                        }
                    }
                }, 300);

            }
        }

    }

    public void toastShow(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    public void goReegister() {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
    }

    public void home(View view) {
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Checkstate checkstate = new Checkstate(this);
        if (!checkstate.checkLogin()) {
        } else {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }


}
