package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public String toString() {
        return "MenuModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", cost=" + cost +
                ", options=" + options +
                '}';
    }

    public MenuModel(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.id = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
        this.detail = jsonObject.getString("detail");
        this.cost = jsonObject.getInt("cost");
        JSONArray jsonArray = jsonObject.getJSONArray("options");
        for(int i=0; i<jsonArray.length(); i++){
            // recursive code
        }
    }
}
