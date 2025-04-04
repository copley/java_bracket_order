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

Review the Warnings:

The warning about 'dependencies.dependency.systemPath' for com.ib:ibapi:jar means that Maven is referencing the TwsApi.jar directly from your project directory. This approach isn’t ideal if others will use your project, because the jar won’t be available in their environment.

To resolve this, you might want to install the jar into your local Maven repository using a command like:

bash
Copy
mvn install:install-file -Dfile=lib/TwsApi.jar -DgroupId=com.ib -DartifactId=ibapi -Dversion=1.0 -Dpackaging=jar
Then update your pom.xml to reference the dependency without the system scope.

Also, note the warning about platform encoding. If you need consistent builds, you can set the encoding in your pom.xml.
