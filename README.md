TODO
How to run, 
mvn clean compile
java -cp target/classes:lib/TwsApi.jar com.mycompany.bracketorder.DynamicBracketOrder



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


problem, 

   ~Below is one Java source file that directly matches the newer IB API interface (which uses Decimal in orderStatus, etc.). It implements all required methods of EWrapper including wshEventData(...), so you no longer get “not abstract and does not override…” errors. It also handles Decimal in places where IB now expects it.
    
    Please replace your existing DynamicBracketOrder.java with this, then compile again. This version should fix the “incompatible types: Decimal cannot be converted to double” and the missing wshEventData(...) method.


    Your code and your TwsApi.jar are out of sync. In other words, the code you’re compiling does not match the exact version of the IB API that your local JAR provides. IB has changed the EWrapper interface over time (e.g., switching double → Decimal in orderStatus, adding methods like historicalSchedule, wshEventData, userInfo, etc.). If your code references old signatures while your JAR wants new ones—or vice versa—you get these “does not override abstract method” and “incompatible types: Decimal cannot be converted to double” errors.

Why This Keeps Happening
EWrapper Interface Has Changed
Different versions of TwsApi.jar have different EWrapper methods. For instance, older versions used double in orderStatus(...), newer versions use Decimal. Also, newer versions add methods like:

historicalSchedule(...)

wshEventData(...)

userInfo(...) If your code doesn’t implement those new methods exactly—or if it tries to override them with the older signatures—compilation fails.

Mixed-Up Signatures
IB’s newer API requires:

java
Copy
public void orderStatus(int orderId, String status,
    Decimal filled, Decimal remaining, Decimal avgFillPrice,
    int permId, int parentId, Decimal lastFillPrice,
    int clientId, String whyHeld, double mktCapPrice);
but the older APIs used:

java
Copy
public void orderStatus(int orderId, String status,
    double filled, double remaining, double avgFillPrice,
    int permId, int parentId, double lastFillPrice,
    int clientId, String whyHeld);
If your TwsApi.jar wants the new signature (with Decimal) but your code uses the old signature (with double), the compiler will complain.

Some Methods Are Entirely Missing
For example, you have an error about historicalSchedule(...) not being implemented. That method didn’t exist in older EWrapper versions. If your JAR is new enough to need it, your code must implement it—even if empty.

How to Fix It
Confirm Exactly Which Version of the IB API Your TwsApi.jar Is.

If it’s a recent version (e.g., 10.21+, or anything from 2023+), you likely need the new method signatures with Decimal and these new methods (historicalSchedule, wshEventData, etc.).

If it’s an older version, you’d need the old method signatures with double.

Implement the EWrapper Interface Exactly as Defined by Your JAR.

The sure-fire approach is to open the TwsApi.jar or do javap -cp TwsApi.jar com.ib.client.EWrapper to see exactly what methods it declares.

Then make sure your class implements each one, with the correct parameters and types.

Remove Any Code for Methods That Don’t Exist in Your Actual JAR, or Add Any Methods That Do Exist.

If your JAR’s EWrapper interface has historicalSchedule(int, String, String, String, List<HistoricalSession>), your code needs that method signature.

If your JAR doesn’t have error(Exception e), remove it from your code.

Match the Decimal Usage

If your IB library uses Decimal for order quantities and fill prices, call Decimal.get(...) when setting them.

Convert them back to double if needed via Double.parseDouble(decimal.toString()).

Pick One “Known Good” Example

You can grab the official IB sample code for your exact version from the Interactive Brokers GitHub or from the IB API install directory, then adapt it for your bracket logic.

The Core Reason
So the fundamental reason is: the code you’re compiling does not match the exact interface definitions in the version of TwsApi.jar you’re actually using. That mismatch triggers all these errors about missing methods, not overriding certain methods, or converting Decimal to double.

Next Steps
Check Your IB API Version

Possibly by looking at the release notes or by unzipping TwsApi.jar to see the version file.

Or run javap -cp lib/TwsApi.jar com.ib.client.EWrapper to see every method your JAR expects.

Use the Correct Code for That Version

Ensure every method in EWrapper is implemented.

For the new versions: orderStatus uses Decimal (not double), there’s a historicalSchedule(...), wshEventData(...), userInfo(...), etc.

Test in a Paper Account

Once it compiles, connect to TWS (paper port 7496 by default), place a simple test order, and confirm it shows up in TWS.

If you do all that, your bracket order code will compile and run. The repeated errors simply mean the code and the JAR are out of sync.

You still have two spots (lines 415 and 437 in DynamicBracketOrder.java) where your code is trying to use a com.ib.client.Decimal as if it were a double. The compiler sees something like:

java
Copy
double someVar = someDecimal;
…which won’t compile because Decimal can’t be directly assigned to a double. You must convert the Decimal to double—for example, by using:

java
Copy
double someVar = Double.parseDouble(someDecimal.toString());
or, if your IB Decimal class provides a helper like doubleValue(), you can do:

java
Copy
double someVar = someDecimal.doubleValue();
Commands to Help Debug Faster
Below are some shell commands you can run to pinpoint the exact code and see helpful details:

Show the Exact Lines of the Error in Your File

bash
Copy
# Because the compiler reported lines 415 and 437,
# let’s see the lines around them:
cat -n src/main/java/com/mycompany/bracketorder/DynamicBracketOrder.java \
    | sed -n '410,445p'
That will print lines 410 through 445 with line numbers. You can adjust as needed to see the code near 415 or 437.

Do a “grep” for Decimal Usage

bash
Copy
grep -n Decimal src/main/java/com/mycompany/bracketorder/DynamicBracketOrder.java
This quickly shows all lines referencing Decimal. You can see where you might be trying to stuff a Decimal into a double.

Compile Verbosely

bash
Copy
mvn clean compile -X
The -X flag (“debug” or “extended output”) prints the full stack trace and more detail about how Maven is compiling your code.

View the IB Decimal Class

bash
Copy
javap -cp lib/TwsApi.jar com.ib.client.Decimal
This tells you what methods Decimal has. If it has something like public double doubleValue(), that’s an easy way to convert. Otherwise, you’ll do Double.parseDouble(decimal.toString()).

Show Java & Maven Versions

bash
Copy
java -version
mvn -version
This confirms you’re using the correct JDK and Maven. (Occasionally a mismatch can cause odd issues.)

You can run these again to confirm:

mvn clean compile (as normal).

mvn clean compile -e (shows a shorter but more descriptive error stacktrace).

mvn clean compile -X (super verbose).

javap -cp lib/TwsApi.jar com.ib.client.Order (to see how auxPrice(...) or lmtPrice(...) are declared).


Run the Code
Since your JAR is local and not a dependency on Maven Central, you need to include both target/classes and lib/TwsApi.jar on the classpath. For example:

bash
java -cp target/classes:lib/TwsApi.jar com.mycompany.bracketorder.DynamicBracketOrder
