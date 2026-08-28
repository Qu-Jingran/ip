package Eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the common behaviour shared by task types. */
public class TaskTest {
    @Test
    public void todo_hasExpectedDisplayText() {
        Todo task = new Todo("read notes");
        assertEquals("[T][ ] read notes", task.toString());
    }

    @Test
    public void markingTaskDoneChangesStatusIcon() {
        Todo task = new Todo("read notes");
        task.markAsDone();
        assertTrue(task.toString().contains("[X]"));
    }

    @Test
    public void deadlineIncludesDueDate() {
        Deadline task = new Deadline("submit", "2026-09-01");
        assertTrue(task.toString().contains("Sep 1 2026"));
    }
}
