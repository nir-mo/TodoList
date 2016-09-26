package il.ac.huji.ins;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class represents a situation where the wifi is disabled.
 */
class WifiDisabledException extends Exception {
    WifiDisabledException(String msg) {
        super(msg);
    }
}

/**
 * Created by nimoshe on 9/5/2016.
 *
 * Singleton class which runs single-thread who scan the wifi connection every quanta.
 *
 */
public class WifiHelper implements CallBack {

    public static int MINUMUM_LEVEL = -90;

    public static int DELAY = 2000;

    private WifiHelper() { /*empty private constructor */ }

    static public WifiHelper getInstance() {
        return instance;
    }

    private ScanReceiver _sr;

    public static int getSignalLevel(final ScanResult s, final int maximalBound) {
        return WifiManager.calculateSignalLevel(s.level, maximalBound);
    }

    public boolean init(Context context) {
        _lock.lock();

        try {
            if (_isInitialized)
                return false;

            _minimumLevelThreshold = MINUMUM_LEVEL;
            _context = context;
            _wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            _delay = DELAY;

            if (!_wifiManager.isWifiEnabled()) {
                return false;
            }

            _scanResults = new ArrayList<ScanResult>();

            _sr = new ScanReceiver(this);
            context.registerReceiver(_sr,
                    new IntentFilter(_wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            _wifiThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    _threadAlive = true;

                    while (_threadAlive) {
                        Log.d("WIFI_THREAD", "Sleep for " + String.valueOf(_delay));
                        SystemClock.sleep(_delay);
                        _wifiManager.startScan();
                    }

                    Log.w("WIFI_THREAD", "Wifi thread destroyied!");
                }
            });

            _wifiThread.start();
            _isInitialized = true;

        } finally {
            _lock.unlock();
        }
        return true;
    }

    public void finalize() {
        _lock.lock();

        try {
            if (null != _sr)
                _context.unregisterReceiver(_sr);

            _sr = null;
            _threadAlive = false;

            if (_wifiThread != null)
                _wifiThread.join();

            _wifiThread = null;
            _isInitialized = false;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            _lock.unlock();
        }
    }


    /**
     * Simple BroadcastReceiver, handles wifi-manager scanning event.
     */
    private class ScanReceiver extends BroadcastReceiver {

        public ScanReceiver(CallBack callback) {
            _callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            _callback.doCallBack();
        }

        private CallBack _callback;
    }

    /**
     * Async function which returns the values of the previous scan.
     *
     * @return returns the values of the previous scan
     */
    public List<ScanResult> getScanResults() {
        return _scanResults;
    }

    @Override
    public boolean doCallBack() {

        if (!_wifiManager.isWifiEnabled())
            return false;

        _lock.lock();

        try {
            _scanResults.clear();

            for (ScanResult sr : _wifiManager.getScanResults()) {
                if (WifiManager.compareSignalLevel(sr.level, _minimumLevelThreshold) >= 0)
                    _scanResults.add(sr);
            }

            Collections.sort(_scanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return lhs.SSID.compareTo(rhs.SSID);
                }
            });

            Intent notify = new Intent();
            notify.setAction(WIFI_HELPER_DONE_SCANNING);
            _context.sendBroadcast(notify);
        } finally {
            _lock.unlock();
        }
        return true;
    }


    public static String WIFI_HELPER_DONE_SCANNING = "WIFI_HELPER_DONE_SCANNING";

    // holds the minimal threshold that we accept.
    private int _minimumLevelThreshold = 0;

    // holds the wifiManager instance.
    private WifiManager _wifiManager;

    // holds the current context
    private Context _context = null;

    private List<ScanResult> _scanResults;

    private static WifiHelper instance = new WifiHelper();

    private boolean _isInitialized = false;

    private final ReentrantLock _lock = new ReentrantLock();

    private Thread _wifiThread = null;

    private boolean _threadAlive = false;

    private long _delay = 0;
}
