package mn.foreman.bulkconfigure.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** A miner's new configuration. */
@Data
@Builder
public class MinerConfig {

    /** Site map configuration. */
    private final Location location;

    /** The mac. */
    private final String mac;

    /** The name. */
    private final String name;

    /** The pools. */
    private final List<Pool> pools;

    /** The static IP configuration. */
    private final StaticIp staticIp;

    /** Site map configuration. */
    @Data
    @Builder
    public static class Location {

        /** The index. */
        private final int index;

        /** The rack. */
        private final String rack;

        /** The row. */
        private final int row;
    }

    /** The pool configuration. */
    @Data
    @Builder
    public static class Pool {

        /** The stratum pass. */
        private final String pass;

        /** The stratum url. */
        private final String url;

        /** The stratum user. */
        private final String user;
    }

    /** Static IP configuration. */
    @Data
    @Builder
    public static class StaticIp {

        /** The DNS. */
        private final String dns;

        /** The gateway. */
        private final String gateway;

        /** The hostname. */
        private final String hostname;

        /** The ip. */
        private final String ip;

        /** The netmask. */
        private final String netmask;
    }
}
