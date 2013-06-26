package com.workshare.micro.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicroConfigService {

    private static final Logger logger = LoggerFactory.getLogger(MicroConfigService.class);

    public static MicroConfig load() {
        final MicroConfig config = ConfigFactory.create(MicroConfig.class);
        if (config != null) {
            File propsfile = configFile();
            if (!propsfile.exists()) {
                createPropsFile(config);
            }
            
            logProperties(config);
        }
        
        return config;
    }

    // FIXME: this is temporary, waiting for a proper toString() method from Owner
    private static void logProperties(final MicroConfig config) {
        StringWriter sw = new StringWriter();
        config.list(new PrintWriter(sw));
        String props = sw.toString().replace('\n', ',').replace('\r', ' ');
        logger.debug("Config "+props);
    }

    private static void createPropsFile(MicroConfig config) {
        try {
            logger.debug("Creating config file {}", configFile());
            configRoot().mkdirs();
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(configFile())));
            try {
                config.list(writer);
            } finally {
                writer.close();
            }
        } catch (IOException ex) {
            logger.error("Unable to create config file "+configFile(), ex);
        }
         
    }

    public static File configFile() {
        return new File(configRoot(), MicroConfig.NAME);
    }

    public static File configRoot() {
        return new File(System.getProperty("user.home"), MicroConfig.PATH);
    }
}
