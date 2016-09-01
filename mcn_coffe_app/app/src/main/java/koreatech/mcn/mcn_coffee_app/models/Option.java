package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Option {

    public String name;
    public int cost;
    public List<Option> options;

    public Option(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("name")) this.name = jsonObject.getString("name");
        if(jsonObject.has("cost")) this.cost = jsonObject.getInt("cost");
        this.options = new ArrayList<>();
        if(jsonObject.has("options")) {
            JSONArray jsonArray = jsonObject.getJSONArray("options");
            for(int i=0; i<jsonArray.length(); i++){
                // recursive
                JSONObject object = jsonArray.getJSONObject(i);
                Option subOption = new Option(object);
                this.options.add(subOption);
            }
        }
    }
}
