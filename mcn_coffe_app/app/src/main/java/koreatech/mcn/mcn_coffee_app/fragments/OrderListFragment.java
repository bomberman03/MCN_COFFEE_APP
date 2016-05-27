package koreatech.mcn.mcn_coffee_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.adapter.MenuRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.models.MenuModel;
import koreatech.mcn.mcn_coffee_app.models.Order;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class OrderListFragment extends TabFragment{

    private String authentication_key;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkAuthKey();
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        return view;
    }

    public void checkAuthKey(){
        SharedPreferences pref = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE);
        authentication_key = pref.getString("authentication_key", "");
        if(authentication_key.length() > 0) {
            // if authentication_key is not valid
        }
    }
}
