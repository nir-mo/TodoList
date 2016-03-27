package il.ac.huji.todolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nimoshe on 3/24/2016.
 */
public class TodoCursorAdapter extends CursorAdapter {

    public TodoCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.missin_date_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getInt(cursor.getColumnIndex(TodoDBHelper.ID));
        long _date = cursor.getLong(cursor.getColumnIndex(TodoDBHelper.DUE));
        String title = cursor.getString(cursor.getColumnIndex(TodoDBHelper.TITLE));
        Date date = new Date(_date);

        TextView txtTodoTitle = (TextView) view.findViewById(R.id.txtTodoTitle);
        txtTodoTitle.setText(title);
        TextView txtTodoDueDate = (TextView) view.findViewById(R.id.txtTodoDueDate);
        SimpleDateFormat frmt = new SimpleDateFormat(context.getString(R.string.date_format));
        txtTodoDueDate.setText(frmt.format(date));

        // set the item color, according to the current date
        int color = (date.before(new Date())) ? Color.RED : Color.BLACK;
        txtTodoTitle.setTextColor(color);
        txtTodoDueDate.setTextColor(color);
        view.setOnLongClickListener(new OnItemLongClickListener(context, title, position));
    }

    private class OnItemLongClickListener implements View.OnLongClickListener {

        private Context context;
        private int position;
        private String title;

        public OnItemLongClickListener(Context context, final String title, final int position) {
            this.position = position;
            this.context = context;
            this.title = title;
        }

        @Override
        public boolean onLongClick(View v) {
            RecordHandlerDialog.showRecordHandlerDialog((TodoListManagerActivity) this.context,
                    this.title, this.position);
            return true;
        }
    }
}
