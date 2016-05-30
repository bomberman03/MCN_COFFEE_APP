package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood_000 on 2016-05-23.
 */
public class Order {

    public String id;
    public MenuModel menu;
    public List<Option> options;
    public int cost;
    public int count;

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", menu=" + menu +
                ", options=" + options +
                ", cost=" + cost +
                ", count=" + count +
                '}';
    }

    public Order(String id, MenuModel menu, List<Option> options, int cost, int count){
        this.id = id;
        this.menu = menu;
        this.options = options;
        this.cost = cost;
        this.count = count;
    }

    public Order(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("_id");
        this.cost = jsonObject.getInt("cost");
        this.count = jsonObject.getInt("count");
        JSONObject jsonMenu = jsonObject.getJSONObject("menu");
        this.menu = new MenuModel(jsonMenu);
        JSONArray jsonArray = jsonObject.getJSONArray("options");
        this.options = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject jsonOption = jsonArray.getJSONObject(i);
            this.options.add(new Option(jsonOption));
        }
    }
}
