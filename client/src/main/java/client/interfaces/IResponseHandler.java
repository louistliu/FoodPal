package client.interfaces;

import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Handles response from the server.
 */
public interface IResponseHandler<T> extends StompFrameHandler {

    /**
     * Handle data sent from the server.
     *
     * @param obj object returned from the server
     */
    void handleResponse(@NotNull Object obj);

    @Override
    default void handleFrame(StompHeaders headers, Object payload) {
        this.handleResponse(payload);
    }
}
