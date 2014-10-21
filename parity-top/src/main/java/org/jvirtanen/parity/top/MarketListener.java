package org.jvirtanen.parity.top;

/**
 * <code>MarketListener</code> is the interface for outbound events from the
 * order book reconstruction.
 */
public interface MarketListener {

    /**
     * An event indicating that the best bid and offer (BBO) has changed.
     *
     * @param instrument the instrument
     * @param bidPrice the bid price
     * @param bidSize the bid size
     * @param askPrice the ask price
     * @param askSize the ask size
     */
    void bbo(long instrument, long bidPrice, long bidSize, long askPrice, long askSize);

    /**
     * An event indicating that a trade has taken place.
     *
     * @param instrument the instrument
     * @param side the side of the resting order
     * @param price the trade price
     * @param size the trade size
     */
    void trade(long instrument, Side side, long price, long size);

}
