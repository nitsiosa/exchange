package org.nts.exchange.verifier.cli.input;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nts.exchange.verifier.cli.input.FileOrderReader;
import org.nts.exchange.verifier.cli.input.OrderReader;
import org.nts.exchange.verifier.core.matchingengine.MatchingEngine;
import org.nts.exchange.verifier.core.matchingengine.PriceTimePriorityMatchingEngine;
import org.nts.exchange.verifier.core.orderbook.InMemoryOrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBookMonitor;
import org.nts.exchange.verifier.core.tradestore.InMemoryTradeStore;
import org.nts.exchange.verifier.core.tradestore.TradeStore;

class ConsoleReaderTest {

	private final String     fileName = "src/test/resources/orders.csv";

	private OrderBook        orderBook;
	private TradeStore       tradeStore;
	private OrderBookMonitor bookMonitor;
	private MatchingEngine   matchingEngine;
	private OrderReader      consoleReader;

	@BeforeEach
	void setUp() {
		orderBook      = new InMemoryOrderBook();
		bookMonitor    = new OrderBookMonitor(orderBook);
		tradeStore     = new InMemoryTradeStore();
		matchingEngine = new PriceTimePriorityMatchingEngine(orderBook, tradeStore);

		bookMonitor.startMonitoring();
		consoleReader = new FileOrderReader(fileName,matchingEngine);
	}

	@Test
	void testParseOrdersFromFile() throws IOException {
		List<String> output = consoleReader.parse();

		assertEquals(4, output.size());
		assertEquals("trade 1,2,50,10 ", output.get(0));
		assertEquals("trade 2,3,50,10 ", output.get(1));
		assertEquals("trade 3,4,55,10 ", output.get(2));
		assertEquals("                |    55        20", output.get(3));

		// Verify that orders were added to the order book correctly
		assertEquals(0, matchingEngine.getOrderBook().getBuyOrders().size());
		assertEquals(1, matchingEngine.getOrderBook().getSellOrders().size());
	}
}