package org.jvirtanen.parity.top;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * <code>Market</code> represents an order book reconstruction.
 */
public class Market {

    private Long2ObjectOpenHashMap<OrderBook> books;

    private Long2ObjectOpenHashMap<Order> orders;

    private MarketListener listener;

    /**
     * Create an order book reconstruction.
     *
     * @param listener a listener for outbound events from the order book
     *   reconstruction
     */
    public Market(MarketListener listener) {
        this.books  = new Long2ObjectOpenHashMap<>();
        this.orders = new Long2ObjectOpenHashMap<>();

        this.listener = listener;
    }

    /**
     * Open an order book.
     *
     * <p>If the order book for the instrument is already open, do nothing.</p>
     *
     * @param instrument an instrument
     */
    public void open(long instrument) {
        if (books.containsKey(instrument))
            return;

        books.put(instrument, new OrderBook(instrument));
    }

    /**
     * Add an order to the order book.
     *
     * <p>A BBO event is triggered if the top of the book changes.</p>
     *
     * <p>If the order book for the instrument is closed or the order
     * identifier is known, do nothing.</p>
     *
     * @param instrument the instrument
     * @param orderId the order identifier
     * @param side the side
     * @param price the price
     * @param size the size
     */
    public void add(long instrument, long orderId, Side side, long price, long size) {
        if (orders.containsKey(orderId))
            return;

        OrderBook book = books.get(instrument);
        if (book == null)
            return;

        Order order = book.add(side, price, size);
        orders.put(orderId, order);

        if (order.isOnBestLevel())
            book.bbo(listener);
    }

    /**
     * Execute a quantity of an order in the order book. If the remaining
     * quantity reaches zero, the order is deleted from the order book.
     *
     * <p>A Trade event and a BBO event are triggered.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param quantity the executed quantity
     */
    public void execute(long orderId, long quantity) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        OrderBook book = order.getOrderBook();

        listener.trade(book.getInstrument(), order.getSide(), order.getPrice(), quantity);

        if (quantity < order.getRemainingQuantity()) {
            order.reduce(quantity);
        } else {
            orders.remove(orderId);
            order.delete();
        }

        book.bbo(listener);
    }

    /**
     * Cancel a quantity of an order in the order book. If the remaining
     * quantity reaches zero, the order is deleted from the order book.
     *
     * <p>A BBO event is triggered if the top of the book changes.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param quantity the canceled quantity
     */
    public void cancel(long orderId, long quantity) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        boolean onBestLevel = order.isOnBestLevel();

        if (quantity < order.getRemainingQuantity()) {
            order.reduce(quantity);
        } else {
            orders.remove(orderId);
            order.delete();
        }

        if (onBestLevel)
            order.getOrderBook().bbo(listener);
    }

    /**
     * Delete an order from the order book.
     *
     * <p>A BBO event is triggered if the top of the book changes.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     */
    public void delete(long orderId) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        boolean onBestLevel = order.isOnBestLevel();

        orders.remove(orderId);
        order.delete();

        if (onBestLevel)
            order.getOrderBook().bbo(listener);
    }

}
