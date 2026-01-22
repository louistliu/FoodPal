package client.utils.communication;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import client.MyModule;
import client.utils.Config;
import client.utils.communication.integration.TestController;
import client.utils.communication.integration.WebSocketConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {
      WebSocketConfig.class, TestController.class})
class ServerSocketsTest {

    @Autowired
    private ApplicationContext applicationContext;

    @LocalServerPort
    private Integer port;

    @Inject
    private ServerSockets sockets;

    @Inject
    private ServerSockets otherSockets;

    @BeforeEach
    public void setup() {
        var config = new Config();
        config.setServerUrl("ws:localhost:" + port + "/food-pal");
        Injector injector = Guice.createInjector(new MyModule(config));
        config.setServerUrl("ws:localhost:" + port + "/food-pal");
        new ServerSockets();
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
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(sockets.isServerAvailable());
        });
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