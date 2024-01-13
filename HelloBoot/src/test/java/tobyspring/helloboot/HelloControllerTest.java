package tobyspring.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloControllerTest {
    @Test
    void helloControllerTest() {
        HelloController helloController = new HelloController(name -> name);

        String ret = helloController.helloV2("Test");

        Assertions.assertThat(ret).isEqualTo("Test");
    }
    @Test
    void failsHelloControllerTest() {
        HelloController helloController = new HelloController(name -> name);
        // null check
        Assertions.assertThatThrownBy(() -> {
            helloController.helloV2(null);
        }).isInstanceOf(IllegalArgumentException.class);
        // empty check
        Assertions.assertThatThrownBy(() -> {
            helloController.helloV2("");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
