package eli;

import java.util.ArrayList;

/** Represents the collection of tasks managed by the application. */
public class TaskList extends ArrayList<Task> {
    private static final long serialVersionUID = 1L;

    /** Adds a task to this list. */
    public void addTask(Task task) {
        assert task != null : "Task list must not contain null tasks";
        add(task);
    }

    /** Returns the task at a one-based user-facing number. */
    public Task getTask(int taskNumber) {
        assert hasTaskNumber(taskNumber) : "Task number must refer to an existing task";
        return get(taskNumber - 1);
    }

    /** Removes and returns the task at a one-based user-facing number. */
    public Task removeTask(int taskNumber) {
        assert hasTaskNumber(taskNumber) : "Task number must refer to an existing task";
        return remove(taskNumber - 1);
    }

    /** Returns whether a one-based task number is valid. */
    public boolean hasTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= size();
    }
}
