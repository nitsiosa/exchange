package org.verifier.core.orderbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.orderbook.InMemoryOrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBookListener;
import org.nts.exchange.verifier.core.orderbook.OrderSide;

class InMemoryOrderBookTest {

	private OrderBook orderBook;

	@BeforeEach
	void setUp() {
		orderBook = new InMemoryOrderBook();
	}

	@Test
	void addListener_shouldAddListener() throws Exception {
		OrderBookListener listener = new OrderBookListener() {
			@Override
			public void onOrderBookChange() {

			}
		};
		orderBook.addListener(listener);

		// Get the listeners field using reflection
		Field listenersField = InMemoryOrderBook.class.getDeclaredField("listeners");
		listenersField.setAccessible(true);

		// Get the listeners from the order book instance using reflection
		@SuppressWarnings("unchecked")
		List<OrderBookListener> listeners = (List<OrderBookListener>) listenersField.get(orderBook);

		// Assert that the listeners list contains the added listener
		assertTrue(listeners.contains(listener));
	}

	@Test
	void removeListener_shouldRemoveListener() throws Exception {
		OrderBookListener listener = new OrderBookListener() {
			@Override
			public void onOrderBookChange() {

			}
		};
		orderBook.addListener(listener);
		orderBook.removeListener(listener);

		// Get the "listeners" field of the InMemoryOrderBook instance using reflection
		Field listenersField = InMemoryOrderBook.class.getDeclaredField("listeners");
		listenersField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<OrderBookListener> listeners = (List<OrderBookListener>) listenersField.get(orderBook);

		// Assert that the listener was removed from the list of listeners
		assertFalse(listeners.contains(listener));
	}

	@Test
	void getNewOrders_shouldReturnEmptyListIfNoNewOrders() {
		List<Order> newOrders = orderBook.getNewOrders();
		assertNotNull(newOrders);
		assertEquals(0, newOrders.size());
	}

	@Test
	void addOrder_shouldAddOrderToOrderBook() {
		Order order = new Order("1", OrderSide.BUY, 100, 10);
		orderBook.addOrder(order);
		List<Order> buyOrders = orderBook.getBuyOrders();
		assertNotNull(buyOrders);
		assertEquals(1, buyOrders.size());
		assertEquals(order, buyOrders.get(0));
	}

	@Test
	void addOrder_shouldNotAddDuplicateOrder() {
		Order order = new Order("1", OrderSide.BUY, 100, 10);
		orderBook.addOrder(order);
		orderBook.addOrder(order);
		List<Order> buyOrders = orderBook.getBuyOrders();
		assertNotNull(buyOrders);
		assertEquals(1, buyOrders.size());
	}

	@Test
	void removeOrder_shouldRemoveOrderFromOrderBook() {
		Order order = new Order("1", OrderSide.BUY, 100, 10);
		orderBook.addOrder(order);
		orderBook.removeOrder("1");
		List<Order> buyOrders = orderBook.getBuyOrders();
		assertNotNull(buyOrders);
		assertEquals(0, buyOrders.size());
	}

	@Test
	void getOrders_shouldReturnAllOrders() {
		Order order1 = new Order("1", OrderSide.BUY, 100, 10);
		Order order2 = new Order("2", OrderSide.SELL, 110, 10);
		orderBook.addOrder(order1);
		orderBook.addOrder(order2);
		List<Order> orders = orderBook.getOrders();
		assertNotNull(orders);
		assertEquals(2, orders.size());
	}

	@Test
	void getSellOrdersAtPrice_shouldReturnSellOrdersAtSpecifiedPrice() {
		Order order1 = new Order("1", OrderSide.SELL, 100, 10);
		Order order2 = new Order("2", OrderSide.SELL, 100, 20);
		orderBook.addOrder(order1);
		orderBook.addOrder(order2);
		List<Order> sellOrders = orderBook.getSellOrdersAtPrice(100);
		assertNotNull(sellOrders);
		assertEquals(2, sellOrders.size());
	}

	@Test
	void getBuyOrdersAt() {
		Order order1 = new Order("order1", OrderSide.BUY, 100, 10);
		Order order2 = new Order("order2", OrderSide.BUY, 200, 20);
		Order order3 = new Order("order3", OrderSide.BUY, 150, 15);
		Order order4 = new Order("order4", OrderSide.SELL, 250, 25);

		orderBook.addOrder(order1);
		orderBook.addOrder(order2);
		orderBook.addOrder(order3);
		orderBook.addOrder(order4);

		List<Order> buyOrders = orderBook.getBuyOrdersAtPrice(150);
		buyOrders.forEach(System.out::println);
		
		assertEquals(2, buyOrders.size());
		assertTrue(buyOrders.contains(order3));
		assertTrue(buyOrders.contains(order2));
		assertFalse(buyOrders.contains(order1));
	}
}
