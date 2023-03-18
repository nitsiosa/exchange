package org.verifier.core.tradestore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.verifier.core.Trade;

class InMemoryTradeStoreTest {

	private InMemoryTradeStore tradeStore;

	@BeforeEach
	void setUp() {
		tradeStore = new InMemoryTradeStore();
	}

	@Test
	void storeTrade_shouldAddTradeToList() {
		Trade trade = new Trade("ABC", "CBA", 100, 50);
		tradeStore.storeTrade(trade);

		List<Trade> expected = new ArrayList<>();
		expected.add(trade);

		assertEquals(expected, tradeStore.getTrades());
	}

	@Test
	void getTrades_shouldReturnUnmodifiableList() {
		Trade trade = new Trade("XYZ", "ZYX", 50, 75);
		tradeStore.storeTrade(trade);

		List<Trade> trades = tradeStore.getTrades();
		try {
			trades.add(new Trade("DEF", "FED", 200, 10));
		} catch (UnsupportedOperationException e) {
			// expected exception
		}

		List<Trade> expected = new ArrayList<>();
		expected.add(trade);

		assertEquals(expected, tradeStore.getTrades());
	}

	@Test
	void storeTrades_shouldAddTradeListToList() {
		Trade       trade1 = new Trade("ABC", "CBA", 100, 50);
		Trade       trade2 = new Trade("XYZ", "CBA", 50, 75);
		List<Trade> trades = Arrays.asList(trade1, trade2);
		tradeStore.storeTrades(trades);

		List<Trade> expected = new ArrayList<>();
		expected.addAll(trades);

		assertEquals(expected, tradeStore.getTrades());
	}
}
