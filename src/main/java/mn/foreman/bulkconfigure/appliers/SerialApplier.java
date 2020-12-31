package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.miners.Miners;
import mn.foreman.bulkconfigure.model.MinerConfig;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.List;
import java.util.concurrent.Future;

/** Applies a miner serial change if one is provided. */
public class SerialApplier
        extends AbstractApplier {

    /** The Foreman API. */
    private final ForemanApi foremanApi;

    /**
     * Constructor.
     *
     * @param miners     The miners.
     * @param foremanApi The API.
     */
    public SerialApplier(
            final List<Miners.Miner> miners,
            final ForemanApi foremanApi) {
        super(miners);
        this.foremanApi = foremanApi;
    }

    @Override
    public boolean isEnabled(final MinerConfig config) {
        final String serial = config.getSerial();
        return serial != null && !serial.trim().isEmpty();
    }

    @Override
    protected Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config) {
        return ConcurrentUtils.constantFuture(
                this.foremanApi.miners().update(
                        miner.id,
                        config.getName(),
                        null,
                        miner.platform,
                        miner.type,
                        config.getSerial()).isPresent());
    }
}
