package il.ac.huji.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by nirmo on 3/8/2016.
 */
public class DeletionDialog {
    private DeletionDialog() {};

    public static void showDeletionDialog(final TodoListManagerActivity activity, final String str,
                                          final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(str);
        builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.removeItem(position);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
