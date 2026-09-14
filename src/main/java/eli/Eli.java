package eli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Runs the Eli task-list application and responds to user commands. */
public class Eli {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String DATA_FILE = "duke.txt";

    private final TaskList tasks;
    private final boolean isStorageEnabled;

    /** Creates Eli and loads any tasks saved previously. */
    public Eli() {
        tasks = new TaskList();
        isStorageEnabled = true;
        loadTasks();
    }

    /** Creates Eli with an existing task list, primarily for isolated testing. */
    Eli(TaskList tasks) {
        this.tasks = tasks;
        isStorageEnabled = false;
    }

    /** Runs Eli using the original command-line interface. */
    public static void main(String[] args) {
        Eli eli = new Eli();
        Scanner input = new Scanner(System.in);

        System.out.println(eli.getWelcomeMessage());
        while (input.hasNextLine()) {
            String command = input.nextLine();
            System.out.println(DIVIDER);
            System.out.println(eli.getResponse(command));
            System.out.println(DIVIDER);
            if (eli.isExitCommand(command)) {
                break;
            }
        }
    }

    /**
     * Executes one command and returns Eli's response for either UI.
     *
     * @param input command entered by the user
     * @return Eli's response to the command
     */
    public String getResponse(String input) {
        String command = input.trim();
        try {
            if (isExitCommand(command)) {
                return "Bye for now!" + System.lineSeparator()
                        + "再见，记得回来找我！";
            } else if (command.equals("list")) {
                return getTaskListResponse();
            } else if (command.equals("sort")) {
                return getSortResponse();
            } else if (command.equals("find")) {
                throw new EliException(getErrorMessage(
                        "Please enter a keyword after find.",
                        "请在 find 后输入关键词。"));
            } else if (command.startsWith("find ")) {
                return getFindResponse(command.substring(5).trim());
            } else if (command.equals("todo")) {
                throw new EliException(getErrorMessage(
                        "A todo needs a description.",
                        "请填写待办事项。"));
            } else if (command.startsWith("todo ")) {
                return addTodo(command.substring(5).trim());
            } else if (command.equals("deadline")) {
                throw new EliException(getErrorMessage(
                        "A deadline needs a description.",
                        "请填写截止事项。"));
            } else if (command.startsWith("deadline ")) {
                return addDeadline(command);
            } else if (command.equals("event")) {
                throw new EliException(getErrorMessage(
                        "An event needs a description.",
                        "请填写活动名称。"));
            } else if (command.startsWith("event ")) {
                return addEvent(command);
            } else if (command.startsWith("mark ")) {
                return updateTaskStatus(command.substring(5), true);
            } else if (command.startsWith("unmark ")) {
                return updateTaskStatus(command.substring(7), false);
            } else if (command.startsWith("delete ")) {
                return deleteTask(command.substring(7));
            } else {
                throw new EliException(getErrorMessage(
                        "I don't recognize that command yet.",
                        "我暂时不认识这个指令。"));
            }
        } catch (EliException exception) {
            return exception.getMessage();
        }
    }

    /** Returns whether the command asks Eli to exit. */
    public boolean isExitCommand(String input) {
        String command = input.trim();
        return command.equals("bye") || command.equals("再见");
    }

    /** Returns the welcome text used by the command-line interface. */
    private String getWelcomeMessage() {
        String banner = " _______     _           _____ \n"
                + "|  _____|   | |         |_   _|\n"
                + "| |___      | |           | |  \n"
                + "|  ___|     | |           | |  \n"
                + "| |_____    | |_____     _| |_ \n"
                + "|_______|   |_______|   |_____|\n";
        return banner + DIVIDER
                + "\nHello! I'm Eli, your bilingual task buddy."
                + "\n你好！准备好一起完成任务了吗？\n"
                + DIVIDER;
    }

    /** Returns all currently stored tasks. */
    private String getTaskListResponse() {
        String taskLines = IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return taskLines.isEmpty()
                ? "Your task list is clear!" + System.lineSeparator() + "任务清单空空如也！"
                : "Let's see what's on your list!" + System.lineSeparator()
                        + "来看看你的任务清单吧：" + System.lineSeparator() + taskLines;
    }

