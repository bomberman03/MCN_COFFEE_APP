package koreatech.mcn.mcn_coffee_app.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.fragments.OrderFragment;
import koreatech.mcn.mcn_coffee_app.models.Option;
import koreatech.mcn.mcn_coffee_app.models.Order;

/**
 * Created by blood_000 on 2016-05-27.
 */
public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderViewHolder>{

    private ArrayList<Order> orders;
    private OrderFragment orderFragment;

    public OrderRecyclerViewAdapter(OrderFragment orderFragment, ArrayList<Order> orders){
        this.orderFragment = orderFragment;
        this.orders = orders;
    }

    public OrderRecyclerViewAdapter(ArrayList<Order> orders){
        this.orders = orders;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.order_item, parent, false);
        OrderViewHolder orderViewHolder = new OrderViewHolder(v);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        holder.menuName.setText(orders.get(position).menu.name);
        holder.orderCost.setText(orders.get(position).cost + "원");
        holder.orderCount.setText(orders.get(position).count + "개");
        String options = "";
        boolean isFirst = true;
        for(Option option: orders.get(position).options){
            if(!isFirst) options += " , ";
            options += option.name;
            isFirst = false;
        }
        holder.optionList.setText(options);
        if(orderFragment != null) {
            holder.increaseOrder.setVisibility(View.VISIBLE);
            holder.increaseOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orders.get(position).count++;
                    holder.orderCount.setText(orders.get(position).count + "개");
                    if (orderFragment != null)
                        orderFragment.updateTotalCost();
                }
            });
        }
        if(orderFragment != null) {
            holder.decreaseOrder.setVisibility(View.VISIBLE);
            holder.decreaseOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orders.get(position).count--;
                    if (orders.get(position).count > 0) {
                        holder.orderCount.setText(orders.get(position).count + "개");
                        if (orderFragment != null)
                            orderFragment.updateTotalCost();
                    } else {
                        removeAt(position);
                    }
                }
            });
        }
    }

    public void add(Order order){
        orders.add(order);
        notifyItemInserted(orders.size() - 1);
        if(orderFragment != null)
            orderFragment.updateTotalCost();
    }

    public void removeAt(int position) {
        orders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orders.size());
        if(orderFragment != null)
            orderFragment.updateTotalCost();
    }

    public void clear(){
        orders.clear();
        notifyDataSetChanged();
        if(orderFragment != null)
            orderFragment.updateTotalCost();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView menuName;
        TextView optionList;
        TextView orderCost;
        TextView orderCount;
        TextView increaseOrder;
        TextView decreaseOrder;

        OrderViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            menuName = (TextView) itemView.findViewById(R.id.menu_name);
            optionList = (TextView) itemView.findViewById(R.id.options_text);
            orderCost = (TextView) itemView.findViewById(R.id.order_cost);
            orderCount = (TextView) itemView.findViewById(R.id.order_count);
            increaseOrder = (TextView) itemView.findViewById(R.id.increase_order);
            decreaseOrder = (TextView) itemView.findViewById(R.id.decrease_order);
        }
    }
}
