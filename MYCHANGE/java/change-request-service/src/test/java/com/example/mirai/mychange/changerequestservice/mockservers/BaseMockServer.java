package com.example.mirai.projectname.changerequestservice.mockservers;

import lombok.extern.slf4j.Slf4j;
import org.mockserver.integration.ClientAndServer;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public abstract class BaseMockServer {
    protected ClientAndServer mockClientAndServer;

    protected abstract int getMockServerPort();

    protected int getMockServerPort(String urlString) {
        try {
            return new URI(urlString).getPort();
        } catch (URISyntaxException e) {
            log.error("Could not parse mockserver port from url, setting port to -1", e);
        }
        return -1;
    }

    public void startMockServer() {
        mockClientAndServer = ClientAndServer.startClientAndServer(getMockServerPort());
    }

    public void stopMockServer() {
        mockClientAndServer.stop();
    }

    public void resetMockServer() {
        mockClientAndServer.reset();
    }
}
