package org.verifier.core.matchingengine;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.verifier.core.Order;
import org.verifier.core.Trade;
import org.verifier.core.matchingengine.PriceTimePriorityMatchingEngine;
import org.verifier.core.orderbook.InMemoryOrderBook;
import org.verifier.core.orderbook.OrderBook;
import org.verifier.core.orderbook.OrderSide;
import org.verifier.core.tradestore.InMemoryTradeStore;
import org.verifier.core.tradestore.TradeStore;

public class PriceTimePriorityMatchingEngineTest {

    private PriceTimePriorityMatchingEngine engine;

    @Before
    public void setup() {
        OrderBook orderBook = new InMemoryOrderBook();
        TradeStore tradeStore = new InMemoryTradeStore();
        engine = new PriceTimePriorityMatchingEngine(orderBook, tradeStore);
    }

    @Test
    public void testMatchSellOrder() throws Exception {
        OrderBook orderBook = engine.getOrderBook();
        Order sellOrder = new Order("1", OrderSide.SELL, 100, 10);
        orderBook.addOrder(sellOrder);

        Order buyOrder1 = new Order("2", OrderSide.BUY, 100, 5);
        engine.match(buyOrder1);

        Order buyOrder2 = new Order("3", OrderSide.BUY, 100, 15);
        engine.match(buyOrder2);

        List<Trade> trades = new ArrayList<>();
        trades.add(new Trade(sellOrder.getId(), buyOrder1.getId(), 100, 5));
        trades.add(new Trade(sellOrder.getId(), buyOrder2.getId(), 100, 5));
        trades.add(new Trade(sellOrder.getId(), buyOrder2.getId(), 100, 5));

        assertEquals(trades, engine.getTradeStore().getTrades());
    }

    @Test
    public void testMatchBuyOrder() throws Exception {
        OrderBook orderBook = engine.getOrderBook();
        Order buyOrder = new Order("1", OrderSide.BUY, 100, 10);
        orderBook.addOrder(buyOrder);

        Order sellOrder1 = new Order("2", OrderSide.SELL, 100, 5);
        engine.match(sellOrder1);

        Order sellOrder2 = new Order("3", OrderSide.SELL, 100, 15);
        engine.match(sellOrder2);

        List<Trade> trades = new ArrayList<>();
        trades.add(new Trade(buyOrder.getId(), sellOrder1.getId(), 100, 5));
        trades.add(new Trade(buyOrder.getId(), sellOrder2.getId(), 100, 5));
        trades.add(new Trade(buyOrder.getId(), sellOrder2.getId(), 100, 5));

        assertEquals(trades, engine.getTradeStore().getTrades());
    }

}
