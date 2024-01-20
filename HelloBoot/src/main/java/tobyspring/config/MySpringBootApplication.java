package tobyspring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 합성 애노테이션
 * 메타 애노테이션을 이용해
 * 1. 일정한 규칙이 있는 애노테이션들을 하나의 메타 애노테이션으로 묶을 수 있다.
 * 2. 의미있는 애노테이션 이름으로 묶을 수 있다.  ex) @UnitTest
 */
@Target(ElementType.TYPE)   // class, interface, enum 에게 부여할 수 있음.
@Retention(RetentionPolicy.RUNTIME)
@Configuration              // meta annotaion
@ComponentScan              // meta annotaion
@EnableMyAutoConfiguration  // meta annotaion
public @interface MySpringBootApplication {
}
