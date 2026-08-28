package duke;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline task.
     *
     * @param description the task description
     * @param by the deadline text entered by the user
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = formatDateTime(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }

    /** Formats common ISO date/time input while preserving natural-language input. */
    private static String formatDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"))
                    .format(DateTimeFormatter.ofPattern("MMM d yyyy, h:mma"));
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(value).format(DateTimeFormatter.ofPattern("MMM d yyyy"));
            } catch (DateTimeParseException ignoredDate) {
                return value;
            }
        }
    }
}