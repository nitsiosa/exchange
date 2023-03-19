package org.nts.exchange.verifier.core;

import lombok.Getter;

/**
 * Represents a trade that occurred between two orders in the order book.
 */
public class Trade {

	@Getter
	private final String aggressorOrderId;
	@Getter
	private final String restingOrderId;
	@Getter
	private final int    price;
	@Getter
	private final int    quantity;

	/**
	 * Creates a new instance of a trade.
	 * 
	 * @param aggressorOrderId the ID of the aggressor order
	 * @param restingOrderId   the ID of the resting order
	 * @param price            the price at which the trade occurred
	 * @param quantity         the quantity that was traded
	 */
	public Trade(String aggressorOrderId, String restingOrderId, int price, int quantity) {
		this.aggressorOrderId = aggressorOrderId;
		this.restingOrderId   = restingOrderId;
		this.price            = price;
		this.quantity         = quantity;
	}

	/**
	 * Returns a string representation of the trade.
	 * 
	 * @return a string representation of the trade
	 */
	@Override
	public String toString() {
		return String.format("trade %s,%s,%d,%d", aggressorOrderId, restingOrderId, price, quantity);
	}
}
