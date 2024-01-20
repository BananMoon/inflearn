package tobyspring.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 1) SpringBoot : 내장형 Servlet Container를 이용하는 독립 실행형 (Containerless 지원)
 *    애플리케이션 방식으로 동작하면서 요구되는 2개의 빈: TomcatServletWebServerFactory, DispatcherServlet
 * 2) 컴포넌트 스캔 대상에서 제외하도록 범위 밖에 디렉토리 생성하여 이동시킴.
 * 3) AutoConfiguration: Spring Boot가 애플리케이션에서 어떤 bean이 필요한지 판단하고 자동 선택해서 초기화함.
 *   - Tomcat 대신 다른 종류의 빈도 동작가능하도록 만들어야 함.
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
