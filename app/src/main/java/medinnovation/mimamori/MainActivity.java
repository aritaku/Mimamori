package medinnovation.mimamori;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
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

import java.security.Provider;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView statusTextView;
    TextView latitudeTextView;
    TextView longitudeTextView;

    double longitude;
    double latitude;

    Date timeStamp;
    private LocationManager locationManager;
    private GpsStatus.Listener mGpsStatusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        latitudeTextView = (TextView)findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView)findViewById(R.id.longitudeTextView);
        statusTextView = (TextView)findViewById(R.id.statusTextView);

        // 精度を設定
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);



        longitude = 0.0;
        latitude = 0.0;

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //locationManager.addGpsStatusListener(this);

        latitudeTextView.setText("経度");
        longitudeTextView.setText("緯度");
        statusTextView.setText("現在は計測していません");


    }

    public void start(View view){
        statusTextView.setText("計測中");
        longitudeTextView.setText("0.0");
        latitudeTextView.setText("0.0");
        Intent i = new Intent(this, BackgroundGPSService.class);
        startService(i);

        //現在有効な位置プロバイダ名を得る
        String providers = android.provider.Settings.Secure.getString(
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
    }

    public void stop(View view){
        statusTextView.setText("現在は計測していません");
        longitudeTextView.setText("経度");
        latitudeTextView.setText("緯度");
        Intent i = new Intent(this, BackgroundGPSService.class);
        stopService(i);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
