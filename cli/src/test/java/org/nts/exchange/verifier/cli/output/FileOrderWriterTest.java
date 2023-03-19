package org.nts.exchange.verifier.cli.output;

import org.junit.jupiter.api.Test;
import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.matchingengine.MatchingEngine;
import org.nts.exchange.verifier.core.matchingengine.PriceTimePriorityMatchingEngine;
import org.nts.exchange.verifier.core.orderbook.InMemoryOrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderSide;
import org.nts.exchange.verifier.core.tradestore.InMemoryTradeStore;
import org.nts.exchange.verifier.core.tradestore.TradeStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileOrderWriterTest {

    @Test
    void testPush() throws IOException {
        String fileName = "test_orders.txt";
        OrderBook orderBook = new InMemoryOrderBook();
        TradeStore tradeStore = new InMemoryTradeStore();
        
        MatchingEngine matchingEngine = new PriceTimePriorityMatchingEngine(orderBook , tradeStore);

        // Add some orders to the matching engine
        orderBook.addOrder(new Order("1", OrderSide.BUY ,100 ,10));
        orderBook.addOrder(new Order("2", OrderSide.SELL,110 , 5));
        orderBook.addOrder(new Order("3", OrderSide.SELL,140 ,15));

        FileOrderWriter writer = new FileOrderWriter(fileName, matchingEngine);
        writer.push();

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("1,B,100,10");
        expectedLines.add("2,S,110,5");
        expectedLines.add("3,S,140,15");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> actualLines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                actualLines.add(line);
                line = reader.readLine();
            }

            assertEquals(expectedLines, actualLines);
        }
    }
}
