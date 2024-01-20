package tobyspring.helloboot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tobyspring.config.Config;

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
@Configuration
@ComponentScan
@Import(Config.class)   // @Import 이용하여 스캔 영역에 없는 클래스를 스캔 및 구성 정보로 추가되도록 함.
public @interface MySpringBootApplication {
}
