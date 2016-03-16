package il.ac.huji.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by nirmo on 3/14/2016.
 */
public class AddNewTodoItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: set layout size & position
        setContentView(R.layout.activity_add_item);

        Button cancel = (Button) findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button okBtn = (Button) findViewById(R.id.btnOK);
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView edtNewItem = (TextView) findViewById(R.id.edtNewItem);
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                Intent result = new Intent();

                result.putExtra(getString(R.string.result_title), edtNewItem.getText().toString());
                result.putExtra(getString(R.string.result_date), new Date(
                        datePicker.getYear() - 1900, // :(
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth())
                );
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }
}
