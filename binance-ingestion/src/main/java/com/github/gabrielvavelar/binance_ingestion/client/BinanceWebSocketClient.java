package com.github.gabrielvavelar.binance_ingestion.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.gabrielvavelar.binance_ingestion.model.TradeEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;

public class BinanceWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(BinanceWebSocketClient.class);

    private final KafkaTemplate<String, TradeEvent> kafkaTemplate;

    public BinanceWebSocketClient(String symbol, KafkaTemplate<String, TradeEvent> kafkaTemplate) throws URISyntaxException {
        super(new URI("wss://stream.binance.com:9443/ws/" + symbol + "@trade"));
        this.kafkaTemplate = kafkaTemplate;
    }

    public void init() {
        new Thread(() -> {
            try {
                super.connectBlocking();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("WebSocket connection opened successfully - Status: {}", handshake.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        try {
            TradeEvent tradeEvent = objectMapper.readValue(message, TradeEvent.class);
            kafkaTemplate.send("crypto-trades", tradeEvent.getSymbol(), tradeEvent);
            log.info("Trade sent: {} - {}", tradeEvent.getSymbol(), tradeEvent.getPrice());
        } catch (JsonProcessingException e) {
            log.error("Failed to convert message: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("WebSocket connection closed. Code: {}, Reason: {}, Remote: {}. Attempting reconnect...",
                code, reason, remote);
        this.reconnect();
    }

    @Override
    public void onError(Exception e) {
        log.error("WebSocket error occurred: {}", e.getMessage(), e);
    }
}
