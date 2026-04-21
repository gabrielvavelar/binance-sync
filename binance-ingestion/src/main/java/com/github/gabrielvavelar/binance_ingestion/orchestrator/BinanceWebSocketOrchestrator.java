package com.github.gabrielvavelar.binance_ingestion.orchestrator;

import com.github.gabrielvavelar.binance_ingestion.client.BinanceWebSocketClient;
import com.github.gabrielvavelar.binance_ingestion.model.TradeEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class BinanceWebSocketOrchestrator {

    @Autowired
    private KafkaTemplate<String, TradeEvent> kafkaTemplate;

    @PostConstruct
    public void init() {
        try {
            new BinanceWebSocketClient("btcusdt", kafkaTemplate).init();
            new BinanceWebSocketClient("ethusdt", kafkaTemplate).init();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}