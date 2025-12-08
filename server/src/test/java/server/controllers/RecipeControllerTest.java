package server.controllers;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.Recipe;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
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
class RecipeControllerTest {
    WebSocketStompClient client;
    StompSession session;
    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() throws Exception {
        client = new WebSocketStompClient(
              new StandardWebSocketClient()
        );
        client.setMessageConverter(new JacksonJsonMessageConverter());
        session = client.connectAsync("ws://localhost:" + port + "/food-pal",
              new StompSessionHandlerAdapter() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {return Recipe.class;}
              }).get(1, TimeUnit.SECONDS);
    }

    @Test
    void testConnectionEstablished() {
        assertNotNull(session);
    }

    @Test
    void createRecipeEndpoint() {
        BlockingQueue<Recipe> blockingQueue = new ArrayBlockingQueue<>(1);
        Recipe payload = new Recipe("test-recipe","a test recipe", new ArrayList<>(), new ArrayList<>());

        session.subscribe("/topic/recipes/create",
              new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Recipe.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                      blockingQueue.add((Recipe) payload);
                  }
              });

        session.send("/app/recipes/create", payload);
        await()
              .atMost(2, TimeUnit.SECONDS)
              .untilAsserted(() -> {
                  Recipe result = blockingQueue.poll();
                  assertEquals(payload.getName(), result != null ? result.getName() : null);
              });
    }
}
