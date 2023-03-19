package org.nts.exchange.verifier.cli.input;

import java.io.IOException;
import java.util.List;

/**

The OrderReader interface provides a way to read orders from various input sources.
The implementation classes are responsible for defining the logic for reading and parsing orders.
*/
public interface OrderReader {

/**
 * Parse the input and return a list of strings with the trades and order book content.
 *
 * @return a list of strings with the trades and order book content
 * @throws IOException if there is an error reading the input
 */
	public List<String> parse() throws IOException ;

}
