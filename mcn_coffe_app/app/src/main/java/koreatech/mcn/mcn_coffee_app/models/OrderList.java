package koreatech.mcn.mcn_coffee_app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by blood_000 on 2016-05-27.
 */
public class OrderList {

    public Cafe cafe;
    public User user;
    public String id;
    public ArrayList<Order> orders;
    public int cost;
    public int idx;
    public int status;

    public OrderList(String id, ArrayList<Order> orders, int cost, int idx, int status){
        this.id = id;
        this.orders = orders;
        this.cost = cost;
        this.idx = idx;
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderList{" +
                "id='" + id + '\'' +
                ", orders=" + orders +
                ", cost=" + cost +
                '}';
    }

    public OrderList(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("_id");
        this.cost = jsonObject.getInt("cost");
        this.idx = 12345;
        this.status = jsonObject.getInt("status");
        this.orders = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("orders");
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            orders.add(new Order(jsonObject1));
        }
    }
}
