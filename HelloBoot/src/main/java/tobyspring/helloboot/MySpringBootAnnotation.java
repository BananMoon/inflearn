package tobyspring.helloboot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)   // class, interface, enum 에게 부여할 수 있음.
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@ComponentScan
public @interface MySpringBootAnnotation {
}
