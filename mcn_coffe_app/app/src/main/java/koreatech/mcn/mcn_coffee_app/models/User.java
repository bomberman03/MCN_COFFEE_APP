package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class User {
    public String id;
    public String username;
    public String name;
    public String email;
    public String phone;
    public String image;

    public User(JSONObject jsonObject)
    {
        try {
            if(jsonObject.has("_id"))
                this.id = jsonObject.getString("_id");
            if(jsonObject.has("username"))
                this.username = jsonObject.getString("username");
            if(jsonObject.has("name"))
                this.name = jsonObject.getString("name");
            if(jsonObject.has("email"))
                this.email = jsonObject.getString("email");
            if(jsonObject.has("phone"))
                this.phone = jsonObject.getString("phone");
            if(jsonObject.has("image"))
                this.image = jsonObject.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
