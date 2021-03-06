package medinnovation.mimamori;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by arimuratakuma on 16/02/21.
 */
public class MyLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String locationKey = LocationManager.KEY_LOCATION_CHANGED;
        String providerEnableKey = LocationManager.KEY_PROVIDER_ENABLED;
        if(intent.hasExtra(providerEnableKey)) {
            Toast.makeText(context, "Provider enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Provider enabled", Toast.LENGTH_SHORT).show();
        }

        if(intent.hasExtra(locationKey)){
            Location loc = (Location)intent.getExtras().get(locationKey);
            Toast.makeText(context, "Location changed : Lat:" + loc.getLatitude() +"  "+ "Lng:" + loc.getLongitude(), Toast.LENGTH_LONG).show();

            //Parse保存部分
            ParseObject geoObject = new ParseObject("GeoData");
            geoObject.put("latitude", loc.getLatitude());
            geoObject.put("longitude", loc.getLongitude());


            Long longTimeStamp = System.currentTimeMillis()/1000;
            String timestamp = longTimeStamp.toString();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd h:mm:ss");
            String timeStamp = sdf.format(c.getTime());
            geoObject.put("timeStamp", timeStamp);
            geoObject.put("OS", "Android");
            geoObject.saveInBackground();

            Log.d("GeoData", String.valueOf(loc.getLatitude()) + " : " + String.valueOf(loc.getLongitude()) + " : " + timeStamp);
        }

    }

}
