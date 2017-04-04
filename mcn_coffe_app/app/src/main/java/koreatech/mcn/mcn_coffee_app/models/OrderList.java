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
    public int status;
    public int rcv_point;
    public int cmt_point;
    public String comment;
    public long idx;

    public OrderList(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("_id")) this.id = jsonObject.getString("_id");
        if(jsonObject.has("cafe")) this.cafe = new Cafe(jsonObject.getJSONObject("cafe"));
        if(jsonObject.has("user")) this.user = new User(jsonObject.getJSONObject("user"));
        if(jsonObject.has("cost")) this.cost = jsonObject.getInt("cost");
        if(jsonObject.has("status")) this.status = jsonObject.getInt("status");
        if(jsonObject.has("order_idx")) this.idx = jsonObject.getInt("order_idx");
        this.orders = new ArrayList<>();
        if(jsonObject.has("orders")) {
            JSONArray jsonArray = jsonObject.getJSONArray("orders");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Order order = new Order(object);
                orders.add(order);
            }
        }
        if(jsonObject.has("rcv_point")) this.rcv_point = jsonObject.getInt("rcv_point");
        if(jsonObject.has("cmt_point")) this.cmt_point = jsonObject.getInt("cmt_point");
        if(jsonObject.has("comment")) this.comment = jsonObject.getString("comment");
    }
}
