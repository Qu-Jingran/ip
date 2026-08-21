/**
 * Lists the supported kinds of tasks and their display icons.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the icon shown in the task list.
     *
     * @param icon the one-letter task-type icon
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon displayed before the task status.
     *
     * @return the one-letter task-type icon
     */
    public String getIcon() {
        return icon;
    }
}
