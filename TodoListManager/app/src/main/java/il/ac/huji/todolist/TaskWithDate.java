package il.ac.huji.todolist;

import java.util.Date;

/**
 * Created by nirmo on 3/14/2016.
 */
public class TaskWithDate {
    private String todo;
    private Date date;

    public TaskWithDate(final String todo, final Date date) {
        this.todo = todo;
        this.date = date;
    }

    public String getTodo() {
        return this.todo;
    }

    public Date getDate() {
        return this.date;
    }
}
