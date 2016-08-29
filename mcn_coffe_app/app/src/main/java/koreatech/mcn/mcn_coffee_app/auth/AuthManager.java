package koreatech.mcn.mcn_coffee_app.auth;

import org.json.JSONObject;

import koreatech.mcn.mcn_coffee_app.models.User;

/**
 * Created by pathFinder on 2016-08-29.
 */
public class AuthManager {

    private static AuthManager singleton = new AuthManager();

    public static AuthManager getInstance(){
        return singleton;
    }

    private User user;

    public void initUser(JSONObject jsonObject)
    {
        this.user = new User(jsonObject);
    }

    public User getCurrentUser()
    {
        return this.user;
    }
}
