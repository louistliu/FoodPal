package client.utils.communication;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import client.MyModule;
import client.utils.Config;
import client.utils.ConfigService;
import client.utils.Endpoint;
import client.utils.ResponseHandler;
import client.utils.communication.integration.CallbackTest;
import client.utils.communication.integration.TestController;
import client.utils.communication.integration.WebSocketConfig;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.Ingredient;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
      WebSocketConfig.class, TestController.class})
@DirtiesContext
class ServerSocketsTest {

    private static final Integer TIMEOUT = 1;
    private static final StompHeaders destination =
          ServerSockets.setDestination(Endpoint.REQUEST_PREFIX + "/test");

    private static final StompHeaders badDestination =
          ServerSockets.setDestination(Endpoint.REQUEST_PREFIX + "/test_bad");
    private static final Ingredient testIngredient = new Ingredient("TESTABLE");

    @Autowired
    private ApplicationContext applicationContext;
    @LocalServerPort
    private Integer port;
    @Inject
    private ServerSockets sockets;
    @Inject
    private ServerSockets otherSockets;

    private CallbackTest testCallback;

    @BeforeEach
    public void setup() {
        Injector injector = Guice.createInjector(new MyModule(new Config()));
        var configService = injector.getInstance(ConfigService.class);
        var config = new Config();
        config.setServerUrl("ws://localhost:" + port + "/");
        configService.setConfig(config);

        injector.injectMembers(this);
        testCallback = new CallbackTest();
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
        await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(sockets.isServerAvailable());
        });
    }

    @Test
    void disconnect() {
        assertDoesNotThrow(() -> sockets.getSessionId());
        sockets.disconnect();
        await().atMost(TIMEOUT, TimeUnit.SECONDS)
              .untilAsserted(() -> {
                  assertThrows(Exception.class, () -> {
                      sockets.send(destination, null);
                  });
              });
    }

    @Test
    void testSubscribe() {
        sockets.subscribe(destination,
              new ResponseHandler<Ingredient>(testCallback::callback) {
              });
        await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals(1, testCallback.getCalledCount());
        });
    }

    @Test
    void subscribeFail() {
        await().atMost(TIMEOUT, TimeUnit.SECONDS)
              .untilAsserted(() ->
                    assertEquals(0, testCallback.getCalledCount()));
    }

    @Test
    void getSessionId() {
        assertNotNull(sockets.getSessionId());
        assertEquals(sockets.getSessionId(), otherSockets.getSessionId());
    }

    @Test
    void testSend() {
        sockets.send(Endpoint.INGREDIENT_CREATE, testIngredient);
        sockets.subscribe(Endpoint.INGREDIENT_CREATE,
              new ResponseHandler<Ingredient>(testCallback::callback) {
              });
        await().atMost(TIMEOUT, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals(testIngredient, testCallback.getResult());
        });
    }
}