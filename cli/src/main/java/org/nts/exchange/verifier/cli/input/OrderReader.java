package org.nts.exchange.verifier.cli.input;

import java.io.IOException;
import java.util.List;

public interface OrderReader {

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
	 * 
	 * @return a list of strings with the trades and order book content
	 * 
	 * @throws IOException if there is an error reading the file
	 */
	public List<String> parse() throws IOException ;

}
