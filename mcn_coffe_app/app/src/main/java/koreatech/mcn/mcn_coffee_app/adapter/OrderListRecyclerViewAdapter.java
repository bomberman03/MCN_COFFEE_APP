package koreatech.mcn.mcn_coffee_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderNotificationActivity;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

/**
 * Created by blood_000 on 2016-05-27.
 */
public class OrderListRecyclerViewAdapter extends RecyclerView.Adapter<OrderListRecyclerViewAdapter.OrderListViewHolder> {

    public class OrderListViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView orderCafe;
        TextView orderCost;
        LinearLayout orderListStatus;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            orderCafe = (TextView) itemView.findViewById(R.id.order_cafe);
            orderCost = (TextView) itemView.findViewById(R.id.order_cost);
            orderListStatus = (LinearLayout) itemView.findViewById(R.id.order_list_status);
        }
    }

    private ViewGroup viewGroup;

    private final int ORDER_STATUS_WAIT     = 0;
    private final int ORDER_STATUS_COMPLETE = 1;
    private final int ORDER_STATUS_CANCEL   = 2;
    private final int ORDER_STATUS_RECEIVE  = 4;

    public Button orderListStatusButton (int status){
        Button button = null;
        switch (status){
            case ORDER_STATUS_WAIT:
                button = (Button) LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.wait_button, viewGroup, false);
                break;
            case ORDER_STATUS_COMPLETE:
                button = (Button) LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.complete_button, viewGroup, false);
                break;
            case ORDER_STATUS_RECEIVE:
                button = (Button) LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.receive_button, viewGroup, false);
                break;
            case ORDER_STATUS_CANCEL:
                button = (Button) LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.cancel_button, viewGroup, false);
                break;
        }
        return button;
    }

    private Context context;
    private List<OrderList> orderLists;

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
        return orderListViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderListViewHolder orderListViewHolder, final int position) {
        orderListViewHolder.orderCafe.setText(String.valueOf(orderLists.get(position).cafe.name));
        orderListViewHolder.orderCost.setText(orderLists.get(position).cost + "원");
        orderListViewHolder.orderListStatus.removeAllViews();
        orderListViewHolder.orderListStatus.addView(orderListStatusButton(orderLists.get(position).status));

        orderListViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderList orderList = orderLists.get(position);
                Intent intent = new Intent(context, OrderNotificationActivity.class);
                intent.putExtra("id", orderList.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clear(){
        orderLists.clear();
        notifyDataSetChanged();
    }
}
