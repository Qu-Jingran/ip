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

    @Test
    public void sort_mixedTasks_returnsSortedSectionsAndReordersTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Event("lecture", "2026-09-09 1400", "2026-09-09 1600"));
        tasks.addTask(new Deadline("submit quiz", "2026-09-10"));
        Eli eli = new Eli(tasks);

        String expected = "Here are your sorted tasks:"
                + System.lineSeparator() + "Deadlines:"
                + System.lineSeparator() + "1.[D][ ] submit quiz (by: Sep 10 2026)"
                + System.lineSeparator() + "Events:"
                + System.lineSeparator() + "2.[E][ ] lecture (from: Sep 9 2026, 2:00PM"
                + " to: Sep 9 2026, 4:00PM)"
                + System.lineSeparator() + "Todos:"
                + System.lineSeparator() + "3.[T][ ] write report";

        assertEquals(expected, eli.getResponse("sort"));
        assertEquals("submit quiz", tasks.get(0).getDescription());
    }

    @Test
    public void sort_emptyTaskList_returnsEmptyMessage() {
        Eli eli = new Eli(new TaskList());

        assertEquals("There are no tasks to sort.", eli.getResponse("sort"));
    }
}
