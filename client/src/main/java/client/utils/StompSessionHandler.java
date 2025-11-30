package client.utils;

import client.interfaces.IResponseHandler;
import com.google.inject.Inject;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * Manages a STOMP session.
 */
public class StompSessionHandler extends StompSessionHandlerAdapter {

    private StompSession session;
    private Map<StompHeaders, IResponseHandler> mappings;

    @Inject
    public StompSessionHandler() {
        this.mappings = new HashMap<>();
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        mappings.forEach(session::subscribe);
    }

    /**
     * Subscribe to a STOMP server endpoint.
     *
     * @param headers         response headers
     * @param callbackHandler handles data from server.
     */
    public void addHandler(StompHeaders headers, IResponseHandler<?> callbackHandler) {
        if (session != null) {
            session.subscribe(headers, callbackHandler);
        }
        mappings.put(headers, callbackHandler);
    }

    public StompSession getSession() {
        return session;
    }

    /**
     * Sends a DISCONNECT frame to server.
     */
    public void disconnect() {
        if (session == null) {
            throw new NotYetConnectedException();
        }
        session.disconnect();
    }

    /**
     * Sends an object with specified headers to server.
     *
     * @param headers headers of the request
     * @param object  payload to send
     */
    public void send(StompHeaders headers, Object object) {
        if (session == null) {
            throw new NotYetConnectedException();
        }
        session.send(headers, object);
    }
}