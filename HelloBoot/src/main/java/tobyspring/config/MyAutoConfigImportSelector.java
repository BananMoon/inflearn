package tobyspring.config;

import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAutoConfigImportSelector implements DeferredImportSelector {
    // 빈 로드할 때 사용하는 클래스 로더. BeanClassLoaderAware 구현 대신 DI
    private final ClassLoader classLoader;

    public MyAutoConfigImportSelector(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 빈 ClassLoader : 클래스 Path에서 리소스 읽어올 때 사용하는 클래스 로더.(빈 생성하기 위해 빈 클래스를 로드하는 사용)
        // 인자로 애노테이션 클래스 :

        /*
        보기 어려운 소스코드
        return StreamSupport.stream(candidates.spliterator(), false).toArray(String[]::new);
         */
        List<String> autoConfigs = new ArrayList<>();
        /*
        for(String candidate : ImportCandidates.load(MyAutoConfiguration.class, classLoader)) {
            autoConfigs.add(candidate);
        }
        */

        ImportCandidates.load(MyAutoConfiguration.class, classLoader).forEach(candidate ->
                autoConfigs.add(candidate)
        );
        /* 메서드 레퍼런스로 대체
        ImportCandidates.load(MyAutoConfiguration.class, classLoader).forEach(autoConfigs::add);
         */
        // array로 변환 방법 1.
        return autoConfigs.toArray(new String[0]);
        // collection -> array로 type safe하게 바꾸는 작업
        // 방법 2.
//        return autoConfigs.stream().toArray(String[]::new);
        // 방법 3.
//        return Arrays.copyOf(autoConfigs.toArray(), autoConfigs.size(), String[].class);
    }
}
