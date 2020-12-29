package mn.foreman.bulkconfigure.appliers;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.actions.Actions;
import mn.foreman.api.actions.StatusRunning;
import mn.foreman.api.actions.StatusSubmit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A {@link RemoteCommand} provides a {@link Callable} implementation that will
 * wait until a remote command has been executed against a miner and has
 * completed fully.
 */
public class RemoteCommand
        implements Callable<Boolean> {

    /** How long to wait for a command to timeout. */
    private static final int COMMAND_TIMEOUT = 15;

    /** How long to wait for a command to timeout (units). */
    private static final TimeUnit COMMAND_TIMEOUT_UNITS = TimeUnit.MINUTES;

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(RemoteCommand.class);

    /** The action to run. */
    private final Supplier<Optional<Actions.Response>> apiAction;

    /** The API handler. */
    private final ForemanApi foremanApi;

    /**
     * Constructor.
     *
     * @param apiAction  The action to run.
     * @param foremanApi The API handler.
     */
    public RemoteCommand(
            final Supplier<Optional<Actions.Response>> apiAction,
            final ForemanApi foremanApi) {
        this.apiAction = apiAction;
        this.foremanApi = foremanApi;
    }

    @Override
    public Boolean call() {
        boolean result = false;

        final Optional<Actions.Response> responseOpt =
                this.apiAction.get();
        if (responseOpt.isPresent()) {
            final Actions.Response response = responseOpt.get();
            if (response.status == StatusSubmit.OKAY) {
                // Command is accepted - start checking the status
                result = checkResult(response);
            } else {
                LOG.warn("The command was rejected");
            }
        } else {
            LOG.warn("Failed to obtain response for action");
        }

        return result;
    }

    /**
     * Continuously checks to see if the command was successful, waiting up to
     * {@link #COMMAND_TIMEOUT} {@link #COMMAND_TIMEOUT_UNITS} before aborting.
     *
     * @param response The response.
     *
     * @return The final result.
     */
    private boolean checkResult(final Actions.Response response) {
        boolean wasSuccess = false;

        long now = System.currentTimeMillis();
        final long deadline =
                now + COMMAND_TIMEOUT_UNITS.toMillis(COMMAND_TIMEOUT);

        while (now < deadline) {
            final StatusRunning statusRunning =
                    this.foremanApi
                            .actions()
                            .status(response.command)
                            .orElse(StatusRunning.IN_PROGRESS);
            LOG.info("Command is {}", statusRunning);
            if (statusRunning.isDone()) {
                wasSuccess = statusRunning.isSuccess();
                break;
            }

            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (final InterruptedException ie) {
                // Ignore
            }
            now = System.currentTimeMillis();
        }

        return wasSuccess;
    }
}
