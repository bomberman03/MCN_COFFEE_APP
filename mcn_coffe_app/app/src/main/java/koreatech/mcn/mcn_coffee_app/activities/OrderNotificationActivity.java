package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.models.OrderList;

public class OrderNotificationActivity extends AppCompatActivity {

    private OrderList orderList;

    public void getOrderList(String id)
    {
        order_id.setText(id);
    }

    private TextView order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);
        Intent intent = getIntent();
        init();
        getOrderList(intent.getStringExtra("id"));
    }

    public void init()
    {
        order_id = (TextView) findViewById(R.id.order_id);
    }

}
