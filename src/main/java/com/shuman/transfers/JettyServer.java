package com.shuman.transfers;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettyServer {
    private Server server;

    public void start() throws Exception {
        server = new Server();
        ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(8090);
        server.setConnectors(new Connector[]{serverConnector});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(DummyServlet.class, "/status");
        server.setHandler(servletHandler);

        server.start();
    }
}
