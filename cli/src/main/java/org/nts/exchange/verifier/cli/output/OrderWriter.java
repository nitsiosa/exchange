package org.nts.exchange.verifier.cli.output;

/**
 * 
 * An interface that defines the contract for writing orders to a specific
 * output target.
 */
public interface OrderWriter {

	/**
	 * Pushes all orders to the output target.
	 */
	public void push();
}
