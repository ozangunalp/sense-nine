module com.mechanitis.demo.sense.service {
    requires java.logging;
    requires javax.websocket.api;
    requires jetty.server;
    requires jetty.servlet;
    requires javax.websocket.server.impl;
    requires javax.websocket.client.impl;

    exports com.mechanitis.demo.sense.service;
}