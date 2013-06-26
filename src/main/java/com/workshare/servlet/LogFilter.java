package com.workshare.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFilter implements javax.servlet.Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final long now = System.nanoTime();
        try {
            chain.doFilter(request, response);
        }
        finally {
            final long elapsed = System.nanoTime() - now;
            if (logger.isDebugEnabled()) {
                final HttpServletRequest httpRequest = (HttpServletRequest)request;
                final long micros = (long)(elapsed/1000.0);
                logger.debug("Request {} server in {} millis", httpRequest.getMethod()+":"+httpRequest.getPathInfo(), micros/1000.0);
            }
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
