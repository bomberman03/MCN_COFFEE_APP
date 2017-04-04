package koreatech.mcn.mcn_coffee_app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDTO;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderListManager;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;

public class OrderAlarmService extends Service {

    private String TAG = "OrderAlarmService";

    private OrderRequestThread orderRequestThread;
    private OrderResponseHandler orderResponseHandler;

    private int startId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startId = startId;
        Log.d(TAG, "onStartCommand(" + startId + ")");
        OrderListManager.getInstance().readFromDB(this);
        updateOrderLIst();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy(" + startId + ")");
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
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/users";
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("user", AuthManager.getInstance().getCurrentUser(getApplicationContext()).id);
        } catch (JSONException e){
            e.printStackTrace();
            return;
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
                                Log.d(TAG, orderObject.toString());
                                String createdAt = "";
                                String updatedAt = "";
                                String id = "";
                                int status = 0;
                                try {
                                    if(orderObject.has("createdAt"))
                                        createdAt = OrderDTO.isoTimeToTimeStamp(getApplicationContext(), orderObject.getString("createdAt"));
                                    if(orderObject.has("updatedAt"))
                                        updatedAt = OrderDTO.isoTimeToTimeStamp(getApplicationContext(), orderObject.getString("updatedAt"));
                                    if(orderObject.has("_id"))
                                        id = orderObject.getString("_id");
                                    if(orderObject.has("status"))
                                        status = orderObject.getInt("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(TAG, "createAt: " + createdAt);
                                Log.d(TAG, "updateAt: " + updatedAt);
                                Log.d(TAG, "id: " + id);
                                Log.d(TAG, "status: " + status);
                                OrderListManager.getInstance().updateOrder(new OrderDTO(createdAt, updatedAt, id, status));
                            }
                            startThread();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(postOrderRequest);
    }

    public void startThread() {
        orderResponseHandler = new OrderResponseHandler(this);
        orderRequestThread = new OrderRequestThread(this, orderResponseHandler);
        orderRequestThread.start();
    }
}
