package koreatech.mcn.mcn_coffee_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.adapter.CafeRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.adapter.MenuRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.MenuModel;
import koreatech.mcn.mcn_coffee_app.models.Option;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.request.CustomArrayRequest;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class MenuFragment extends TabFragment {

    private String authentication_key;

    private TextView cafe_name;
    private TextView cafe_detail;

    private Cafe cafe;
    private ArrayList<MenuModel> menus = new ArrayList<>();

    private MenuRecyclerViewAdapter menuRecyclerViewAdapter;
    private RecyclerView recyclerView;

    public void init(View view){
        cafe = ((OrderActivity) getActivity()).getCafe();

        cafe_name = (TextView) view.findViewById(R.id.cafe_name);
        cafe_detail = (TextView) view.findViewById(R.id.cafe_detail);

        cafe_name.setText(cafe.name);
        cafe_detail.setText(cafe.detail);
    }

    public void initRecyclerView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        menuRecyclerViewAdapter = new MenuRecyclerViewAdapter(getContext(), this, menus);
        recyclerView.setAdapter(menuRecyclerViewAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkAuthKey();
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        init(view);
        initRecyclerView(view);
        content_request();
        return view;
    }

    public void appendOrder(Order order){
        ((OrderActivity)getActivity()).routeOrder(order);
    }

    public void checkAuthKey(){
        SharedPreferences pref = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE);
        authentication_key = pref.getString("authentication_key", "");
        if(authentication_key.length() > 0) {
            // if authentication_key is not valid
        }
    }

    public void content_request(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/cafes/" + cafe.id + "/menus/";

        Map<String, String> params = new HashMap<>();

        CustomArrayRequest cafeListRequest = new CustomArrayRequest(Request.Method.GET, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                for(int i=0; i<jsonArray.length(); i++){
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MenuModel menu = new MenuModel(jsonObject);
                        menus.add(menu);
                        menuRecyclerViewAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("TAG", volleyError.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(cafeListRequest);
    }
}
