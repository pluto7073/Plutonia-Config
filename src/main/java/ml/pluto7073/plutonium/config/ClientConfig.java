package ml.pluto7073.plutonium.config;

import org.apache.logging.log4j.Logger;

public class ClientConfig extends AbstractConfig {

    private final boolean managed;

    public ClientConfig(String modid, Logger logger) {
        this(modid, logger, false);
    }

    public ClientConfig(String modid, Logger logger, boolean managed) {
        super(modid, "client", logger);
        this.managed = managed;
    }

    public boolean isManaged() {
        return managed;
    }
}
