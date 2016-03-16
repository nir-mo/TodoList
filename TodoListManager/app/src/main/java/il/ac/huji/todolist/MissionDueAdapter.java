package il.ac.huji.todolist;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nirmo on 3/14/2016.
 */
public class MissionDueAdapter extends BaseAdapter {
    private ArrayList<TaskWithDate> items;
    private Context context;
    private LayoutInflater inflater;

    public MissionDueAdapter(Context context, ArrayList<TaskWithDate> items) {
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
            vi = inflater.inflate(R.layout.missin_date_view, null);

        TaskWithDate item = this.items.get(position);

        TextView txtTodoTitle = (TextView) vi.findViewById(R.id.txtTodoTitle);
        txtTodoTitle.setText(item.getTodo());
        TextView txtTodoDueDate = (TextView) vi.findViewById(R.id.txtTodoDueDate);
        SimpleDateFormat frmt = new SimpleDateFormat(context.getString(R.string.date_format));
        txtTodoDueDate.setText(frmt.format(item.getDate()));

        // set the item color, according to the current date
        int color = (item.getDate().before(new Date())) ? Color.RED : Color.BLACK;
        txtTodoTitle.setTextColor(color);
        txtTodoDueDate.setTextColor(color);
        vi.setOnLongClickListener(new OnItemLongClickListener(this.context, item, position));

        return vi;
    }

    private class OnItemLongClickListener implements View.OnLongClickListener {

        private Context context;
        private int position;
        private TaskWithDate task;

        public OnItemLongClickListener(Context context, final TaskWithDate task,
                                       final int position) {
            this.position = position;
            this.context = context;
            this.task = task;
        }

        @Override
        public boolean onLongClick(View v) {
            RecordHandlerDialog.showRecordHandlerDialog((TodoListManagerActivity) this.context,
                    this.task.getTodo(), this.position);
            return true;
        }
    }

}
