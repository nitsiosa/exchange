# Order Book Coding Assignment


Context of the problem
We are running a small exchange and we must ensure that its behaviour is fully deterministic
and fully compliant with regulations. Our exchange is quite small but very optimized, so even
if it supports only one feature, the limit order, it’s hard to make it right. To ensure that our
exchange is behaving properly, we need to compare its output with the one generated by a
verifier. The verifier does not have any performance constraints, it just has to behave
properly.
Your task is to build that verifier. We will check if our exchange is behaving correctly
comparing its output with the one coming from your assignment

High level description
An exchange allows the buyers and sellers of a product to discover each other and trade.
Buyers and sellers (traders) submit orders to the exchange and the exchange applies simple
rules to determine if a trade has occurred. The dominant kind of exchange is a central limit
order book (CLOB) where orders are matched using ‘price time priority’.
When placing an order, traders specify if they wish to buy or sell, the limit price ie. worst
possible price they will trade at, and the quantity (number of shares) they wish to trade. On
our exchange trades only occur during the processing of a newly posted order, and happen
immediately, which is known as ‘continuous trading’.

Matching example
As orders arrive at the exchange, they are considered for aggressive matching first against
the opposite side of the book. Once this is completed, any remaining order quantity will rest
on their own side of the book. Consider 3 orders have been submitted to the exchange, in
the following order:
● Buy 1000 @ 99
● Buy 1200 @ 98
● Buy 500 @ 99

As there are no Sell orders yet, they rest on the order book as follows (note Buy for 98 is
lowest priority):

Bids (Buying) Asks (Selling)

Quantity Price
1000 99
500 99
1200 98

Price Quantity

Price time priority refers to the order in which orders in the book are eligible to be matched
during the aggressive phase. Orders are first matched in order of price (most aggressive to
least aggressive), then by arrival time into the book (oldest to newest). A Sell order is now
submitted, with a limit price that does not cross with any of the existing resting orders:
● Sell 2000 @ 101

Bids (Buying) Asks (Selling)

Quantity Price
1000 99
500 99
1200 98

Price Quantity
101 2000

A Sell order is now submitted that is aggressively-priced: ● Sell 2000 @ 95
This triggers a matching event as there are orders on the Buy side that match with the new
Sell order.
The orders are matched in price time priority (first by price, then by arrival time into the book)
i.e.
● Buy 1000 @ 99 is matched first (as it is the oldest order at the highest price level)
● Buy 500 @ 99 is matched second (as it is at the highest price level and arrived
after the BUY 1000 @ 99 order)

● Buy 500 @ 98 is matched third (as it is at a lower price. This partially fills the
resting order of 1200, leaving 700 in the order book)

Bids (Buying) Asks (Selling)

Quantity Price
700 98

Price Quantity
101 2000

Limit order handling

The assignment is to produce executable code that will accept orders from standard input,
and to emit to standard output the trades as they occur. Once standard input ends, the
program should print the final contents of the order book.

Order inputs will be given as a comma separated values, one order per line of the input,
delimited by a new line character. The fields are: order-id, side, price, quantity. Side will have a
value of ‘B’ for Buy or ‘S’ for Sell. Price and quantity will both be integers. order-id should be
handled as a string.
