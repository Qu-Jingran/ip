package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the task collection's user-facing operations. */
public class TaskListTest {
    @Test
    public void addAndRemoveTaskByUserNumber() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Todo("second"));

        assertEquals("first", tasks.getTask(1).toString().substring(7));
        assertEquals("second", tasks.removeTask(2).toString().substring(7));
        assertEquals(1, tasks.size());
    }
}
