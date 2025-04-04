package com.mycompany.bracketorder;

import com.ib.client.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Matches the EWrapper interface from your TwsApi.jar exactly (per javap).
 * Uses IB's Decimal in places required by the new interface, and correct
 * method signatures for orderStatus, error(...), updatePortfolio(...), etc.
 */
public class DynamicBracketOrder implements EWrapper {

    // ----- IB API Objects -----
    private static EClientSocket clientSocket;
    private static EJavaSignal signal;

    // Tracks the next valid order ID from TWS
    private static int nextOrderId = 1;

    /**
     * Callback interface: notify when the parent MKT order is filled.
     */
    public interface OrderExecutionListener {
        void onMarketOrderFilled(int orderId, double filledPrice);
    }

    private static OrderExecutionListener executionListener;

    // ----------------------------------------------------------------
    // Implementing EWrapper from your JAR EXACTLY
    // ----------------------------------------------------------------

    // 1) TICK PRICE
    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {}

    // 2) TICK SIZE
    @Override
    public void tickSize(int tickerId, int field, Decimal size) {}

    // 3) TICK OPTION
    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib,
                                      double impliedVol, double delta,
                                      double optPrice, double pvDividend,
                                      double gamma, double vega, double theta,
                                      double undPrice) {}

    // 4) TICK GENERIC
    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {}

    // 5) TICK STRING
    @Override
    public void tickString(int tickerId, int tickType, String value) {}

    // 6) TICK EFP
    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints,
                        String formattedBasisPoints, double totalDividends,
                        int holdDays, String futureExpiry, double dividendImpact,
                        double dividendsToExpiry) {}

    // 7) ORDER STATUS  (Note the signature from your javap output!)
    @Override
    public void orderStatus(int orderId, String status,
                            Decimal filled, Decimal remaining,
                            double avgFillPrice, int permId,
                            int parentId, double lastFillPrice,
                            int clientId, String whyHeld,
                            double mktCapPrice) {
        System.out.println(">>> orderStatus: orderId=" + orderId
                + ", status=" + status
                + ", filled=" + filled
                + ", avgFillPrice=" + avgFillPrice);

        // When the parent MKT is "Filled", place the bracket children
        if ("Filled".equalsIgnoreCase(status) && executionListener != null) {
            // 'avgFillPrice' is already a double in this IB version
            double fillPrice = avgFillPrice;
            executionListener.onMarketOrderFilled(orderId, fillPrice);
        }
    }

    // 8) OPEN ORDER
    @Override
    public void openOrder(int orderId, Contract contract,
                          Order order, OrderState orderState) {}
    // 9) OPEN ORDER END
    @Override
    public void openOrderEnd() {}
    // 10) UPDATE ACCOUNT VALUE
    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {}
    // 11) UPDATE PORTFOLIO (Note the Decimal position param!)
    @Override
    public void updatePortfolio(Contract contract, Decimal position,
                                double marketPrice, double marketValue,
                                double averageCost, double unrealizedPnl,
                                double realizedPnl, String accountName) {}
    // 12) UPDATE ACCOUNT TIME
    @Override
    public void updateAccountTime(String timeStamp) {}
    // 13) ACCOUNT DOWNLOAD END
    @Override
    public void accountDownloadEnd(String accountName) {}
    // 14) NEXT VALID ID
    @Override
    public void nextValidId(int orderId) {
        nextOrderId = orderId;
        System.out.println(">>> nextValidId: " + nextOrderId);
    }
    // 15) CONTRACT DETAILS
    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {}
    // 16) BOND CONTRACT DETAILS
    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {}
    // 17) CONTRACT DETAILS END
    @Override
    public void contractDetailsEnd(int reqId) {}
    // 18) EXEC DETAILS
    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {}
    // 19) EXEC DETAILS END
    @Override
    public void execDetailsEnd(int reqId) {}
    // 20) UPDATE MKT DEPTH
    @Override
    public void updateMktDepth(int tickerId, int position, int operation,
                               int side, double price, Decimal size) {}
    // 21) UPDATE MKT DEPTH L2
    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker,
                                 int operation, int side, double price, Decimal size,
                                 boolean isSmartDepth) {}
    // 22) UPDATE NEWS BULLETIN
    @Override
    public void updateNewsBulletin(int msgId, int msgType,
                                   String message, String origExchange) {}
    // 23) MANAGED ACCOUNTS
    @Override
    public void managedAccounts(String accountsList) {}
    // 24) RECEIVE FA
    @Override
    public void receiveFA(int faDataType, String xml) {}
    // 25) HISTORICAL DATA (newer versions have Bar object param)
    @Override
    public void historicalData(int reqId, Bar bar) {}
    // 26) SCANNER PARAMETERS
    @Override
    public void scannerParameters(String xml) {}
    // 27) SCANNER DATA
    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails,
                            String distance, String benchmark,
                            String projection, String legsStr) {}
    // 28) SCANNER DATA END
    @Override
    public void scannerDataEnd(int reqId) {}
    // 29) REALTIME BAR
    @Override
    public void realtimeBar(int reqId, long time, double open, double high,
                            double low, double close, Decimal volume,
                            Decimal wap, int count) {}
    // 30) CURRENT TIME
    @Override
    public void currentTime(long time) {}
    // 31) FUNDAMENTAL DATA
    @Override
    public void fundamentalData(int reqId, String data) {}
    // 32) DELTA NEUTRAL VALIDATION
    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {}
    // 33) TICK SNAPSHOT END
    @Override
    public void tickSnapshotEnd(int reqId) {}
    // 34) MARKET DATA TYPE
    @Override
    public void marketDataType(int reqId, int marketDataType) {}
    // 35) COMMISSION REPORT
    @Override
    public void commissionReport(CommissionReport commissionReport) {}
    // 36) POSITION
    @Override
    public void position(String account, Contract contract, Decimal pos, double avgCost) {}
    // 37) POSITION END
    @Override
    public void positionEnd() {}
    // 38) ACCOUNT SUMMARY
    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {}
    // 39) ACCOUNT SUMMARY END
    @Override
    public void accountSummaryEnd(int reqId) {}
    // 40) VERIFY MESSAGE API
    @Override
    public void verifyMessageAPI(String apiData) {}
    // 41) VERIFY COMPLETED
    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {}
    // 42) VERIFY AND AUTH MESSAGE API
    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {}
    // 43) VERIFY AND AUTH COMPLETED
    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {}
    // 44) DISPLAY GROUP LIST
    @Override
    public void displayGroupList(int reqId, String groups) {}
    // 45) DISPLAY GROUP UPDATED
    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {}
    // 46) ERROR(Exception)
    @Override
    public void error(Exception e) {
        e.printStackTrace();
    }
    // 47) ERROR(String)
    @Override
    public void error(String str) {
        System.err.println("ERROR(str): " + str);
    }
    // 48) ERROR(int,int,String,String)  <-- 4th param for advanced info
    @Override
    public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        System.err.println("ERROR: id=" + id
                + ", code=" + errorCode
                + ", msg=" + errorMsg
                + ", advReject=" + advancedOrderRejectJson);
    }
    // 49) CONNECTION CLOSED
    @Override
    public void connectionClosed() {
        System.out.println(">>> Connection closed.");
    }
    // 50) CONNECT ACK
    @Override
    public void connectAck() {
        // Stub
    }
    // 51) POSITION MULTI
    @Override
    public void positionMulti(int reqId, String account, String modelCode,
                              Contract contract, Decimal pos, double avgCost) {}
    // 52) POSITION MULTI END
    @Override
    public void positionMultiEnd(int reqId) {}
    // 53) ACCOUNT UPDATE MULTI
    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode,
                                   String key, String value, String currency) {}
    // 54) ACCOUNT UPDATE MULTI END
    @Override
    public void accountUpdateMultiEnd(int reqId) {}
    // 55) SECURITY DEFINITION OPTIONAL PARAMETER
    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange,
                                                    int underlyingConId, String tradingClass,
                                                    String multiplier, Set<String> expirations,
                                                    Set<Double> strikes) {}
    // 56) SECURITY DEFINITION OPTIONAL PARAMETER END
    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {}
    // 57) SOFT DOLLAR TIERS
    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {}
    // 58) FAMILY CODES
    @Override
    public void familyCodes(FamilyCode[] familyCodes) {}
    // 59) SYMBOL SAMPLES
    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {}
    // 60) HISTORICAL DATA END
    @Override
    public void historicalDataEnd(int reqId, String start, String end) {}
    // 61) MKT DEPTH EXCHANGES
    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] descriptions) {}
    // 62) TICK NEWS
    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode,
                         String articleId, String headline, String extraData) {}
    // 63) SMART COMPONENTS
    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {}
    // 64) TICK REQ PARAMS
    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {}
    // 65) NEWS PROVIDERS
    @Override
    public void newsProviders(NewsProvider[] newsProviders) {}
    // 66) NEWS ARTICLE
    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {}
    // 67) HISTORICAL NEWS
    @Override
    public void historicalNews(int reqId, String time, String providerCode, String articleId, String headline) {}
    // 68) HISTORICAL NEWS END
    @Override
    public void historicalNewsEnd(int reqId, boolean hasMore) {}
    // 69) HEAD TIMESTAMP
    @Override
    public void headTimestamp(int reqId, String headTimestamp) {}
    // 70) HISTOGRAM DATA
    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {}
    // 71) HISTORICAL DATA UPDATE (Bar param)
    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {}
    // 72) REROUTE MKT DATA REQ
    @Override
    public void rerouteMktDataReq(int reqId, int conid, String exchange) {}
    // 73) REROUTE MKT DEPTH REQ
    @Override
    public void rerouteMktDepthReq(int reqId, int conid, String exchange) {}
    // 74) MARKET RULE
    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {}
    // 75) PNL
    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {}
    // 76) PNL SINGLE
    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {}
    // 77) HISTORICAL TICKS
    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {}
    // 78) HISTORICAL TICKS BID/ASK
    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {}
    // 79) HISTORICAL TICKS LAST
    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {}
    // 80) TICK BY TICK ALL LAST
    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time,
                                  double price, Decimal size,
                                  TickAttribLast tickAttribLast,
                                  String exchange, String specialConditions) {}
    // 81) TICK BY TICK BID/ASK
    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice,
                                 double askPrice, Decimal bidSize,
                                 Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {}
    // 82) TICK BY TICK MID POINT
    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {}
    // 83) ORDER BOUND
    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {}
    // 84) COMPLETED ORDER
    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {}
    // 85) COMPLETED ORDERS END
    @Override
    public void completedOrdersEnd() {}
    // 86) REPLACE FA END
    @Override
    public void replaceFAEnd(int reqId, String text) {}
    // 87) WSH META DATA
    @Override
    public void wshMetaData(int reqId, String data) {}
    // 88) WSH EVENT DATA
    @Override
    public void wshEventData(int reqId, String data) {}
    // 89) HISTORICAL SCHEDULE
    @Override
    public void historicalSchedule(int reqId, String startDateTime,
                                   String endDateTime, String timeZone,
                                   List<HistoricalSession> sessions) {}
    // 90) USER INFO
    @Override
    public void userInfo(int reqId, String info) {}

    // ----------------------------------------------------------------
    // BRACKET ORDER LOGIC
    // ----------------------------------------------------------------

    /**
     * Place the parent MKT order.
     */
    public static void placeMarketOrder(Contract contract,
                                        int parentOrderId,
                                        String action,
                                        double quantity) {
        Order parentOrder = new Order();
        parentOrder.orderId(parentOrderId);
        parentOrder.action(action);
        parentOrder.orderType("MKT");
        parentOrder.totalQuantity(Decimal.get(quantity));
        parentOrder.transmit(true);
        parentOrder.orderRef("ParentMarketOrder");

        System.out.println(">>> Placing MARKET order: " + parentOrder);
        clientSocket.placeOrder(parentOrderId, contract, parentOrder);
    }

    /**
     * Place the STP (stop-loss) child order.
     */
    public static void placeStopLossOrder(Contract contract,
                                          int parentOrderId,
                                          String action,
                                          double quantity,
                                          double stopPrice) {
        Order stopLossOrder = new Order();
        stopLossOrder.orderId(parentOrderId + 1);
        stopLossOrder.action(action.equalsIgnoreCase("BUY") ? "SELL" : "BUY");
        stopLossOrder.orderType("STP");
        stopLossOrder.totalQuantity(Decimal.get(quantity));
        stopLossOrder.auxPrice(Double.parseDouble(Decimal.get(stopPrice).toString()));
        stopLossOrder.parentId(parentOrderId);
        stopLossOrder.transmit(false);  // bracket
        stopLossOrder.orderRef("StopLoss");

        System.out.println(">>> Placing STOP-LOSS order: " + stopLossOrder);
        clientSocket.placeOrder(stopLossOrder.orderId(), contract, stopLossOrder);
    }

    /**
     * Place the LMT (take-profit) child order.
     */
    public static void placeLimitOrder(Contract contract,
                                       int parentOrderId,
                                       String action,
                                       double quantity,
                                       double limitPrice) {
        Order limitOrder = new Order();
        limitOrder.orderId(parentOrderId + 2);
        limitOrder.action(action.equalsIgnoreCase("BUY") ? "SELL" : "BUY");
        limitOrder.orderType("LMT");
        limitOrder.totalQuantity(Decimal.get(quantity));
        limitOrder.lmtPrice(Double.parseDouble(Decimal.get(limitPrice).toString()));
        limitOrder.parentId(parentOrderId);
        limitOrder.transmit(true);
        limitOrder.orderRef("TakeProfit");

        System.out.println(">>> Placing TAKE-PROFIT order: " + limitOrder);
        clientSocket.placeOrder(limitOrder.orderId(), contract, limitOrder);
    }

    // ----------------------------------------------------------------
    // MAIN
    // ----------------------------------------------------------------
    public static void main(String[] args) {
        // EWrapper instance
        DynamicBracketOrder wrapper = new DynamicBracketOrder();
        // EReaderSignal
        signal = new EJavaSignal();
        // EClientSocket
        clientSocket = new EClientSocket(wrapper, signal);

        System.out.println(">>> Connecting to TWS...");
        clientSocket.eConnect("127.0.0.1", 7496, 0);

        // Start reader for TWS messages
        final EReader reader = new EReader(clientSocket, signal);
        reader.start();
        new Thread(() -> {
            while (clientSocket.isConnected()) {
                signal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Wait a bit for TWS to send back nextValidId, etc.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { /* ignore */ }

        // Example FUT contract
        Contract contract = new Contract();
        contract.symbol("MHNG");
        contract.secType("FUT");
        contract.exchange("NYMEX");
        contract.currency("USD");
        contract.lastTradeDateOrContractMonth("20250425");
        contract.localSymbol("MNGK5");
        contract.multiplier("1000");

        // Example bracket parameters
        int parentOrderId = nextOrderId;
        String action = "BUY";
        double quantity = 1.0;
        double offset = 0.010;  //

        // When parent is filled, place children
        executionListener = (orderId, filledPrice) -> {
            System.out.println(">>> Parent filled: ID=" + orderId + ", fillPrice=" + filledPrice);

            double stopPrice = filledPrice - offset;
            double limitPrice = filledPrice + offset;

            // Stop
            placeStopLossOrder(contract, orderId, action, quantity, stopPrice);

            // Short delay
            try { Thread.sleep(50); } catch (InterruptedException e) {}

            // Limit
            placeLimitOrder(contract, orderId, action, quantity, limitPrice);
        };

        // Place parent MKT
        placeMarketOrder(contract, parentOrderId, action, quantity);

        // Keep running briefly to see orderStatus callbacks
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) { /* ignore */ }

        System.out.println(">>> Disconnecting...");
        clientSocket.eDisconnect();
    }
}
