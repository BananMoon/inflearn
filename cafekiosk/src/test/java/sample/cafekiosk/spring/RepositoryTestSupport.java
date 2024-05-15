package sample.cafekiosk.spring;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.config.JpaConfig;
/**
 * 레파지토리 테스트의 환경 통합 지원하는 클래스
 */
@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
public abstract class RepositoryTestSupport {
}
