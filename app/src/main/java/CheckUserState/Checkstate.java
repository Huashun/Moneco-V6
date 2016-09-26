package CheckUserState;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.example.liangchenzhou.moneco.LoginActivity;
import com.example.liangchenzhou.moneco.ProfileActivity;

import Entity.User;

/**
 * Created by liangchenzhou on 31/08/16.
 */
public final class Checkstate extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Context context;

    public Checkstate(Context context) {
        this.context = context;
    }

    //check if user logged in
    public boolean checkLogin() {
        Boolean state = false;
        if (context.getSharedPreferences("currentUser", Context.MODE_PRIVATE) != null) {
            sharedPreferences = context.getSharedPreferences("currentUser", Context.MODE_PRIVATE);
            state = sharedPreferences.getBoolean("state", false);
            return state;
        } else {
            state = false;
            return state;
        }
    }

    public User handleLoggedIn() {
        User user = new User(sharedPreferences.getInt("userid", -1), sharedPreferences.getString("username", ""),
                sharedPreferences.getString("password", ""), sharedPreferences.getString("email", ""));
        return user;
    }

    public void clearLoginState() {
        sharedPreferences = context.getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }
}
