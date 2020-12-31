package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.miners.Miners;
import mn.foreman.bulkconfigure.model.MinerConfig;
import mn.foreman.model.Pool;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/** Applies a pool configuration if pools are provided on the miner config. */
public class PoolApplier
        extends AbstractApplier {

    /** The thread pool for running actions. */
    private final ExecutorService executor;

    /** The Foreman API. */
    private final ForemanApi foremanApi;

    /**
     * Constructor.
     *
     * @param miners     The miners.
     * @param foremanApi The API.
     * @param executor   The thread pool.
     */
    public PoolApplier(
            final List<Miners.Miner> miners,
            final ForemanApi foremanApi,
            final ExecutorService executor) {
        super(miners);
        this.foremanApi = foremanApi;
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Only enabled if a pool exists with a non-empty URL.</p>
     */
    @Override
    public boolean isEnabled(final MinerConfig config) {
        return config
                .getPools()
                .stream()
                .map(MinerConfig.Pool::getUrl)
                .anyMatch(url -> url != null && !url.trim().isEmpty());
    }

    @Override
    protected Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config) {
        return this.executor.submit(
                new RemoteCommand(
                        () -> PoolApplier.this.foremanApi.actions().changePools(
                                miner.id,
                                toPools(config.getPools())),
                        this.foremanApi));
    }

    /**
     * Converts the provided pool configuration to the pools to be sent to the
     * API.
     *
     * @param pools The pools.
     *
     * @return The new pools.
     */
    private static List<Pool> toPools(final List<MinerConfig.Pool> pools) {
        return pools
                .stream()
                .map(pool ->
                        Pool
                                .builder()
                                .url(pool.getUrl())
                                .username(pool.getUser())
                                .password(pool.getPass())
                                .build())
                .collect(Collectors.toList());
    }
}