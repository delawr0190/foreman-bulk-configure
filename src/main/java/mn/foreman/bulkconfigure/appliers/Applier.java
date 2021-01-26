package mn.foreman.bulkconfigure.appliers;

import mn.foreman.bulkconfigure.model.MinerConfig;

import java.util.concurrent.Future;

/**
 * An {@link Applier} provides a mechanism for applying changes to miners on an
 * as-configured basis.
 */
public interface Applier {

    /**
     * Applies the provided configuration.
     *
     * @param config The configuration.
     *
     * @return A future indicating success.
     *
     * @throws ConfigurationException on failure.
     */
    Future<Boolean> configure(MinerConfig config)
            throws ConfigurationException;

    /**
     * Returns the name.
     *
     * @return The name.
     */
    String getName();

    /**
     * Returns whether or not the process should be executed.
     *
     * @param config The configuration.
     *
     * @return Whether or not the process should be executed.
     */
    boolean isEnabled(MinerConfig config);
}
