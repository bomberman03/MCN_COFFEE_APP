package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class MenuModel {

    public String id;
    public String name;
    public String detail;
    public int cost;
    public List<Option> options;

    public MenuModel(String id, String name, String detail, int cost, List<Option> options){
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.cost = cost;
        this.options = options;
    }

    public MenuModel(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("_id")) this.id = jsonObject.getString("_id");
        if(jsonObject.has("name")) this.name = jsonObject.getString("name");
        if(jsonObject.has("detail")) this.detail = jsonObject.getString("detail");
        if(jsonObject.has("cost")) this.cost = jsonObject.getInt("cost");
        options = new ArrayList<>();
        if(jsonObject.has("options")) {
            JSONArray jsonArray = jsonObject.getJSONArray("options");
            for (int i = 0; i < jsonArray.length(); i++) {
                // recursive code
                JSONObject object = jsonArray.getJSONObject(i);
                Option option = new Option(object);
                options.add(option);
            }
        }
    }
}
