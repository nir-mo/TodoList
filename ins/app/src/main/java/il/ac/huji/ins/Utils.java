package il.ac.huji.ins;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by nimoshe on 9/22/2016.
 */
public class Utils {
    private Utils() {}

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public static Bitmap decodeImage(final String picRaw) {
        byte[] decodedString = Base64.decode(picRaw, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    public static void showINSDialog(Context context, final String title, final String message,
                                     final int icon, final String listTitle,
                                     BaseAdapter listAdapter) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.ins_dialog);

        dialog.setTitle((null == title) ? "I.N.S" : title);

        TextView messageTxt = (TextView) dialog.findViewById(R.id.msg_txt_dialog);
        messageTxt.setText(message);

        ImageView iconImg = (ImageView) dialog.findViewById(R.id.icon_img_dialog);
        iconImg.setImageResource(icon);;

        Button ok = (Button) dialog.findViewById(R.id.ok_btn_dialog);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (null != listTitle) {
            ListView list = (ListView) dialog.findViewById(R.id.list_dialog);
            list.setAdapter(listAdapter);

            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.listview_layout_dialog);
            layout.setVisibility(View.VISIBLE);

            TextView titleList = (TextView) dialog.findViewById(R.id.list_title_dialog);
            titleList.setText(listTitle);
        }

        dialog.show();
    }

    public static void showINSDialog(Context context, final String title, final int message,
                                     final Bitmap icon, final String listTitle,
                                     BaseAdapter listAdapter) {
        showINSDialog(context, title, context.getString(message), icon, listTitle, listAdapter);
    }

    public static void showINSDialog(Context context, final String title, final String message,
                                     final Bitmap icon, final String listTitle,
                                     BaseAdapter listAdapter) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.ins_dialog);

        dialog.setTitle((null == title) ? "I.N.S" : title);

        TextView messageTxt = (TextView) dialog.findViewById(R.id.msg_txt_dialog);
        messageTxt.setText(message);

        if (null != icon) {
            ImageView iconImg = (ImageView) dialog.findViewById(R.id.icon_img_dialog);
            iconImg.setImageBitmap(icon);
        }

        Button ok = (Button) dialog.findViewById(R.id.ok_btn_dialog);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (null != listTitle) {
            ListView list = (ListView) dialog.findViewById(R.id.list_dialog);
            list.setAdapter(listAdapter);

            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.listview_layout_dialog);
            layout.setVisibility(View.VISIBLE);

            TextView titleList = (TextView) dialog.findViewById(R.id.list_title_dialog);
            titleList.setText(listTitle);
        }

        dialog.show();
    }

    public static ArrayList<Location> BFSSearch(Location from, Location to) {
        Queue<ArrayList<Location>> locationsQueue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        FirebaseManager fm = FirebaseManager.getInstance();

        ArrayList<Location> first = new ArrayList<>();
        first.add(from);
        locationsQueue.add(first);

        while (!locationsQueue.isEmpty()) {
            ArrayList<Location> current = locationsQueue.remove();

            Location lastElement = current.get(current.size() - 1);

            // check if we already visit this node
            if (visited.contains(lastElement.getId()))
                continue;

            // add it to visited nodes.
            visited.add(lastElement.getId());

            // check if we found our location
            if (lastElement.getId().compareTo(to.getId()) == 0)
                return current;

            for (String near: lastElement.getNear().keySet()) {
                ArrayList<Location> temp = new ArrayList<>(current);
                fm.addLocation(near);

                Location n = null;

                do {
                    n = fm.getLocation(near);
                } while (null == n);

                temp.add(n);

                locationsQueue.add(temp);
            }
        }

        return null;
    }
}
