package medinnovation.mimamori;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arimuratakuma on 16/02/07.
 */
public class BackgroundGPSService extends Service implements LocationListener {
    private final String TAG = "BackgroundGPSService";
    private Timer timer;
    private LocationManager locationManager;
    private PendingIntent pendingIntent;

    /*
     *サービス初回起動時のみ実行
     *
     *@see android.app.Service#onCreate()
     */

    @Override
    public void onCreate() {
        super.onCreate();

        // 位置情報取得時に実行するインテントを生成
        Intent intent = new Intent(this, BackgroundGPSService.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        startService(intent);

        Log.i(TAG, "onCreate");
        Toast.makeText(this, "位置情報の測定を開始しました！", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //非同期で（別スレッドで)定期的に処理を実行するためにTimerを実行する
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
//                locationManager.requestSingleUpdate(
//                        LocationManager.NETWORK_PROVIDER,
//                        pendingIntent
//                );
                saveData();
                Log.i(TAG, "onStartCommand");
            }
        }, 0, 1000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //locationManager.removeUpdates(this);

        Log.i(TAG, "onDestroy");
        timer.cancel();
        Toast.makeText(this, "位置情報の収集を中止しました！", Toast.LENGTH_LONG).show();
    }

    public void saveData(){
        MainActivity mainActivity = new MainActivity();
        Location location = null;


        mainActivity.latitude = location.getLatitude();
        mainActivity.longitude = location.getLongitude();


        mainActivity.latitudeTextView.setText(String.valueOf(mainActivity.latitude));
        mainActivity.longitudeTextView.setText(String.valueOf(mainActivity.longitude));

        ParseObject geoObject = new ParseObject("GeoData");
        geoObject.put("latitude", mainActivity.latitude);
        geoObject.put("longitude", mainActivity.longitude);


        Long longTimeStamp = System.currentTimeMillis()/1000;
        String timestamp = longTimeStamp.toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd h:mm:ss");
        String timeStamp = sdf.format(c.getTime());
        geoObject.put("timeStamp", timeStamp);
        geoObject.put("OS", "Android");
        geoObject.saveInBackground();
    }


    @Override
    public void onLocationChanged(Location location) {

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                600000, // 通知のための最小時間間隔（ミリ秒）
                10, // 通知のための最小距離間隔（メートル）
                this
        );
        saveData();
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
