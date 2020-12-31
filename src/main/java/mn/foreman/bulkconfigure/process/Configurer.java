package mn.foreman.bulkconfigure.process;

import mn.foreman.bulkconfigure.appliers.Applier;
import mn.foreman.bulkconfigure.model.MinerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Configurer} provides the primary application behavior to mass
 * configure ASICs via the Foreman API.
 */
@Component
public class Configurer {

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(Configurer.class);

    /** The appliers. */
    private final List<Applier> appliers;

    /** The miner configurations to load. */
    private final List<MinerConfig> configs;

    /** The thread pool. */
    private final ExecutorService executor;

    /**
     * Constructor.
     *
     * @param configs  The configurations.
     * @param appliers All of the appliers.
     * @param executor The thread pool.
     */
    @Autowired
    public Configurer(
            final List<MinerConfig> configs,
            final List<Applier> appliers,
            final ExecutorService executor) {
        this.configs = new ArrayList<>(configs);
        this.appliers = new ArrayList<>(appliers);
        this.executor = executor;
    }

    /** Runs the configuration process. */
    @EventListener(ApplicationStartedEvent.class)
    public void configure() {
        this.configs
                .stream()
                .map(ConfigurationRunnable::new)
                .forEach(this.executor::submit);
    }

    /** A {@link Runnable} for performing a configuration. */
    private class ConfigurationRunnable
            implements Runnable {

        /** The configuration. */
        private final MinerConfig minerConfig;

        /**
         * Constructor.
         *
         * @param minerConfig The config.
         */
        ConfigurationRunnable(final MinerConfig minerConfig) {
            this.minerConfig = minerConfig;
        }

        @Override
        public void run() {
            final UUID uuid = UUID.randomUUID();
            LOG.info("{}: {} will be applied as part of this task",
                    uuid,
                    this.minerConfig);

            for (final Applier applier : Configurer.this.appliers) {
                if (applier.isEnabled(this.minerConfig)) {
                    LOG.info("{}: {} is enabled - will be applied",
                            uuid,
                            applier.getName());
                    try {
                        Boolean result = false;
                        final Future<Boolean> resultFuture =
                                applier.configure(this.minerConfig);
                        LOG.info("{}: application was successful - waiting", uuid);

                        try {
                            result = resultFuture.get(15, TimeUnit.MINUTES);
                        } catch (final Exception e) {
                            LOG.warn("{}: exception occurred while configuring", uuid, e);
                        }

                        if (resultFuture.isDone() && result) {
                            LOG.info("{}: finished successfully", uuid);
                        } else {
                            resultFuture.cancel(true);
                            LOG.warn("{}: failed to apply {} - terminating for this miner",
                                    uuid,
                                    applier.getName());
                            break;
                        }
                    } catch (final Exception e) {
                        LOG.warn("{}: failed to configure - terminating for this miner",
                                uuid,
                                e);
                        break;
                    }
                } else {
                    LOG.info("{}: {} is disabled - skipping",
                            uuid,
                            applier.getName());
                }
            }
        }
    }
}