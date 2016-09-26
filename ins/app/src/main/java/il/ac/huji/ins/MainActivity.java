package il.ac.huji.ins;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WifiHelper _wifiHelper;

    private BroadcastReceiver _br = null;

    private FirebaseManager _fm = FirebaseManager.getInstance();

    private NearPlacesAdapter _placesAdapter;

    private List<Location> _locations;

    private Location currentLocation = null;

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FirebaseManager.getInstance().addScanResultHandlers(_wifiHelper.getScanResults());
            updateUI();
        }
    }

    private Map<String, Integer> getPossibleLocations() {
        List<ScanResult> l = new ArrayList<>(_wifiHelper.getScanResults());
        Map<String, Integer> histogram = new HashMap<>();

        for (ScanResult sr : l) {
            Map<String, Integer> temp = _fm.getBssid(sr.BSSID);
            if (null == temp)
                continue;

            for (String loc: temp.keySet()) {
                _fm.addLocation(loc);
                histogram.put(loc, (histogram.get(loc) != null) ? histogram.get(loc) + 1: 1);
            }
        }

        if (histogram.isEmpty())
            return null;
        return histogram;
    }

    private String getNearestLocation(Map<String, Integer> locations) {
        List<String> maximalValues = new ArrayList<>();
        List<String> minimalValues = new ArrayList<>();
        Map<Location, Integer> scores = new HashMap<>();

        int maximum = Collections.max(locations.values());
        for (String k: locations.keySet()) {
            if (locations.get(k) == maximum)
                maximalValues.add(k);
        }

        // calculate score for each "best" location
        for (String b: maximalValues) {
            Location temp = _fm.getLocation(b);
            if (null == temp)
                continue;
            scores.put(temp, calcScore(temp));
        }
        if (scores.isEmpty())
            return maximalValues.get(0);

        int minimum = Collections.min(scores.values());
        for (Location k: scores.keySet()) {
            if (scores.get(k) == minimum)
                minimalValues.add(k.getId());
        }

        return minimalValues.get(0);
    }

    private Integer calcScore(Location l) {
        Map<String, Integer> points = l.getPoints();
        int score = 0;

        for (ScanResult sr: _wifiHelper.getScanResults()) {
            Integer bssidLevel = points.get(sr.BSSID);
            if (null != bssidLevel)
                score += Math.abs(sr.level - points.get(sr.BSSID));
        }
        return score;
    }

    private void updateNearLoacations(Map<String, Integer> locations) {
        _locations.clear();
        for (String k : locations.keySet()) {
            Location temp = _fm.getLocation(k);
            if (null == temp)
                continue;
            _locations.add(temp);
        }
        _placesAdapter.notifyDataSetChanged();
    }

    private void updateUI() {
        Map<String, Integer> locations = getPossibleLocations();

        TextView placeTxt = (TextView) findViewById(R.id.place_txt_main);
        TextView descTxt = (TextView) findViewById(R.id.desc_txt_main);

        if (locations != null) {
            String loc = getNearestLocation(locations);
            updateNearLoacations(locations);
            Location bestLocation = _fm.getLocation(loc);
            if (null == bestLocation)
                return;

            _placesAdapter.setCurrentLocation(bestLocation);

            if ((currentLocation != null) &&
                    (currentLocation.getId().compareTo(bestLocation.getId()) != 0)) {
                // we moved! the current place was changed!
                if (currentLocation.getNear().get(bestLocation.getId()) == null) {
                    // unknown place - we should add it to the db
                    currentLocation.getNear().put(bestLocation.getId(), 1);
                    bestLocation.getNear().put(currentLocation.getId(), 1);

                    Firebase location = new Firebase(FirebaseConstants.FIREBASE_LOACATIONS_TABELE);
                    location.child(currentLocation.getId()).setValue(currentLocation);
                    location.child(bestLocation.getId()).setValue(bestLocation);
                }
            }

            currentLocation = bestLocation;

            placeTxt.setText(bestLocation.getName());
            descTxt.setText(bestLocation.getDescription());

        } else {
            placeTxt.setText(R.string.place_unknown);
            descTxt.setText(R.string.place_unknown);
        }
    }

    private void loadUI(final Activity activity) {
        ListView nearby = (ListView) findViewById(R.id.nearby_list_main);

        nearby.setAdapter(_placesAdapter);

        Button pinpoint_img = (Button) findViewById(R.id.button);
        pinpoint_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, PinpointActivity.class),
                        PinpointActivity.PINPOINT_ACTIVITY);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _locations = new ArrayList<>();

        _placesAdapter = new NearPlacesAdapter(this, _locations);

        _wifiHelper = WifiHelper.getInstance();

        _wifiHelper.init(this);

        Firebase.setAndroidContext(this);

        _br = new LocationReceiver();
        registerReceiver(_br, new IntentFilter(WifiHelper.WIFI_HELPER_DONE_SCANNING));
        loadUI(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_detector:
                startActivityForResult(new Intent(this, DetectorActivity.class),
                        DetectorActivity.DETECTOR_ACTIVITY_ID);
                break;

            case R.id.action_pinpoint:
                startActivityForResult(new Intent(this, PinpointActivity.class),
                        PinpointActivity.PINPOINT_ACTIVITY);
                break;
            case R.id.action_about:
                startActivityForResult(new Intent(this, AboutActivity.class), AboutActivity.ABOUT_ACTIVITY_CODE);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SplashActivity.instance.finish();

        _wifiHelper.finalize();
        FirebaseManager.getInstance().finalize();

        if (null != _br)
            unregisterReceiver(_br);
    }

}
