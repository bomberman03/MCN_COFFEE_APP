package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Cafe implements Serializable {

    public String id;
    public String name;
    public String detail;
    public List<String> images;
    public List<User> likes;
    public List<MenuModel> menus;

    public Cafe(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("_id")) this.id = jsonObject.getString("_id");
        if(jsonObject.has("name")) this.name = jsonObject.getString("name");
        if(jsonObject.has("detail")) this.detail = jsonObject.getString("detail");
        this.images = new ArrayList<>();
        if(jsonObject.has("images")){
            JSONArray jsonImageArray = jsonObject.getJSONArray("images");
            for(int i=0; i<jsonImageArray.length(); i++)
            {
                String image = jsonImageArray.getString(i);
                this.images.add(image);
            }
        }
        this.menus = new ArrayList<>();
        if(jsonObject.has("menu")) {
            JSONArray jsonMenuArray = jsonObject.getJSONArray("menus");
            for (int i = 0; i < jsonMenuArray.length(); i++) {
                // recursive code
            }
        }
    }
}
