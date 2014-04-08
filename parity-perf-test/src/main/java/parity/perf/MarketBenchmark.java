package parity.perf;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Mode;
import java.util.concurrent.TimeUnit;
import parity.match.MarketListener;
import parity.match.Market;
import parity.match.Side;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MarketBenchmark {

    private long sequence;
    private Market market;

    @Setup(Level.Iteration)
    public void prepare() {
        MarketListener listener = new MarketListener() {
            public void match(long restingOrderId, long incomingOrderId, int quantity) {
            }
            public void add(long orderId, Side side, long price, int size) {
            }
            public void cancel(long orderId, int quantity) {
            }
            public void delete(long orderId) {
            }
        };

        market = new Market(listener);

        sequence = 0;
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(Mode.SampleTime)
    public void testEnterOrder() {
        market.enter(sequence++, Side.BUY, 34090, 100);
    }

    @GenerateMicroBenchmark
    @BenchmarkMode(Mode.SampleTime)
    public void testEnterAndDeleteOrder() {
        long id = sequence++;
        market.enter(id, Side.BUY, 34090, 100);
        market.delete(id);
    }
}
