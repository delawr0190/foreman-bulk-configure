package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.endpoints.miners.Miners;
import mn.foreman.bulkconfigure.model.MinerConfig;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.List;
import java.util.concurrent.Future;

/** Applies tags to the provided miner. */
public class TagApplier
        extends AbstractApplier {

    /** The Foreman API. */
    private final ForemanApi foremanApi;

    /**
     * Constructor.
     *
     * @param miners     The miners.
     * @param foremanApi The API.
     */
    public TagApplier(
            final List<Miners.Miner> miners,
            final ForemanApi foremanApi) {
        super(miners);
        this.foremanApi = foremanApi;
    }

    @Override
    public boolean isEnabled(final MinerConfig config) {
        return !config.getTags().isEmpty();
    }

    @Override
    protected Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config) {
        return ConcurrentUtils.constantFuture(
                this.foremanApi.tags().tag(
                        miner.id,
                        config.getTags()));
    }
}