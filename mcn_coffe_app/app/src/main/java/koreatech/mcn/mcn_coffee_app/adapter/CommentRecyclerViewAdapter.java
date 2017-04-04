package koreatech.mcn.mcn_coffee_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import koreatech.mcn.mcn_coffee_app.activities.OrderNotificationActivity;
import koreatech.mcn.mcn_coffee_app.fragments.MenuFragment;
import koreatech.mcn.mcn_coffee_app.models.MenuModel;
import koreatech.mcn.mcn_coffee_app.models.Option;
import koreatech.mcn.mcn_coffee_app.models.Order;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

/**
 * Created by blood_000 on 2016-05-25.
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView userName;
        TextView commentMessage;
        ArrayList<ImageView> colorStars = new ArrayList<>();
        ArrayList<ImageView> blankStars = new ArrayList<>();

        CommentViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            commentMessage = (TextView) itemView.findViewById(R.id.comment_message);

            colorStars.add((ImageView) itemView.findViewById(R.id.color_star1));
            colorStars.add((ImageView) itemView.findViewById(R.id.color_star2));
            colorStars.add((ImageView) itemView.findViewById(R.id.color_star3));
            colorStars.add((ImageView) itemView.findViewById(R.id.color_star4));
            colorStars.add((ImageView) itemView.findViewById(R.id.color_star5));

            blankStars.add((ImageView) itemView.findViewById(R.id.blank_star1));
            blankStars.add((ImageView) itemView.findViewById(R.id.blank_star2));
            blankStars.add((ImageView) itemView.findViewById(R.id.blank_star3));
            blankStars.add((ImageView) itemView.findViewById(R.id.blank_star4));
            blankStars.add((ImageView) itemView.findViewById(R.id.blank_star5));
        }
    }

    private ViewGroup viewGroup;
    private Context context;
    private List<OrderList> orderLists;

    public CommentRecyclerViewAdapter(Context context,
                                        ArrayList<OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    @Override
    public CommentRecyclerViewAdapter.CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        this.viewGroup = viewGroup;
        View v = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.comment_item, viewGroup, false);
        CommentRecyclerViewAdapter.CommentViewHolder commentViewHolder = new CommentRecyclerViewAdapter.CommentViewHolder(v);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(CommentRecyclerViewAdapter.CommentViewHolder commentViewHolder, final int position) {
        OrderList orderList = orderLists.get(position);
        commentViewHolder.userName.setText(orderList.user.username);
        commentViewHolder.commentMessage.setText(orderList.comment);
        for(int i=0; i<orderList.cmt_point; i++) commentViewHolder.colorStars.get(i).setVisibility(View.VISIBLE);
        for(int i=0; i<5-orderList.cmt_point; i++) commentViewHolder.blankStars.get(i).setVisibility(View.VISIBLE);
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
