package il.ac.huji.ins;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nimoshe on 9/21/2016.
 */
public class NearPlacesAdapter extends BaseAdapter {

    private List<Location> _locations;

    private Context _context;

    private LayoutInflater _inflater;

    private Location _currentLocation = null;


    private class GetInformationDialog implements View.OnClickListener {

        private Location _curLocation;

        public GetInformationDialog(Location curLocation) {
            _curLocation = curLocation;
        }

        @Override
        public void onClick(View v) {
            Utils.showINSDialog(_context,
                    _curLocation.getName(),
                    _curLocation.getDescription(),
                    (_curLocation.getPicture() == null) ?
                            null : Utils.decodeImage(_curLocation.getPicture()),
                    "Related Bssid", new LocationAdapter(_context, _curLocation));
        }
    }

    NearPlacesAdapter(Context context, List<Location> locations) {
        _context = context;
        _locations = locations;
        _inflater = LayoutInflater.from(context);
    }

    public void setCurrentLocation(final Location currentLocation) {
        _currentLocation = currentLocation;
    }

    @Override
    public int getCount() {
        return _locations.size();
    }

    @Override
    public Object getItem(int position) {
        return _locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null)
            vi = _inflater.inflate(R.layout.nearby_listview_layout, null);


        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.nearby_listview_layout);
        layout.setBackgroundColor((position % 2 == 0) ?
                _context.getResources().getColor(R.color.b100) :
                _context.getResources().getColor(R.color.b200));

        final Location curLocation = _locations.get(position);

        TextView placeTxt = (TextView) vi.findViewById(R.id.place_txt_nearby);
        placeTxt.setText(curLocation.getName());

        // handle the place picture
        if (_locations.get(position).getPicture() != null) {
            ImageView placePic = (ImageView) vi.findViewById(R.id.place_pic_near);
            placePic.setImageBitmap(Utils.decodeImage(curLocation.getPicture()));
        }

        //vi.setOnClickListener(new GetInformationDialog(curLocation));

        ImageView infoImg = (ImageView) vi.findViewById(R.id.info_img_nearby);
        infoImg.setOnClickListener(new GetInformationDialog(curLocation));

        ImageView navImg = (ImageView) vi.findViewById(R.id.nav_img_nearby);
        navImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showINSDialog(_context, null, 
                        "Navigate from " + _currentLocation.getName() +  " to " + curLocation.getName(),
                        R.drawable.ic_explore_black_24dp,
                        "Directions", 
                        new DirectionsAdapter(_context, _currentLocation, curLocation));
            }
        });

        return vi;
    }

    private class LocationAdapter extends BaseAdapter {

        private Context _context;
        private Location _location;
        private LayoutInflater _inflater;
        private List<String> arr;

        public LocationAdapter(Context context, Location location) {
            _context = context;
            _location = location;
            _inflater = LayoutInflater.from(context);

            arr = new ArrayList<>();
            for (String bssid: _location.getPoints().keySet()) {
                arr.add(bssid);
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
    
    private class DirectionsAdapter extends BaseAdapter {

        private final Context mContext;
        private final Location mFrom;
        private final Location mTo;
        private LayoutInflater mInflater;
        private ArrayList<Location> arr;

        public DirectionsAdapter(Context context, Location from, Location to) {
            mContext = context;
            mFrom = from;
            mTo = to;
            mInflater = LayoutInflater.from(context);

            arr = Utils.BFSSearch(from, to);
            if (null == arr)
                arr = new ArrayList<>();
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
            txt.setText(arr.get(position).getName());

            if (arr.get(position).getPicture() != null) {
                ImageView placePic = (ImageView) vi.findViewById(R.id.image_list_dialog);
                placePic.setImageBitmap(Utils.decodeImage(arr.get(position).getPicture()));
            }

            LinearLayout layout = (LinearLayout) vi.findViewById(R.id.list_element_layout);
            layout.setBackgroundColor((position % 2 == 0) ?
                    _context.getResources().getColor(R.color.lightGrey) :
                    _context.getResources().getColor(R.color.bitDarkGrey));

            return vi;
        }
    }
}
