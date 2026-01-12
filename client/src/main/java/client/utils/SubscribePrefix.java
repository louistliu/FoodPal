package client.utils;

import client.interfaces.IStompHeaders;

/**
 * Enum containing prefixes for server responses.
 */
public enum SubscribePrefix {
    SUBSCRIBE(IStompHeaders.SUBSCRIBE_PREFIX),
    TO_USER(IStompHeaders.USER_SUBSCRIBE_PREFIX),
    FETCH(IStompHeaders.REQUEST_PREFIX);

    public final String prefix;

    private SubscribePrefix(String prefix) {
        this.prefix = prefix;
    }
}
