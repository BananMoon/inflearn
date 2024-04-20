package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.MailSendHistory;
import sample.cafekiosk.spring.domain.history.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Mocking 처리하는 객체에 대해 특정 메서드만 stubbing할 수 있는 @Spy 이용하는 테스트 코드
 */
@TestPropertySource(locations="/application.yml")
@ExtendWith(MockitoExtension.class)
class MailServiceTestWithSpy {
    @InjectMocks    // 생성자에 필요한 Mocking 객체들을 주입
    private MailService mailService;
    @Spy    // 일부 기능에 대해서만 Mocking 처리 가능함.
    private MailSendClient mailSendClient;
    @Mock       // Mocking 처리. 행위 검증에 사용
    private MailSendHistoryRepository mailSendHistoryRepository;

    @DisplayName("메일 발송 테스트. MailSendClient.sendMail()만 stubbing 처리할 수 있다.")
    @Test
    void sendMail() {
        // given
        /*Mockito.doReturn(true)
                .when(mailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());*/
        BDDMockito.doReturn(true)
                .when(mailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());
        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        Mockito.verify(mailSendHistoryRepository, Mockito.times(1)).save(any(MailSendHistory.class));
    }
}
