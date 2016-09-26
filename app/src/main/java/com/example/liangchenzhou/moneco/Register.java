package com.example.liangchenzhou.moneco;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Entity.User;


public class Register extends AppCompatActivity implements View.OnClickListener {
    private Button regNow, backNow;
    private EditText email, userName, pwd, confirmPwd;
    private User regUser = new User();
    private String preparedCheckEmail = "";
    private String confirmInputPwd = "";
    private CheckBox checkBoxKnowledge;
    private TextView knowledgeText;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        checkBoxKnowledge = (CheckBox) findViewById(R.id.checkBoxKnowledge);
        knowledgeText = (TextView) findViewById(R.id.knowledgeText);
        knowledgeText.setOnClickListener(this);
        regNow = (Button) findViewById(R.id.buttonSubmitReg);
        backNow = (Button) findViewById(R.id.regBackMain);
        email = (EditText) findViewById(R.id.emailRegNow);
        userName = (EditText) findViewById(R.id.userNameRegNow);
        pwd = (EditText) findViewById(R.id.pwdRegNow);
        confirmPwd = (EditText) findViewById(R.id.confirmRegNow);
        regNow.setOnClickListener(this);
        backNow.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Register.this.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSubmitReg) {
            this.attemptRegister();
        } else if (v.getId() == R.id.regBackMain) {
            this.backHome();
        } else if (v.getId() == R.id.knowledgeText) {
            showPopupKnowledge();
        } else if (v.getId() == R.id.closeButton){
            popupWindow.dismiss();
        }
    }

    //show popup knowledge
    public void showPopupKnowledge(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.knowledge_popup, (ViewGroup) findViewById(R.id.layoutRegister), false);
        View parentView = findViewById(R.id.layoutRegister);
        popupWindow = new PopupWindow(view, (int) Math.floor(parentView.getWidth()* 0.8), (int) Math.floor(parentView.getHeight()* 0.8), true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0 ,0);
        Button closeButton = (Button) view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);
    }

    //get 'confirm inputs' and validate if thay are empty
    public String getInputConfirmValues() {
        String newConfirm = confirmPwd.getText().toString();
        if (!newConfirm.equals("")) {
            return newConfirm;
        }
        return "NA";
    }

    //get 'user email' and 'passwords' input and validate them
    public User getInputValues() {
        String newEmail = email.getText().toString();
        String newUserName = userName.getText().toString();
        String newPassword = pwd.getText().toString();
        if (!newEmail.equals("") && !newPassword.equals("") && !newUserName.equals("")) {
            return new User(newUserName, newPassword, newEmail);
        }
        return null;
    }

    // check if email already exists (not finishing)
    public void checkEmailExist(String checkEmail) {
        preparedCheckEmail = checkEmail;
        String query = "http://moneco.monash.edu/checkEmailExist.php";
        new AsyncTaskCheckEmail(this).execute(query);
    }

    //check if two times passwords input are same
    public boolean checkInputPasswords(String newPwds, String confirmPwds) {
        if (newPwds.equals(confirmPwds)) {
            return true;
        } else {
            return false;
        }
    }

    // validate the email address format
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

    // attempt register action and validate all the inputs
    public void attemptRegister() {
        try {
            if (this.getInputValues() != null && !this.getInputConfirmValues().equals("NA")) {
                regUser = this.getInputValues();
                confirmInputPwd = this.getInputConfirmValues();
                if (isEmailValid(regUser.getEmailAddress())) {
                    if (checkBoxKnowledge.isChecked()) {
                        //invoke asynctask to check email exist
                        checkEmailExist(regUser.getEmailAddress());
                    } else {
                        Toast.makeText(this, "Please agree to the terms and conditions firstly", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Email address not correct", Toast.LENGTH_SHORT).show();
                }
            } else {
                // validation for empty inputs
                Toast.makeText(this, "Inputs cannot be Empty", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {

        }
    }

    //insert user to server
    class AsyncRegister extends AsyncTask<String, Void, String> {
        private Context context;

        AsyncRegister(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... insertUrl) {
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(insertUrl[0]);
                String data = "userName=" + URLEncoder.encode(regUser.getUserName(), "UTF-8")
                        + "&passwords=" + URLEncoder.encode(regUser.getPassword(), "UTF-8")
                        + "&email=" + URLEncoder.encode(regUser.getEmailAddress(), "UTF-8");
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
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int states = 0;
                    states = jsonObject.getInt("state");

                    switch (states) {
                        case 1:
                            toastShow("Register Successfully");
                            Intent intent = new Intent(Register.this, LoginActivity.class);
                            startActivity(intent);
                            break;
                        case 7:
                            toastShow("Register Failed, inputs can't be empty, try again");
                            break;
                        case 9:
                            toastShow("Register Failed, try again");
                            break;
                        default:
                            toastShow("Error");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //prompt information for users
    public void toastShow(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    //back to home screen
    public void backHome() {
        Intent intent = new Intent(Register.this, MapsActivity.class);
        startActivity(intent);
    }


    //check email exist asynctask
    class AsyncTaskCheckEmail extends AsyncTask<String, Void, String> {
        Context context;

        public AsyncTaskCheckEmail(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (!preparedCheckEmail.equals("")) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String data = "email=" + URLEncoder.encode(String.valueOf(preparedCheckEmail), "UTF-8");
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
                Integer dataCode = Integer.parseInt(data);
                if (dataCode != null) {
                    switch (dataCode) {
                        case 1:
                            //popup dialog with "Email already exist"
                            Toast.makeText(Register.this, "Email already exist", Toast.LENGTH_SHORT).show();
                            break;
                        case 0:
                            boolean checkInputPasswordsState = checkInputPasswords(regUser.getPassword(), confirmInputPwd);
                            if (checkInputPasswordsState == true) {
                                String queryUrl = "http://moneco.monash.edu.au/insert.php";
                                //pass valid 'regUser' to global variable 'userInsert'
                                if (regUser != null) {
                                    new AsyncRegister(Register.this).execute(queryUrl);
                                }
                            } else {
                                //popup dialog with "Input Passwords not same!"
                                Toast.makeText(Register.this, "Passwords are not same, try again", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            }
        }
    }
}
