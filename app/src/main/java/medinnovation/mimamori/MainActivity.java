package medinnovation.mimamori;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView statusTextView;
    TextView latitudeTextView;
    TextView longitudeTextView;

    double longitude;
    double latitude;

    Date timeStamp;
    private LocationManager locationManager;
    PendingIntent pendingIntent;
    //private GpsStatus.Listener mGpsStatusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        latitudeTextView = (TextView)findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView)findViewById(R.id.longitudeTextView);
        statusTextView = (TextView)findViewById(R.id.statusTextView);

        longitude = 0.0;
        latitude = 0.0;

        //---use the LocationManager class to obtain locations data---
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        latitudeTextView.setText("経度");
        longitudeTextView.setText("緯度");
        statusTextView.setText("現在は計測していません");

    }

    public void singleGPS(){
        locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                this,
                getMainLooper()
        );
    }

    public void start(View view){

        //Intent i = new Intent(this, BackgroundGPSService.class);
        Intent intent1 = new Intent(this, MyLocationReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent1,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        //startService(i);

        //現在有効な位置プロバイダ名を得る
        String providers = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.i("***", "LOCATION_PROVIDERS_ALLOWED:" + providers);

        //プロバイダ名にgpsが含まれていなければ
        if (providers.indexOf("gps", 0) == -1){

            //位置情報の設定を呼び出すインテントを作成
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            //アクティビティを起動
            startActivity(intent);

            Toast.makeText(this, "GPS機能をONにしてください", Toast.LENGTH_LONG).show();
        }

        singleGPS();

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                600000,
                10,
                pendingIntent
        );

        latitudeTextView.setText(String.valueOf(latitude));
        longitudeTextView.setText(String.valueOf(longitude));
        statusTextView.setText("計測中");
        Log.d("GeoData", String.valueOf(latitude) + " + " + String.valueOf(longitude));
    }

    public void stop(View view){
        statusTextView.setText("現在は計測していません");
        longitudeTextView.setText("経度");
        latitudeTextView.setText("緯度");
        //Intent i = new Intent(this, BackgroundGPSService.class);

        locationManager.removeUpdates(this);
        //stopService(i);

        Intent intent1 = new Intent(this, MyLocationReceiver.class);
        locationManager.removeUpdates(pendingIntent);
        Toast.makeText(this, "GPS情報の取得を停止しました", Toast.LENGTH_LONG).show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

        //更新時間600秒、更新距離10m
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                6000, // 通知のための最小時間間隔（ミリ秒）
//                1, // 通知のための最小距離間隔（メートル）
//                this
//        );

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        latitudeTextView.setText(String.valueOf(latitude));
        longitudeTextView.setText(String.valueOf(longitude));

        //Parse保存部分
        ParseObject geoObject = new ParseObject("GeoData");
        geoObject.put("latitude", latitude);
        geoObject.put("longitude", longitude);


        Long longTimeStamp = System.currentTimeMillis()/1000;
        String timestamp = longTimeStamp.toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timeStamp = sdf.format(c.getTime());
        geoObject.put("timeStamp", timeStamp);
        geoObject.put("OS", "Android");
        geoObject.saveInBackground();

        Log.d("GeoData", String.valueOf(latitude) + " : " + String.valueOf(longitude) + " : " + timeStamp);
        Toast.makeText(this, "GPS情報の取得を開始しました", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        locationManager.removeUpdates(this);
    }
}
