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

        String expected = "Let's see what's on your list!"
                + System.lineSeparator() + "来看看你的任务清单吧："
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

        String expected = "I found these tasks!"
                + System.lineSeparator() + "找到这些任务啦："
                + System.lineSeparator() + "1.[T][ ] read notes"
                + System.lineSeparator() + "3.[T][ ] review notes";

        assertEquals(expected, eli.getResponse("find notes"));
    }

    @Test
    public void find_noMatchingTask_returnsNoMatchesMessage() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        Eli eli = new Eli(tasks);

        String expected = "No matching tasks found."
                + System.lineSeparator() + "没有找到相关任务。";

        assertEquals(expected, eli.getResponse("find quiz"));
    }

    @Test
    public void sort_mixedTasks_returnsSortedSectionsAndReordersTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Event("lecture", "2026-09-09 1400", "2026-09-09 1600"));
        tasks.addTask(new Deadline("submit quiz", "2026-09-10"));
        Eli eli = new Eli(tasks);

        String expected = "All sorted and ready!"
                + System.lineSeparator() + "任务已经排好啦："
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

        String expected = "There are no tasks to sort yet."
                + System.lineSeparator() + "暂时没有任务可以排序。";

        assertEquals(expected, eli.getResponse("sort"));
    }

    @Test
    public void bye_returnsBilingualFarewell() {
        Eli eli = new Eli(new TaskList());

        String expected = "Bye for now!"
                + System.lineSeparator() + "再见，记得回来找我！";

        assertEquals(expected, eli.getResponse("bye"));
    }
}
