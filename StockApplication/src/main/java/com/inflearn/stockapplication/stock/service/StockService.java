package com.inflearn.stockapplication.stock.service;

import com.inflearn.stockapplication.stock.domain.Stock;
import com.inflearn.stockapplication.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow();

        stock.decrease(quantity);
    }
}
