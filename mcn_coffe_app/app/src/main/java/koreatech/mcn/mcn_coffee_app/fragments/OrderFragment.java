package koreatech.mcn.mcn_coffee_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import koreatech.mcn.mcn_coffee_app.models.Order;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class OrderFragment extends TabFragment {

    private String authentication_key;

    private ArrayList<Order> orders = new ArrayList<>();
    private int orderCost = 0;

    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    private Button orderButton;
    private Button initButton;

    private MaterialDialog progressDialog;
    private MaterialDialog failureDialog;
    private MaterialDialog successDialog;

    public void showSuccessDialog()
    {
        successDialog.show();
    }

    public void hideSuccessDialog()
    {
        successDialog.hide();
    }

    public void  showFailureDialog(String message){
        failureDialog.setContent(message);
        failureDialog.show();
    }

    public void hideFailureDialog(){
        failureDialog.hide();
    }

    public void showProgressDialog(){
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.hide();
    }

    public void init(View view){
        orderButton = (Button) view.findViewById(R.id.order_button);
        initButton = (Button) view.findViewById(R.id.init_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressDialog = new MaterialDialog.Builder(getContext())
                .title("주문 요청중")
                .content("잠시만 기다려주세요")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
        failureDialog = new MaterialDialog.Builder(getContext())
                .title("주문 실패")
                .content("")
                .positiveText("확인")
                .build();
        successDialog = new MaterialDialog.Builder(getContext())
                .title("주문 성공")
                .content("주문이 성공적으로 처리되었습니다.")
                .positiveText("확인")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        clearOrder();
                    }
                })
                .build();
    }

    public void initListener(){
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // order confirmation dialog and push order to server
                showProgressDialog();
                postOrderRequest();
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
        checkAuthKey();
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

    public void checkAuthKey(){
        SharedPreferences pref = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE);
        authentication_key = pref.getString("authentication_key", "");
        if(authentication_key.length() > 0) {
            // if authentication_key is not valid
        }
    }

    public void postOrderRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/cafes/" + ((OrderActivity) getActivity()).getCafe().id + "/orders/";
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("cafe", ((OrderActivity) getActivity()).getCafe().id);
            jsonParam.put("user", AuthManager.getInstance().getCurrentUser().id);
            jsonParam.put("cost", orderCost);
            JSONArray jsonArray = new JSONArray();

            for(int i=0; i<orders.size(); i++)
            {
                JSONObject orderObject = new JSONObject();

                JSONObject menuObject = new JSONObject();
                orderObject.put("menu",menuObject);

                menuObject.put("_id", orders.get(i).menu.id);
                menuObject.put("name", orders.get(i).menu.name);

                JSONArray optionJsonArray = new JSONArray();
                for(int j=0; j<orders.get(i).options.size(); j++)
                {
                    JSONObject optionObject = new JSONObject();
                    //optionObject.put("_id", orders.get(i).menu.options.get(j).id);
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
            e.printStackTrace();
        }
        JsonObjectRequest postOrderRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        showSuccessDialog();
                        String id = "";
                        try {
                            if(jsonObject.has("_id"))
                                id = jsonObject.getString("_id");
                            else
                                return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        OrderListManager.getInstance().insertNewOrder(new OrderDTO(id, OrderDBHelper.ORDERS_STATUS_WAIT));
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
