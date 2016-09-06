package koreatech.mcn.mcn_coffee_app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Timestamp;

import koreatech.mcn.mcn_coffee_app.activities.OrderActivity;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDBHelper;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDTO;
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
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/users";
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("user", AuthManager.getInstance().getCurrentUser().id);
        } catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest postOrderRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        JSONArray jsonArray = new JSONArray();
                        try {
                            jsonArray = jsonObject.getJSONArray("orders");
                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                JSONObject orderObject = jsonArray.getJSONObject(i);
                                String createdAt = "";
                                String updatedAt = "";
                                String id = "";
                                int status = 0;
                                try {
                                    if(orderObject.has("createdAt"))
                                        createdAt = OrderDTO.isoTimeToTimeStamp(orderObject.getString("createdAt"));
                                    if(orderObject.has("updatedAt"))
                                        updatedAt = OrderDTO.isoTimeToTimeStamp(orderObject.getString("updatedAt"));
                                    if(orderObject.has("_id"))
                                        id = orderObject.getString("_id");
                                    if(orderObject.has("status"))
                                        status = orderObject.getInt("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                OrderListManager.getInstance().updateOrder(new OrderDTO(createdAt, updatedAt, id, status));
                            }
                            //startThread();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        //queue.add(postOrderRequest);

    }

    public void startThread() {
        orderResponseHandler = new OrderResponseHandler(this);
        orderRequestThread = new OrderRequestThread(this, orderResponseHandler);
        orderRequestThread.start();
    }
}
