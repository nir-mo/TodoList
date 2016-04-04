package il.ac.huji.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;


public class TodoListManagerActivity extends AppCompatActivity {

    final int ACTIVITY_ADD_CODE = 1;

    private MissionDueAdapter adapter;
    private SQLiteDatabase db;
    private TodoDBHelper todoDBHelper;
    private ArrayList<TaskWithDate> tasks;

    public void addItem(final TaskWithDate task) {
        // Add item to the list and update views...
        new InsertTableTask().execute(task);
    }

    public void removeItem(final int position) {
        // Remove Item from the array and notify.
        new DeleteTableTask().execute(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        tasks = new ArrayList<>();
        todoDBHelper = new TodoDBHelper(this);
        db = todoDBHelper.getWritableDatabase();

        ListView listView = (ListView) findViewById(R.id.lstTodoItems);
        listView.setLongClickable(true);

        adapter = new MissionDueAdapter(this, tasks);
        new UpdateTableTask().execute();
        listView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItemAdd) {
            Intent add = new Intent(this, AddNewTodoItemActivity.class);
            startActivityForResult(add, ACTIVITY_ADD_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_ADD_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle results = data.getExtras();

                addItem(new TaskWithDate(results.getString(getString(R.string.result_title)),
                        (Date) results.get(getString(R.string.result_date))));
            }
        }
    }

    private class InsertTableTask  extends AsyncTask<TaskWithDate, Void, Long> {

        private TaskWithDate task;

        @Override
        protected Long doInBackground(TaskWithDate... params) {
            task = params[0];
            return TodoDBHelper.insert(db, task);
        }

        @Override
        protected void onPostExecute(Long result) {
            task.setDBid(result);
            tasks.add(task);
            adapter.notifyDataSetChanged();
        }
    }

    private class DeleteTableTask  extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int position = params[0];
            long id = tasks.get(position).getDBId();
            tasks.remove(position);
            return TodoDBHelper.delete(db, id);
        }

        @Override
        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
        }
    }

    private class UpdateTableTask extends AsyncTask<Void, TaskWithDate, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            if (isCancelled())
                return null;

            Cursor cur =  todoDBHelper.selectAll(db);
            try {
                while (cur.moveToNext()) {
                    if (isCancelled())
                        break;

                    long id = cur.getLong(cur.getColumnIndex(TodoDBHelper.ID));
                    long _date = cur.getLong(cur.getColumnIndex(TodoDBHelper.DUE));
                    String title = cur.getString(cur.getColumnIndex(TodoDBHelper.TITLE));
                    TaskWithDate task = new TaskWithDate(title, new Date(_date), id);
                    publishProgress(task);
                }
            } finally {
                cur.close();
            }
            return cur;
        }

        @Override
        protected void onProgressUpdate(TaskWithDate...task_list) {
            tasks.add(task_list[0]);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Cursor result) {
            adapter.notifyDataSetChanged();
        }
    }
}
