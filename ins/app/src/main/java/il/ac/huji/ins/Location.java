package il.ac.huji.ins;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * Created by nimoshe on 9/15/2016.
 */
public class Location {
    private String _name;
    private String _id;
    private String _desc;
    private Map<String, Integer> _points;
    private String _picture;
    private Map<String, Integer> _near;

    public Location() {};

    public Location(LinkedHashMap m) {
        _id = m.get("id").toString();
        _name = m.get("name").toString();
        _desc = m.get("description").toString();
        _picture = (String) m.get("picture");

        _points = (Map) m.get("points");

        _near = (Map) m.get("near");
        if (_near == null)
            _near = new HashMap<>();
    };

    public Location(String locationName, String desc, List<ScanResult> points) {
        _name = locationName;
        _points = new HashMap<>();
        _near = new HashMap<>();
        _desc = desc;
        _picture = null;
        _id = String.valueOf(System.currentTimeMillis()) + _name;

        for (ScanResult sr : points) {
            _points.put(sr.BSSID, sr.level);
        }
    }

    public void setPicture(final String pic) {
        _picture = pic;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Map<String, Integer> getPoints() {
        return _points;
    }

    public String getDescription() {
        return _desc;
    }

    public String getPicture() {
        return _picture;
    }

    public Map<String, Integer> getNear() {
        return _near;
    }
}
