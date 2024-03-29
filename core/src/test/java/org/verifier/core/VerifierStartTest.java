package org.verifier.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.Trade;
import org.nts.exchange.verifier.core.matchingengine.PriceTimePriorityMatchingEngine;
import org.nts.exchange.verifier.core.matchingengine.exception.InvalidQuantityException;
import org.nts.exchange.verifier.core.orderbook.InMemoryOrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderSide;
import org.nts.exchange.verifier.core.tradestore.InMemoryTradeStore;
import org.nts.exchange.verifier.core.tradestore.TradeStore;


class VerifierStartTest {

	private OrderBook        orderBook;
	private TradeStore       tradeStore;

	@BeforeEach
	public void setUp() {
		orderBook      = new InMemoryOrderBook();
		tradeStore     = new InMemoryTradeStore();
		
		new PriceTimePriorityMatchingEngine(orderBook, tradeStore);
	}

	@Test
	void testMatchingExample() throws InvalidQuantityException {
		// Add some buy orders
		orderBook.addOrder(new Order("1", OrderSide.BUY,99, 1000));
		orderBook.addOrder(new Order("2", OrderSide.BUY,99, 500));
		orderBook.addOrder(new Order("3", OrderSide.BUY,98, 1200));
		
		
		// Add some sell orders
		orderBook.addOrder(new Order("4", OrderSide.SELL,101, 2000));
		orderBook.addOrder(new Order("5", OrderSide.SELL,95, 2000));

		List<Trade> trades = tradeStore.getTrades();
		
		assertEquals(3, trades.size());
		Order firstBuyOrder = orderBook.getBuyOrders().get(0);
		Order firstSellOrder = orderBook.getSellOrders().get(0);
		assertEquals(98, firstBuyOrder.getPrice());
		assertEquals(700, firstBuyOrder.getQuantity());
		
		assertEquals(101, firstSellOrder.getPrice());
		assertEquals(2000, firstSellOrder.getQuantity());
	}
	
	@Test
	void testMatchingExample2() throws InvalidQuantityException {
		// Add some orders
		orderBook.addOrder(new Order("10000", OrderSide.BUY ,98, 25500));
		orderBook.addOrder(new Order("10005", OrderSide.SELL,105, 20000));
		orderBook.addOrder(new Order("10001", OrderSide.SELL,100, 500));
		orderBook.addOrder(new Order("10002", OrderSide.SELL,100, 10000));
		orderBook.addOrder(new Order("10003", OrderSide.BUY,99, 50000));
		orderBook.addOrder(new Order("10004", OrderSide.SELL,103, 100));
		
		List<Trade> trades = tradeStore.getTrades();
				
		assertEquals(0, trades.size());
	}

	@Test
	void testMatchingExample3() throws InvalidQuantityException {
		// Add some orders
		orderBook.addOrder(new Order("10000", OrderSide.BUY,98, 25500));
		orderBook.addOrder(new Order("10003", OrderSide.BUY,99, 50000));
		
		
		orderBook.addOrder(new Order("10001", OrderSide.SELL,100, 500));
		orderBook.addOrder(new Order("10002", OrderSide.SELL,100, 10000));
		orderBook.addOrder(new Order("10004", OrderSide.SELL,103, 100));
		orderBook.addOrder(new Order("10005", OrderSide.SELL,105, 20000));
		
		orderBook.addOrder(new Order("10006", OrderSide.BUY,105, 16000));
		
		List<Trade> trades = tradeStore.getTrades();
		
		assertEquals(4, trades.size());
	}

}