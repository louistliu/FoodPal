package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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