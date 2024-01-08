package tobyspring.helloboot;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MySpringApplication {
    // SpringBoot가 ServletContainer 을 기동하고 Bean을 등록하는 과정을 담고 있는 코드
    public static void run(Class<?> applicationClass, String... args) {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext() {
            @Override
            protected void onRefresh() {
                super.onRefresh();

                // Dispatcher Servlet 등록
                ServletWebServerFactory serverFactory = this.getBean(ServletWebServerFactory.class);
                DispatcherServlet dispatcherServlet = this.getBean(DispatcherServlet.class);
//                dispatcherServlet.setApplicationContext(this);  // SpringContainer가 DispatcherServlet은 의존하는 Bean ApplicationContext을 대신 주입해줌.(setter 이용)

                WebServer webServer = serverFactory.getWebServer(servletContext -> {
                    // Dispatcher Servlet: GenericWebApplication Context를 주입하여 Spring Container를 인지하도록 함
                    servletContext.addServlet("dispatcherServlet", dispatcherServlet
                    ).addMapping("/*");
                });
                webServer.start();
            }
        };
        applicationContext.register(applicationClass);
        applicationContext.refresh();
    }

}
