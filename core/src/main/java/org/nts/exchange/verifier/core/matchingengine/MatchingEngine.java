package org.nts.exchange.verifier.core.matchingengine;

import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.Trade;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.tradestore.TradeStore;

/**
 * Represents the matching engine for a trading instrument.
 */
public interface MatchingEngine {

    /**
     * Matches an incoming order against the existing orders in the order book.
     * 
     * @param order the incoming order to match
     * @return the generated trades
     */
    public Trade[] match(Order order) ;


    /**
     * Gets the order book to match against.
     * 
     * @return the order book
     */
    OrderBook getOrderBook();
    
    /**
     * Gets the trade book to match against.
     * 
     * @return the trade book
     */
    TradeStore getTradeStore();
}