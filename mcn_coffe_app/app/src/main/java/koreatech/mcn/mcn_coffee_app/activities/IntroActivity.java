package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.models.OrderList;
import koreatech.mcn.mcn_coffee_app.models.User;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;

public class IntroActivity extends NetworkActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler mHandler = new Handler();
        mHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        User user = AuthManager.getInstance().getCurrentUser(getApplicationContext());
                        if(user == null) {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            overridePendingTransition(R.anim.fade, R.anim.hold);
                            finish();
                        } else {
                            if(user.username == null)
                                requestUser();
                            else {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(R.anim.fade, R.anim.hold);
                                finish();
                            }
                        }
                    }
                }, 500);
    }

    public void requestUser()
    {
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/users/" + AuthManager.getInstance().getCurrentUser(getApplicationContext()).id   + "/";

        JSONObject jsonParam = new JSONObject();

        JsonObjectRequest getOrderRequest = new JsonObjectRequest
                (Request.Method.GET, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        AuthManager.getInstance().initUser(getApplicationContext(), jsonObject);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        showFailureDialog(error.getMessage());
                    }
                });
        queue.add(getOrderRequest);
        showProgressDialog();
    }

}
