package koreatech.mcn.mcn_coffee_app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;

import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderDTO;
import koreatech.mcn.mcn_coffee_app.localStorage.OrderListManager;

/**
 * Created by blood_000 on 2016-08-31.
 */
public class OrderRequestThread extends Thread {

    private Context context;
    private Handler handler;
    private boolean isRunning = true;

    private String TAG = "OrderRequestThread";

    public OrderRequestThread(Context context, Handler handler)
    {
        this.context = context;
        this.handler = handler;
    }

    public void stopForever()
    {
        synchronized (this) {
            this.isRunning = false;
        }
    }

    @Override
    public void run() {
        while(isRunning)
        {
            requestOrder();
            try{
                Thread.sleep(3000); //1초씩 쉰다.
            }catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public void requestOrder()
    {
        ArrayList<OrderDTO> orderDTOs = OrderListManager.getInstance().getRequestOrderList();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/orders/status/";
        JSONObject jsonParam = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray();
            for(OrderDTO orderDTO : orderDTOs) {
                jsonArray.put(orderDTO.id);
            }
            jsonParam.put("orders", jsonArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest orderRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("orders");
                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String createdAt = "";
                                String updatedAt = "";
                                String id = "";
                                int status = 0;
                                if(object.has("createdAt"))
                                    createdAt = OrderDTO.isoTimeToTimeStamp(object.getString("createdAt"));
                                if(object.has("updatedAt"))
                                    updatedAt = OrderDTO.isoTimeToTimeStamp(object.getString("updatedAt"));
                                if(object.has("_id"))
                                    id = object.getString("_id");
                                if(object.has("status"))
                                    status = object.getInt("status");
                                OrderDTO res = OrderListManager.getInstance().updateOrder(new OrderDTO(createdAt, updatedAt, id, status));
                                if(res != null)
                                {
                                    Message message = handler.obtainMessage();
                                    message.obj = res;
                                    handler.sendMessage(message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null)
                            Log.d(TAG, error.getMessage());
                        else
                            Log.d(TAG, "error object is null");
                    }
                });
        queue.add(orderRequest);
    }

}
