import java.util.Scanner;

/** Runs the Eli task-list application. */
public class Eli {
    private static final String DIVIDER = "____________________________________________________________";

    /** Reads commands and manages the user's task list. */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        printWelcome();

        while (input.hasNextLine()) {
            String command = input.nextLine().trim();

            if (command.equals("bye") || command.equals("再见")) {
                break;
            } else if (command.equals("list")) {
                printList(tasks, taskCount);
            } else if (command.equals("todo")) {
                printError("OOPS!!! The description of a todo cannot be empty.");
            } else if (command.startsWith("todo ")) {
                String description = command.substring(5).trim();
                if (description.isEmpty()) {
                    printError("OOPS!!! The description of a todo cannot be empty.");
                } else {
                    tasks[taskCount] = new Todo(description);
                    printAddedTask(tasks[taskCount], ++taskCount);
                }
            } else if (command.equals("deadline")) {
                printError("OOPS!!! The description of a deadline cannot be empty.");
            } else if (command.startsWith("deadline ")) {
                int byIndex = command.indexOf(" /by ");
                if (byIndex == -1) {
                    printError("OOPS!!! A deadline needs a /by value.");
                } else {
                    String description = command.substring(9, byIndex).trim();
                    String by = command.substring(byIndex + 5).trim();
                    if (description.isEmpty()) {
                        printError("OOPS!!! The description of a deadline cannot be empty.");
                    } else if (by.isEmpty()) {
                        printError("OOPS!!! A deadline needs a /by value.");
                    } else {
                        tasks[taskCount] = new Deadline(description, by);
                        printAddedTask(tasks[taskCount], ++taskCount);
                    }
                }
            } else if (command.equals("event")) {
                printError("OOPS!!! The description of an event cannot be empty.");
            } else if (command.startsWith("event ")) {
                int fromIndex = command.indexOf(" /from ");
                int toIndex = command.indexOf(" /to ");
                if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                    printError("OOPS!!! An event needs /from and /to values.");
                } else {
                    String description = command.substring(6, fromIndex).trim();
                    String from = command.substring(fromIndex + 7, toIndex).trim();
                    String to = command.substring(toIndex + 5).trim();
                    if (description.isEmpty()) {
                        printError("OOPS!!! The description of an event cannot be empty.");
                    } else if (from.isEmpty() || to.isEmpty()) {
                        printError("OOPS!!! An event needs /from and /to values.");
                    } else {
                        tasks[taskCount] = new Event(description, from, to);
                        printAddedTask(tasks[taskCount], ++taskCount);
                    }
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = parseTaskNumber(command.substring(5));
                if (isValidTaskNumber(taskNumber, taskCount)) {
                    tasks[taskNumber - 1].markAsDone();
                    printTaskStatus("Nice! I've marked this task as done:", tasks[taskNumber - 1]);
                } else {
                    printTaskNumberError();
                }
            } else if (command.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(command.substring(7));
                if (isValidTaskNumber(taskNumber, taskCount)) {
                    tasks[taskNumber - 1].markAsNotDone();
                    printTaskStatus("OK, I've marked this task as not done yet:", tasks[taskNumber - 1]);
                } else {
                    printTaskNumberError();
                }
            } else {
                printError("OOPS!!! I'm sorry, but I don't know what that means :-(");
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
    private static void printList(Task[] tasks, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
        System.out.println(DIVIDER);
    }

    /** Prints a confirmation after a task is added. */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
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

    /** Prints an error for an invalid task number. */
    private static void printTaskNumberError() {
        System.out.println(DIVIDER);
        System.out.println("Sorry, we don't have a task with that number.");
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
}
