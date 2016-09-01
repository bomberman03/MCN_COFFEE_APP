package koreatech.mcn.mcn_coffee_app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderListManager;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.User;

public class OrderAlarmService extends Service {

    private User user;
    private Cafe cafe;
    private Socket mSocket;

    private String TAG = "OrderAlarmService";

    public void connect(Intent intent)
    {
        cafe = (Cafe) intent.getSerializableExtra("cafe");
        user = AuthManager.getInstance().getCurrentUser();
        try {
            mSocket = IO.socket("http://" + Settings.serverIp + ":" + Settings.socket_port);
        } catch (URISyntaxException e) {
            Log.d("TAG",e.getMessage());
        }
        mSocket.connect();
        mSocket.on(cafe.id, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    String method = "";
                    if(jsonObject.has("method")) method = jsonObject.getString("method");
                    Log.d(TAG, method);
                    if(!method.equals("put"))
                        return;

                    String name = "";
                    if(jsonObject.has("name")) name = jsonObject.getString("name");
                    Log.d(TAG, name);
                    if(!name.equals("order"))
                        return;

                    JSONObject data = new JSONObject();
                    if(jsonObject.has("data")) data = jsonObject.getJSONObject("data");
                    Log.d(TAG, data.toString());

                    String user = "";
                    if(data.has("user")) user = data.getString("user");
                    Log.d(TAG, user);

                    if(user.equals(AuthManager.getInstance().getCurrentUser().id))
                    {
                        Log.d(TAG, "this is my order!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void disconnect()
    {
        mSocket.disconnect();
        mSocket.off(cafe.id);
    }

    private OrderRequestThread orderRequestThread;
    private OrderResponseHandler orderResponseHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
        OrderListManager.getInstance().readFromDB(this);
        updateOrderLIst();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(orderRequestThread != null) {
            orderRequestThread.stopForever();
            orderRequestThread = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void updateOrderLIst() {
        startThread();
    }

    public void startThread() {
        orderResponseHandler = new OrderResponseHandler(this);
        orderRequestThread = new OrderRequestThread(this, orderResponseHandler);
        orderRequestThread.start();
    }
}
