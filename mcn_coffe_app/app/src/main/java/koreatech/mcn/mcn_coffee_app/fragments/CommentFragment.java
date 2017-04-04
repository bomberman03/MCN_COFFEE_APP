package koreatech.mcn.mcn_coffee_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.adapter.CommentRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.adapter.OrderListRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.OrderList;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;
import koreatech.mcn.mcn_coffee_app.request.CustomArrayRequest;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class CommentFragment extends NetworkFragment{

    private ArrayList<OrderList> orderLists = new ArrayList<>();

    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;

    public void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    public void initRecyclerView(){
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(getContext(), orderLists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(commentRecyclerViewAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        init(view);
        initRecyclerView();
        content_request();
        return view;
    }

    public void content_request(){

        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getContext());
        Cafe cafe = ((OrderActivity) getActivity()).getCafe();
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/cafes/" + cafe.id + "/orders/comments";

        JSONObject jsonParam = new JSONObject();

        JsonObjectRequest postOrderRequest = new JsonObjectRequest
                (Request.Method.GET, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        commentRecyclerViewAdapter.clear();
                        hideProgressDialog();
                        JSONArray jsonArray;
                        try {
                            jsonArray = jsonObject.getJSONArray("orders");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OrderList orderList = new OrderList(object);
                                orderLists.add(orderList);
                                commentRecyclerViewAdapter.notifyDataSetChanged();
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
