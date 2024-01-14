package tobyspring.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UnitTest
@interface FastUnitTest {

}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Test
@interface UnitTest {

}
public class HelloServiceTest {
    @UnitTest
    void simpleHelloService() {
        HelloService helloService = new SimpleHelloService();

        String ret = helloService.sayHello("Spring");

        Assertions.assertThat(ret).isEqualTo("Hello Spring");
    }

    /**
     * HelloController는 의존하고 있는 HelloService를 호출할 뿐, 내부적으로 어떤 일이 일어나는지는 모른다.
     *
     * 호출자는 단지 그 대상을 호출하지만, DI를 이용하여 HelloService를 대리해서 부가적인 효과를 줄 수 있게 된다.
     * 데코레이터 패턴, 프록시 패턴 등이 있는데,
     * - 데코레이터 패턴 : 실제 구현체를 호출하기 전에 부가적인 역할이 필요한데 실제 구현체에 로직을 넣기 어려울 때 사용하기 용이함.
     * - 프록시 패턴 : 비용이 큰 오브젝트는 서버가 초기화될 때 같이 초기화되는 것보다, 실제로 사용될 때 첫 요청이 들어오면 on-demand로 이를 만들어 최대한 지연시켜 생성한다.
     */
    @UnitTest
    void helloDecorator() {
        HelloDecorator helloDecorator = new HelloDecorator(name -> name);   // 간단한 람다식으로 대체
        String ret = helloDecorator.sayHello("TEST");

        Assertions.assertThat(ret).isEqualTo("**TEST**");
    }
}
