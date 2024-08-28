package com.inflearn.stockapplication.stock.service;

import com.inflearn.stockapplication.stock.domain.Stock;
import com.inflearn.stockapplication.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.save(Stock.builder().productId(1L).quantity(100L).build());
    }

    @Test
    @DisplayName("재고 감소시킨다.")
    void decrease() {
        stockService.decrease(1L, 1L);

        // when
        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertThat(stock.getQuantity()).isEqualTo(99L);
    }

    @Test
    @DisplayName("[실패] 동시에 재고 감소시킨다.")
    void decrease_동시에_100개_요청() throws InterruptedException {
        // given
        // 멀티스레드
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        // when
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // then
        assertThat(stock.getQuantity()).isZero();
    }

    @Test
    @DisplayName("[성공 - Trnsctional 제거 및 synchronized 추가] 동시에 재고 감소시킨다.")
    void decrease_동시에_100개_요청_noTransactional_synchronized() throws InterruptedException {
        // given
        // 멀티스레드
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease_noTransactional(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        // when
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // then
        assertThat(stock.getQuantity()).isZero();
    }
}