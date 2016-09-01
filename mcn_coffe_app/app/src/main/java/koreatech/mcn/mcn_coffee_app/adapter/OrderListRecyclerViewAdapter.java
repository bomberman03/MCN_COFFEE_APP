package koreatech.mcn.mcn_coffee_app.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.fragments.OrderListFragment;
import koreatech.mcn.mcn_coffee_app.models.Option;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

/**
 * Created by blood_000 on 2016-05-27.
 */
public class OrderListRecyclerViewAdapter extends RecyclerView.Adapter<OrderListRecyclerViewAdapter.OrderListViewHolder> {

    public class OrderListViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView orderListCost;
        TextView orderListIdx;
        LinearLayout orderListStatus;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            orderListCost = (TextView) itemView.findViewById(R.id.order_list_cost);
            orderListIdx = (TextView) itemView.findViewById(R.id.order_list_idx);
            orderListStatus = (LinearLayout) itemView.findViewById(R.id.order_list_status);
        }
    }

    private List<OrderList> orderLists;
    private Context context;
    private LinearLayout order_list_layout;
    private MaterialDialog order_list_dialog;

    private ViewGroup viewGroup;

    public Button orderListStatusButton (int status){
        Button button = null;
        switch (status){
            case 0:
                button = (Button) LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.new_button, viewGroup, false);
                break;
            case 1:
                button = (Button) LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.wait_button, viewGroup, false);
                break;
        }
        return button;
    }

    public OrderListRecyclerViewAdapter(Context context,
                                        ArrayList<OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    @Override
    public OrderListViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        this.viewGroup = viewGroup;
        View v = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.order_list_item, viewGroup, false);
        OrderListViewHolder orderListViewHolder = new OrderListViewHolder(v);
        order_list_layout =  (LinearLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.order_list_dialog, viewGroup, false);
        boolean wrapInScrollView = true;
        order_list_dialog = new MaterialDialog.Builder(context)
                .title("주문번호 12345")
                .customView(order_list_layout, wrapInScrollView)
                .positiveText("확인")
                .build();
        return orderListViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderListViewHolder orderListViewHolder, final int position) {
        //orderListViewHolder.orderListIdx.setText(String.valueOf(orderLists.get(position).idx));
        orderListViewHolder.orderListCost.setText(orderLists.get(position).cost + "원");
        orderListViewHolder.orderListStatus.removeAllViews();
        orderListViewHolder.orderListStatus.addView(orderListStatusButton(orderLists.get(position).status));

        orderListViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderList orderList = orderLists.get(position);
                updateOptionDialog(orderList);
                order_list_dialog.show();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeAt(int position) {
        orderLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderLists.size());
    }

    public LinearLayout generateOrderLayout(LinearLayout parent, final Order order){
        LinearLayout order_layout = (LinearLayout) LayoutInflater.from(context).
                inflate(R.layout.order_list_dialog_item, parent, false);
        TextView menuName = (TextView) order_layout.findViewById(R.id.menu_name);
        TextView options = (TextView) order_layout.findViewById(R.id.options);
        TextView orderCost = (TextView) order_layout.findViewById(R.id.order_cost);
        TextView orderCount = (TextView) order_layout.findViewById(R.id.order_count);
        menuName.setText(order.menu.name);
        String optionStr = "";
        boolean isFirst = true;
        for(Option option: order.options){
            if(!isFirst) optionStr += " , ";
            optionStr += option.name;
            isFirst = false;
        }
        options.setText(optionStr);
        orderCost.setText(order.cost + "원");
        orderCount.setText(order.count + "개");
        return order_layout;
    }

    public void updateOptionDialog(OrderList orderList){
        //order_list_dialog.setTitle("주문 번호 " + orderList.idx);
        LinearLayout orderList_section = (LinearLayout) order_list_layout.findViewById(R.id.order_list_section);
        orderList_section.removeAllViews();
        for(Order order: orderList.orders) {
            orderList_section.addView(generateOrderLayout(orderList_section, order));
        }
    }
}
