package org.nts.exchange.verifier.cli.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.nts.exchange.verifier.core.Order;
import org.nts.exchange.verifier.core.matchingengine.MatchingEngine;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderSide;

public class FileOrderWriter implements OrderWriter {

	private final String outputFileName ;
	private final MatchingEngine matchingEngine;

	public FileOrderWriter(String outputFileName ,MatchingEngine matchingEngine) {
		this.outputFileName= outputFileName;
		this.matchingEngine = matchingEngine;
	}

	private void writeOrder(BufferedWriter writer, Order order, OrderSide side) throws IOException {
        String line = String.format("%s,%s,%d,%d", order.getId(), side.toString().substring(0, 1), order.getPrice(), order.getQuantity());
        writer.write(line);
        writer.newLine();
    }
	
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
