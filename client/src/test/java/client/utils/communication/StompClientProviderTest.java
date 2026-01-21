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
import org.springframework.web.socket.messaging.WebSocketStompClient;

class StompClientProviderTest {
    @Inject
    private WebSocketStompClient client;

    @Inject
    private WebSocketStompClient otherClient;

    @BeforeEach
    void setup() {
        Injector injector = Guice.createInjector(new MyModule(new Config()));
        injector.injectMembers(this);
    }

    @Test
    void testInjection() {
        assertNotNull(client);
    }

    @Test
    void testSingleton() {
        assertEquals(client, otherClient);
    }
}