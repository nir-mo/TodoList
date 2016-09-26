package il.ac.huji.ins;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.firebase.client.Firebase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nimoshe on 9/23/2016.
 */
public class SplashActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 8000;

    private WifiHelper _wifiHelper;
    private BroadcastReceiver _br = null;
    private FirebaseManager _fm = FirebaseManager.getInstance();

    public static Activity instance;

    private void initSystem()  {
        _br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // start initialize the firebase.
                FirebaseManager.getInstance().addScanResultHandlers(_wifiHelper.getScanResults());

                for (ScanResult sr : _wifiHelper.getScanResults()) {
                    Map<String, Integer> temp = _fm.getBssid(sr.BSSID);
                    if (null == temp)
                        continue;

                    for (String loc: temp.keySet()) {
                        _fm.addLocation(loc);
                        Location t = _fm.getLocation(loc);
                        if (t != null) {
                            for (String b : t.getPoints().keySet()) {
                                _fm.addBssid(b);
                            }
                        }
                    }
                }
            }
        };

        registerReceiver(_br, new IntentFilter(WifiHelper.WIFI_HELPER_DONE_SCANNING));

        Firebase.setAndroidContext(this);
    }

    public void showDialog(final Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null)
            builder.setTitle(title);

        builder.setMessage(message);
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wm.setWifiEnabled(true);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.show();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);

        instance = this;

        _wifiHelper = WifiHelper.getInstance();
        if (!_wifiHelper.init(this)) {
            showDialog(this, "I.N.S", "Your Wifi is disabled, what do you want to do?");
        }

        initSystem();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);

                _wifiHelper.finalize();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != _br)
            unregisterReceiver(_br);
    }

}
