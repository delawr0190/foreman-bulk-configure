package mn.foreman.bulkconfigure.appliers;

import mn.foreman.bulkconfigure.model.MinerConfig;

import java.util.concurrent.Future;

public interface Applier {

    /**
     * Applies the provided configuration.
     *
     * @param config The configuration.
     *
     * @return A future indicating success.
     */
    Future<Boolean> configure(MinerConfig config);

    /**
     * Returns whether or not the process should be executed.
     *
     * @param config The configuration.
     *
     * @return Whether or not the process should be executed.
     */
    boolean isEnabled(MinerConfig config);
}
