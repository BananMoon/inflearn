package sample.cafekiosk.spring.domain.history;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.BaseEntity;

/**
 * 메일 발송 이력 Entity.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "mail_send_history")
@Entity
public class MailSendHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromEmail;
    private String toEmail;
    private String title;
    private String content;
    @Builder
    private MailSendHistory(String fromEmail, String toEmail, String title, String content) {
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.title = title;
        this.content = content;
    }
}
