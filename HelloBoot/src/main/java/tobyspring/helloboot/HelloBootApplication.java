package tobyspring.helloboot;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
// 독립적으로 실행 가능한 서블릿 애플리케이션
public class HelloBootApplication {

    public static void main(String[] args) {
//        servletContainerOnlyVer();

        springContainerUseVer();

    }

    /**
     * Servlet Conatiner 내 Front Controller 사용하여 HelloController 매핑 & 바인딩
     */
    public static void servletContainerOnlyVer() {
        ServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
        WebServer webServer = serverFactory.getWebServer(servletContext -> {
            HelloController helloController = new HelloController();
            // Servlet(Web Component) 추가
            // Front Controller (Servlet Container) 는 특정 요청에 대해 HelloController에 작업을 위임
            servletContext.addServlet("front-controller", new HttpServlet() {
                @Override
                protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                    // 인증, 보안, 다국어, 공통 기능
                    // 1) mapping : 요청 url&메서드에 대해 특정 컨트롤러와 매핑
                    if (req.getRequestURI().equals("/hello") && req.getMethod().equals(HttpMethod.GET.name())) {
                        // 2) binding : input으로 들어온 값 (예:폼 데이터)을 HttpServletRequest에서 추출해서 DTO로 전환
                        String name = req.getParameter("name");

                        String ret = helloController.hello(name);

                        // 응답 로직
                        resp.setStatus(HttpStatus.OK.value());
                        resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                        resp.getWriter().print(ret);

                    } else if (req.getRequestURI().equals("/user")) {  // 또다른 매핑 가능한 controller

                    } else {
                        resp.setStatus(HttpStatus.NOT_FOUND.value());
                    }
                }
            }).addMapping("/hello");    // /hello 요청 들어오면 해당 오브젝트가 처리하도록 설정
        });
        webServer.start();  // Tomcat Servlet Container 동작!
    }

    // ########### Spring Container 생성 ###########
    // applicaton context(==Spring Container) 를 코드로 만드는데 지원하는 클래스 : GenericApplicationContext
    public static void springContainerUseVer() {
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        // meta 정보(클래스)를 넣어서 bean 등록
        applicationContext.registerBean(HelloController.class);

        applicationContext.refresh();   // application context : bean object 모두 생성

        ServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
        WebServer webServer = serverFactory.getWebServer(servletContext -> {
            servletContext.addServlet("front-controller2", new HttpServlet() {
                @Override
                protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                    if ("/hello".equals(req.getRequestURI()) && HttpMethod.GET.name().equals(req.getMethod())) {

                        String name = req.getParameter("name");
                        // application context에서 bean 찾아서 사용
                        HelloController helloControllerBean = applicationContext.getBean(HelloController.class);
                        String ret = helloControllerBean.hello(name);

                        resp.setContentType(MediaType.TEXT_PLAIN_VALUE);
                        resp.getWriter().print(ret);
                    } else {
                        resp.setStatus(HttpStatus.NOT_FOUND.value());
                    }
                }
            }).addMapping("/hello");
        });
        webServer.start();
    }

}
