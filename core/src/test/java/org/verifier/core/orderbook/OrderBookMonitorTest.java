package org.verifier.core.orderbook;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.verifier.core.Order;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;

class OrderBookMonitorTest {

	private OrderBook         orderBook;
	private OrderBookListener listener;
	private OrderBookMonitor  orderBookMonitor;

	@BeforeEach
	void setUp() {
		orderBook        = mock(OrderBook.class);
		listener         = mock(OrderBookListener.class);
		
		orderBook.addListener(listener);
		doAnswer((invocation) -> {
			listener.onOrderBookChange();
		    return null;
		}).when(orderBook).addOrder(any());
		
		
		orderBookMonitor = new OrderBookMonitor(orderBook);
	}

	@Test
	void registerListener_shouldAddListener() throws NoSuchFieldException, IllegalAccessException {
		OrderBookListener listener = new OrderBookListener() {
			@Override
			public void onOrderBookChange() {

			}
		};

		orderBookMonitor.registerListener(listener);

		Field listenersField = OrderBookMonitor.class.getDeclaredField("listeners");
		listenersField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<OrderBookListener> listeners = (List<OrderBookListener>) listenersField.get(orderBookMonitor);

		assertTrue(listeners.contains(listener));
	}

	@Test
	void unregisterListener_shouldRemoveListener() throws Exception {
		OrderBookListener       listener  = new OrderBookListener() {
												@Override
												public void onOrderBookChange() {
													// Do nothing
												}
											};

		// Register the listener using reflection
		@SuppressWarnings("unchecked")
		List<OrderBookListener> listeners = (List<OrderBookListener>) FieldUtils.readField(orderBookMonitor,
				"listeners", true);
		listeners.add(listener);

		// Unregister the listener using the method being tested
		orderBookMonitor.unregisterListener(listener);

		// Ensure that the listener was removed from the list using reflection
		assertEquals(0, listeners.size());
	}

	@Test
	void startMonitoring_shouldNotifyListenersOnOrderBookChange() throws InterruptedException {

		orderBookMonitor.startMonitoring();
		verify(listener, never()).onOrderBookChange();
		orderBook.addOrder(new Order("order1", OrderSide.BUY, 100, 10));
		Thread.sleep(100);
		verify(listener, times(1)).onOrderBookChange();
	}
}
