package il.ac.huji.todolist;

import java.util.Date;

/**
 * Created by nirmo on 3/14/2016.
 */
public class TaskWithDate {

    public static final int NO_DB_ID = -1;

    private String todo;
    private Date date;
    private long db_id;

    private void setInitialValues(final String todo, final Date date, final long db_id) {
        this.todo = todo;
        this.date = date;
        this.db_id = db_id;
    }

    public TaskWithDate(final String todo, final Date date) {
        setInitialValues(todo, date, NO_DB_ID);
    }

    public TaskWithDate(final String todo, final Date date, final long db_id) {
        setInitialValues(todo, date, db_id);
    }

    public String getTodo() {
        return this.todo;
    }

    public Date getDate() {
        return this.date;
    }

    public long getDBId() {
        return this.db_id;
    }

    public void setDBid(final long db_id) {
        this.db_id = db_id;
    }
}
