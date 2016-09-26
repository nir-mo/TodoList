package il.ac.huji.ins;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nimoshe on 9/7/2016.
 */
public class DetectorAdapter extends BaseAdapter {
    private List<ScanResult> _items;
    private Context _context;
    private LayoutInflater _inflater;

    public DetectorAdapter(Context context, List<ScanResult> scan) {
        _context = context;
        _items = scan;
        _inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return _items.size();
    }

    @Override
    public Object getItem(int position) {
        return _items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null)
            vi = _inflater.inflate(R.layout.detector_listview_layout, null);


        final ScanResult sr = _items.get(position);

        TextView txtSSID = (TextView) vi.findViewById(R.id.detector_SSID);
        txtSSID.setText(sr.SSID);

        TextView txtBSSID = (TextView) vi.findViewById(R.id.detector_BSSID);
        txtBSSID.setText(sr.BSSID);

        ImageView wifiSignalImg = (ImageView) vi.findViewById(R.id.imageView);
        int imgSignalLevel = WifiHelper.getSignalLevel(sr, 5);
        wifiSignalImg.setImageResource(getSignalImage(imgSignalLevel));

        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.detector_listview_layout);
        int signalLevel = WifiHelper.getSignalLevel(sr, 10);
        layout.setBackgroundColor(getSignalColor(signalLevel));

        final int finalSignalLevel = imgSignalLevel;
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showINSDialog(_context, sr.BSSID, sr.toString(),
                        getSignalImage(finalSignalLevel), "Related locations",
                        new BssidAdapter(_context, sr.BSSID));
            }
        });

        return vi;
    }

    private int getSignalImage(int signalLevel) {
        switch (signalLevel) {
            case 0: return R.drawable.wifi_0;
            case 1: return R.drawable.wifi_1;
            case 2: return R.drawable.wifi_2;
            case 3: return R.drawable.wifi_3;
            case 4: return R.drawable.wifi_4;
            default:
                Log.e("ERROR", "Invalid signal " + String.valueOf(signalLevel));
                return R.drawable.wifi_0;
        }
    }

    private int getSignalColor(int signalLevel) {
        switch (signalLevel) {
            case 0: return _context.getResources().getColor(R.color.b50);
            case 1: return _context.getResources().getColor(R.color.b100);
            case 2: return _context.getResources().getColor(R.color.b200);
            case 3: return _context.getResources().getColor(R.color.b300);
            case 4: return _context.getResources().getColor(R.color.b400);
            case 5: return _context.getResources().getColor(R.color.b500);
            case 6: return _context.getResources().getColor(R.color.b600);
            case 7: return _context.getResources().getColor(R.color.b700);
            case 8: return _context.getResources().getColor(R.color.b800);
            case 9: return _context.getResources().getColor(R.color.b900);
            default:
                Log.e("ERROR", "Invalid signal level " + String.valueOf(signalLevel));
                return _context.getResources().getColor(R.color.b50);
        }
    }


    private class BssidAdapter extends BaseAdapter {

        private Context _context;
        private String _bssid;
        private LayoutInflater _inflater;
        private List<String> arr;

        public BssidAdapter(Context context, String bssid) {
            _context = context;
            _bssid = bssid;
            _inflater = LayoutInflater.from(context);
            FirebaseManager fm = FirebaseManager.getInstance();

            Map<String, Integer> temp = fm.getBssid(bssid);

            arr = new ArrayList<>();
            if (temp != null) {
                for (String loc: temp.keySet()) {
                    Location t = fm.getLocation(loc);
                    if (null == t)
                        continue;
                    arr.add(t.getName());
                }
            }
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public Object getItem(int position) {
            return arr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;

            if (convertView == null)
                vi = _inflater.inflate(R.layout.list_dialog_element_layout, null);

            TextView txt = (TextView) vi.findViewById(R.id.txt_list_dialog);
            txt.setText(arr.get(position));

            return vi;
        }
    }

}
