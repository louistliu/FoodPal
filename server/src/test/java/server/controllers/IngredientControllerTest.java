package server.controllers;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.Ingredient;
import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IngredientControllerTest {
    WebSocketStompClient client;
    StompSession session;
    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() throws ExecutionException, InterruptedException, TimeoutException {
        client = new WebSocketStompClient(
              new StandardWebSocketClient()
        );
        client.setMessageConverter(new JacksonJsonMessageConverter());
        session = client.connectAsync("ws://localhost:" + port + "/food-pal",
              new StompSessionHandlerAdapter() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Ingredient.class;
                  }
              }).get(1, TimeUnit.SECONDS);
    }

    @Test
    void testConnectionEstablished() {
        assertNotNull(session);
    }

    @Test
    void verifyTestConnection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        // when handling frame
        // latch.countDown();
    }

    @Test
    void createIngredientEndpoint() {
        BlockingQueue<Ingredient> blockingQueue = new ArrayBlockingQueue<>(1);
        Ingredient payload = new Ingredient("test");

        session.subscribe("/topic/ingredients/create",
              new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Ingredient.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                      blockingQueue.add((Ingredient) payload);
                  }
              });
        var headers = new StompHeaders();

        session.send("/app/ingredients/create", payload);
        await()
              .atMost(1, TimeUnit.SECONDS)
              .untilAsserted(() -> {
                  Ingredient result = blockingQueue.poll();
                  assertEquals(payload.getName(), result != null ? result.getName() : null);
              });
    }

    @Test
    void initialReply() {
        // TODO: Test to be implemented
    }
}