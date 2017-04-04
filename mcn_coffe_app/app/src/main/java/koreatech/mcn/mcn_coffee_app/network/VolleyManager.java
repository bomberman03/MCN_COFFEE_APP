package koreatech.mcn.mcn_coffee_app.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by blood_000 on 2016-09-20.
 */

public class VolleyManager {

    private static VolleyManager singleton = new VolleyManager();
    private RequestQueue requestQueue;

    public static VolleyManager getInstance()
    {
        return singleton;
    }

    public RequestQueue getRequestQueue(Context context)
    {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

}
