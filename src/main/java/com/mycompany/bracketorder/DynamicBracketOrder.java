package com.mycompany.bracketorder;

import com.ib.client.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicBracketOrder implements EWrapper {

    // The IB API client
    private static EClientSocket clientSocket;
    // Next valid order ID received from TWS
    private static int nextOrderId = 1001;
    // Listener for order fill callback
    private static OrderExecutionListener executionListener;

    // Minimal EWrapper implementation. Most methods are empty stubs.
    @Override
    public void nextValidId(int orderId) {
        nextOrderId = orderId;
        System.out.println("Next valid order id: " + nextOrderId);
    }

    @Override
    public void orderStatus(int orderId, String status, double filled, double remaining,
                            double avgFillPrice, int permId, int parentId,
                            double lastFillPrice, int clientId, String whyHeld) {
        System.out.println("Order Status: orderId=" + orderId + ", status=" + status + ", filled=" + filled);
        // When the market order is filled, trigger the listener.
        if (status.equalsIgnoreCase("Filled") && executionListener != null) {
            executionListener.onMarketOrderFilled(orderId, avgFillPrice);
        }
    }

    @Override
    public void error(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void error(String str) {
        System.err.println(str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        System.err.println("Error: id=" + id + ", code=" + errorCode + ", msg=" + errorMsg);
    }

    @Override
    public void connectionClosed() {
        System.out.println("Connection closed.");
    }

    // Stub implementations for the rest of the EWrapper interface.
    @Override public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {}
    @Override public void tickSize(int tickerId, int field, int size) {}
    @Override public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta,
                                               double optPrice, double pvDividend, double gamma,
                                               double vega, double theta, double undPrice) {}
    @Override public void tickGeneric(int tickerId, int tickType, double value) {}
    @Override public void tickString(int tickerId, int tickType, String value) {}
    @Override public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints,
                                 double totalDividends, int holdDays, String futureExpiry,
                                 double dividendImpact, double dividendsToExpiry) {}
    @Override public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {}
    @Override public void openOrderEnd() {}
    @Override public void updateAccountValue(String key, String value, String currency, String accountName) {}
    @Override public void updatePortfolio(Contract contract, double position, double marketPrice,
                                          double marketValue, double averageCost, double unrealizedPNL,
                                          double realizedPNL, String accountName) {}
    @Override public void updateAccountTime(String timeStamp) {}
    @Override public void accountDownloadEnd(String accountName) {}
    @Override public void contractDetails(int reqId, ContractDetails contractDetails) {}
    @Override public void bondContractDetails(int reqId, ContractDetails contractDetails) {}
    @Override public void contractDetailsEnd(int reqId) {}
    @Override public void execDetails(int reqId, Contract contract, Execution execution) {}
    @Override public void execDetailsEnd(int reqId) {}
    @Override public void updateMktDepth(int tickerId, int position, int operation, int side,
                                         double price, int size) {}
    @Override public void updateMktDepthL2(int tickerId, int position, String marketMaker,
                                           int operation, int side, double price, int size) {}
    @Override public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {}
    @Override public void managedAccounts(String accountsList) {}
    @Override public void receiveFA(int faDataType, String xml) {}
    @Override public void historicalData(int reqId, String date, double open, double high, double low,
                                         double close, int volume, int count, double WAP, boolean hasGaps) {}
    @Override public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {}
    @Override public void scannerParameters(String xml) {}
    @Override public void scannerData(int reqId, int rank, ContractDetails contractDetails,
                                      String distance, String benchmark, String projection, String legsStr) {}
    @Override public void scannerDataEnd(int reqId) {}
    @Override public void realtimeBar(int reqId, long time, double open, double high, double low,
                                      double close, long volume, double wap, int count) {}
    @Override public void currentTime(long time) {}
    @Override public void fundamentalData(int reqId, String data) {}
    @Override public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {}
    @Override public void tickSnapshotEnd(int reqId) {}
    @Override public void marketDataType(int reqId, int marketDataType) {}
    @Override public void commissionReport(CommissionReport commissionReport) {}
    @Override public void position(String account, Contract contract, double pos, double avgCost) {}
    @Override public void positionEnd() {}
    @Override public void accountSummary(int reqId, String account, String tag, String value, String currency) {}
    @Override public void accountSummaryEnd(int reqId) {}
    @Override public void verifyMessageAPI(String apiData) {}
    @Override public void verifyCompleted(boolean isSuccessful, String errorText) {}
    @Override public void verifyAndAuthMessageAPI(String apiData, String xyzChallange) {}
    @Override public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {}
    @Override public void displayGroupList(int reqId, String groups) {}
    @Override public void displayGroupUpdated(int reqId, String contractInfo) {}
    @Override public void positionMulti(int reqId, String account, String modelCode,
                                        Contract contract, double pos, double avgCost) {}
    @Override public void positionMultiEnd(int reqId) {}
    @Override public void accountUpdateMulti(int reqId, String account, String modelCode,
                                             String key, String value, String currency) {}
    @Override public void accountUpdateMultiEnd(int reqId) {}
    @Override public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId,
                                                              String tradingClass, String multiplier,
                                                              Set<String> expirations, Set<Double> strikes) {}
    @Override public void securityDefinitionOptionalParameterEnd(int reqId) {}
    @Override public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {}
    @Override public void familyCodes(FamilyCode[] familyCodes) {}
    @Override public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {}
    @Override public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {}
    @Override public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId,
                                   String headline, String extraData) {}
    @Override public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {}
    @Override public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {}
    @Override public void newsProviders(NewsProvider[] newsProviders) {}
    @Override public void newsArticle(int requestId, int articleType, String articleText) {}
    @Override public void historicalNews(int reqId, String time, String providerCode, String articleId,
                                         String headline) {}
    @Override public void historicalNewsEnd(int reqId, boolean hasMore) {}
    @Override public void headTimestamp(int reqId, String headTimestamp) {}
    @Override public void histogramData(int reqId, List<HistogramEntry> items) {}
    @Override public void historicalDataUpdate(int reqId, String date, double open, double high, double low,
                                               double close, int volume, int count, double WAP, boolean hasGaps) {}
    @Override public void rerouteMktDataReq(int reqId, int conid, String exchange) {}
    @Override public void rerouteMktDepthReq(int reqId, int conid, String exchange) {}
    @Override public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {}
    @Override public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {}
    @Override public void pnlSingle(int reqId, int pos, double dailyPnL, double unrealizedPnL,
                                    double realizedPnL, double value) {}
    @Override public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {}
    @Override public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {}
    @Override public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {}
    @Override public void tickByTickAllLast(int reqId, int tickType, long time, double price, int size,
                                            TickAttribLast tickAttribLast, String exchange, String specialConditions) {}
    @Override public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, int bidSize,
                                           int askSize, TickAttribBidAsk tickAttribBidAsk) {}
    @Override public void tickByTickMidPoint(int reqId, long time, double midPoint) {}
    @Override public void orderBound(long orderId, int apiClientId, int apiOrderId) {}

    // Define a callback interface to notify when the market order is filled.
    public interface OrderExecutionListener {
        void onMarketOrderFilled(int orderId, double filledPrice);
    }

    /**
     * Place the parent market order using the real IB API call.
     */
    public static void placeMarketOrder(Contract contract,
                                        int parentOrderId,
                                        String action,
                                        double quantity) {
        Order parentOrder = new Order();
        parentOrder.orderId(parentOrderId);
        parentOrder.action(action);
        parentOrder.orderType("MKT");        // Market order
        parentOrder.totalQuantity(Decimal.get(quantity));
        parentOrder.transmit(true);          // Transmit immediately
        parentOrder.orderRef("ParentMarketOrder");

        System.out.println("Placing market order: " + parentOrder);
        clientSocket.placeOrder(parentOrder.orderId(), contract, parentOrder);
    }

    /**
     * Place the stop loss order as a child of the market order.
     */
    public static void placeStopLossOrder(Contract contract,
                                          int parentOrderId,
                                          String action,
                                          double quantity,
                                          double stopPrice) {
        Order stopLossOrder = new Order();
        stopLossOrder.orderId(parentOrderId + 1);
        stopLossOrder.action(action.equalsIgnoreCase("BUY") ? "SELL" : "BUY");
        stopLossOrder.orderType("STP");    // Stop order type
        stopLossOrder.totalQuantity(Decimal.get(quantity));
        stopLossOrder.auxPrice(Double.parseDouble(Decimal.get(stopPrice).toString()));
        stopLossOrder.parentId(parentOrderId);
        stopLossOrder.transmit(false);     // Transmit later as part of the bracket
        stopLossOrder.orderRef("StopLoss");

        System.out.println("Placing stop loss order: " + stopLossOrder);
        clientSocket.placeOrder(stopLossOrder.orderId(), contract, stopLossOrder);
    }

    /**
     * Place the limit (take‐profit) order as the final child.
     */
    public static void placeLimitOrder(Contract contract,
                                       int parentOrderId,
                                       String action,
                                       double quantity,
                                       double limitPrice) {
        Order limitOrder = new Order();
        limitOrder.orderId(parentOrderId + 2);
        limitOrder.action(action.equalsIgnoreCase("BUY") ? "SELL" : "BUY");
        limitOrder.orderType("LMT");       // Limit order for profit taking
        limitOrder.totalQuantity(Decimal.get(quantity));
        limitOrder.lmtPrice(Double.parseDouble(Decimal.get(limitPrice).toString()));
        limitOrder.parentId(parentOrderId);
        limitOrder.transmit(true);         // Final child order transmits the bracket
        limitOrder.orderRef("TakeProfit");

        System.out.println("Placing limit order: " + limitOrder);
        clientSocket.placeOrder(limitOrder.orderId(), contract, limitOrder);
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        // Create an instance of our DynamicBracketOrder as the EWrapper.
        DynamicBracketOrder wrapper = new DynamicBracketOrder();
        clientSocket = new EClientSocket(wrapper);

        // Connect to TWS (here using paper trading settings)
        String host = "127.0.0.1";
        int port = 7496; // Paper trading port (live trading usually uses 7497)
        int clientId = 0;
        clientSocket.eConnect(host, port, clientId);

        // Wait briefly for the connection to be established and for nextValidId callback.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Define a sample contract (for example, a Natural Gas Future on NYMEX)
        Contract contract = new Contract();
        contract.symbol("MHNG");
        contract.secType("FUT");
        contract.exchange("NYMEX");
        contract.currency("USD");
        contract.lastTradeDateOrContractMonth("20250425");
        contract.localSymbol("MNGK5");
        contract.multiplier("1000");

        int parentOrderId = nextOrderId; // Use the valid order id from TWS
        String action = "BUY";
        double quantity = 1.0;
        double offset = 0.10;  // 10 cents offset

        // Set up the listener that is triggered when the market order is filled.
        executionListener = new OrderExecutionListener() {
            @Override
            public void onMarketOrderFilled(int orderId, double filledPrice) {
                System.out.println("Market order filled: Order ID = "
                                   + orderId + ", Filled Price = " + filledPrice);

                // Calculate child order prices based on the fill price.
                double stopPrice = filledPrice - offset;
                double limitPrice = filledPrice + offset;

                // Place the stop loss order.
                placeStopLossOrder(contract, orderId, action, quantity, stopPrice);

                // Brief delay before placing the limit order.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                placeLimitOrder(contract, orderId, action, quantity, limitPrice);
            }
        };

        // Place the parent market order.
        placeMarketOrder(contract, parentOrderId, action, quantity);

        // Keep the application running long enough to receive callbacks.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Disconnect from TWS.
        clientSocket.eDisconnect();
    }
}
