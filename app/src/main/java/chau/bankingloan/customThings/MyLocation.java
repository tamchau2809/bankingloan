package chau.bankingloan.customThings;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by com08 (26-Aug-16).
 */
public class MyLocation {
    Timer timer;
    LocationManager locationManager;
    LocationResult locationResult;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    public boolean getLocation(Context context, LocationResult result)
    {
        locationResult = result;
        if(locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        try
        {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //don't start listeners if no provider is enabled
        if(!isGPSEnabled && !isNetworkEnabled)
            return false;
        try {
            if (isGPSEnabled)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            if (isNetworkEnabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        timer = new Timer();
        timer.schedule(new GetLastLocation(), 20000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            try {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerNetwork);
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            try {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerGps);
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            try {
                locationManager.removeUpdates(locationListenerGps);
                locationManager.removeUpdates(locationListenerNetwork);
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

            Location net_loc = null, gps_loc = null;
            if(isGPSEnabled) {
                try {
                    gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
            if(isNetworkEnabled) {
                try {
                    net_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
            //if there are both values use the latest one
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime() > net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }

            if(gps_loc!=null){
                locationResult.gotLocation(gps_loc);
                return;
            }
            if(net_loc!=null){
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}
