package com.coderiders.AggregateService.utilities;


import org.apache.hc.core5.net.URIBuilder;

public class UriBuilderWrapper {
    private URIBuilder uriBuilder;

    public UriBuilderWrapper(String path) {
        try {
            this.uriBuilder = new URIBuilder().setPath(path);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing UriBuilderWrapper", e);
        }
    }

    public UriBuilderWrapper setParameter(String name, String value) {
        uriBuilder.setParameter(name, value);
        return this;
    }

    public UriBuilderWrapper setPort(int port) {
        uriBuilder.setPort(port);
        return this;
    }

    public String build() {
        try {
            return uriBuilder.build().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error building URI", e);
        }
    }
}

