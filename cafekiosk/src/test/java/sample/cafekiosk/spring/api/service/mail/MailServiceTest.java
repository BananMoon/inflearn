package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.MailSendHistory;
import sample.cafekiosk.spring.domain.history.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * 순수 Mockito를 사용하여 테스트해본다.
 * - 애노테이션으로도 제공한다. (단, @ExtendWith 를 걸어줘야함)
 */
//@ExtendWith(MockitoExtension.class)
class MailServiceTest {
//    @Mock
    private MailSendClient mailSendClient;
//    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;
//    @InjectMocks
    private MailService mailService;
    @BeforeEach
    void setUp() {
        mailSendClient = Mockito.mock(MailSendClient.class);
        mailSendHistoryRepository = Mockito.mock(MailSendHistoryRepository.class);
        mailService = new MailService(mailSendClient, mailSendHistoryRepository);
    }
    @DisplayName("메일 발송 테스트")
    @Test
    void sendMail() {
        // given
        /*Mockito.when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Boolean.TRUE);
*/
        // BDDMockito를 사용한다.
        BDDMockito.given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .willReturn(Boolean.TRUE);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        Mockito.verify(mailSendHistoryRepository, Mockito.times(1)).save(any(MailSendHistory.class));
    }
}