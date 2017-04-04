package koreatech.mcn.mcn_coffee_app.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by blood_000 on 2016-09-26.
 */

public class MyLocationManager implements LocationListener, GpsStatus.Listener{

    private static MyLocationManager singleton = new MyLocationManager();

    private static LocationManager locationManager;
    private static LocationProvider locationProvider;

    class TMapLocation {
        public double latitude = 0.;
        public double longitude = 0.;
    }

    private Location location;
    private TMapLocation pivotLocation;

    public static MyLocationManager getInstance() {
        return singleton;
    }

    public void init(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if (locationProvider == null) {
            Toast.makeText(context, "GPS가 지원되지 않는 디바이스입니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.addGpsStatusListener(MyLocationManager.getInstance());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "GPS 권한 획득에 실패했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            this.location = location;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MyLocationManager.getInstance());
        }
    }

    public boolean isLocation() {
        return !(this.location == null);
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            this.location = location;
    }

    public boolean isPivotLocation() {
        return !(this.pivotLocation == null);
    }

    public void setPivotLocation(double latitude, double longitude) {
        if(pivotLocation == null) pivotLocation = new TMapLocation();
        pivotLocation.latitude = latitude;
        pivotLocation.longitude = longitude;
    }

    public TMapLocation getPivotLocation() {
        return pivotLocation;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
        }
    }

    public double getPivotLatitude() {
        return pivotLocation.latitude;
    }

    public double getPivotLongitude() {
        return pivotLocation.longitude;
    }
}
