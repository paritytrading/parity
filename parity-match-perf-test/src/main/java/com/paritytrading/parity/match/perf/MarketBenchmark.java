package com.paritytrading.parity.match.perf;

import com.paritytrading.parity.match.Market;
import com.paritytrading.parity.match.MarketListener;
import com.paritytrading.parity.match.Side;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3, jvmArgsAppend = {"-XX:+UseParallelGC", "-Xms1G", "-Xmx1G"})
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class MarketBenchmark {

    private Market market;

    private long nextOrderId;

    @Setup(Level.Iteration)
    public void prepare() {
        market = new Market(new MarketListener() {

            @Override
            public void match(long restingOrderId, long incomingOrderId, Side incomingSide, long price,
                    long executedQuantity, long remainingQuantity) {
            }

            @Override
            public void add(long orderId, Side side, long price, long size) {
            }

            @Override
            public void cancel(long orderId, long canceledQuantity, long remainingQuantity) {
            }

        });

        nextOrderId = 0;
    }

    @Benchmark
    public void enter() {
        market.enter(nextOrderId++, Side.BUY, 100000, 100);
    }

    @Benchmark
    public void enterAndCancel() {
        long orderId = nextOrderId++;

        market.enter(orderId, Side.BUY, 100000, 100);
        market.cancel(orderId, 0);
    }

}
