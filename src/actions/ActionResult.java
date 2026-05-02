package actions;

/**
 * Value object returned by {@link PlayerAction#execute()}.
 *
 * <p>Three states:
 * <ul>
 *   <li>{@code success} — action fully complete; turn may advance.</li>
 *   <li>{@code pending} — first phase done, waiting for next input; turn does NOT advance.</li>
 *   <li>{@code failure} — action rejected; turn does NOT advance.</li>
 * </ul>
 */
public final class ActionResult {

    private enum Status { SUCCESS, PENDING, FAILURE }

    private final Status status;
    private final String message;

    private ActionResult(Status status, String message) {
        this.status  = status;
        this.message = message;
    }

    /** @return a result indicating the action completed fully */
    public static ActionResult success(String message) {
        return new ActionResult(Status.SUCCESS, message);
    }

    /**
     * @return a result indicating phase 1 of a multi-phase action completed;
     *         the turn does NOT advance until a success result is returned
     */
    public static ActionResult pending(String message) {
        return new ActionResult(Status.PENDING, message);
    }

    /** @return a result indicating the action was rejected */
    public static ActionResult failure(String reason) {
        return new ActionResult(Status.FAILURE, reason);
    }

    /** @return true only for a fully completed action */
    public boolean isSuccess() { return status == Status.SUCCESS; }

    /** @return true when the action is mid-execution and awaiting further input */
    public boolean isPending() { return status == Status.PENDING; }

    /** @return status message for display in the UI */
    public String getMessage() { return message; }
}
