package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.endpoints.StatusSubmit;
import mn.foreman.api.endpoints.miners.Miners;
import mn.foreman.api.endpoints.sitemap.SiteMap;
import mn.foreman.bulkconfigure.model.MinerConfig;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/** Applies a miner site map change if a rack is provided. */
public class SiteMapApplier
        extends AbstractApplier {

    /** The Foreman API. */
    private final ForemanApi foremanApi;

    /**
     * Constructor.
     *
     * @param miners     The miners.
     * @param foremanApi The API.
     */
    public SiteMapApplier(
            final List<Miners.Miner> miners,
            final ForemanApi foremanApi) {
        super(miners);
        this.foremanApi = foremanApi;
    }

    @Override
    public boolean isEnabled(final MinerConfig config) {
        final String rack = config.getLocation().getRack();
        return rack != null && !rack.trim().isEmpty();
    }

    @Override
    protected Future<Boolean> runConfigure(
            final Miners.Miner miner,
            final MinerConfig config) {
        final MinerConfig.Location location = config.getLocation();
        final Optional<SiteMap.Response> responseOpt =
                this.foremanApi.siteMap().setLocation(
                        miner.id,
                        location.getRack(),
                        location.getRow(),
                        location.getIndex());
        if (responseOpt.isPresent()) {
            final SiteMap.Response response =
                    responseOpt.get();
            return ConcurrentUtils.constantFuture(
                    response.status == StatusSubmit.OKAY);
        }
        return ConcurrentUtils.constantFuture(false);
    }
}
