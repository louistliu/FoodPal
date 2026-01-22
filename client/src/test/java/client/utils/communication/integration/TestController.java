package client.utils.communication.integration;

import commons.Ingredient;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
    @SubscribeMapping("/test")
    public Ingredient subscribe() {
        return new Ingredient("TEST");
    }

    /**
     * Test if payload successfully sent back.
     *
     * @param payload Payload to send back;
     * @return payload
     */
    @MessageMapping("/ingredients/create")
    @SendTo
    public Ingredient create(Ingredient payload) {
        return payload;
    }

}
