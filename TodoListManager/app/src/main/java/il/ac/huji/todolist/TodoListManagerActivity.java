package il.ac.huji.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TodoListManagerActivity extends AppCompatActivity {

    final int ACTIVITY_ADD_CODE = 1;

    private ArrayList<TaskWithDate> todos;
    private MissionDueAdapter adapter;

    public void addItem(final TaskWithDate task) {
        // Add item to the list and update views...
        this.todos.add(task);
        adapter.notifyDataSetChanged();
    }

    public void removeItem(final int position) {
        // Remove Item from the array and notify.
        this.todos.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        todos = new ArrayList();
        adapter = new MissionDueAdapter(this, todos);

        ListView listView = (ListView) findViewById(R.id.lstTodoItems);
        listView.setLongClickable(true);
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
}
