/**
 * Represents a task that can be marked as done or not done.
 * Specific task types provide their own type icon and extra details.
 */
public abstract class Task {
    private final String description;
    private final TaskType taskType;
    private boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description
     * @param taskType the type of this task
     */
    protected Task(String description, TaskType taskType) {
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the icon that shows whether this task is done.
     *
     * @return {@code X} for a done task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
