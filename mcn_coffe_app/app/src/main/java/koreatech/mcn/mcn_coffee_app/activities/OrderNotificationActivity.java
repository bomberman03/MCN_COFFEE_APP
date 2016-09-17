package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.adapter.OrderRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

public class OrderNotificationActivity extends AppCompatActivity {

    private OrderList orderList;

    private String order_id;

    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    private Button receiveButton;

    public void getOrderList(String id)
    {
        order_id = id;
    }

    private String TAG = "OrderNotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);
        Intent intent = getIntent();
        init();
        initRecyclerView();
        content_request();
    }

    public void init()
    {
        getOrderList("57dbf945b4df13216b775b16");
        receiveButton = (Button) findViewById(R.id.order_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initRecyclerView(){
        orderRecyclerViewAdapter  = new OrderRecyclerViewAdapter(orderList.orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderRecyclerViewAdapter);
    }

    public void content_request(){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/57dbf945b4df13216b775b16/";

        JSONObject jsonParam = new JSONObject();

        JsonObjectRequest getOrderRequest = new JsonObjectRequest
                (Request.Method.GET, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(TAG, jsonObject.toString());
                        try {
                            orderList = new OrderList(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateView();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(getOrderRequest);
    }

    public void updateView() {
        Iterator<Order> iterator = orderList.orders.iterator();
        while(iterator.hasNext()){
            Order order = iterator.next();
            orderRecyclerViewAdapter.add(order);
        }
    }
}
