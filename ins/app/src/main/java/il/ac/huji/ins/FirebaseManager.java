package il.ac.huji.ins;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nimoshe on 9/15/2016.
 */
public class FirebaseManager {


    private class FirebaseLocation<T> {

        private Firebase _handler = null;
        private T object;
        private boolean _isReady = false;
        private ValueEventListener listener = null;

        public FirebaseLocation(final String k) {
            _handler = new Firebase(k);

            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        object = (T) dataSnapshot.getValue(Object.class);
                        _isReady = true;
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            };

            _handler.addValueEventListener(listener);
        }

        public boolean isReady() {
            return _isReady;
        }

        public void finalize() {
            if (_handler != null)
                _handler.removeEventListener(listener);
        }

        public Object getObject() {
            if (isReady())
                return object;
            return null;
        }
    }

    public static FirebaseManager getInstance() {
        return instance;
    }

    private FirebaseManager() {}

    public void addScanResultHandlers(final List<ScanResult> scanRes) {
        List<ScanResult> scanResults = new ArrayList<>(scanRes);

        for (ScanResult sr: scanResults) {
            final String k = FirebaseConstants.FIREBASE_BSSID_TABLE + sr.BSSID;

            if (null != _firebaseHandlres.get(k))
                continue;

            _firebaseHandlres.put(k, new FirebaseLocation<Map<String, Integer>>(k));
        }
    }

    public void addBssid(final String bssid) {
        final String k = FirebaseConstants.FIREBASE_BSSID_TABLE + bssid;

        if (null != _firebaseHandlres.get(k))
            return;

        _firebaseHandlres.put(k, new FirebaseLocation<Map<String, Integer>>(k));
    }

    public void addLocation(final String location) {
        final String k = FirebaseConstants.FIREBASE_LOACATIONS_TABELE + location;

        if (null != _firebaseHandlres.get(k))
            return;

        _firebaseHandlres.put(k, new FirebaseLocation<Location>(k));
    }

    public void finalize() {
        for (FirebaseLocation fl: _firebaseHandlres.values()) {
            fl.finalize();
        }
    }

    public Location getLocation(String k) {
        FirebaseLocation f =
                _firebaseHandlres.get(FirebaseConstants.FIREBASE_LOACATIONS_TABELE + k);
        Log.d("FB_MANAGER", FirebaseConstants.FIREBASE_LOACATIONS_TABELE + k);
        if (null != f) {
            if (null != f.getObject()) {
                return new Location((LinkedHashMap<String, Object>) f.getObject());
            }

            return null;
        }
        return null;
    }

    public Map<String, Integer> getBssid(String k) {
        FirebaseLocation f = _firebaseHandlres.get(FirebaseConstants.FIREBASE_BSSID_TABLE + k);
        if (null != f)
            return (Map<String, Integer>) f.getObject();
        return null;
    }


    private static Map<String, FirebaseLocation> _firebaseHandlres = new ConcurrentHashMap<>();

    private static FirebaseManager instance = new FirebaseManager();
}
