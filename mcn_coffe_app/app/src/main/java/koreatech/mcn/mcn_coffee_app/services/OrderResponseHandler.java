package koreatech.mcn.mcn_coffee_app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.activities.OrderNotificationActivity;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDBHelper;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDTO;

/**
 * Created by blood_000 on 2016-08-31.
 */
public class OrderResponseHandler extends Handler {

    private final String TAG = "OrderResponseHandler";

    private Context context;
    private NotificationManager notificationManager;

    public OrderResponseHandler(Context context)
    {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
    }

    public void orderCompleteNotification(String id) {
        Intent intent = new Intent(context, OrderNotificationActivity.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(context)
                    .setContentTitle("주문 완료")
                    .setContentText("주문하신 음료가 완성되었습니다!")
                    .setSmallIcon(R.drawable.ic_stat_maps_local_cafe)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();
        }

        //소리추가
        notification.defaults = Notification.DEFAULT_SOUND;

        //알림 소리를 한번만 내도록
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;

        //확인하면 자동으로 알림이 제거 되도록
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify( 777 , notification);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Object obj = msg.obj;
        OrderDTO orderDTO =  (OrderDTO) obj;

        switch (orderDTO.status)
        {
            case OrderDBHelper.ORDERS_STATUS_REQUEST:
                break;
            case OrderDBHelper.ORDERS_STATUS_WAIT:
                Toast.makeText(context, "order( " + orderDTO.id + " ) is requested", Toast.LENGTH_SHORT).show();
                break;
            case OrderDBHelper.ORDERS_STATUS_COMPLETE:
                Toast.makeText(context, "order( " + orderDTO.id + " ) is complete", Toast.LENGTH_SHORT).show();
                orderCompleteNotification(orderDTO.id);
                break;
            case OrderDBHelper.ORDERS_STATUS_RECEIVE_REQUEST:
                break;
            case OrderDBHelper.ORDERS_STATUS_RECEIVE:
                Toast.makeText(context, "order( " + orderDTO.id + " ) is received", Toast.LENGTH_SHORT).show();
                break;
            case OrderDBHelper.ORDERS_STATUS_CANCEL:
                Toast.makeText(context, "order( " + orderDTO.id + " ) is canceled", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
