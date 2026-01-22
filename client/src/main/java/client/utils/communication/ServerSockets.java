package client.utils.communication;

import client.interfaces.IResponseHandler;
import client.interfaces.IStompHeaders;
import client.utils.ConfigService;
import client.utils.communication.StompSessionHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.TimeUnit;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Handles client-server communication via STOMP over websockets.
 */
@Singleton
public class ServerSockets {
    public static final String OBJECT_TYPE_KEY = "ObjectType";
    private static final String INITIAL_ENDPOINT = "food-pal";
    private static String URL = "ws://127.0.0.1:8080/";
    private final WebSocketStompClient client;
    private final StompSessionHandler sessionHandler;
    private final boolean isAvailable;

    /**
     * Create a new STOMP client, and configure session.
     *
     */
    @Inject
    public ServerSockets(WebSocketStompClient client, StompSessionHandler sessionHandler,
                         ConfigService configservice) {
        URL = configservice.getConfig().getServerUrl() != null
              ? configservice.getConfig().getServerUrl()
              : "ws://127.0.0.1:8080/";
        this.client = client;
        this.sessionHandler = sessionHandler;
        try {
            System.out.println("Connecting to " + URL);
            this.client.connectAsync(URL + INITIAL_ENDPOINT, this.sessionHandler)
                  .get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("FAILED to CONNECT to " + URL + INITIAL_ENDPOINT);
            isAvailable = false;
            return;
        }
        isAvailable = true;
    }

    /**
     * Create headers for sending and subscribing to server endpoints.
     *
     * @param destination endpoint path
     * @return returns STOMP headers
     */
    public static StompHeaders setDestination(String destination) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(destination);
        return headers;
    }

    /**
     * Check if a server for this client is reachable.
     *
     * @return returns {@code true} if the server can be reached via a get method,
     *       otherwise returns {@code false}
     */
    public boolean isServerAvailable() {
        return isAvailable;
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
     * @param handler interface specifying how to deal with objects set from the
     *                server
     */
    public void subscribe(StompHeaders headers, IResponseHandler<?> handler) {
        sessionHandler.addHandler(headers, handler);
    }

    /**
     * Add a listener for a server endpoint.
     *
     * @param headers metadata to be sent to the server upon subscription
     * @param handler interface specifying how to deal with objects set from the
     *                server
     */
    public void subscribe(IStompHeaders headers, IResponseHandler<?> handler) {
        subscribe(headers.getSubscribeHeaders(), handler);
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
        StompHeaders headers = setDestination(path);
        sessionHandler.send(headers, null);
    }

    /**
     * Send payload to the server with the specified {@link StompHeaders}.
     *
     * @param headers headers to be sent with the payload
     * @param payload the payload
     */
    public void send(StompHeaders headers, Object payload) {
        sessionHandler.send(headers, payload);
    }

    /**
     * Send payload to the server with the specified {@link StompHeaders}.
     *
     * @param headers headers to be sent with the payload
     * @param payload the payload
     */
    public void send(IStompHeaders headers, Object payload) {
        send(headers.getRequestHeaders(), payload);
    }
}
