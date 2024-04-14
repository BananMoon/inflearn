package sample.cafekiosk.spring.client.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 메일 발송 클라이언트
 */
@Slf4j
@Component
public class MailSendClient {

    public boolean sendEmail(String fromEmail, String toEmail, String title, String content) {
        log.info("메일 전송");

        return true;
    }

    public void a() {
        log.info("a");
    }
    public void b() {
        log.info("b");
    }
    public void c() {
        log.info("c");
    }
}
