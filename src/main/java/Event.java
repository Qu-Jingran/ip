/**
 * Represents a task that has a start time and an end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task.
     *
     * @param description the event description
     * @param from the start time text entered by the user
     * @param to the end time text entered by the user
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = formatDateTime(from);
        this.to = formatDateTime(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** Formats common ISO date/time input while preserving natural-language input. */
    private static String formatDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"))
                    .format(DateTimeFormatter.ofPattern("MMM d yyyy, h:mma"));
        } catch (DateTimeParseException ignored) {
            return value;
        }
    }
}
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
