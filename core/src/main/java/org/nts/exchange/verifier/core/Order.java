package org.nts.exchange.verifier.core;

import org.nts.exchange.verifier.core.matchingengine.exception.InvalidQuantityException;
import org.nts.exchange.verifier.core.orderbook.OrderSide;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an order submitted by a trader to buy or sell a quantity of shares
 * at a specific price.
 */
public class Order implements Comparable<Order> {
	@Getter
	private final String    id;
	@Getter
	private final OrderSide side;
	@Getter
	private final int       price;
	@Getter
	@Setter
	private int             quantity;
	@Getter
	private long            timestamp;

	/**
	 * Constructs a new Order object with the specified parameters.
	 * 
	 * @param id       the unique identifier for the order
	 * @param side     the side of the order (buy or sell)
	 * @param price    the price of the order
	 * @param quantity the quantity of shares in the order
	 */
	public Order(String id, OrderSide side, int price, int quantity) {
		this.id        = id;
		this.side      = side;
		this.price     = price;
		this.quantity  = quantity;
		this.timestamp = System.nanoTime();
	}

	/**
	 * Subtracts the given quantity from the order.
	 *
	 * @param quantityToSubtract the quantity to subtract from the order
	 * @throws InvalidQuantityException if quantity to subtract is not positive or
	 *                                  exceeds the order quantity
	 */
	public void subtractQuantity(int quantityToSubtract) throws InvalidQuantityException {
		// Check that quantity to subtract is positive
		if (quantityToSubtract <= 0) {
			throw new InvalidQuantityException("Quantity to subtract must be greater than zero.");
		}

		// Check that quantity to subtract does not exceed order quantity
		if (quantityToSubtract > quantity) {
			throw new InvalidQuantityException("Quantity to subtract is greater than order quantity.");
		}

		// Subtract quantity from order quantity
		quantity -= quantityToSubtract;
	}

	/**
	 * Returns a string representation of the Order object.
	 * 
	 * @return a string representation of the Order object
	 */
	@Override
	public String toString() {
		return String.format("%s %d @ %d time:%d", side, quantity, price, timestamp);
	}

	@Override
	public int compareTo(Order o) {
		if (this.price == o.price) {
			return Long.compare(this.timestamp, o.timestamp);
		}
		return Integer.compare(o.price, this.price);
	}

}
