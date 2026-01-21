package client.utils;

import client.interfaces.IStompHeaders;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.util.MultiValueMap;

/**
 * Enum of Recipe Endpoints on the server and the required headers for it.
 */
public enum Endpoint implements IStompHeaders {
    RECIPE_CREATE("/recipes/create"),
    RECIPE_USER_CREATE("/recipes/create", SubscribePrefix.TO_USER),
    RECIPE_UPDATE("/recipes/update"),
    RECIPE_USER_UPDATE("/recipes/update", SubscribePrefix.TO_USER),
    RECIPE_DELETE("/recipes/delete"),
    RECIPE_FETCH("/recipes/fetch", SubscribePrefix.FETCH),
    INGREDIENT_CREATE("/ingredients/create"),
    INGREDIENT_USER_CREATE("/ingredients/create", SubscribePrefix.TO_USER),
    INGREDIENT_UPDATE("/ingredients/update"),
    INGREDIENT_DELETE("/ingredients/delete"),
    INGREDIENT_FETCH("/ingredients/fetch", SubscribePrefix.FETCH),
    ERROR("/errors", SubscribePrefix.TO_USER);

    public final String path;
    public final StompHeaders headers;
    private final SubscribePrefix subscribePrefix;

    private Endpoint(String path) {
        this.path = path;
        this.headers = new StompHeaders();
        this.headers.setDestination(path);
        this.subscribePrefix = SubscribePrefix.SUBSCRIBE;
    }

    private Endpoint(String path, SubscribePrefix prefix) {
        this.path = path;
        this.headers = new StompHeaders();
        this.headers.setDestination(path);
        this.subscribePrefix = prefix;
    }

    private Endpoint(String path, MultiValueMap<String, String> headers, SubscribePrefix prefix) {
        this.path = path;
        this.headers = new StompHeaders();
        this.headers.addAll(headers);
        this.headers.setDestination(path);
        this.subscribePrefix = prefix;
    }

    private Endpoint(String path, StompHeaders headers, SubscribePrefix prefix) {
        this.path = path;
        if (headers == null) {
            headers = new StompHeaders();
        }
        this.headers = headers;
        this.headers.setDestination(path);
        this.subscribePrefix = prefix;
    }

    /**
     * Copies data to new object and updates it's destination with prefix.
     *
     * @param prefix  Destination prefix - different based on the type of request
     * @param headers headers to create a copy of and update
     * @return new stomp headers
     */
    public static StompHeaders getPrefixedHeaders(String prefix, StompHeaders headers) {
        var tempHeaders = new StompHeaders();
        tempHeaders.addAll(headers);
        tempHeaders.setDestination(prefix + headers.getDestination());
        return tempHeaders;
    }

    @Override
    public String toString() {
        return this.name() + " " + this.path + " with headers: " + this.headers;
    }

    /**
     * Returns stomp headers for sockets to use during requests and subscriptions.
     *
     * @return returns {@link StompHeaders}.
     */
    @Override
    public StompHeaders getSubscribeHeaders() {
        return getPrefixedHeaders(subscribePrefix.prefix, headers);
    }

    /**
     * Returns stomp headers with destination set to request destination.
     *
     * @return stomp headers
     */
    @Override
    public StompHeaders getRequestHeaders() {
        return getPrefixedHeaders(IStompHeaders.REQUEST_PREFIX, headers);
    }
}
