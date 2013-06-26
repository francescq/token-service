package com.workshare.micro.config;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:~/" + MicroConfig.PATH + MicroConfig.NAME })
public interface MicroConfig extends Config {

    public static final String PATH = ".workshare/api-tokens/";
    public static final String NAME = "config.properties";

    @Key("jdbc.drv") @DefaultValue("org.h2.Driver")
    String jdbcDriver();

    @Key("jdbc.url") @DefaultValue("jdbc:h2:mem:test")
    String jdbcUrl();

    @Key("jdbc.user") @DefaultValue("sa")
    String jdbcUser();

    @Key("jdbc.pass") @DefaultValue("")
    String jdbcPass();

    void list(PrintStream out);

    void list(PrintWriter out);
}
