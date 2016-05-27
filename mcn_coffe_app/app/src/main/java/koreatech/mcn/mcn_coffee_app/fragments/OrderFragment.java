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
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.adapter.OrderRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.models.MenuModel;
import koreatech.mcn.mcn_coffee_app.models.Option;
import koreatech.mcn.mcn_coffee_app.models.Order;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class OrderFragment extends TabFragment {

    private String authentication_key;

    private ArrayList<Order> orders = new ArrayList<>();

    private RecyclerView recyclerView;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    private Button orderButton;
    private Button initButton;

    public void generateOrder(){
        orders.clear();
        List<Option> options  = new ArrayList<>();
        List<Option> shots = new ArrayList<>();
        Option shot1 = new Option("0", "1샷 추가", 0, null); shots.add(shot1);
        Option shot2 = new Option("1", "2샷 추가", 600, null); shots.add(shot2);
        Option shot3 = new Option("2", "3샷 추가", 900, null); shots.add(shot3);
        Option shot = new Option("3", "샷 추가", 0, shots); options.add(shot);
        Option cream = new Option("4", "크림 추가", 500, null); options.add(cream);
        MenuModel americano = new MenuModel("0", "아메리카노",
                "쓰지만 계속 먹으면 중독되는 이 맛", 3000, options);
        Order order = new Order("0", americano, shots, 3000, 1);
        orders.add(order);
    }

    public void init(View view){
        orderButton = (Button) view.findViewById(R.id.order_button);
        initButton = (Button) view.findViewById(R.id.init_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    public void initListener(){
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        //generateOrder();

        init(view);
        initListener();
        initRecyclerView();
        updateTotalCost();

        return view;
    }

    public void updateTotalCost(){
        int cost = 0;
        for(Order order: orders){
            cost += order.cost;
        }
        orderButton.setText("주문하기(" + cost + "원)");
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

}
