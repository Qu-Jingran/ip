package eli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Runs the Eli task-list application and responds to user commands. */
public class Eli {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String DATA_FILE = "duke.txt";

    private final TaskList tasks;

    /** Creates Eli and loads any tasks saved previously. */
    public Eli() {
        tasks = new TaskList();
        loadTasks();
    }

    /** Creates Eli with an existing task list, primarily for isolated testing. */
    Eli(TaskList tasks) {
        this.tasks = tasks;
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
                return "Bye. 记得来找我";
            } else if (command.equals("list")) {
                return getTaskListResponse();
            } else if (command.equals("find")) {
                throw new EliException("OOPS!!! The keyword for find cannot be empty.");
            } else if (command.startsWith("find ")) {
                return getFindResponse(command.substring(5).trim());
            } else if (command.equals("todo")) {
                throw new EliException("OOPS!!! The description of a todo cannot be empty.");
            } else if (command.startsWith("todo ")) {
                return addTodo(command.substring(5).trim());
            } else if (command.equals("deadline")) {
                throw new EliException("OOPS!!! The description of a deadline cannot be empty.");
            } else if (command.startsWith("deadline ")) {
                return addDeadline(command);
            } else if (command.equals("event")) {
                throw new EliException("OOPS!!! The description of an event cannot be empty.");
            } else if (command.startsWith("event ")) {
                return addEvent(command);
            } else if (command.startsWith("mark ")) {
                return updateTaskStatus(command.substring(5), true);
            } else if (command.startsWith("unmark ")) {
                return updateTaskStatus(command.substring(7), false);
            } else if (command.startsWith("delete ")) {
                return deleteTask(command.substring(7));
            } else {
                throw new EliException("OOPS!!! I'm sorry, but I don't know what that means :-(");
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
        return banner + DIVIDER + "\nHello! I'm Eli.\n你需要什么帮助？\n" + DIVIDER;
    }

    /** Returns all currently stored tasks. */
    private String getTaskListResponse() {
        String taskLines = IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return taskLines.isEmpty()
                ? "Here are the tasks in your list:"
                : "Here are the tasks in your list:" + System.lineSeparator() + taskLines;
    }

    /** Returns tasks whose descriptions contain the keyword. */
    private String getFindResponse(String keyword) throws EliException {
        if (keyword.isEmpty()) {
            throw new EliException("OOPS!!! The keyword for find cannot be empty.");
        }

        String searchTerm = keyword.toLowerCase();
        String matches = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase().contains(searchTerm))
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        return matches.isEmpty() ? "No matching tasks found." : matches;
    }

    /** Adds a to-do task and returns a confirmation. */
    private String addTodo(String description) throws EliException {
        if (description.isEmpty()) {
            throw new EliException("OOPS!!! The description of a todo cannot be empty.");
        }
        return addTask(new Todo(description));
    }

    /** Adds a deadline task and returns a confirmation. */
    private String addDeadline(String command) throws EliException {
        int byIndex = command.indexOf(" /by ");
        if (byIndex == -1) {
            throw new EliException("OOPS!!! A deadline needs a /by value.");
        } else if (byIndex <= 9) {
            throw new EliException("OOPS!!! The description of a deadline cannot be empty.");
        }

        String description = command.substring(9, byIndex).trim();
        String by = command.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new EliException("OOPS!!! The description of a deadline cannot be empty.");
        } else if (by.isEmpty()) {
            throw new EliException("OOPS!!! A deadline needs a /by value.");
        }
        return addTask(new Deadline(description, by));
    }

    /** Adds an event task and returns a confirmation. */
    private String addEvent(String command) throws EliException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new EliException("OOPS!!! An event needs /from and /to values.");
        } else if (fromIndex <= 6) {
            throw new EliException("OOPS!!! The description of an event cannot be empty.");
        } else if (toIndex < fromIndex + 7) {
            throw new EliException("OOPS!!! An event needs /from and /to values.");
        }

        String description = command.substring(6, fromIndex).trim();
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new EliException("OOPS!!! The description of an event cannot be empty.");
        } else if (from.isEmpty() || to.isEmpty()) {
            throw new EliException("OOPS!!! An event needs /from and /to values.");
        }
        return addTask(new Event(description, from, to));
    }

    /** Adds and saves one task. */
    private String addTask(Task task) throws EliException {
        tasks.addTask(task);
        saveTasks();
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /** Marks or unmarks a task and returns a confirmation. */
    private String updateTaskStatus(String numberText, boolean isDone) throws EliException {
        int taskNumber = parseTaskNumber(numberText);
        if (!tasks.hasTaskNumber(taskNumber)) {
            throw new EliException("OOPS!!! We don't have a task with that number.");
        }

        Task task = tasks.getTask(taskNumber);
        if (isDone) {
            task.markAsDone();
            saveTasks();
            return "Nice! I've marked this task as done:\n  " + task;
        }
        task.markAsNotDone();
        saveTasks();
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /** Deletes a task and returns a confirmation. */
    private String deleteTask(String numberText) throws EliException {
        int taskNumber = parseTaskNumber(numberText);
        if (!tasks.hasTaskNumber(taskNumber)) {
            throw new EliException("OOPS!!! We don't have a task with that number.");
        }

        Task removedTask = tasks.removeTask(taskNumber);
        saveTasks();
        return "Noted. I've removed this task:\n  " + removedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
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
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            output.writeObject(tasks);
        } catch (IOException exception) {
            throw new EliException("OOPS!!! Could not save your tasks.");
        }
    }
}
