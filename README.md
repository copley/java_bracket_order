TODO
Explanation
Simulated Classes:
We define simple Contract and Order classes with attributes and a string representation. In a real application, you would import these from the IB API.

Listener Mechanism:
Instead of a Java interface, we simply assign a callable (in this case, the on_market_order_filled function) to a global variable execution_listener. When the simulated fill occurs, this listener is called with the order ID and fill price.

Order Placement Functions:

place_market_order sets up a market order and starts a thread that simulates an order fill after 100 milliseconds.

place_stop_loss_order creates a stop order with a reversed action (if the parent order is a BUY, the stop order is a SELL) and does not immediately transmit it.

place_limit_order creates a limit order (for take profit) and marks it to transmit immediately.

Main Function:
A sample contract is defined and a market order is placed. Once the simulated market order fill is triggered, the listener calculates the child order prices and places the stop loss and limit orders with a slight delay between them.


