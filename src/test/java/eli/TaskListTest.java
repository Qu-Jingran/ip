package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void getTask_invalidUserNumber_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("only task"));

        assertThrows(AssertionError.class, () -> tasks.getTask(0));
        assertThrows(AssertionError.class, () -> tasks.getTask(2));
    }

    @Test
    public void addTask_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.addTask(null));
    }
}
