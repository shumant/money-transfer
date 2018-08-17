package com.shuman.transfers;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Application {

    public static void main(String[] args) throws Exception {
        new JettyServer().start();
    }

    private static ServletContainer resourceConfig() {
        return new ServletContainer(new ResourceConfig(
                new ResourceLoader().getClasses()));
    }


}
