package org.verifier.core.orderbook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.verifier.core.Order;

import lombok.extern.log4j.Log4j2;

/**
 * Represents the order book for a trading instrument where orders are stored in
 * memory.
 */
@Log4j2
public class InMemoryOrderBook implements OrderBook {

	private final Map<String, Order>                orders;
	private final Map<Integer, NavigableSet<Order>> buySide;
	private final Map<Integer, NavigableSet<Order>> sellSide;
	private final ReentrantReadWriteLock            lock;
	private List<OrderBookListener>                 listeners;
	private ConcurrentLinkedQueue<Order>            newOrders;

	/**
	 * Creates a new instance of the order book
	 */
	public InMemoryOrderBook() {
		this.orders    = new ConcurrentHashMap<>();
		this.buySide   = new ConcurrentSkipListMap<>();
		this.sellSide  = new ConcurrentSkipListMap<>();
		this.lock      = new ReentrantReadWriteLock(true);
		this.listeners = new ArrayList<>();
		this.newOrders = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void addListener(OrderBookListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(OrderBookListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners() {
		for (OrderBookListener listener : listeners) {
			listener.onOrderBookChange();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Order> getNewOrders() {
		List<Order> orders = new ArrayList<>();
		while (!newOrders.isEmpty()) {
			Order order = newOrders.poll();
			if (order != null) {
				orders.add(order);
			}
		}
		return orders;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addOrder(Order newOrder) {
		lock.writeLock().lock();
		try {
			Order existingOrder = orders.get(newOrder.getId());
			if (existingOrder == null || existingOrder.getQuantity() != newOrder.getQuantity()) {
				orders.put(newOrder.getId(), newOrder);
				if (newOrder.getSide() == OrderSide.BUY) {
					if(log.isDebugEnabled()) {
						log.debug("New Buy Order received {}",newOrder);	
					}
					addOrderToBuySide(newOrder);
				} else {
					if(log.isDebugEnabled()) {
						log.debug("New Sell Order received {}",newOrder);	
					}
					addOrderToSellSide(newOrder);
				}
				newOrders.offer(newOrder);
				notifyListeners();
				
			}
		} catch (Exception e) {
			log.error("Error addin order {} : {}",newOrder,e.getMessage(),e);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Adds an order to the buy side of the order book.
	 * 
	 * @param order the order to add
	 */
	private void addOrderToBuySide(Order order) {
		NavigableSet<Order> ordersAtPrice = buySide.computeIfAbsent(order.getPrice(),k -> new ConcurrentSkipListSet<>());
		ordersAtPrice.add(order);
	}

	/**
	 * Adds an order to the sell side of the order book.
	 * 
	 * @param order the order to add
	 */
	private void addOrderToSellSide(Order order) {
		NavigableSet<Order> ordersAtPrice = sellSide.computeIfAbsent(order.getPrice(),k -> new ConcurrentSkipListSet<>());
		ordersAtPrice.add(order);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeOrder(String orderId) {
		lock.writeLock().lock();
		try {
			Order order = orders.get(orderId);
			if (order != null) {
				orders.remove(orderId);
				if (order.getSide() == OrderSide.BUY) {
					removeOrderFromBuySide(order);
				} else {
					removeOrderFromSellSide(order);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Removes an order from the order book. The order is removed from either the
	 * buy side or the sell side, depending on its side (BUY or SELL) and the price
	 * at which it is placed.
	 *
	 * @param order the order to remove from the book
	 */
	private void removeOrderFromBuySide(Order order) {
		NavigableSet<Order> ordersAtPrice = buySide.get(order.getPrice());
		ordersAtPrice.remove(order);
		if (ordersAtPrice.isEmpty()) {
			buySide.remove(order.getPrice());
		}
	}

	/**
	 * Removes an order from the order book. The order is removed from either the
	 * buy side or the sell side, depending on its side (BUY or SELL) and the price
	 * at which it is placed.
	 *
	 * @param order the order to remove from the book
	 */
	private void removeOrderFromSellSide(Order order) {
		NavigableSet<Order> ordersAtPrice = sellSide.get(order.getPrice());
		ordersAtPrice.remove(order);
		if (ordersAtPrice.isEmpty()) {
			sellSide.remove(order.getPrice());
		}
	}

	/**
	 * Returns all the orders in the order book, in no particular order.
	 *
	 * @return a collection of all orders in the book
	 */
	@Override
	public List<Order> getOrders() {
		List<Order> allOrders = new ArrayList<>();
		allOrders.addAll(getSellOrders());
		allOrders.addAll(getBuyOrders());

		return allOrders;
	}

	/**
	 * Returns all the orders in the order book, in no particular order.
	 *
	 * @return a collection of all orders in the book
	 */
	@Override
	public List<Order> getSellOrders() {
		List<Order> sellOrdersList = new ArrayList<>();
		for (NavigableSet<Order> sellOrders : sellSide.values()) {
			sellOrdersList.addAll(sellOrders);
		}
		return new ArrayList<>(sellOrdersList);
	}

	/**
	 * Returns all the orders in the order book, in no particular order.
	 *
	 * @return a collection of all orders in the book
	 */
	@Override
	public List<Order> getBuyOrders() {
		List<Order> buyOrdersList = new ArrayList<>();
		for (NavigableSet<Order> buyOrders : buySide.values()) {
			buyOrdersList.addAll(buyOrders);
		}
		return new ArrayList<>(buyOrdersList);
	}

	/**
	 * Returns a list of sell orders at the specified price level.
	 * 
	 * @param price the price level to look up
	 * @return a list of sell orders at the specified price level, or an empty list
	 *         if none exist
	 */
	@Override
	public List<Order> getSellOrdersAtPrice(int price) {
		if(log.isDebugEnabled()) {
			log.debug("Searching for Orders selling under {}",price );
		}
		
		List<Integer> eligibleKeys = sellSide.keySet().
		stream().filter(key -> key.compareTo(price)<=0).collect(Collectors.toList());
		
		return  sellSide.entrySet().stream()
			    .filter(entry -> eligibleKeys.contains(entry.getKey()))
			    .map(Map.Entry::getValue)
			    .flatMap(NavigableSet::stream)
			    .sorted(Comparator.comparingLong(Order::getTimestamp))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of buy orders at the specified price.
	 * 
	 * @param price the price to retrieve the buy orders for
	 * @return a list of buy orders at the specified price
	 */
	public List<Order> getBuyOrdersAtPrice(int price) {
		if(log.isDebugEnabled()) {
			log.debug("Searching for Orders buying over {}",price );
		}

		List<Integer> eligibleKeys = buySide.keySet().
		stream().filter(key -> key.compareTo(price)>=0).collect(Collectors.toList());
		
		return buySide.entrySet().stream()
			    .filter(entry -> eligibleKeys.contains(entry.getKey()))
			    .map(Map.Entry::getValue)
			    .flatMap(NavigableSet::stream)
			    .sorted(Comparator.comparingLong(Order::getTimestamp))
			    .collect(Collectors.toList());
	}
}
