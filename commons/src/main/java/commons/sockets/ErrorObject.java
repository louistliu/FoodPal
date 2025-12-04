package commons.sockets;

/**
 * Wraps error data in a Jackson parsable format for sending from server to client.
 */
public record ErrorObject(Throwable exception) {
}
