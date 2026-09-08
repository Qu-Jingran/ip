package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void eventWithDateTimes_hasFormattedDisplayText() {
        Event task = new Event("lecture", "2026-09-08 1400", "2026-09-08 1600");

        assertTrue(task.toString().contains("Sep 8 2026, 2:00PM"));
        assertTrue(task.toString().contains("Sep 8 2026, 4:00PM"));
    }

    @Test
    public void taskWithNaturalLanguageDate_preservesOriginalText() {
        Deadline task = new Deadline("submit", "next Friday");

        assertTrue(task.toString().contains("next Friday"));
    }

    @Test
    public void todo_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo("  "));
    }
}
