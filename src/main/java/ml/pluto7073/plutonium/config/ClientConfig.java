package ml.pluto7073.plutonium.config;

import org.apache.logging.log4j.Logger;

public class ClientConfig extends AbstractConfig {

    public ClientConfig(String modid, Logger logger) {
        super(modid, "client", logger);
    }

}
