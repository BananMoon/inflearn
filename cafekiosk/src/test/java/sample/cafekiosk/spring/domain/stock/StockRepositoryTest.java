package sample.cafekiosk.spring.domain.stock;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.RepositoryTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class StockRepositoryTest extends RepositoryTestSupport {

    @Autowired
    JPAQueryFactory jpaQueryFactory;
    @Autowired
    private StockRepository stockRepository;

    @DisplayName("상품번호 리스트로 재고를 조회한다.")
    @Test
    void findAllByProductNumberIn() {
        // given
        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 5);
        Stock stock3 = Stock.create("003", 4);

        stockRepository.saveAll(List.of(stock1, stock2, stock3));
        // when
        List<Stock> results = stockRepository.findAllByProductNumberIn(List.of("001", "002"));

        // then
        assertThat(results)
                .hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 2),
                        tuple("002", 5)
                );
    }
}