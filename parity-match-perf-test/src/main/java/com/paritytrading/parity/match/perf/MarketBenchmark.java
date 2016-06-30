package com.paritytrading.parity.match.perf;

import com.paritytrading.parity.match.Market;
import com.paritytrading.parity.match.MarketListener;
import com.paritytrading.parity.match.Side;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class MarketBenchmark {

    private long sequence;
    private Market market;

    @Setup(Level.Iteration)
    public void prepare() {
        MarketListener listener = new MarketListener() {
            public void match(long restingOrderId, long incomingOrderId, Side incomingSide, long price,
                    long executedQuantity, long remainingQuantity) {
            }
            public void add(long orderId, Side side, long price, long size) {
            }
            public void cancel(long orderId, long canceledQuantity, long remainingQuantity) {
            }
        };

        market = new Market(listener);

        sequence = 0;
    }

    @Benchmark
    public void testEnterOrder() {
        market.enter(sequence++, Side.BUY, 34090, 100);
    }

    @Benchmark
    public void testEnterAndDeleteOrder() {
        long id = sequence++;
        market.enter(id, Side.BUY, 34090, 100);
        market.cancel(id, 0);
    }
}
