package org.verifier.core.tradestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.verifier.core.Trade;

/**
 * An implementation of the {@link TradeStore} interface that stores trades in an in-memory list.
 * This implementation is not thread-safe.
 */
public class InMemoryTradeStore implements TradeStore {
    private List<Trade> trades = new ArrayList<>();

    /**
     * Stores a trade in the in-memory list.
     *
     * @param trade The trade to store.
     */
    public void storeTrade(Trade trade) {
        trades.add(trade);
    }

    /**
     * Returns an unmodifiable view of the list of trades.
     *
     * @return An unmodifiable view of the list of trades.
     */
    public List<Trade> getTrades() {
        return Collections.unmodifiableList(trades);
    }
    
    /**
     * Stores a list of trades in the in-memory list.
     *
     * @param tradelist The tradestore  to store.
     */
	@Override
	public void storeTrades(List<Trade> bulktrades) {
		trades.addAll(bulktrades);
		
	}
}