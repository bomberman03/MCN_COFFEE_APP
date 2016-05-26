package koreatech.mcn.mcn_coffee_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.MenuModel;
import koreatech.mcn.mcn_coffee_app.models.Option;
import koreatech.mcn.mcn_coffee_app.models.Order;

/**
 * Created by blood_000 on 2016-05-25.
 */
public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder> {

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView menuName;
        TextView menuDetail;
        TextView menuCost;
        ImageView menuThumbnail;

        MenuViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            menuName = (TextView) itemView.findViewById(R.id.menu_name);
            menuDetail = (TextView) itemView.findViewById(R.id.menu_detail);
            menuCost = (TextView) itemView.findViewById(R.id.menu_cost);
            menuThumbnail = (ImageView) itemView.findViewById(R.id.menu_thumbnail);
        }
    }

    private List<MenuModel> menus;
    private Context context;
    private LinearLayout option_layout;
    private MaterialDialog option_dialog;
    private Order order;

    public MenuRecyclerViewAdapter(Context context, List<MenuModel> menus) {
        this.context = context;
        this.menus = menus;
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.menu_item, viewGroup, false);
        MenuViewHolder menuViewHolder = new MenuViewHolder(v);
        option_layout = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.option_dialog, viewGroup, false);
        boolean wrapInScrollView = true;
        option_dialog = new MaterialDialog.Builder(context)
                .title("메뉴이름(0원)")
                .customView(option_layout, wrapInScrollView)
                .positiveText("주문")
                .negativeText("취소")
                .build();
        return menuViewHolder;
    }

    @Override
    public void onBindViewHolder(MenuViewHolder menuViewHolder, final int i) {
        menuViewHolder.menuName.setText(menus.get(i).name);
        menuViewHolder.menuDetail.setText(menus.get(i).detail);
        menuViewHolder.menuCost.setText(menus.get(i).cost + "원");
        menuViewHolder.menuThumbnail.setImageResource(R.mipmap.coffee_default);

        menuViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuModel menu = menus.get(i);
                updateOptionDialog(menu);
                option_dialog.show();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeAt(int position) {
        menus.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, menus.size());
    }

    public LinearLayout generateOptionLayout(final Order order, final Option option){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView option_name = new TextView(context);
        option_name.setText(option.name);
        linearLayout.addView(option_name);
        if(option.options != null){
            RadioGroup radioGroup = new RadioGroup(context);
            for(Option sub_option: option.options){
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(sub_option.name);
                radioGroup.addView(radioButton);
            }
            linearLayout.addView(radioGroup);
        }
        else{
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(option.name);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox c = (CheckBox) v;
                    if(c.isChecked()){
                        order.cost += option.cost;
                    } else {
                        order.cost -= option.cost;
                    }
                }
            });
            linearLayout.addView(checkBox);
        }
        return linearLayout;
    }

    public void updateOptionDialog(MenuModel menu){
        TextView menu_name = (TextView) option_layout.findViewById(R.id.menu_name);
        TextView menu_cost = (TextView) option_layout.findViewById(R.id.menu_cost);

        menu_name.setText(menu.name);
        menu_cost.setText(String.valueOf(menu.cost));

        order = new Order("0", menu, new ArrayList<Option>(), menu.cost);

        option_dialog.setTitle(menu.name + "(" + order.cost + "원)");

        LinearLayout options_section = (LinearLayout) option_layout.findViewById(R.id.options_section);
        options_section.removeAllViews();
        for(Option option: menu.options) {
            options_section.addView(generateOptionLayout(order, option));
        }
    }
}
