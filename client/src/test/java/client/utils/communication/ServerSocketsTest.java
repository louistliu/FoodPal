package client.utils.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import client.MyModule;
import client.utils.Config;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerSocketsTest {

    @Inject
    private ServerSockets sockets;

    @Inject
    private ServerSockets otherSockets;

    @BeforeEach
    public void setup() {
        Injector injector = Guice.createInjector(new MyModule(new Config()));
        injector.injectMembers(this);
    }

    @Test
    void injected() {
        assertNotNull(sockets);
    }

    @Test
    void singleton() {
        assertEquals(sockets, otherSockets);
    }

    @Test
    void isServerAvailable() {
    }

    @Test
    void disconnect() {
    }

    @Test
    void subscribe() {
    }

    @Test
    void testSubscribe() {
    }

    @Test
    void getSessionId() {
    }

    @Test
    void send() {
    }

    @Test
    void testSend() {
    }
}