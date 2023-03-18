package org.cliAdapter.input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.verifier.core.Order;
import org.verifier.core.Trade;
import org.verifier.core.matchingengine.MatchingEngine;
import org.verifier.core.orderbook.OrderBook;
import org.verifier.core.orderbook.OrderSide;
import org.verifier.core.tradestore.TradeStore;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsoleReader {

	@AllArgsConstructor
	class OrderBookOutputLinePair {
		String buyLine;
		String sellLine;

		@Override
		public String toString() {
			return String.format("%s|%s", buyLine, sellLine);
		}
	}

	private final OrderBook  orderBook;
	private final TradeStore tradeStore;

	public ConsoleReader(MatchingEngine matchingEngine) {
		this.orderBook  = matchingEngine.getOrderBook();
		this.tradeStore = matchingEngine.getTradeStore();
	}

	private OrderSide getSideFromText(String text) {
		return (text.equalsIgnoreCase("B")) ? OrderSide.BUY : OrderSide.SELL;
	}

	private String formatTradeOutPutString(Trade trade) {
		return String.format("trade %s,%s,%d,%d ", trade.getRestingOrderId(), trade.getAggressorOrderId(),
				trade.getPrice(), trade.getQuantity());
	}

	private String formatBuyOrderOutPutString(Order order) {
		return String.format("%1$9d %2$6d", order.getQuantity(),order.getPrice());
	}

	private String formatSellOrderOutPutString(Order order) {
		return String.format("%1$6d %2$9d", order.getPrice(),order.getQuantity());
	}

	private void readStringAndAddOrder(String line) {
		String[] fields = line.split(",");
		if (fields.length != 4) {
			log.error("Invalid order format: " + line);
		} else {

			String    orderId  = fields[0];
			OrderSide side     = getSideFromText(fields[1]);
			int       price    = Integer.parseInt(fields[2]);
			int       quantity = Integer.parseInt(fields[3]);

			Order     order    = new Order(orderId, side, price, quantity);
			orderBook.addOrder(order);
		}
	}

	public void parseOrdersFromFile(String fileName) throws IOException {

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line = reader.readLine();
			while (line != null) {
				readStringAndAddOrder(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			log.error("Error reading file: " + fileName);
		}

		tradeStore.getTrades().stream().map(this::formatTradeOutPutString).forEach(System.out::println);

		List<String> formatedBuyOrders   = orderBook.getBuyOrders().stream().map(this::formatBuyOrderOutPutString).collect(Collectors.toList());
		List<String>  formatedSellOrders  = orderBook.getSellOrders().stream().map(this::formatSellOrderOutPutString).collect(Collectors.toList());
		List<OrderBookOutputLinePair> bookOutputLinePairs = new ArrayList<>();

		for (int i = 0; i < formatedBuyOrders.size() || i < formatedSellOrders.size(); i++) {
			String buyOrder  = (formatedBuyOrders.size() > i) ? formatedBuyOrders.get(i) :   "                ";
			String sellOrder = (formatedSellOrders.size() > i) ? formatedSellOrders.get(i) : "                ";

			bookOutputLinePairs.add(new OrderBookOutputLinePair(buyOrder, sellOrder));
		}
		bookOutputLinePairs.forEach(System.out::println);
	}
}
