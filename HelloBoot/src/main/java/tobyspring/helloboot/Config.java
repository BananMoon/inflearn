package tobyspring.helloboot;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * SpringBoot가 Containerless 지원하기 위해 내장형 Servlet Container를 이용하는 독립 실행형
 * 애플리케이션 방식으로 동작하면서 요구된 2개의 빈: TomcatServletWebServerFactory, DispatcherServlet
 */
@Configuration
public class Config {
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
