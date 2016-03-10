package il.ac.huji.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nirmo on 3/8/2016.
 */
public class RedBlueAdapter extends BaseAdapter {
    private ArrayList<String> items;
    private Context context;
    private LayoutInflater inflater;

    public RedBlueAdapter(Context context, ArrayList<String> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View vi = convertView;

        if(convertView == null)
            vi = inflater.inflate(R.layout.red_text_view, null);

        TextView txtView = (TextView) vi.findViewById(R.id.textView);
        String newItem = this.items.get(position);
        txtView.setText(newItem);

        // set the item color, according to the current position
        int color = (position % 2 == 0) ? Color.RED : Color.BLUE;
        txtView.setTextColor(color);

        vi.setOnClickListener(new OnItemClickListener(this.context, position));

        return vi;
    }

    /**
     * Internal class. click listener. will load the dialog.
     */
    private class OnItemClickListener implements View.OnClickListener {
        private int position;
        private Context context;

        OnItemClickListener(Context context, int position){
            this.position = position;
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            LinearLayout linearLayout = (LinearLayout) view;
            TextView txtView = (TextView) linearLayout.findViewById(R.id.textView);
            DeletionDialog.showDeletionDialog((TodoListManagerActivity)
                    this.context, txtView.getText().toString(), this.position);
        }
    }
}

