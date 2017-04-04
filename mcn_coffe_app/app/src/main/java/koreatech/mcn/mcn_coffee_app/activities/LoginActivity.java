package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;

public class LoginActivity extends NetworkActivity {

    private EditText editEmail;
    private EditText editPassword;

    private Button loginButton;

    private Context self = this;

    public void init(){
        editEmail  = (EditText) findViewById(R.id.edit_email);
        editEmail.setText("");
        editPassword = (EditText) findViewById(R.id.edit_password);
        editPassword.setText("");
        loginButton = (Button) findViewById(R.id.button_login);

        progressDialog.setTitle("로그인 요청중");
        progressDialog.setContent("잠시만 기다려주세요");
        failureDialog.setTitle("인증에 실패했습니다.");
    }

    public void initListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initListener();
    }

    public void loginRequest() {
        showProgressDialog();
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/login/";
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("username", editEmail.getText());
            jsonParam.put("password", editPassword.getText());
        } catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        try {
                            String token = jsonObject.getString("token");
                            String jsonString = new String(android.util.Base64.decode(token.split("\\.")[1], Base64.DEFAULT));
                            JSONObject object = new JSONObject(jsonString);

                            AuthManager.getInstance().initUser(self, object);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    String json = null;
                    public String trimMessage(String json, String key){
                        String trimmedString = null;
                        try{
                            JSONObject obj = new JSONObject(json);
                            trimmedString = obj.getString(key);
                        } catch(JSONException e){
                            e.printStackTrace();
                            return null;
                        }
                        return trimmedString;
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        // TODO Auto-generated method stub
                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.data != null){
                            switch(networkResponse.statusCode){
                                case 400:
                                case 401:
                                    json = new String(networkResponse.data);
                                    json = trimMessage(json, "message");
                                    showFailureDialog(json);
                                    break;
                            }
                        }
                    }
                });
        queue.add(loginRequest);
    }
}
