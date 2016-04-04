package il.ac.huji.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nimoshe on 3/24/2016.
 */
public class TodoDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "todo_db";
    public static final String TABLE_NAME = "todo";
    public static final int DB_VERSION = 1;

    // ****** TABLE FIELDS ********
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String DUE = "due";

    // ****** SQL QUERIES ********
    public static final String SELECT_ALL_SQL = "SELECT  * FROM " + TABLE_NAME;
    private static final String CREATE_DB_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TITLE + " STRING, " +
            DUE + " LONG);";
    private static final String ERASE_DB_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String ID_LIKE_SQL = ID + " LIKE ?";

    public TodoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ERASE_DB_SQL);
        onCreate(db);
    }

    public Cursor selectAll(final SQLiteDatabase db) {
        return db.rawQuery(SELECT_ALL_SQL, null);
    }

    static public long insert(SQLiteDatabase db, TaskWithDate td) {
        ContentValues cv = new ContentValues();

        cv.put(TITLE, td.getTodo());
        cv.put(DUE, td.getDate().getTime());
        return db.insert(TABLE_NAME, null, cv);
    }

    static public int delete(SQLiteDatabase db, final long idToRemove) {
        String args[] = { String.valueOf(idToRemove) };
        return db.delete(TABLE_NAME, ID_LIKE_SQL, args);
    }

}
