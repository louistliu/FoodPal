package client.utils;

import client.interfaces.IResponseHandler;
import com.google.inject.Inject;
import commons.Ingredient;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Handles client-server communication via STOMP over websockets.
 */
public class ServerSockets {
    public static final String OBJECT_TYPE_KEY = "ObjectType";
    private static final String URL = "ws://127.0.0.1:8080/";
    private static final String INITIAL_ENDPOINT = "food-pal";
    private final WebSocketStompClient client;
    private final StompSessionHandler sessionHandler;

    /**
     * Create a new STOMP client, and configure session.
     *
     */
    @Inject
    public ServerSockets() {
        WebSocketClient socketClient = new StandardWebSocketClient();
        TaskScheduler scheduler = new DefaultManagedTaskScheduler();

        this.client = new WebSocketStompClient(socketClient);
        client.setMessageConverter(new JacksonJsonMessageConverter());
        client.setTaskScheduler(scheduler);

        this.sessionHandler = new StompSessionHandler();
        try {
            client.connectAsync(URL + INITIAL_ENDPOINT, sessionHandler)
                  .get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create headers for sending and subscribing to server endpoints.
     *
     * @param destination endpoint path
     * @return returns STOMP headers
     */
    public static StompHeaders createHeaders(String destination) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(destination);
        return headers;
    }

    /**
     * Disconnects from STOMP session, if connected.
     */
    public void disconnect() {
        sessionHandler.disconnect();
    }

    /**
     * Add a listener for a server endpoint.
     *
     * @param headers metadata to be sent to the server upon subscription
     * @param handler interface specifying how to deal with objects set from the server
     */
    public void subscribe(StompHeaders headers, IResponseHandler<?> handler) {
        sessionHandler.addHandler(headers, handler);
    }

    /**
     * Adds an ingredient on the server.
     *
     * @param ingredient ingredient to add.
     */
    public void addIngredient(Ingredient ingredient) {
        StompHeaders headers = createHeaders("/app/ingredients/create");
        sessionHandler.send(headers, ingredient);
    }

    /**
     * Get sessionId.
     *
     * @return returns the sessionId
     */
    public String getSessionId() {
        return sessionHandler.getSession().getSessionId();
    }

    /**
     * Pings the endpoint to send data to client.
     *
     * @param path endpoint
     */
    public void get(String path) {
        StompHeaders headers = createHeaders(path);
        sessionHandler.send(headers, null);
    }
}
