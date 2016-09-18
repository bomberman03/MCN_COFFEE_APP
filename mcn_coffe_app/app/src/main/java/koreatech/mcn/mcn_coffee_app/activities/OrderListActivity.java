package koreatech.mcn.mcn_coffee_app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.adapter.OrderListRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

public class OrderListActivity extends NetworkActivity {

    private ArrayList<OrderList> orderLists = new ArrayList<>();

    private RecyclerView recyclerView;
    private OrderListRecyclerViewAdapter orderListRecyclerViewAdapter;

    public void init(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initRecyclerView(){
        orderListRecyclerViewAdapter = new OrderListRecyclerViewAdapter(this, orderLists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderListRecyclerViewAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        init();
        initRecyclerView();
        content_request();
    }

    @Override
    protected void onResume() {
        super.onResume();
        content_request();
    }

    public void content_request(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/users";

        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("user", AuthManager.getInstance().getCurrentUser(getApplicationContext()).id);
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }

        JsonObjectRequest postOrderRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        orderListRecyclerViewAdapter.clear();
                        hideProgressDialog();
                        JSONArray jsonArray;
                        try {
                            jsonArray = jsonObject.getJSONArray("orders");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OrderList orderList = new OrderList(object);
                                orderLists.add(orderList);
                                orderListRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },  new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            hideProgressDialog();
                            showFailureDialog(volleyError.getMessage());
                        }
                });
        queue.add(postOrderRequest);
        showProgressDialog();
    }
}
