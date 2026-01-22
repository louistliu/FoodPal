package server.controllers;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void initialReply() throws InterruptedException {
        BlockingQueue<Ingredient[]> queue = new ArrayBlockingQueue<>(1);


        session.subscribe("/app/ingredients/fetch", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Ingredient[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.add((Ingredient[]) payload);
            }
        });

        await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Ingredient[] ingredients = queue.poll();

                    assertNotNull(ingredients, "The initial reply should not be null");

                    assertTrue(ingredients.length > 0,
                            "The initial reply should contain default ingredients");

                    System.out.println("Received " + ingredients.length + " initial ingredients");
                });
    }

    @Test
    void testIngredientCreationAndFetch() throws InterruptedException {
        BlockingQueue<Ingredient> createdQueue = new ArrayBlockingQueue<>(1);
        Ingredient newIngredient = new Ingredient("Cucumber");

        session.subscribe("/topic/ingredients/create", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Ingredient.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                createdQueue.add((Ingredient) payload);
            }
        });

        session.send("/app/ingredients/create", newIngredient);

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Ingredient created = createdQueue.poll();
            assertNotNull(created);
            assertEquals(newIngredient.getName(), created.getName());
        });

        BlockingQueue<Ingredient[]> fetchedQueue = new ArrayBlockingQueue<>(1);

        session.subscribe("/app/ingredients/fetch", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Ingredient[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                fetchedQueue.add((Ingredient[]) payload);
            }
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Ingredient[] ingredients = fetchedQueue.poll();
            assertNotNull(ingredients);
            boolean found = false;
            for (Ingredient i : ingredients) {
                if (i.getName().equals(newIngredient.getName())) {
                    found = true;
                    break;
                }
            }

            String msg = "Newly created ingredient should appear in fetched list";
            assertTrue(found, msg);
        });
    }

    @Test
    void testIngredientLifecycle() throws InterruptedException {
        BlockingQueue<Ingredient> deleteQueue = new ArrayBlockingQueue<>(1);
        BlockingQueue<Ingredient> createQueue = new ArrayBlockingQueue<>(1);

        session.subscribe("/topic/ingredients/delete", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Ingredient.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                deleteQueue.add((Ingredient) payload);
            }
        });

        session.subscribe("/topic/ingredients/create", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Ingredient.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                createQueue.add((Ingredient) payload);
            }
        });

        Ingredient tempIng = new Ingredient("DeleteMe");
        session.send("/app/ingredients/create", tempIng);

        await().atMost(5, TimeUnit.SECONDS).until(() -> !createQueue.isEmpty());

        final BlockingQueue<Ingredient[]> fetchQueue = new ArrayBlockingQueue<>(1);
        session.subscribe("/app/ingredients/fetch", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Ingredient[].class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                fetchQueue.add((Ingredient[]) payload);
            }
        });

        Ingredient[] list = fetchQueue.poll(2, TimeUnit.SECONDS);
        assertNotNull(list);

        Ingredient target = null;
        for (Ingredient i : list) {
            if (i.getName().equals("DeleteMe")) {
                target = i;
                break;
            }
        }
        assertNotNull(target, "Ingredient should have been created");

        session.send("/app/ingredients/delete", target);

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Ingredient deleted = deleteQueue.poll();
            assertNotNull(deleted);
            assertEquals("DeleteMe", deleted.getName());
        });
    }

}