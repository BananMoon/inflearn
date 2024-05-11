package com.inflearn.stockapplication.stock.repository;

import com.inflearn.stockapplication.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
