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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RecipeController recipeController;

    @BeforeEach
    void setup() throws Exception {
        client = new WebSocketStompClient(
              new StandardWebSocketClient()
        );
        client.setMessageConverter(new JacksonJsonMessageConverter());
        session = client.connectAsync("ws://localhost:" + port + "/food-pal",
              new StompSessionHandlerAdapter() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Recipe.class;
                  }
              }).get(1, TimeUnit.SECONDS);
    }

    @Test
    void testConnectionEstablished() {
        assertNotNull(session);
    }

    @Test
    void createRecipeEndpoint() {
        BlockingQueue<Recipe> blockingQueue = new ArrayBlockingQueue<>(1);

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

          Recipe payload = new Recipe(
              "test-recipe",
              "a test recipe",
              new ArrayList<>(),
              new ArrayList<>()
          );

        session.send("/app/recipes/create", payload);
        await()
              .atMost(2, TimeUnit.SECONDS)
              .untilAsserted(() -> {
                  Recipe result = blockingQueue.poll();
                  assertEquals(payload.getName(), result != null ? result.getName() : null);
              });
    }
    
    @Test
    void fetchRecipesEndpoint() {
        try {
            // Create a unique recipe directly via controller to avoid relying on DummyData
            String uniqueName = "fetch-test-" + System.currentTimeMillis();
            Recipe toCreate = new Recipe(uniqueName, "created for fetch test", new ArrayList<>(), new ArrayList<>());
            recipeController.create(toCreate);

            var list = recipeController.fetchIngredients();
            assertNotNull(list);

            // Filter out any default/test recipes and check our unique recipe is present
            long matches = list.stream().filter(r -> uniqueName.equals(r.getName())).count();
            assertEquals(1, matches);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void updateRecipeEndpoint() {
        BlockingQueue<Recipe> createQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<Recipe> updateQueue = new ArrayBlockingQueue<>(1);

        session.subscribe("/topic/recipes/create",
              new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Recipe.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                      createQueue.add((Recipe) payload);
                  }
              });

        session.subscribe("/topic/recipes/update",
              new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Recipe.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                      updateQueue.add((Recipe) payload);
                  }
              });

        Recipe payload = new Recipe(
              "to-update",
              "will be updated",
              new ArrayList<>(),
              new ArrayList<>()
        );
        session.send("/app/recipes/create", payload);

        // wait for created
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Recipe created = createQueue.poll();
            assertNotNull(created);

            // modify and send update
            created.setName("updated-name");
            session.send("/app/recipes/update", created);
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Recipe updated = updateQueue.poll();
            assertNotNull(updated);
            assertEquals("updated-name", updated.getName());
        });
    }

    @Test
    void deleteRecipeEndpoint() {
        BlockingQueue<Recipe> createQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<Recipe> deleteQueue = new ArrayBlockingQueue<>(1);

        session.subscribe("/topic/recipes/create",
              new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Recipe.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                      createQueue.add((Recipe) payload);
                  }
              });

        session.subscribe("/topic/recipes/delete",
              new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                      return Recipe.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                      deleteQueue.add((Recipe) payload);
                  }
              });

        Recipe payload = new Recipe(
              "to-delete",
              "will be deleted",
              new ArrayList<>(),
              new ArrayList<>()
        );
        session.send("/app/recipes/create", payload);

        // wait for created and then request delete
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Recipe created = createQueue.poll();
            assertNotNull(created);
            session.send("/app/recipes/delete", created);
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Recipe deleted = deleteQueue.poll();
            assertNotNull(deleted);
            assertEquals("to-delete", deleted.getName());
        });
    }
}
