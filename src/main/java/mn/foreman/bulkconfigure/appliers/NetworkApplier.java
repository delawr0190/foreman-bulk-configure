package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.miners.Miners;
import mn.foreman.bulkconfigure.model.MinerConfig;
import mn.foreman.model.Network;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Applies a network configuration if a static IP is provided on the miner
 * config.
 */
public class NetworkApplier
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
    public NetworkApplier(
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
     * <p>Only enabled if a static IP is present.</p>
     */
    @Override
    public boolean isEnabled(final MinerConfig config) {
        final String ip = config.getStaticIp().getIp();
        return ip != null && !ip.trim().isEmpty();
    }

    @Override
    protected Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config) {
        return this.executor.submit(
                new RemoteCommand(
                        () -> NetworkApplier.this.foremanApi.actions().changeNetwork(
                                miner.id,
                                toNetwork(config.getStaticIp())),
                        this.foremanApi));
    }

    /**
     * Converts the provided {@link MinerConfig.StaticIp} to a {@link Network}.
     *
     * @param staticIp The {@link MinerConfig.StaticIp}.
     *
     * @return The new {@link mn.foreman.model.Network}.
     */
    private static Network toNetwork(final MinerConfig.StaticIp staticIp) {
        return Network
                .builder()
                .dns(staticIp.getDns())
                .gateway(staticIp.getGateway())
                .hostname(staticIp.getHostname())
                .ipAddress(staticIp.getIp())
                .netmask(staticIp.getNetmask())
                .build();
    }
}
