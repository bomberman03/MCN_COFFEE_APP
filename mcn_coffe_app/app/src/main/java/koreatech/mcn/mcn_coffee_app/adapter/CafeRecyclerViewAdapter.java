package koreatech.mcn.mcn_coffee_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.Cafe;

/**
 * Created by blood_000 on 2016-05-25.
 */
public class CafeRecyclerViewAdapter extends RecyclerView.Adapter<CafeRecyclerViewAdapter.CafeViewHolder> {

    public class CafeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cafeName;
        TextView cafeDetail;
        ImageView cafeThumbnail;

        CafeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cafeName = (TextView) itemView.findViewById(R.id.cafe_name);
            cafeDetail = (TextView) itemView.findViewById(R.id.cafe_detail);
            cafeThumbnail = (ImageView) itemView.findViewById(R.id.cafe_thumbnail);
        }
    }

    private List<Cafe> cafes;
    private Context context;

    private String TAG = "CafeRecyclerViewAdapter";

    public CafeRecyclerViewAdapter(Context context, List<Cafe> cafes) {
        this.context = context;
        this.cafes = cafes;
    }

    @Override
    public int getItemCount() {
        return cafes.size();
    }

    @Override
    public CafeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cafe_item, viewGroup, false);
        CafeViewHolder cafeViewHolder = new CafeViewHolder(v);
        return cafeViewHolder;
    }

    @Override
    public void onBindViewHolder(CafeViewHolder cafeViewHolder, final int i) {
        cafeViewHolder.cafeName.setText(cafes.get(i).name);
        cafeViewHolder.cafeDetail.setText(cafes.get(i).detail);
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/image/cafe/" + cafes.get(i).images.get(0);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.cafe_default)
                .into(cafeViewHolder.cafeThumbnail);

        cafeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cafe cafe = cafes.get(i);
                Intent intent = new Intent(context, OrderActivity.class);
                intent.putExtra("cafe", cafe);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeAt(int position) {
        cafes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cafes.size());
    }
}
