package server.controllers;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class IngredientController {

    @MessageMapping("ingredients/create/")
    @SendTo("/updates/create/")
    public String create(@Payload String obj) throws Exception {
        return obj;
    }
}
