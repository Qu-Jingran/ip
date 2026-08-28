package Eli;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;
/** Runs the Eli task-list application. */
public class Eli {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String DATA_FILE = "duke.txt";

    /** Reads commands and manages the user's task list. */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        TaskList tasks = new TaskList();

        loadTasks(tasks);

        printWelcome();

        while (input.hasNextLine()) {
            String command = input.nextLine().trim();

            try {
                if (command.equals("bye") || command.equals("再见")) {
                    break;
                } else if (command.equals("list")) {
                    printList(tasks);
                } else if (command.equals("find")) {
                    throw new EliException("OOPS!!! The keyword for find cannot be empty.");
                } else if (command.startsWith("find ")) {
                    findTasks(tasks, command.substring(5).trim());
                } else if (command.equals("todo")) {
                    throw new EliException("OOPS!!! The description of a todo cannot be empty.");
            } else if (command.startsWith("todo ")) {
                String description = command.substring(5).trim();
                if (description.isEmpty()) {
                    throw new EliException("OOPS!!! The description of a todo cannot be empty.");
                } else {
                    Task task = new Todo(description);
                    tasks.addTask(task);
                    saveTasks(tasks);
                    printAddedTask(task, tasks.size());
                }
            } else if (command.equals("deadline")) {
                throw new EliException("OOPS!!! The description of a deadline cannot be empty.");
            } else if (command.startsWith("deadline ")) {
                int byIndex = command.indexOf(" /by ");
                if (byIndex == -1) {
                    throw new EliException("OOPS!!! A deadline needs a /by value.");
                } else if (byIndex <= 9) {
                    throw new EliException("OOPS!!! The description of a deadline cannot be empty.");
                } else {
                    String description = command.substring(9, byIndex).trim();
                    String by = command.substring(byIndex + 5).trim();
                    if (description.isEmpty()) {
                        throw new EliException("OOPS!!! The description of a deadline cannot be empty.");
                    } else if (by.isEmpty()) {
                        throw new EliException("OOPS!!! A deadline needs a /by value.");
                    } else {
                        Task task = new Deadline(description, by);
                        tasks.addTask(task);
                        saveTasks(tasks);
                        printAddedTask(task, tasks.size());
                    }
                }
            } else if (command.equals("event")) {
                throw new EliException("OOPS!!! The description of an event cannot be empty.");
            } else if (command.startsWith("event ")) {
                int fromIndex = command.indexOf(" /from ");
                int toIndex = command.indexOf(" /to ");
                if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                    throw new EliException("OOPS!!! An event needs /from and /to values.");
                } else if (fromIndex <= 6) {
                    throw new EliException("OOPS!!! The description of an event cannot be empty.");
                } else if (toIndex < fromIndex + 7) {
                    throw new EliException("OOPS!!! An event needs /from and /to values.");
                } else {
                    String description = command.substring(6, fromIndex).trim();
                    String from = command.substring(fromIndex + 7, toIndex).trim();
                    String to = command.substring(toIndex + 5).trim();
                    if (description.isEmpty()) {
                        throw new EliException("OOPS!!! The description of an event cannot be empty.");
                    } else if (from.isEmpty() || to.isEmpty()) {
                        throw new EliException("OOPS!!! An event needs /from and /to values.");
                    } else {
                        Task task = new Event(description, from, to);
                        tasks.addTask(task);
                        saveTasks(tasks);
                        printAddedTask(task, tasks.size());
                    }
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = parseTaskNumber(command.substring(5));
                if (isValidTaskNumber(taskNumber, tasks.size())) {
                    tasks.getTask(taskNumber).markAsDone();
                    saveTasks(tasks);
                    printTaskStatus("Nice! I've marked this task as done:", tasks.getTask(taskNumber));
                } else {
                    throw new EliException("OOPS!!! We don't have a task with that number.");
                }
            } else if (command.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(command.substring(7));
                if (isValidTaskNumber(taskNumber, tasks.size())) {
                    tasks.getTask(taskNumber).markAsNotDone();
                    saveTasks(tasks);
                    printTaskStatus("OK, I've marked this task as not done yet:", tasks.getTask(taskNumber));
                } else {
                    throw new EliException("OOPS!!! We don't have a task with that number.");
                }
            } else if (command.startsWith("delete ")) {
                int taskNumber = parseTaskNumber(command.substring(7));
                if (isValidTaskNumber(taskNumber, tasks.size())) {
                    Task removedTask = tasks.removeTask(taskNumber);
                    saveTasks(tasks);
                    printDeletedTask(removedTask, tasks.size());
                } else {
                    throw new EliException("OOPS!!! We don't have a task with that number.");
                }
            } else {
                throw new EliException("OOPS!!! I'm sorry, but I don't know what that means :-(");
            }
            } catch (EliException exception) {
                printError(exception.getMessage());
            }
        }

        System.out.println("Bye. 记得来找我\n" + DIVIDER);
    }

    /** Prints the application's welcome message. */
    private static void printWelcome() {
        String banner = " _______     _           _____ \n"
                + "|  _____|   | |         |_   _|\n"
                + "| |___      | |           | |  \n"
                + "|  ___|     | |           | |  \n"
                + "| |_____    | |_____     _| |_ \n"
                + "|_______|   |_______|   |_____|\n";
        System.out.println(banner);
        System.out.println(DIVIDER + "\nHello! I'm Eli.\n你需要什么帮助？\n" + DIVIDER);
    }

    /** Prints all tasks in the list. */
    private static void printList(TaskList tasks) {
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }

    /** Prints tasks whose descriptions contain the given keyword. */
    private static void findTasks(TaskList tasks, String keyword) {
        if (keyword.isEmpty()) {
            printError("OOPS!!! The keyword for find cannot be empty.");
            return;
        }
        String searchTerm = keyword.toLowerCase();
        int matches = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(searchTerm)) {
                System.out.println((i + 1) + "." + tasks.get(i));
                matches++;
            }
        }
        if (matches == 0) {
            System.out.println("No matching tasks found.");
        }
    }

    /** Prints a confirmation after a task is added. */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /** Prints a confirmation after a task is deleted. */
    private static void printDeletedTask(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /** Prints a confirmation after a task's status changes. */
    private static void printTaskStatus(String message, Task task) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println("  " + task);
        System.out.println(DIVIDER);
    }

    /** Prints an error message in Eli's standard format. */
    private static void printError(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /** Converts task-number text to a number, or returns -1 when it is invalid. */
    private static int parseTaskNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /** Returns whether a task number refers to an existing task. */
    private static boolean isValidTaskNumber(int taskNumber, int taskCount) {
        return taskNumber >= 1 && taskNumber <= taskCount;
    }

    /** Loads saved tasks, if a save file exists. */
    @SuppressWarnings("unchecked")
    private static void loadTasks(TaskList tasks) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            tasks.addAll((TaskList) input.readObject());
        } catch (IOException | ClassNotFoundException exception) {
            // A missing or unreadable file is treated as an empty task list.
        }
    }

    /** Saves the current task list to disk. */
    private static void saveTasks(TaskList tasks) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            output.writeObject(tasks);
        } catch (IOException exception) {
            printError("OOPS!!! Could not save your tasks.");
        }
    }
}
