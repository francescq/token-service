package com.workshare.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SpyFilter implements javax.servlet.Filter {
    
    public static class Server {
        private String scheme;
        private String host;
        private int port;

        private Server(ServletRequest request) {
            scheme = request.getScheme();
            host = request.getServerName();
            port = request.getServerPort();
        }

        public String getScheme() {
            return scheme;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
        
        public URL getBasedURL(String path) {
            try {
                return new URL(scheme, host, port, path);
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }
    
    private static Server info = null;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (info == null) {
            info = new Server(request);
        }
    
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    public static Server server() {
        return info;
    }
}
