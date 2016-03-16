package il.ac.huji.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by nirmo on 3/14/2016.
 */
public class RecordHandlerDialog {
    private RecordHandlerDialog() {};

    public static void showRecordHandlerDialog(final TodoListManagerActivity activity,
                                               final String str, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(str);
        builder.setNegativeButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.removeItem(position);
            }
        });

        if (str.startsWith(activity.getString(R.string.call_signature))) {
            final String phone = str.substring(
                    activity.getString(R.string.call_signature).length());

            builder.setPositiveButton(str, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent dial = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(activity.getString(R.string.tel_uri) + phone));

                    activity.startActivity(dial);
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
