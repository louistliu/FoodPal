package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Test Spring Boot controller, shows a string if the base address of the server is visited.
 */
@Controller
@RequestMapping("/")
public class SomeController {

    /**
     * the default test endpoint.
     *
     * @return returns a string greeting the user
     */
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }

}