package org.nts.exchange.verifier.cli;

import java.io.File;
import java.io.IOException;

import org.nts.exchange.verifier.cli.input.FileOrderReader;
import org.nts.exchange.verifier.cli.input.OrderReader;
import org.nts.exchange.verifier.cli.output.FileOrderWriter;
import org.nts.exchange.verifier.cli.output.OrderWriter;
import org.nts.exchange.verifier.core.matchingengine.MatchingEngine;
import org.nts.exchange.verifier.core.matchingengine.PriceTimePriorityMatchingEngine;
import org.nts.exchange.verifier.core.orderbook.InMemoryOrderBook;
import org.nts.exchange.verifier.core.orderbook.OrderBook;
import org.nts.exchange.verifier.core.tradestore.InMemoryTradeStore;
import org.nts.exchange.verifier.core.tradestore.TradeStore;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Exchange {
	
	private static String outputFileName = "RemainingOrderBackup.txt";
	
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
		TradeStore       tradeStore       = new InMemoryTradeStore();
		MatchingEngine   matchingEngine   = new PriceTimePriorityMatchingEngine(orderBook, tradeStore);
	
		if(new File(outputFileName).exists()) {
			OrderReader backupReader = new FileOrderReader(outputFileName,matchingEngine);
			backupReader.parse();
		}
		
		OrderReader consoleReader = new FileOrderReader(filename,matchingEngine);
		consoleReader.parse().forEach(System.out::println);
		
		OrderWriter orderWriter = new FileOrderWriter(outputFileName,matchingEngine);
		orderWriter.push();
	}
}
