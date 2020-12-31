package mn.foreman.bulkconfigure.config;

import mn.foreman.api.ForemanApi;
import mn.foreman.api.ForemanApiImpl;
import mn.foreman.api.JdkWebUtil;
import mn.foreman.api.WebUtil;
import mn.foreman.api.miners.Miners;
import mn.foreman.bulkconfigure.appliers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A configuration for Foreman-specific beans. */
@Configuration
public class ConfigurationForeman {

    /**
     * Creates the appliers.
     *
     * @param miners          The miners.
     * @param foremanApi      The API.
     * @param executorService The thread pool for running commands.
     *
     * @return The appliers.
     */
    @Bean
    public List<Applier> appliers(
            final List<Miners.Miner> miners,
            final ForemanApi foremanApi,
            final ExecutorService executorService) {
        return Arrays.asList(
                new NameApplier(
                        miners,
                        foremanApi),
                new SerialApplier(
                        miners,
                        foremanApi),
                new NetworkApplier(
                        miners,
                        foremanApi,
                        executorService),
                new PoolApplier(
                        miners,
                        foremanApi,
                        executorService),
                new SiteMapApplier(
                        miners,
                        foremanApi));
    }

    /**
     * Creates the thread pool for performing remote commands.
     *
     * @return The thread pool.
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Creates a new {@link ForemanApi}.
     *
     * @param clientId     The client ID.
     * @param pickaxeId    The pickaxe ID.
     * @param objectMapper The mapper.
     * @param webUtil      The web util for API operations.
     *
     * @return The new {@link ForemanApi}.
     */
    @Bean
    public ForemanApi foremanApi(
            @Value("${client.id}") final String clientId,
            @Value("${client.pickaxe}") final String pickaxeId,
            final ObjectMapper objectMapper,
            final WebUtil webUtil) {
        return new ForemanApiImpl(
                clientId,
                pickaxeId,
                objectMapper,
                webUtil);
    }

    /**
     * Returns the miners in Foreman.
     *
     * @param foremanApi The API.
     *
     * @return The miners.
     */
    @Bean
    public List<Miners.Miner> miners(final ForemanApi foremanApi) {
        return foremanApi.miners().all();
    }

    /**
     * Creates the mapper for reading/writing json.
     *
     * @return The mapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Creates a new {@link WebUtil} for performing API operations.
     *
     * @param foremanUrl The base URL.
     * @param apiKey     The API key.
     *
     * @return The new {@link WebUtil}.
     */
    @Bean
    public WebUtil webUtil(
            @Value("${foreman.url}") final String foremanUrl,
            @Value("${user.apiKey}") final String apiKey) {
        return new JdkWebUtil(
                foremanUrl,
                apiKey);
    }
}
