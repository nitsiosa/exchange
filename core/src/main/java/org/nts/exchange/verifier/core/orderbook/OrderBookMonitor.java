package org.nts.exchange.verifier.core.orderbook;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j2;

/**
 * The OrderBookMonitor class monitors changes to an order book and notifies
 * registered listeners of any changes.
 */
@Log4j2
public class OrderBookMonitor {

	private List<OrderBookListener> listeners; // List of registered listeners
	private OrderBook               orderBook; // The order book being monitored

	/**
	 * Constructs a new OrderBookMonitor instance.
	 *
	 * @param orderBook the order book to be monitored
	 */
	public OrderBookMonitor(OrderBook orderBook) {
		this.orderBook = orderBook;
		this.listeners = new ArrayList<>();
	}

	/**
	 * Registers a listener to receive notifications of changes to the order book.
	 *
	 * @param listener the listener to register
	 */
	public void registerListener(OrderBookListener listener) {
		listeners.add(listener);
	}

	/**
	 * Unregisters a listener so that it will no longer receive notifications of
	 * changes to the order book.
	 *
	 * @param listener the listener to unregister
	 */
	public void unregisterListener(OrderBookListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Starts monitoring the order book for changes. This method will run
	 * indefinitely until the program is terminated or an exception occurs. When a
	 * change is detected, all registered listeners will be notified of the change.
	 */
	public void startMonitoring() {
		Thread monitorThread = new Thread(() -> {
			while (true) {
				// Wait for a change in the order book
				synchronized (orderBook) {
					try {
						orderBook.wait();
					} catch (InterruptedException e) {
						log.warn("Interrupted while waiting for order book changes", e);
						Thread.currentThread().interrupt();
					}
				}

				// Notify all listeners of the change
				for (OrderBookListener listener : listeners) {
					listener.onOrderBookChange();
				}
			}
		});

		monitorThread.setDaemon(true);
		monitorThread.start();
	}
}
