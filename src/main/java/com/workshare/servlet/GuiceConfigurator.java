package com.workshare.servlet;

import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.googlecode.flyway.core.Flyway;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.wordnik.swagger.jaxrs.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.ApiListingResourceXML;
import com.workshare.micro.api.tokens.CachingTokensDao;
import com.workshare.micro.api.tokens.TokensApi;
import com.workshare.micro.api.tokens.persistence.dao.TokensDao;
import com.workshare.micro.config.MicroConfig;
import com.workshare.micro.config.MicroConfigService;
import com.workshare.micro.utils.UUIDGenerator;

public class GuiceConfigurator extends GuiceServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(GuiceConfigurator.class);

    private static final String JERSEY_API_JSON_POJO_MAPPING_FEATURE = "com.sun.jersey.api.json.POJOMappingFeature";
    private static final String JERSEY_CONFIG_PROPERTY_PACKAGES = "com.sun.jersey.config.property.packages";

    @Override
    protected Injector getInjector() {

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(JERSEY_CONFIG_PROPERTY_PACKAGES, "com.wordnik.swagger.jaxrs.listing;com.workshare.micro.api.tokens;com.workshare.micro.api");
        params.put(JERSEY_API_JSON_POJO_MAPPING_FEATURE, "true");

        final MicroConfig config = MicroConfigService.load();

        return Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {

                // bind generic objects
                bind(UUIDGenerator.class).asEagerSingleton();
                bind(ApiListingResourceJSON.class);
                bind(ApiListingResourceXML.class);

                // DataBase connectionPool
                DataSource dataSource = createPool();

                // Create the database DAO
                DBI dbi = new DBI(dataSource);
                TokensDao dbDao = dbi.onDemand(TokensDao.class);

                // Init flyWay databaseMigrating tools
                Flyway flyway = new Flyway();
                flyway.setDataSource(dataSource);
                flyway.migrate();

                // create and bind the caching DAO
                TokensDao cachingDao = new CachingTokensDao(dbDao, Executors.newSingleThreadScheduledExecutor());
                bind(TokensDao.class).toInstance(cachingDao);

                // Validation Service
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                bind(Validator.class).toInstance(validator);

                // Bind APIs
                bind(TokensApi.class).asEagerSingleton();

                // Route all requests through GuiceContainer
                serve("/api/*").with(GuiceContainer.class, params);
            }

            private DataSource createPool() {
                try {
                    Class.forName(config.jdbcDriver());
                } catch (ClassNotFoundException ex) {
                    logger.error("Unable to load JDBC driver " + config.jdbcDriver(), ex);
                    die();
                }

                BoneCPConfig boneConfig = new BoneCPConfig();
                boneConfig.setJdbcUrl(config.jdbcUrl());
                boneConfig.setUsername(config.jdbcUser());
                boneConfig.setPassword(config.jdbcPass());
                return new BoneCPDataSource(boneConfig);
            }

            private void die() {
                logger.error("Unrecovarable error - system will shutdown");
                throw new RuntimeException("Unrecoverable error - please kill me!");
            }
        });
    }
}
