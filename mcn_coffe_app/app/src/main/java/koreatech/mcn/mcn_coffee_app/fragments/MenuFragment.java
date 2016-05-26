package koreatech.mcn.mcn_coffee_app.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.adapter.CafeRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.adapter.MenuRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.MenuModel;
import koreatech.mcn.mcn_coffee_app.models.Option;

/**
 * Created by blood_000 on 2016-05-24.
 */
public class MenuFragment extends TabFragment {

    private String authentication_key;

    private TextView cafe_name;
    private TextView cafe_detail;

    private Cafe cafe;
    private List<MenuModel> menus;

    private RecyclerView recyclerView;

    public void generateMenu(){
        menus = new ArrayList<>();
        List<Option> options  = new ArrayList<>();
        List<Option> shots = new ArrayList<>();
        Option shot1 = new Option("0", "1샷 추가", 300, null); shots.add(shot1);
        Option shot2 = new Option("1", "2샷 추가", 600, null); shots.add(shot2);
        Option shot3 = new Option("2", "3샷 추가", 900, null); shots.add(shot3);
        Option shot = new Option("3", "샷 추가", 0, shots); options.add(shot);
        Option cream = new Option("4", "크림 추가", 500, null); options.add(cream);

        for(int i=0; i<3; i++) {
            MenuModel americano = new MenuModel("0", "아메리카노",
                    "쓰지만 계속 먹으면 중독되는 이 맛", 3000, options);
            menus.add(americano);
            MenuModel cafemoca = new MenuModel("1", "카페모카",
                    "모카빵이랑 같이 먹는 카페", 4000, options);
            menus.add(cafemoca);
            MenuModel afogato = new MenuModel("2", "아포카토",
                    "아이스크림과 커피의 환상적인 조합", 5000, options);
            menus.add(afogato);
            MenuModel cafeLatte = new MenuModel("3", "카페라떼",
                    "라떼 한잔 마시고 가세요", 3500, options);
            menus.add(cafeLatte);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkAuthKey();
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        cafe_name = (TextView) view.findViewById(R.id.cafe_name);
        cafe_detail = (TextView) view.findViewById(R.id.cafe_detail);

        cafe = ((OrderActivity) getActivity()).getCafe();

        cafe_name.setText(cafe.name);
        cafe_detail.setText(cafe.detail);

        generateMenu();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        MenuRecyclerViewAdapter adapter = new MenuRecyclerViewAdapter(getContext(), menus);
        recyclerView.setAdapter(adapter);

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
