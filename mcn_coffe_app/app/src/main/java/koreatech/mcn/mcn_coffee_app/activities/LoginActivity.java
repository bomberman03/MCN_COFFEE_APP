package koreatech.mcn.mcn_coffee_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import java.util.HashMap;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Handler;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.config.Settings;

public class LoginActivity extends AppCompatActivity {

    public static void  showFailureDialog(String message){
        failureDialog.setContent(message);
        failureDialog.show();
    }

    public static void hideFailureDialog(){
        failureDialog.hide();
    }

    public static void showProgressDialog(){
        progressDialog.show();
    }

    public static void hideProgressDialog(){
        progressDialog.hide();
    }

    private EditText editEmail;
    private EditText editPassword;

    private Button loginButton;

    public static MaterialDialog progressDialog;
    public static MaterialDialog failureDialog;

    public void init(){
        editEmail  = (EditText) findViewById(R.id.edit_email);
        editEmail.setText("pathfinder");
        editPassword = (EditText) findViewById(R.id.edit_password);
        editPassword.setText("nosweat12$");
        loginButton = (Button) findViewById(R.id.button_login);
        progressDialog = new MaterialDialog.Builder(this)
                .title("로그인 요청중")
                .content("잠시만 기다려주세요")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();
        failureDialog = new MaterialDialog.Builder(this)
                .title("인증에 실패했습니다.")
                .content("")
                .positiveText("확인")
                .build();
    }

    public void initListener(){
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
        RequestQueue queue = Volley.newRequestQueue(this);
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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
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
                                    Log.d("TAG",json);
                                    showFailureDialog(json);
                                    break;
                            }
                        }
                    }
                });
        queue.add(loginRequest);
    }
}
