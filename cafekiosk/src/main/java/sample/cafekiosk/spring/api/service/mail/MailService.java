package sample.cafekiosk.spring.api.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.MailSendHistory;
import sample.cafekiosk.spring.domain.history.MailSendHistoryRepository;

/**
 * 메일 발송 Service.
 */
@RequiredArgsConstructor
@Service
public class MailService {
    private final MailSendClient mailSendClient;
    private final MailSendHistoryRepository mailSendHistory;
    /**
     * 메일 발송 API 호출 및 히스토리 저장한다.
     * @param fromEmail
     * @param toEmail
     * @param title
     * @param content
     * @return
     */
    public boolean sendMail(String fromEmail, String toEmail, String title, String content) {
        // 메일 발송 클라이언트 API 호출
        boolean result = mailSendClient.sendEmail(fromEmail, toEmail, title, content);
        // 히스토리 저장
        if (result) {
            mailSendHistory.save(MailSendHistory.builder()
                    .fromEmail(fromEmail)
                    .toEmail(toEmail)
                    .title(title)
                    .content(content)
                    .build()
            );
            return true;
        }
        return false;
    }
}
