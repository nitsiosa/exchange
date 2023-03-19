package org.nts.exchange.verifier.cli.input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.Trade;
import org.nts.exchange.verifier.core.matchingengine.MatchingEngine;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderSide;
import org.nts.exchange.verifier.core.tradestore.TradeStore;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * This class reads orders from standard input and emits trades as they occur on
 * standard output. Once standard input ends, the program prints the final
 * contents of the order book. Order inputs are given as comma-separated values,
 * one order per line of the input, delimited by a new line character. The
 * fields are: order-id, side, price, quantity. Side will have a value of ‘B’
 * for Buy or ‘S’ for Sell. Price and quantity will both be integers. order-id
 * should be handled as a string.
 * 
 * @author Andreas Nitsios
 */
@Log4j2
public class FileOrderReader implements OrderReader {

	/**
	 * A helper class for pairing buy and sell order strings for output.
	 */
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
	private final String     fileName;

	/**
	 * 
	 * Constructor for the ConsoleReader class. Takes a matching engine as a
	 * parameter and uses it to initialize its order book and trade store.
	 * 
	 * @param matchingEngine The matching engine to use for order processing.
	 */
	public FileOrderReader(String fileName, MatchingEngine matchingEngine) {
		this.orderBook  = matchingEngine.getOrderBook();
		this.tradeStore = matchingEngine.getTradeStore();
		this.fileName   = fileName;
	}

	/**
	 * 
	 * Returns an OrderSide enum value based on a string representation of the side.
	 * 
	 * @param text A string representation of the order side.
	 * @return An OrderSide enum value corresponding to the input string.
	 */
	private OrderSide getSideFromText(String text) {
		return (text.equalsIgnoreCase("B")) ? OrderSide.BUY : OrderSide.SELL;
	}

	/**
	 * 
	 * Formats a Trade object as a string for output.
	 * 
	 * @param trade The Trade object to format.
	 * @return A formatted string representation of the input Trade object.
	 */
	private String formatTradeOutPutString(Trade trade) {
		return String.format("trade %s,%s,%d,%d ", trade.getRestingOrderId(), trade.getAggressorOrderId(),
				trade.getPrice(), trade.getQuantity());
	}

	/**
	 * 
	 * Formats a Buy Order object as a string for output.
	 * 
	 * @param order The Buy Order object to format.
	 * @return A formatted string representation of the input Buy Order object.
	 */
	private String formatBuyOrderOutPutString(Order order) {
		return String.format("%1$9d %2$6d", order.getQuantity(), order.getPrice());
	}

	/**
	 * 
	 * Formats a Sell Order object as a string for output.
	 * 
	 * @param order The Sell Order object to format.
	 * @return A formatted string representation of the input Sell Order object.
	 */
	private String formatSellOrderOutPutString(Order order) {
		return String.format("%1$6d %2$9d", order.getPrice(), order.getQuantity());
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

	/**
	 * 
	 * Reads an input line from a string and adds an order to the order book.
	 * 
	 * @param line The input line to parse and process.
	 */
	private void readlineAndAddOrder(String fileName) {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line = reader.readLine();
			while (line != null) {
				readStringAndAddOrder(line);
				line = reader.readLine();

			}
		} catch (IOException e) {
			log.error("Error reading file: " + fileName);
		}
	}

	/**
	 * 
	 * Reads input lines from a file and adds orders to the order book.
	 * 
	 * @param fileName The name of the file to read.
	 */
	private List<String> formatOrdersForprinting() {
		List<OrderBookOutputLinePair> bookOutputLinePairs = new ArrayList<>();

		List<String>                  formatedBuyOrders   = orderBook.getBuyOrders().stream()
				.map(this::formatBuyOrderOutPutString).collect(Collectors.toList());
		List<String>                  formatedSellOrders  = orderBook.getSellOrders().stream()
				.map(this::formatSellOrderOutPutString).collect(Collectors.toList());

		for (int i = 0; i < formatedBuyOrders.size() || i < formatedSellOrders.size(); i++) {
			String buyOrder  = (formatedBuyOrders.size() > i) ? formatedBuyOrders.get(i) : "                ";
			String sellOrder = (formatedSellOrders.size() > i) ? formatedSellOrders.get(i) : "                ";

			bookOutputLinePairs.add(new OrderBookOutputLinePair(buyOrder, sellOrder));
		}
		return bookOutputLinePairs.stream().map(OrderBookOutputLinePair::toString).collect(Collectors.toList());
	}

	/**
	 * 
	 * Parses orders from a file and returns a list of strings with the trades and
	 * order book content.
	 * 
	 * Each line in the input file should contain a comma-separated order with the
	 * following fields:
	 * 
	 * order-id, side, price, quantity. Side should be either 'B' for buy or 'S' for
	 * sell.
	 * 
	 * @return a list of strings with the trades and order book content
	 * 
	 * @throws IOException if there is an error reading the file
	 */
	@Override
	public List<String> parse() throws IOException {
		List<String> output = new ArrayList<>();
		readlineAndAddOrder(fileName);
		output.addAll(tradeStore.getTrades().stream().map(this::formatTradeOutPutString).collect(Collectors.toList()));
		output.addAll(formatOrdersForprinting());

		return output;
	}
}
