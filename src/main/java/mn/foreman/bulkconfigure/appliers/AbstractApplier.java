package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.endpoints.miners.Miners;
import mn.foreman.bulkconfigure.model.MinerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An {@link AbstractApplier} applies a configuration change to a miner, but
 * only after pairing the provided {@link MinerConfig} with the miner present in
 * Foreman.
 */
public abstract class AbstractApplier
        implements Applier {

    /** All of the miners in Foreman. */
    private final List<Miners.Miner> miners;

    /**
     * Constructor.
     *
     * @param miners All of the miners in Foreman.
     */
    protected AbstractApplier(final List<Miners.Miner> miners) {
        this.miners = new ArrayList<>(miners);
    }

    @Override
    public Future<Boolean> configure(final MinerConfig config) {
        return runConfigure(
                this.miners
                        .stream()
                        .filter(miner -> config.getMac().equalsIgnoreCase(miner.mac))
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Miner not found by MAC address")),
                config);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Runs the configuration.
     *
     * @param miner  The miner.
     * @param config The configuration.
     *
     * @return A future indicating whether or not the configuration was
     *         successful.
     */
    protected abstract Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config);
}
