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
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.adapter.OrderRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.models.OrderList;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;

public class OrderNotificationActivity extends NetworkActivity {

    private final int WAIT = 0;
    private final int COMPLETE = 1;
    private final int CANCEL = 2;
    private final int RECEIVE = 4;

    private OrderList orderList;

    private String order_id;

    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    private TextView orderIdx;
    private TextView cafeName;
    private TextView cafeDetail;

    private Button receiveButton;
    private Button cancelButton;
    private Button commentButton;

    private ArrayList<Order> orders;

    private static MaterialDialog confirmDialog;
    private static MaterialDialog cancelDialog;
    private static MaterialDialog commentDialog;

    public static void showConfirmDialog(){  confirmDialog.show(); }

    public static void hideConfirmDialog(){ confirmDialog.hide(); }

    public static void showCancelDialog() { cancelDialog.show(); }

    public static void hideCancelDialog() { cancelDialog.hide(); }

    public static void showCommentDialog() { commentDialog.show(); }

    public static void hideCommentDialog() { commentDialog.hide(); }

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
        orderIdx = (TextView) findViewById(R.id.order_idx);
        cafeName = (TextView) findViewById(R.id.cafe_name);
        cafeDetail = (TextView) findViewById(R.id.cafe_detail);

        receiveButton = (Button) findViewById(R.id.order_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        commentButton = (Button) findViewById(R.id.comment_button);

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
                        RatingBar ratingBar = (RatingBar) confirmDialog.getCustomView().findViewById(R.id.ratingBar);
                        hideConfirmDialog();
                        requestReceiveMessage((int) ratingBar.getRating());
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
        cancelDialog = new MaterialDialog.Builder(this)
                .title("주문 취소 확인")
                .content("주문을 취소 하시겠습니까?")
                .positiveText("예")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideCancelDialog();
                        requestCancelMessage();
                    }
                })
                .negativeText("아니오")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideCancelDialog();
                    }
                })
                .build();
        successDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commentDialog = new MaterialDialog.Builder(this)
                .title("주문 후기")
                .customView(R.layout.comment_view, true)
                .positiveText("전송")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RatingBar ratingBar = (RatingBar) commentDialog.getCustomView().findViewById(R.id.ratingBar);
                        EditText comment = (EditText) commentDialog.getCustomView().findViewById(R.id.comment);
                        hideCommentDialog();
                        requestCommentMessage((int)ratingBar.getRating(), comment.getText().toString());
                    }
                })
                .negativeText("취소")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hideCommentDialog();
                    }
                })
                .build();
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
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
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
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/" + order_id + "/";

        JSONObject jsonParam = new JSONObject();

        JsonObjectRequest getOrderRequest = new JsonObjectRequest
                (Request.Method.GET, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        try {
                            orderList = new OrderList(jsonObject);
                            switch (orderList.status)
                            {
                                case WAIT:
                                    cancelButton.setVisibility(View.VISIBLE);
                                    break;
                                case COMPLETE:
                                    receiveButton.setVisibility(View.VISIBLE);
                                    break;
                                case CANCEL:
                                case RECEIVE:
                                    commentButton.setVisibility(View.VISIBLE);
                                    break;
                            }
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

    public void requestReceiveMessage(int rcv_point) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/" + order_id + "/receive/";

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("rcv_point",rcv_point);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void requestCancelMessage() {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/" + order_id + "/cancel/";

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

    public void requestCommentMessage(int cmt_point, String comment) {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/" + order_id + "/comment/";

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("cmt_point", cmt_point);
            jsonParam.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        orderIdx.setText("주문 번호 " + orderList.idx);
        cafeName.setText(orderList.cafe.name);
        cafeDetail.setText(orderList.cafe.detail);
        Iterator<Order> iterator = orderList.orders.iterator();
        while(iterator.hasNext()) {
            Order order = iterator.next();
            orderRecyclerViewAdapter.add(order);
        }
    }
}
