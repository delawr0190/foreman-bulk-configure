package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.miners.Miners;
import mn.foreman.bulkconfigure.model.MinerConfig;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.List;
import java.util.concurrent.Future;

/** Applies a miner name change if one is provided. */
public class NameApplier
        extends AbstractApplier {

    /** The Foreman API. */
    private final ForemanApi foremanApi;

    /**
     * Constructor.
     *
     * @param miners     The miners.
     * @param foremanApi The API.
     */
    public NameApplier(
            final List<Miners.Miner> miners,
            final ForemanApi foremanApi) {
        super(miners);
        this.foremanApi = foremanApi;
    }

    @Override
    public boolean isEnabled(final MinerConfig config) {
        final String name = config.getName();
        return name != null && !name.trim().isEmpty();
    }

    @Override
    protected Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config) {
        return ConcurrentUtils.constantFuture(
                this.foremanApi.miners().update(
                        miner.id,
                        config.getName(),
                        null).isPresent());
    }
}
