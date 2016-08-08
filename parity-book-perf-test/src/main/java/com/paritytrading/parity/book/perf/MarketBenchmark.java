package com.paritytrading.parity.book.perf;

import com.paritytrading.parity.book.Market;
import com.paritytrading.parity.book.MarketListener;
import com.paritytrading.parity.book.Side;
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

    private static final int INSTRUMENT = 1;

    private Market market;

    private long nextOrderId;

    @Setup(Level.Iteration)
    public void prepare() {
        market = new Market(new MarketListener() {

            @Override
            public void bbo(long instrument, long bidPrice, long bidSize, long askPrice, long askSize) {
            }

            @Override
            public void trade(long instrument, Side side, long price, long size) {
            }

        });

        market.open(INSTRUMENT);

        nextOrderId = 0;
    }

    @Benchmark
    public void add() {
        market.add(INSTRUMENT, nextOrderId++, Side.BUY, 100000, 100);
    }

    @Benchmark
    public void addAndModify() {
        long orderId = nextOrderId++;

        market.add(INSTRUMENT, orderId, Side.BUY, 100000, 100);
        market.modify(orderId, 0);
    }

    @Benchmark
    public void addAndExecute() {
        long orderId = nextOrderId++;

        market.add(INSTRUMENT, orderId, Side.BUY, 100000, 100);
        market.execute(orderId, 100);
    }

    @Benchmark
    public void addAndCancel() {
        long orderId = nextOrderId++;

        market.add(INSTRUMENT, orderId, Side.BUY, 100000, 100);
        market.cancel(orderId, 100);
    }

    @Benchmark
    public void addAndDelete() {
        long orderId = nextOrderId++;

        market.add(INSTRUMENT, orderId, Side.BUY, 100000, 100);
        market.delete(orderId);
    }

}
