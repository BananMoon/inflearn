package sample.cafekiosk.spring;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.client.mail.MailSendClient;

/**
 * 통합 테스트의 환경 통합 지원하는 클래스
 */
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntigrationTestSupport {

    @MockBean
    protected MailSendClient mailSendClient;  // 행동 정의를 해줘야함.
}
