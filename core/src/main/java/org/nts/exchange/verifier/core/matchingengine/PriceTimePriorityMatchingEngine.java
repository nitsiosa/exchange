package org.nts.exchange.verifier.core.matchingengine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.Trade;
import org.nts.exchange.verifier.core.matchingengine.exception.InvalidQuantityException;
import org.nts.exchange.verifier.core.matchingengine.exception.MatchingEngineException;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBookListener;
import org.nts.exchange.verifier.core.orderbook.OrderSide;
import org.nts.exchange.verifier.core.tradestore.TradeStore;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * The PriceTimePriorityMatchingEngine is an implementation of the
 * {@link MatchingEngine} interface
 * 
 * that matches buy and sell orders based on the price-time priority rule.
 * 
 * This engine matches the highest buy order with the lowest sell order, and
 * creates trades for all matching orders.
 * 
 * The engine also stores all trades in a {@link TradeStore} instance.
 * 
 * This implementation subscribes to the {@link OrderBook} events using an
 * {@link OrderBookListener}.
 * 
 * When a new order is added to the order book, this implementation processes it
 * by calling its {@link #match(Order)}
 * 
 * method, which attempts to match the new order with existing orders in the
 * order book and creates trades if any matching orders are found.
 */
@Log4j2
public class PriceTimePriorityMatchingEngine implements MatchingEngine, OrderBookListener {
	private final OrderBook  orderBook;  // The order book to match orders against
	private final TradeStore tradeStore; // The trade store to store trades in

	/**
	 * 
	 * Constructs a new PriceTimePriorityMatchingEngine instance.
	 * 
	 * @param orderBook  the order book to match orders against
	 * @param tradeStore the trade store to store trades in
	 */
	public PriceTimePriorityMatchingEngine(OrderBook orderBook, TradeStore tradeStore) {
		this.orderBook  = orderBook;
		this.tradeStore = tradeStore;
		this.orderBook.addListener(this);
	}

	/**
	 * 
	 * This method is called whenever the monitored {@link OrderBook} instance
	 * changes.
	 * 
	 * When this method is called, the implementation processes all new orders in
	 * the order book by calling the {@link #match(Order)} method.
	 */
	@Override
	public void onOrderBookChange() {
		List<Order> newOrders = orderBook.getNewOrders();
		
		if (log.isDebugEnabled() && !newOrders.isEmpty()) {
			log.debug("OrderBook has been changed ... processing new orders");
			newOrders.forEach(log::debug);
		}

		newOrders.forEach(this::match);

	}

	/**
	 * 
	 * Returns the order book being used by this matching engine.
	 * 
	 * @return the order book being used by this matching engine
	 */
	@Override
	public OrderBook getOrderBook() {
		return orderBook;
	}

	/**
	 * 
	 * Attempts to match the specified new order with existing orders in the order
	 * book.
	 * 
	 * This method matches the highest buy order with the lowest sell order, and
	 * creates trades for all matching orders.
	 * 
	 * If no matching orders are found, this method simply adds the new order to the
	 * order book.
	 * 
	 * @param newOrder the new order to match
	 * 
	 * @return an array of trades created as a result of matching orders
	 * 
	 * @throws MatchingEngineException if an error occurs while attempting to create
	 *                                 a trade
	 */
	@Override
	public Trade[] match(Order newOrder) throws MatchingEngineException {
		List<Trade> trades = new ArrayList<>();
		try {
			if (newOrder.getSide() == OrderSide.BUY) {
				trades.addAll(matchBuyOrder(orderBook, newOrder));
			} else {
				trades.addAll(matchSellOrder(orderBook, newOrder));
			}
		} catch (Exception e) {
			throw new MatchingEngineException("Error creating match", e);
		}

		// Add all trades to Trade store
		tradeStore.storeTrades(trades);
		if (log.isDebugEnabled() & !trades.isEmpty()) {
			log.debug("Trades happened... ");
			trades.forEach(log::debug);
		}

		return trades.toArray(new Trade[trades.size()]);
	}
	
	/**
	 * Returns a list of all sell orders at the specified price level.
	 *
	 * @param price the price level to get sell orders for
	 * @return a list of all sell orders at the specified price level
	 */
	public List<Order> getSellOrdersAtPrice(int price) {

		return orderBook.getSellOrders().stream().filter(order -> order.getPrice() == price)
				.collect(Collectors.toList());
	}

	/**
	 * Matches a sell order against buy orders in the specified order book.
	 * 
	 * @param orderBook the order book to match against
	 * @param sellOrder the sell order to match
	 * @return a list of trades resulting from the match, or an empty list if no
	 *         trades occurred
	 * @throws InvalidQuantityException
	 */
	private List<Trade> matchSellOrder(OrderBook orderBook, Order sellOrder) throws InvalidQuantityException {
		List<Trade> trades           = new ArrayList<>();

		// Get all buy orders at the specified price level
		List<Order> buyOrdersAtPrice = orderBook.getBuyOrdersAtPrice(sellOrder.getPrice());

		if (log.isDebugEnabled()) {

			if (!buyOrdersAtPrice.isEmpty()) {
				buyOrdersAtPrice.forEach(log::debug);

			} else {
				log.debug("Buy side orders at {} not found ", sellOrder.getPrice());
			}
		}

		// Match the sell order against the buy orders
		int totalQuantityMatched = 0;
		for (Order buyOrder : buyOrdersAtPrice) {
			if (totalQuantityMatched == sellOrder.getQuantity()) {
				break; // We've matched the entire sell order
			}
			// Calculate the quantity to match between the sell and buy orders
			int quantityToMatch = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity() - totalQuantityMatched);
			if (quantityToMatch > 0) {
				// Update the quantities of the orders and add the trade to the list
				totalQuantityMatched += quantityToMatch;
				buyOrder.subtractQuantity(quantityToMatch);
				if (buyOrder.getQuantity() == 0) {
					orderBook.removeOrder(buyOrder.getId()); // Remove the buy order from the book if it's been
																// completely filled
				}
				trades.add(new Trade(sellOrder.getId(), buyOrder.getId(), buyOrder.getPrice(), quantityToMatch));
				//sellOrder.setQuantity(quantityToMatch);
			}
		}

		// If the sell order hasn't been completely matched, add it to the book
		if (totalQuantityMatched < sellOrder.getQuantity()) {
			if(totalQuantityMatched>0) {
				sellOrder.subtractQuantity(totalQuantityMatched);
			}
			orderBook.addOrder(sellOrder);
		}
		// If the sell order has been completely matched, remove it from the book
		else{
			orderBook.removeOrder(sellOrder.getId());
		}

		return trades;
	}

	/**
	 * Matches a buy order against sell orders in the specified order book.
	 * 
	 * @param orderBook the order book to match against
	 * @param buyOrder  the buy order to match
	 * @return a list of trades resulting from the match, or an empty list if no
	 *         trades occurred
	 * @throws InvalidQuantityException
	 */
	private List<Trade> matchBuyOrder(OrderBook orderBook, Order buyOrder) throws InvalidQuantityException {
		List<Trade> trades               = new ArrayList<>();

		// Get all sell orders at the specified price level.
		List<Order> sellOrdersAtPrice    = orderBook.getSellOrdersAtPrice(buyOrder.getPrice());

		// Match the buy order against the sell orders
		int         totalQuantityMatched = 0;
		for (Order sellOrder : sellOrdersAtPrice) {
			if (totalQuantityMatched == buyOrder.getQuantity()) {
				break; // We've matched the entire buy order
			}
			// Calculate the quantity to match between the buy and sell orders
			int quantityToMatch = Math.min(sellOrder.getQuantity(), buyOrder.getQuantity() - totalQuantityMatched);
			if (quantityToMatch > 0) {
				// Update the quantities of the orders and add the trade to the list
				totalQuantityMatched += quantityToMatch;
				sellOrder.subtractQuantity(quantityToMatch);
				if (sellOrder.getQuantity() == 0) {
					orderBook.removeOrder(sellOrder.getId()); // Remove the sell order from the book if it's been
																// completely filled
				}
				trades.add(new Trade(buyOrder.getId(), sellOrder.getId(), sellOrder.getPrice(), quantityToMatch));
				//buyOrder.setQuantity(quantityToMatch);
			}
		}

		// If the buy order hasn't been completely matched, add it to the book
		if (totalQuantityMatched < buyOrder.getQuantity()) {
			if(totalQuantityMatched>0) {
				buyOrder.subtractQuantity(totalQuantityMatched);
			}
			orderBook.addOrder(buyOrder);
		}
		else {
			orderBook.removeOrder(buyOrder.getId());
		}

		return trades;
	}

	@Override
	public TradeStore getTradeStore() {
		return tradeStore;
	}

}