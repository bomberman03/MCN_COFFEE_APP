package koreatech.mcn.mcn_coffee_app.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import koreatech.mcn.mcn_coffee_app.models.User;

/**
 * Created by pathFinder on 2016-08-29.
 */
public class AuthManager {

    private final String prefName = "Auth";

    private static AuthManager singleton = new AuthManager();

    public static AuthManager getInstance(){
        return singleton;
    }

    private User user;

    private String TAG = "AuthManager";

    public void initUser(Context context, JSONObject jsonObject) {
        this.user = new User(jsonObject);
        String jsonString = jsonObject.toString();
        SharedPreferences pref = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Log.d(TAG, "initUser : " + jsonString);
        editor.putString("user", jsonString);
        editor.commit();
    }

    public User getCurrentUser(Context context)
    {
        if(this.user==null) {
            SharedPreferences pref = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
            String jsonString = pref.getString("user", "");
            Log.d(TAG, "getCurrentUser : " + jsonString);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                this.user = new User(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this.user;
    }

    public void clearUser(Context context) {
        this.user = null;
        SharedPreferences pref = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("user");
        editor.commit();
    }
}
