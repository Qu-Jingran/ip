package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests command responses that operate on an existing task list. */
public class EliTest {
    @Test
    public void list_multipleTasks_returnsNumberedTaskLines() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        tasks.addTask(new Todo("submit quiz"));
        Eli eli = new Eli(tasks);

        String expected = "Here are the tasks in your list:"
                + System.lineSeparator() + "1.[T][ ] read notes"
                + System.lineSeparator() + "2.[T][ ] submit quiz";

        assertEquals(expected, eli.getResponse("list"));
    }

    @Test
    public void find_matchingTasks_preservesOriginalTaskNumbers() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        tasks.addTask(new Todo("submit quiz"));
        tasks.addTask(new Todo("review notes"));
        Eli eli = new Eli(tasks);

        String expected = "1.[T][ ] read notes"
                + System.lineSeparator() + "3.[T][ ] review notes";

        assertEquals(expected, eli.getResponse("find notes"));
    }

    @Test
    public void find_noMatchingTask_returnsNoMatchesMessage() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        Eli eli = new Eli(tasks);

        assertEquals("No matching tasks found.", eli.getResponse("find quiz"));
    }
}