    /** Returns tasks whose descriptions contain the keyword. */
    private String getFindResponse(String keyword) throws EliException {
        if (keyword.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "Please enter a keyword after find.",
                    "请在 find 后输入关键词。"));
        }

        String searchTerm = keyword.toLowerCase();
        String matches = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase().contains(searchTerm))
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return matches.isEmpty()
                ? "No matching tasks found." + System.lineSeparator() + "没有找到相关任务。"
                : "I found these tasks!" + System.lineSeparator()
                        + "找到这些任务啦：" + System.lineSeparator() + matches;
    }

    /** Sorts and saves the task list, then returns it grouped by task type. */
    private String getSortResponse() throws EliException {
        if (tasks.isEmpty()) {
            return "There are no tasks to sort yet." + System.lineSeparator()
                    + "暂时没有任务可以排序。";
        }

        tasks.sortTasks();
        saveTasks();
        String sections = Arrays.stream(TaskType.values())
                .sorted(Comparator.comparingInt(TaskType::getSortOrder))
                .map(this::formatTaskSection)
                .filter(section -> !section.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
        return "All sorted and ready!" + System.lineSeparator()
                + "任务已经排好啦：" + System.lineSeparator() + sections;
    }

    /** Formats one non-empty task-type section using the list's current numbers. */
    private String formatTaskSection(TaskType taskType) {
        String taskLines = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getTaskType() == taskType)
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return taskLines.isEmpty()
                ? ""
                : taskType.getSectionName() + ":" + System.lineSeparator() + taskLines;
    }

    /** Adds a to-do task and returns a confirmation. */
    private String addTodo(String description) throws EliException {
        if (description.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "A todo needs a description.",
                    "请填写待办事项。"));
        }
        return addTask(new Todo(description));
    }

    /** Adds a deadline task and returns a confirmation. */
    private String addDeadline(String command) throws EliException {
        int byIndex = command.indexOf(" /by ");
        if (byIndex == -1) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a /by value.",
                    "请使用 /by 填写截止时间。"));
        } else if (byIndex <= 9) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a description.",
                    "请填写截止事项。"));
        }

        String description = command.substring(9, byIndex).trim();
        String by = command.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a description.",
                    "请填写截止事项。"));
        } else if (by.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "A deadline needs a /by value.",
                    "请使用 /by 填写截止时间。"));
        }
        return addTask(new Deadline(description, by));
    }

    /** Adds an event task and returns a confirmation. */
    private String addEvent(String command) throws EliException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new EliException(getErrorMessage(
                    "An event needs /from and /to values.",
                    "请使用 /from 和 /to 填写活动时间。"));
        } else if (fromIndex <= 6) {
            throw new EliException(getErrorMessage(
                    "An event needs a description.",
                    "请填写活动名称。"));
        } else if (toIndex < fromIndex + 7) {
            throw new EliException(getErrorMessage(
                    "An event needs /from and /to values.",
                    "请使用 /from 和 /to 填写活动时间。"));
        }

        String description = command.substring(6, fromIndex).trim();
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "An event needs a description.",
                    "请填写活动名称。"));
        } else if (from.isEmpty() || to.isEmpty()) {
            throw new EliException(getErrorMessage(
                    "An event needs /from and /to values.",
                    "请使用 /from 和 /to 填写活动时间。"));
        }
        return addTask(new Event(description, from, to));
    }

    /** Adds and saves one task. */
    private String addTask(Task task) throws EliException {
        tasks.addTask(task);
        saveTasks();
        return "Task captured! 任务记下来啦：\n  " + task
                + "\nYou now have " + tasks.size() + " tasks."
                + " 你现在有 " + tasks.size() + " 个任务。";
    }

    /** Marks or unmarks a task and returns a confirmation. */
    private String updateTaskStatus(String numberText, boolean isDone) throws EliException {
        int taskNumber = parseTaskNumber(numberText);
        if (!tasks.hasTaskNumber(taskNumber)) {
            throw new EliException(getErrorMessage(
                    "There is no task with that number.",
                    "没有这个编号的任务。"));
        }

        Task task = tasks.getTask(taskNumber);
        if (isDone) {
            task.markAsDone();
            saveTasks();
            return "Nice work! 做得好！\nThis task is now complete:\n  " + task;
        }
        task.markAsNotDone();
        saveTasks();
        return "No worries! 没关系！\nThis task is back on your list:\n  " + task;
    }

    /** Deletes a task and returns a confirmation. */
    private String deleteTask(String numberText) throws EliException {
        int taskNumber = parseTaskNumber(numberText);
        if (!tasks.hasTaskNumber(taskNumber)) {
            throw new EliException(getErrorMessage(
                    "There is no task with that number.",
                    "没有这个编号的任务。"));
        }

        Task removedTask = tasks.removeTask(taskNumber);
        saveTasks();
        return "Task cleared! 已删除这个任务：\n  " + removedTask
                + "\nYou now have " + tasks.size() + " tasks."
                + " 你现在有 " + tasks.size() + " 个任务。";
    }

    /** Converts task-number text to a number, or returns -1 when it is invalid. */
    private int parseTaskNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /** Loads saved tasks, if a save file exists. */
    @SuppressWarnings("unchecked")
    private void loadTasks() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            tasks.addAll((TaskList) input.readObject());
        } catch (IOException | ClassNotFoundException exception) {
            // A missing or unreadable file is treated as an empty task list.
        }
    }

    /** Saves the current task list to disk. */
    private void saveTasks() throws EliException {
        if (!isStorageEnabled) {
            return;
        }

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            output.writeObject(tasks);
        } catch (IOException exception) {
            throw new EliException(getErrorMessage(
                    "I could not save your tasks.",
                    "任务保存失败。"));
        }
    }

    /** Builds a consistent bilingual error response in Eli's friendly voice. */
    private static String getErrorMessage(String english, String chinese) {
        return "Oops! 哎呀！" + System.lineSeparator()
                + english + System.lineSeparator()
                + chinese;
    }
}
