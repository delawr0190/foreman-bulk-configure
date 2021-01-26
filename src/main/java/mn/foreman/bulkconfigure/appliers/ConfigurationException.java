package mn.foreman.bulkconfigure.appliers;

/** An {@link Exception} that represents a mis-configuration. */
public class ConfigurationException
        extends Exception {

    /** Constructor. */
    public ConfigurationException() {
        // Do nothing
    }

    /**
     * Constructor.
     *
     * @param message The message.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message The message.
     * @param cause   The cause.
     */
    public ConfigurationException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause The cause.
     */
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }
}
