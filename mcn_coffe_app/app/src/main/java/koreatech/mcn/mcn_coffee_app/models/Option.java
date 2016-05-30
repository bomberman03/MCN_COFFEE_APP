package koreatech.mcn.mcn_coffee_app.models;

import android.graphics.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Option {

    public String id;
    public String name;
    public int cost;
    public List<Option> options;

    public Option(String id, String name, int cost, List<Option> options){
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.options = options;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", options=" + options +
                '}';
    }

    public Option(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("_id")) this.id = jsonObject.getString("_id");
        if(jsonObject.has("name")) this.name = jsonObject.getString("name");
        if(jsonObject.has("cost")) this.cost = jsonObject.getInt("cost");
        this.options = new ArrayList<>();
        if(jsonObject.has("options")) {
            JSONArray jsonArray = jsonObject.getJSONArray("options");
            for(int i=0; i<jsonArray.length(); i++){
                // recursive
            }
        }
    }
}
