package com.mycompany.bracketorder;

// IB API imports (adjust if your package names differ)
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.Decimal;

public class DynamicBracketOrder {

    /**
     * Callback interface that notifies when the market order is filled.
     */
    public interface OrderExecutionListener {
        void onMarketOrderFilled(int orderId, double filledPrice);
    }

    // Listener to be set so that when the market order fills, child orders can be placed.
    private static OrderExecutionListener executionListener;

    /**
     * This method simulates receiving a market order fill.
     * In a live implementation, this would be invoked by the IB API callback.
     */
    public static void simulateMarketOrderFill(int orderId, double filledPrice) {
        System.out.println("Simulating market order fill: Order ID "
                           + orderId + ", Price " + filledPrice);
        if (executionListener != null) {
            executionListener.onMarketOrderFilled(orderId, filledPrice);
        }
    }

    /**
     * Place the parent market order.
     */
    public static void placeMarketOrder(Contract contract,
                                        int parentOrderId,
                                        String action,
                                        double quantity)
    {
        Order parentOrder = new Order();
        parentOrder.orderId(parentOrderId);
        parentOrder.action(action);
        parentOrder.orderType("MKT");        // Market order
        // Pass double directly, e.g. Decimal.get(double)
        parentOrder.totalQuantity(Decimal.get(quantity));
        parentOrder.transmit(true);          // Transmit immediately
        parentOrder.orderRef("ParentMarketOrder");

        System.out.println("Placing market order: " + parentOrder);

        // In a real implementation, you would send this order:
        // clientSocket.placeOrder(parentOrder.orderId(), contract, parentOrder);

        // Simulate asynchronous order fill after a short delay (e.g., 100ms).
        new Thread(() -> {
            try {
                Thread.sleep(100);  // simulate delay for fill
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Convert Decimal to double via String conversion.
            double fillPrice = Double.parseDouble(Decimal.get(2.50).toString());
            simulateMarketOrderFill(parentOrderId, fillPrice);
        }).start();
    }

    /**
     * Place the stop loss order as a child of the market order.
     */
    public static void placeStopLossOrder(Contract contract,
                                          int parentOrderId,
                                          String action,
                                          double quantity,
                                          double stopPrice)
    {
        Order stopLossOrder = new Order();
        stopLossOrder.orderId(parentOrderId + 1);
        // Reverse action: if buying, then sell to exit, and vice versa.
        stopLossOrder.action(action.equalsIgnoreCase("BUY") ? "SELL" : "BUY");
        stopLossOrder.orderType("STP");    // Stop order type

        // Use Decimal.get(...) with doubles
        stopLossOrder.totalQuantity(Decimal.get(quantity));
        // Convert Decimal to double via String conversion.
        stopLossOrder.auxPrice(Double.parseDouble(Decimal.get(stopPrice).toString()));

        stopLossOrder.parentId(parentOrderId);
        stopLossOrder.transmit(false);     // not sending entire bracket yet
        stopLossOrder.orderRef("StopLoss");

        System.out.println("Placing stop loss order: " + stopLossOrder);

        // In a real system:
        // clientSocket.placeOrder(stopLossOrder.orderId(), contract, stopLossOrder);
    }

    /**
     * Place the limit (take‐profit) order as the final child.
     */
    public static void placeLimitOrder(Contract contract,
                                       int parentOrderId,
                                       String action,
                                       double quantity,
                                       double limitPrice)
    {
        Order limitOrder = new Order();
        limitOrder.orderId(parentOrderId + 2);
        limitOrder.action(action.equalsIgnoreCase("BUY") ? "SELL" : "BUY");
        limitOrder.orderType("LMT");       // Limit order for profit taking

        limitOrder.totalQuantity(Decimal.get(quantity));
        // Convert Decimal to double via String conversion.
        limitOrder.lmtPrice(Double.parseDouble(Decimal.get(limitPrice).toString()));

        limitOrder.parentId(parentOrderId);
        limitOrder.transmit(true);         // final child transmits bracket
        limitOrder.orderRef("TakeProfit");

        System.out.println("Placing limit order: " + limitOrder);

        // In a real system:
        // clientSocket.placeOrder(limitOrder.orderId(), contract, limitOrder);
    }

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        // Define a sample contract (e.g., Natural Gas Future on NYMEX)
        Contract contract = new Contract();
        contract.symbol("MHNG");
        contract.secType("FUT");
        contract.exchange("NYMEX");
        contract.currency("USD");
        contract.lastTradeDateOrContractMonth("20250425");
        contract.localSymbol("MNGK5");
        contract.multiplier("1000");

        int parentOrderId = 1001;
        String action = "BUY";
        double quantity = 1.0;
        double offset = 0.10;  // 10 cents offset

        // Set up the listener that will be triggered when the market order is filled.
        executionListener = new OrderExecutionListener() {
            @Override
            public void onMarketOrderFilled(int orderId, double filledPrice) {
                System.out.println("Market order filled: Order ID = "
                                   + orderId + ", Filled Price = " + filledPrice);

                // Dynamically calculate child order prices based on the fill price.
                double stopPrice = filledPrice - offset;
                double limitPrice = filledPrice + offset;

                // Place the stop loss order first.
                placeStopLossOrder(contract, orderId, action, quantity, stopPrice);

                // Delay 10 milliseconds before placing the limit order.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                placeLimitOrder(contract, orderId, action, quantity, limitPrice);
            }
        };

        // Place the market order.
        placeMarketOrder(contract, parentOrderId, action, quantity);
    }
}
