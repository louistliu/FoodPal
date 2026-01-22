package client.utils.communication.integration;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * Mock Server controller.
 */
@Controller
public class TestController {

    /**
     * Test method for subscribing.
     *
     * @return connected.
     */
    @SubscribeMapping
    @SendTo
    public String subscribe() {
        return "Server Connected";
    }
}
