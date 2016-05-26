package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Cafe implements Serializable {

    public String id;
    public String name;
    public String detail;
    public String thumbnail;
    public List<User> likes;
    public List<MenuModel> menus;

    @Override
    public String toString() {
        return "Cafe{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", likes=" + likes +
                ", menus=" + menus +
                '}';
    }

    public Cafe(String id, String name, String detail, String thumbnail, List<User> likes, List<MenuModel> menus){
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.thumbnail = thumbnail;
        this.likes = likes;
        this.menus = menus;
    }

    public Cafe(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.id = jsonObject.getString("_id");
        this.name = jsonObject.getString("name");
        this.detail = jsonObject.getString("detail");
        this.thumbnail = jsonObject.getString("thumbnail");
        JSONArray jsonLikeArray = jsonObject.getJSONArray("likes");
        for(int i=0; i<jsonLikeArray.length(); i++){
            // recursive code
        }
        JSONArray jsonMenuArray = jsonObject.getJSONArray("menus");
        for(int i=0; i<jsonMenuArray.length(); i++){
            // recursive code
        }
    }
}
