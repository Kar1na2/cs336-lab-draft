package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.example.Servlets.*;

/**
 * Main class that will run the simple server
 */
public class Main {
    /** Server port **/
    public static final int PORT = 8090;

    /**
     * Runs the simple server to simulate multiple redirects
     *
     * @param args Command-line arguments
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(redirect1.class, "/a");
        handler.addServletWithMapping(redirect2.class, "/b");
        handler.addServletWithMapping(redirect3.class, "/c");
        handler.addServletWithMapping(final_redirect.class, "/d");
        handler.addServletWithMapping(secret_redirect.class, "/secret");

        SessionHandler session = new SessionHandler();
        session.setHandler(handler);
        server.setHandler(session);

        server.start();
        server.join();
    }
}