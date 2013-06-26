package com.workshare.swagger;

import java.net.URL;

import javax.servlet.ServletConfig;

import com.wordnik.swagger.jaxrs.JerseyConfigReader;
import com.workshare.micro.api.ApiOriginFilter;
import com.workshare.servlet.SpyFilter;

public class CustomSwaggerConfigReader extends JerseyConfigReader {
    
    private String apiDocsURL;

    public CustomSwaggerConfigReader(ServletConfig config) {
        super(config);
 
        final URL url = SpyFilter.server().getBasedURL(config.getServletContext().getContextPath()+"/api");
        this.apiDocsURL = (url == null ? null : url.toString());
    }

    @Override
    public String basePath() {
        return firstNonEmpty(System.getProperty("api.basepath"), super.basePath(), apiDocsURL);
    }

    @Override
    public String swaggerVersion() {
        return com.wordnik.swagger.core.SwaggerSpec.version();
    }

    @Override
    public String apiVersion() {
        return firstNonEmpty(super.apiVersion(), "1.0");
    }

    @Override
    public String modelPackages() {
        return super.modelPackages();
    }

    @Override
    public String apiFilterClassName() {
        return firstNonEmpty(super.apiFilterClassName(), ApiOriginFilter.class.getName());
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isEmpty())
                return value;
        }

        return "";
    }
}
