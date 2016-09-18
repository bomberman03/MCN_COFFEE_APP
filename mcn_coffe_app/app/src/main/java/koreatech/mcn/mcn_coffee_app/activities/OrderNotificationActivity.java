package koreatech.mcn.mcn_coffee_app.activities;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.adapter.OrderRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

public class OrderNotificationActivity extends NetworkActivity {

    private OrderList orderList;

    private String order_id;

    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    private TextView cafeName;
    private TextView cafeDetail;
    private Button receiveButton;

    private ArrayList<Order> orders;

    private static MaterialDialog confirmDialog;

    public static void showConfirmDialog(){  confirmDialog.show(); }

    public static void hideConfirmDialog(){ confirmDialog.hide(); }

    public void getOrderList(String id)
    {
        order_id = id;
    }

    private String TAG = "OrderNotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);
        getOrderList(getIntent().getStringExtra("id"));
        init();
        initListener();
        initRecyclerView();
        content_request();
    }

    public void init()
    {
        orders = new ArrayList<>();
        cafeName = (TextView) findViewById(R.id.cafe_name);
        cafeDetail = (TextView) findViewById(R.id.cafe_detail);
        receiveButton = (Button) findViewById(R.id.order_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        confirmDialog = new MaterialDialog.Builder(this)
                .title("주문 수령 확인")
                .customView(R.layout.score_view, true)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        RatingBar ratingBar = (RatingBar) confirmDialog.getCustomView().findViewById(R.id.ratingBar);
                        ratingBar.setRating(2);
                    }
                })
                .positiveText("수령")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideConfirmDialog();
                        requestReceiveMessage();
                    }
                })
                .negativeText("취소")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideConfirmDialog();
                    }
                })
                .build();
        successDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        confirmDialog.dismiss();
    }

    public void initListener(){
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    public void initRecyclerView(){
        orderRecyclerViewAdapter  = new OrderRecyclerViewAdapter(orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderRecyclerViewAdapter);
    }

    public void content_request() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/" + order_id + "/";

        JSONObject jsonParam = new JSONObject();

        JsonObjectRequest getOrderRequest = new JsonObjectRequest
                (Request.Method.GET, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
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
                        hideProgressDialog();
                        showFailureDialog(error.getMessage());
                    }
                });
        queue.add(getOrderRequest);
        showProgressDialog();
    }

    public void requestReceiveMessage() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/" + order_id + "/receive/";

        JSONObject jsonParam = new JSONObject();

        JsonObjectRequest updateOrderRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        showSuccessDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        showFailureDialog(error.getMessage());
                    }
                });
        queue.add(updateOrderRequest);
        showProgressDialog();
    }

    public void updateView() {
        //cafeName.setText(orderList.cafe.name);
        //cafeDetail.setText(orderList.cafe.detail);
        Iterator<Order> iterator = orderList.orders.iterator();
        while(iterator.hasNext()) {
            Order order = iterator.next();
            orderRecyclerViewAdapter.add(order);
        }
    }
}
