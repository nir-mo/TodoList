package il.ac.huji.ins;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirmo on 9/5/2016.
 *
 */
public class DetectorActivity extends Activity {

    final public static int DETECTOR_ACTIVITY_ID = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detector_activity_layout);

        _scanResults = new ArrayList<ScanResult>();
        _detectorAdapter = new DetectorAdapter(this, _scanResults);

        ListView lstview = (ListView) findViewById(R.id.listView);
        lstview.setAdapter(_detectorAdapter);

        _wifiHelper = WifiHelper.getInstance();
        _wifiHelper.init(this);

        _br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                _scanResults.clear();
                for (ScanResult sr : _wifiHelper.getScanResults())
                    _scanResults.add(sr);
                _detectorAdapter.notifyDataSetChanged();
            }
        };

        registerReceiver(_br, new IntentFilter(WifiHelper.WIFI_HELPER_DONE_SCANNING));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(_br);
    }

    // holds the wifi helper
    private WifiHelper _wifiHelper;

    // holds all the scan results
    private List<ScanResult> _scanResults;

    private DetectorAdapter _detectorAdapter;

    private BroadcastReceiver _br;

}

