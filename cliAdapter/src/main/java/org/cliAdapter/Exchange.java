package org.cliAdapter;

import java.io.IOException;

import org.cliAdapter.input.ConsoleReader;
import org.verifier.core.matchingengine.MatchingEngine;
import org.verifier.core.matchingengine.PriceTimePriorityMatchingEngine;
import org.verifier.core.orderbook.InMemoryOrderBook;
import org.verifier.core.orderbook.OrderBook;
import org.verifier.core.orderbook.OrderBookMonitor;
import org.verifier.core.tradestore.InMemoryTradeStore;
import org.verifier.core.tradestore.TradeStore;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Exchange {
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				new Exchange(args[0]);
			} catch (IOException e) {
				log.error("Error processing file {}", e.getMessage());
			}

		} else {
			log.error("Please specify a file");
		}
	}

	public Exchange(String filename) throws IOException {
		OrderBook        orderBook        = new InMemoryOrderBook();
		OrderBookMonitor orderBookMonitor = new OrderBookMonitor(orderBook);
		TradeStore       tradeStore       = new InMemoryTradeStore();
		MatchingEngine   matchingEngine   = new PriceTimePriorityMatchingEngine(orderBook, tradeStore);
				
		orderBookMonitor.startMonitoring();
		
		ConsoleReader consoleReader = new ConsoleReader(matchingEngine);
		consoleReader.parseOrdersFromFile(filename);

	}

}
