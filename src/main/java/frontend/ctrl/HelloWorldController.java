package frontend.ctrl;

import nl.tudelft.doda25.team2.VersionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        String version = VersionUtil.getVersion();
        return "Hello World! (lib-version: " + version + ")";
    }
}