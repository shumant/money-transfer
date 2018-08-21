package com.shuman.transfers;

import com.shuman.transfers.config.AppConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;

public class Application {

    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SECURITY);

        Servlet jerseyServlet = new ServletContainer(new AppConfig());
        ServletHolder servletHolder = new ServletHolder("jersey", jerseyServlet);
        servletHolder.setInitOrder(0);
        context.addServlet(servletHolder, "/*");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
