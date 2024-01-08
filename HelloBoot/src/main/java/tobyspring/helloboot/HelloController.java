package tobyspring.helloboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
// @Controller : Dispatcher Servlet하고는 직접적인 연관 없고 Component Scan 목적이 아니었지만 Boot 3.0부터는 무조건 필요.
@RequestMapping("/hello") // 클래스 레벨로 매핑할 컨트롤러 bean들을 찾은 후 메서드 레벨로 탐색하여 bean 등록한다.
@RestController // Spring Boot 3.x 버전부터 @RequestMapping만으로는 DispatcherServlet이 인식못함.
@MyComponent    // meta annotation으로 @Controller 등록
public class HelloController {
    private final HelloService helloService;
    // DI 주입 시 사용
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    public  HelloController() {
        helloService =  new SimpleHelloService();
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
        return helloService.sayHello(Objects.requireNonNull(name)); // null인 경우 예외 발생

    }
}
