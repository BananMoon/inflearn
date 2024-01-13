package tobyspring.helloboot;

import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 *  ApplicationContextAare implements하여 setter를 통해 setApplicationContext() 를 통해 ApplicationContext 세팅 가능.
 * => SpringContainer는 자기 자신이지만, `private ApplicationContext applicationContext;` 또한 bean으로 취급. 생성자를 통해 이미 Instance가 만들어진 이후에 setApplicationContext가 호출되기 때문에 final로 선언하지 못함.
 */
@RequestMapping("/hello")
@RestController
public class HelloController {
    private final HelloService helloService;

    // DI 주입 시 사용
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/false")
    public String helloV1(String name) {
        SimpleHelloService helloService = new SimpleHelloService();

        return helloService.sayHello(Objects.requireNonNull(name)); // null인 경우 예외 발생
    }

    /**
     * bean으로 등록해놓고, 이를 사용하도록 한다.
     */
    @ResponseBody
    @GetMapping
    public String helloV2(String name) {
        if (Strings.isEmpty(name) || name.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        return helloService.sayHello(name); // null인 경우 예외 발생

    }

}
