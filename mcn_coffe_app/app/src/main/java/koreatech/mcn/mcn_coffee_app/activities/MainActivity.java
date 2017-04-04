package koreatech.mcn.mcn_coffee_app.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import koreatech.mcn.mcn_coffe_app.R;
import koreatech.mcn.mcn_coffee_app.adapter.CafeRecyclerViewAdapter;
import koreatech.mcn.mcn_coffee_app.auth.AuthManager;
import koreatech.mcn.mcn_coffee_app.config.Settings;
import koreatech.mcn.mcn_coffee_app.location.MyLocationManager;
import koreatech.mcn.mcn_coffee_app.models.Cafe;
import koreatech.mcn.mcn_coffee_app.models.User;
import koreatech.mcn.mcn_coffee_app.network.VolleyManager;
import koreatech.mcn.mcn_coffee_app.request.CustomArrayRequest;
import koreatech.mcn.mcn_coffee_app.request.CustomObjectRequest;
import koreatech.mcn.mcn_coffee_app.services.OrderAlarmService;

public class MainActivity extends NetworkActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Cafe> cafes = new ArrayList<>();
    private RecyclerView recyclerView;
    private CafeRecyclerViewAdapter cafeRecyclerViewAdapter;

    private TextView address;
    private ImageView gps_search;

    private String TAG = "MainActivity";

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JodaTimeAndroid.init(this);
        MyLocationManager.getInstance().init(getApplicationContext());

        if (!isMyServiceRunning(OrderAlarmService.class)) {
            Intent intent = new Intent(this, OrderAlarmService.class);
            startService(intent);
        }

        init();
        initListener();
        initRecyclerView();

        int timeout = 5;
        showProgressDialog();
        while((MyLocationManager.getInstance().isLocation() == false) && timeout > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout--;
        }
        hideProgressDialog();
        address_request();
    }

    public void initListener() {
        gps_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address_request();
            }
        });
    }

    public void init() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        User user = AuthManager.getInstance().getCurrentUser(getApplicationContext());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView user_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        TextView user_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email);
        user_name.setText(user.name);
        user_email.setText(user.email);

        gps_search = (ImageView) findViewById(R.id.gps_search);
        address = (TextView) findViewById(R.id.address);
    }

    public void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        cafeRecyclerViewAdapter = new CafeRecyclerViewAdapter(this, cafes);
        recyclerView.setAdapter(cafeRecyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                openWebSite();
                break;
            case R.id.nav_news:
                startNewsActivity();
                break;
            case R.id.nav_invoice:
                startInvoiceActivity();
                break;
            case R.id.nav_settings:
                startSettingActivity();
                break;
            case R.id.nav_logout:
                logOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openWebSite() {

    }

    public void startNewsActivity() {

    }

    public void startInvoiceActivity() {
        startActivity(new Intent(this, OrderListActivity.class));
    }

    public void startSettingActivity() {

    }

    public void logOut() {
        AuthManager.getInstance().clearUser(getApplicationContext());
        startActivity(new Intent(this, IntroActivity.class));
        finish();
    }

    public void coordinate_request() {
        if(!MyLocationManager.getInstance().isLocation())
            return;
        showProgressDialog();
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "https://apis.skplanetx.com/tmap/geo/coordconvert?";
        url += "lon=" + String.valueOf(MyLocationManager.getInstance().getLocation().getLongitude());
        url += "&lat=" + String.valueOf(MyLocationManager.getInstance().getLocation().getLatitude());
        url += "&fromCoord=" + "WGS84GEO";
        url += "&toCoord=" + "EPSG3857";
        url += "&version=" + "1";
        url += "&appKey=" + "b7224677-d5a3-3c7e-9a1c-33db6ad8e19e";

        Map<String, String> params = new HashMap<>();

        CustomObjectRequest coordConvertRequest = new CustomObjectRequest(Request.Method.GET, url, params,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideProgressDialog();
                if(jsonObject.has("coordinate")) {
                    try {
                        JSONObject coordObject = jsonObject.getJSONObject("coordinate");
                        String latitude = "", longitude = "";
                        if(coordObject.has("lat"))
                            latitude = coordObject.getString("lat");
                        if(coordObject.has("lon"))
                            longitude = coordObject.getString("lon");
                        MyLocationManager.getInstance().setPivotLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        content_request();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgressDialog();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(coordConvertRequest);
    }

    public void address_request() {
        if(!MyLocationManager.getInstance().isLocation())
            return;
        showProgressDialog();
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/naver/getAddress";
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("hostname", "openapi.naver.com");
            jsonParam.put("path","/v1/map/reversegeocode?encoding=utf-8&coord=latlng&output=json&query=" +
                    MyLocationManager.getInstance().getLocation().getLongitude() + "," +
                    MyLocationManager.getInstance().getLocation().getLatitude());
        } catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest getAddressRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideProgressDialog();
                        try {
                            if (jsonObject.has("result")) {
                                JSONObject resultObject = jsonObject.getJSONObject("result");
                                if (resultObject.has("items")) {
                                    JSONArray jsonArray = resultObject.getJSONArray("items");
                                    JSONObject addressObject = jsonArray.getJSONObject(0);
                                    if(addressObject.has("address")){
                                        String _address = addressObject.getString("address");
                                        address.setText(_address);
                                    }
                                }
                            }
                            coordinate_request();
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                    }
                });
        queue.add(getAddressRequest);
    }

    public void content_request() {
        showProgressDialog();
        RequestQueue queue = VolleyManager.getInstance().getRequestQueue(getApplicationContext());
        String url = "http://" + Settings.serverIp + ":" + Settings.port + "/cafes/";

        Map<String, String> params = new HashMap<>();

        CustomArrayRequest cafeListRequest = new CustomArrayRequest(Request.Method.GET, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                cafeRecyclerViewAdapter.clear();
                hideProgressDialog();
                ArrayList<Cafe> cafe_list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Cafe cafe = new Cafe(jsonObject);
                        if(MyLocationManager.getInstance().isLocation())
                            cafe.setPivot(MyLocationManager.getInstance().getPivotLatitude(), MyLocationManager.getInstance().getPivotLongitude());
                        cafe_list.add(cafe);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(cafe_list, new Comparator<Cafe>() {
                    @Override
                    public int compare(Cafe lhs, Cafe rhs) {
                        return Double.valueOf(lhs.dist).compareTo(rhs.dist);
                    }
                });
                for(Cafe cafe: cafe_list) {
                    cafes.add(cafe);
                    cafeRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgressDialog();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(cafeListRequest);
    }
}
