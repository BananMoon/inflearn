package sample.cafekiosk.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
/**
 * RestDocs 테스트의 환경 통합 지원하는 클래스
 */
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     * RestDocsSupport를 상속받는 컨트롤러들을 초기화하고, Docs Configuration을 설정하여 mockMvc를 커스텀 생성한다.
     * @param provider RestDocs 환경 제공 Provider
     */
    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())       // 스프링에서 만들어주는 MockMvc를 주입해줬지만, 이번에는 직접 생성한다.
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))   // 문서를 만들기 위한 설정에 provider 제공
                .build();
    }

    protected abstract Object initController();
}
