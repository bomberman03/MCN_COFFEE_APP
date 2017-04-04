package koreatech.mcn.mcn_coffee_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.adapter.OrderRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDBHelper;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDTO;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderListManager;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.models.User;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class OrderFragment extends NetworkFragment {

    private ArrayList<Order> orders = new ArrayList<>();
    private int orderCost = 0;

    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    private Button orderButton;
    private Button initButton;

    private MaterialDialog paymentDialog;

    private String TAG = "OrderFragment";

    public void init(View view){
        orderButton = (Button) view.findViewById(R.id.order_button);
        initButton = (Button) view.findViewById(R.id.init_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        paymentDialog = new MaterialDialog.Builder(getContext())
                .title("결제 방식")
                .content("예상 시간은 ")
                .items(new String[]{"신용 카드", "계좌 이체", "Google Pay"})
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        return true;
                    }
                })
                .positiveText("결제하기")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        hidePaymentDialog();
                        postOrderRequest();
                    }
                })
                .negativeText("취소")
                .build();
    }

    public String getLastTime() {
        int interval = 15;
        double time = 0.;
        for(Order order: orders){
            double mul = 1;
            for(int j=0; j<order.count; j++) {
                time += order.menu.time * mul;
                mul *= 0.5;
            }
            time += interval;
        }
        String timeStr = "";
        if(time > 3600) {
            timeStr += String.valueOf((int)Math.floor(time / 3600)) + "시 ";
            time %= 3600;
        }
        if(time > 60) {
            timeStr += String.valueOf((int)Math.floor(time / 60)) + "분 ";
            time %= 60;
        }
        if(time > 0) {
            timeStr += String.valueOf((int)Math.floor(time)) + "초 ";
        }
        timeStr += "입니다.";
        return timeStr;
    }

    public void showPaymentDialog() {
        paymentDialog.getContentView().setText("예상 대기 시간은 " + getLastTime());
        paymentDialog.show();
    }
    public void hidePaymentDialog() { paymentDialog.hide(); }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paymentDialog.dismiss();
    }

    public void initListener(){
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialog();
            }
        });
        initButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOrder();
            }
        });
    }

    public void initRecyclerView(){
        orderRecyclerViewAdapter  = new OrderRecyclerViewAdapter(this, orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderRecyclerViewAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        init(view);
        initListener();
        initRecyclerView();
        updateTotalCost();
        return view;
    }

    public void updateTotalCost(){
        orderCost = 0;
        for(Order order: orders){
            orderCost += (order.cost * order.count);
        }
        orderButton.setText("주문하기(" + orderCost + "원)");
        ((OrderActivity) getActivity()).updateOrder(orders.size());
    }

    public void addOrder(Order order) {
        if (orderRecyclerViewAdapter == null) {
            orderRecyclerViewAdapter = new OrderRecyclerViewAdapter(this, orders);
        }
        orderRecyclerViewAdapter.add(order);
        updateTotalCost();
    }

    public void clearOrder(){
        orderRecyclerViewAdapter.clear();
        updateTotalCost();
    }

    public void postOrderRequest() {
        showProgressDialog();
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/cafes/" + ((OrderActivity) getActivity()).getCafe().id + "/orders/";
        JSONObject jsonParam = new JSONObject();

        try {
            Cafe cafe = ((OrderActivity)getActivity()).getCafe();
            JSONObject cafeObject = new JSONObject();
            cafeObject.put("_id", cafe.id);
            cafeObject.put("name", cafe.name);
            cafeObject.put("detail", cafe.detail);
            jsonParam.put("cafe", cafeObject);
            User user = AuthManager.getInstance().getCurrentUser(getContext());
            if(user == null)
                Log.d(TAG, "user is null");
            else
                Log.d(TAG, "user : " + user);
            JSONObject userObject = new JSONObject();
            userObject.put("_id", user.id);
            userObject.put("username", user.username);
            userObject.put("name", user.name);
            userObject.put("email", user.email);
            userObject.put("phone", user.phone);
            jsonParam.put("user", userObject);
            jsonParam.put("cost", orderCost);
            JSONArray jsonArray = new JSONArray();

            for(int i=0; i<orders.size(); i++)
            {
                JSONObject orderObject = new JSONObject();

                JSONObject menuObject = new JSONObject();
                orderObject.put("menu", menuObject);

                menuObject.put("_id", orders.get(i).menu.id);
                menuObject.put("name", orders.get(i).menu.name);

                JSONArray optionJsonArray = new JSONArray();
                for(int j=0; j<orders.get(i).options.size(); j++)
                {
                    JSONObject optionObject = new JSONObject();
                    optionObject.put("name", orders.get(i).options.get(j).name);
                    optionJsonArray.put(optionObject);
                }

                orderObject.put("options", optionJsonArray);
                orderObject.put("cost", orders.get(i).cost);
                orderObject.put("count", orders.get(i).count);

                jsonArray.put(orderObject);
            }
            jsonParam.put("orders",jsonArray);
            jsonParam.put("status", 0);
        } catch (JSONException e){
            hideProgressDialog();
            e.printStackTrace();
        }
        JsonObjectRequest postOrderRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        showSuccessDialog();
                        orderRecyclerViewAdapter.clear();
                        String createdAt = "";
                        String updatedAt = "";
                        String id = "";
                        try {
                            if(jsonObject.has("createdAt"))
                                createdAt = jsonObject.getString("createdAt");
                            if(jsonObject.has("updatedAt"))
                                updatedAt = jsonObject.getString("updatedAt");
                            if(jsonObject.has("_id"))
                                id = jsonObject.getString("_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideProgressDialog();
                            showFailureDialog(e.getMessage());
                        }
                        OrderListManager.getInstance().insertNewOrder(new OrderDTO(createdAt, updatedAt, id, OrderDBHelper.ORDERS_STATUS_WAIT));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        showFailureDialog(error.getMessage());
                    }
                });
        queue.add(postOrderRequest);
    }

}
