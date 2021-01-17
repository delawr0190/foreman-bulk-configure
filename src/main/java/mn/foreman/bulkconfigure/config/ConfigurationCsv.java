package mn.foreman.bulkconfigure.config;

import mn.foreman.bulkconfigure.model.MinerConfig;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/** CSV parsing-related configuration. */
@Configuration
public class ConfigurationCsv {

    /**
     * Loads the CSV file and parses {@link MinerConfig configs}.
     *
     * @param confFile The conf file.
     *
     * @return The configs.
     *
     * @throws IOException on failure.
     */
    @Bean
    public List<MinerConfig> csv(@Value("${conf.file}") final String confFile)
            throws IOException {
        final List<List<String>> records = new LinkedList<>();
        try (final FileReader fileReader =
                     new FileReader(confFile);
             final CSVReader csvReader =
                     new CSVReaderBuilder(fileReader)
                             .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                             // Skip the header
                             .withSkipLines(1)
                             .build()) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        }

        final List<MinerConfig> configs =
                toConfigs(records);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("CSV was empty");
        }

        return configs;
    }

    /**
     * Converts the provided raw CSV row to a {@link MinerConfig}.
     *
     * @param values The CSV values.
     *
     * @return The configuration.
     */
    private static MinerConfig toConfig(final List<String> values) {
        int position = 0;
        return MinerConfig
                .builder()
                .mac(values.get(position++))
                .serial(values.get(position++))
                .location(
                        MinerConfig.Location
                                .builder()
                                .rack(values.get(position++))
                                .row(toInt(values.get(position++)))
                                .index(toInt(values.get(position++)))
                                .build())
                .name(values.get(position++))
                .staticIp(
                        MinerConfig.StaticIp
                                .builder()
                                .ip(values.get(position++))
                                .netmask(values.get(position++))
                                .gateway(values.get(position++))
                                .dns(values.get(position++))
                                .hostname(values.get(position++))
                                .build())
                .pools(
                        Arrays.asList(
                                MinerConfig.Pool
                                        .builder()
                                        .url(values.get(position++))
                                        .user(values.get(position++))
                                        .pass(values.get(position++))
                                        .build(),
                                MinerConfig.Pool
                                        .builder()
                                        .url(values.get(position++))
                                        .user(values.get(position++))
                                        .pass(values.get(position++))
                                        .build(),
                                MinerConfig.Pool
                                        .builder()
                                        .url(values.get(position++))
                                        .user(values.get(position++))
                                        .pass(values.get(position++))
                                        .build()))
                .tags(
                        values.get(position) != null
                                ? Arrays.asList(values.get(position).split(","))
                                : Collections.emptyList())
                .build();
    }

    /**
     * Converts the provided raw CSV rows to {@link MinerConfig configs}.
     *
     * @param csv The CSV values.
     *
     * @return The configurations.
     */
    private static List<MinerConfig> toConfigs(final List<List<String>> csv) {
        return csv
                .stream()
                .map(ConfigurationCsv::toConfig)
                .collect(Collectors.toList());
    }

    /**
     * Safely reads an integer.
     *
     * @param value The source.
     *
     * @return The value.
     */
    private static Integer toInt(final String value) {
        return value != null ? Integer.parseInt(value) : 0;
    }
}
