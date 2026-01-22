package client.utils.communication;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import client.MyModule;
import client.utils.Config;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StompSessionHandlerTest {

    @Inject
    private StompSessionHandler handler;
    @Inject
    private StompSessionHandler otherHandler;

    @BeforeEach
    public void setup() {
        Injector injector = Guice.createInjector(new MyModule(new Config()));
        injector.injectMembers(this);
    }

    @Test
    void injection() {
        assertNotNull(handler);
    }

    @Test
    void notSingleton() {
        assertNotEquals(handler, otherHandler);
    }
}