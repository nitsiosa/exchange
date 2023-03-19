package org.nts.exchange.verifier.cli.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.matchingengine.MatchingEngine;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderSide;

/**
 * 
 * This class implements the OrderWriter interface to write the remaining orders
 * to a file. It takes an output file name and a matching engine object as
 * parameters in its constructor. It writes the buy and sell orders to the
 * output file in the format "order-id, side, price, quantity".
 * 
 * @author [Your Name]
 */
public class FileOrderWriter implements OrderWriter {

	/**
	 * The name of the file to write the orders to.
	 */
	private final String         outputFileName;

	/**
	 * The matching engine used to retrieve the remaining orders.
	 */
	private final MatchingEngine matchingEngine;

	/**
	 * Constructs a FileOrderWriter object with the specified output file name and
	 * matching engine.
	 * 
	 * @param outputFileName The name of the file to write the orders to.
	 * @param matchingEngine The matching engine to use to retrieve the remaining
	 *                       orders.
	 */
	public FileOrderWriter(String outputFileName, MatchingEngine matchingEngine) {
		this.outputFileName = outputFileName;
		this.matchingEngine = matchingEngine;
	}

	/**
	 * Writes an order to the output file in the format "order-id, side, price,
	 * quantity".
	 * 
	 * @param writer The BufferedWriter object used to write to the output file.
	 * @param order  The order to write to the output file.
	 * @param side   The side of the order (BUY or SELL).
	 * @throws IOException If an I/O error occurs while writing to the output file.
	 */
	private void writeOrder(BufferedWriter writer, Order order, OrderSide side) throws IOException {
		String line = String.format("%s,%s,%d,%d", order.getId(), side.toString().substring(0, 1), order.getPrice(),
				order.getQuantity());
		writer.write(line);
		writer.newLine();
	}

	/**
	 * Retrieves the remaining buy and sell orders from the matching engine and
	 * writes them to the output file in the format "order-id, side, price,
	 * quantity".
	 */
	@Override
	public void push() {

		OrderBook   orderBook  = matchingEngine.getOrderBook();
		List<Order> buyOrders  = orderBook.getBuyOrders();
		List<Order> sellOrders = orderBook.getSellOrders();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
			// Write buy orders
			for (Order buyOrder : buyOrders) {
				writeOrder(writer, buyOrder, OrderSide.BUY);
			}

			// Write sell orders
			for (Order sellOrder : sellOrders) {
				writeOrder(writer, sellOrder, OrderSide.SELL);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
