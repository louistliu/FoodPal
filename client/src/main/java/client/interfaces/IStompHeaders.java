package client.interfaces;

import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Interface for streamlining header declaration
 * when registering callbacks and sending requests.
 */
public interface IStompHeaders {
    static final String REQUEST_PREFIX = "/app";
    static final String SUBSCRIBE_PREFIX = "/topic";
    static final String USER_SUBSCRIBE_PREFIX = "/user/queue";

    /**
     * Returns stomp headers for sockets to use during subscriptions (with subscribe prefix).
     *
     * @return returns {@link StompHeaders}.
     */
    StompHeaders getSubscribeHeaders();

    /**
     * Returns stomp headers with destination set to request destination.
     *
     * @return stomp headers.
     */
    StompHeaders getRequestHeaders();
}

