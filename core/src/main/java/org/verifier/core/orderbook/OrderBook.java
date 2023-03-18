package org.verifier.core.orderbook;

import java.util.List;

import org.verifier.core.Order;

/**
 * Defines the interface for an order book for a trading instrument.
 */
public interface OrderBook {

	/**
	 * Adds a new order to the order book.
	 * 
	 * @param order the order to add
	 */
	void addOrder(Order order);

	/**
	 * Removes an existing order from the order book.
	 * 
	 * @param orderId the ID of the order to remove
	 */
	void removeOrder(String orderId);

	/**
	 * Gets all the orders in the order book.
	 * 
	 * @return an unmodifiable collection of orders
	 */
	List<Order> getOrders();

	/**
	 * Gets all new  orders in the order book, since the time it was last checked .
	 * 
	 * @return an unmodifiable collection of orders
	 */
	List<Order> getNewOrders();
	
	/**
	 * Gets all sell the orders in the order book.
	 * 
	 * @return an unmodifiable collection of orders
	 */
	List<Order> getSellOrders();

	/**
	 * Gets all buy the orders in the order book.
	 * 
	 * @return an unmodifiable collection of orders
	 */
	List<Order> getBuyOrders();

	/**
	 * Returns a list of sell orders at the specified price level.
	 * 
	 * @param price the price level to look up
	 * @return a list of sell orders at the specified price level, or an empty list
	 *         if none exist
	 */
	List<Order> getSellOrdersAtPrice(int price);

	/**
	 * 
	 * Gets all the buy orders at a particular price level in the order book.
	 * 
	 * @param price the price level at which to get the buy orders
	 * @return a list of all the buy orders at the specified price level
	 */
	List<Order> getBuyOrdersAtPrice(int price);

	/**
	 * 
	 * Adds a listener to the order book that will be notified whenever there is a
	 * change to the buy or sell sides of the book.
	 * 
	 * @param listener the listener to add
	 */
	void addListener(OrderBookListener listener);

	/**
	 * 
	 * Removes a listener from the order book.
	 * 
	 * @param listener the listener to remove
	 */
	void removeListener(OrderBookListener listener);
}