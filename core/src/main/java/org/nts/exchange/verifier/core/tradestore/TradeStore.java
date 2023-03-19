package org.nts.exchange.verifier.core.tradestore;

import java.util.List;

import org.nts.exchange.verifier.core.Trade;

/**
 * 
 * This interface defines a trade store that can be used to store and retrieve
 * trades.
 */
public interface TradeStore {

	/**
	 * 
	 * Stores a trade in the trade store.
	 * 
	 * @param trade The trade to be stored.
	 */
	void storeTrade(Trade trade);

	/**
	 * 
	 * Retrieves all trades from the trade store.
	 * 
	 * @return A list of all trades in the trade store.
	 */
	List<Trade> getTrades();

	void storeTrades(List<Trade> trades);
}