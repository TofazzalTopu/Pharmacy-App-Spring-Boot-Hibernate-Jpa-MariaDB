package com.asl.pms.service;

import com.asl.pms.model.Stock;
import com.asl.pms.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotificationService {

    @Autowired
    StockRepository stockRepository;

    public List<Stock> getStockList() {
        List<Stock> stockList = new ArrayList<>();

        stockList = stockRepository.findAll()
                .stream()
                .filter(stock ->
                        stock.getDrug().getSafetyMargin() > stock.getQty())
                .collect(Collectors.toList());

        return stockList;
    }
}
