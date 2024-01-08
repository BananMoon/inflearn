package tobyspring.helloboot;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

// 독립적으로 실행 가능한 서블릿 애플리케이션
// SpringContainer에게 애플리케이션 구성을 어떻게 할 것인가 알려주는 정보들을 가지고 있는 클래스
@ComponentScan
public class HelloBootApplication {

    public static void main(String[] args) {
        MySpringApplication.run(HelloBootApplication.class, args);
    }

    /**
     * 팩토리 메서드
     */
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

}
