package com.paritytrading.parity.top;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * An order book reconstruction.
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
     * Find an order.
     *
     * @param orderId the order identifier
     * @return the order or <code>null</code> if the order identifier is unknown
     */
    public Order find(long orderId) {
        return orders.get(orderId);
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

        Order order = new Order(book, side, price, size);

        book.add(side, price, size);

        orders.put(orderId, order);

        if (order.isOnBestLevel())
            book.bbo(listener);
    }

    /**
     * Modify an order in the order book. The order will retain its time
     * priority. If the new size is zero, the order is deleted from the
     * order book.
     *
     * <p>A BBO event is triggered if the top of the book changes.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param size the new size
     */
    public void modify(long orderId, long size) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        OrderBook book = order.getOrderBook();

        long newSize = Math.max(0, size);

        boolean onBestLevel = order.isOnBestLevel();

        book.update(order.getSide(), order.getPrice(), newSize - order.getRemainingQuantity());

        if (newSize == 0)
            orders.remove(orderId);
        else
            order.setRemainingQuantity(newSize);

        if (onBestLevel)
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

        execute(orderId, order, quantity, order.getPrice());
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
     * @param price the execution price
     */
    public void execute(long orderId, long quantity, long price) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        execute(orderId, order, quantity, price);
    }

    private void execute(long orderId, Order order, long quantity, long price) {
        OrderBook book = order.getOrderBook();

        Side side = order.getSide();

        long remainingQuantity = order.getRemainingQuantity();

        long executedQuantity = Math.min(quantity, remainingQuantity);

        listener.trade(book.getInstrument(), side, price, executedQuantity);

        book.update(side, order.getPrice(), -executedQuantity);

        if (executedQuantity == remainingQuantity)
            orders.remove(orderId);
        else
            order.reduce(executedQuantity);

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

        OrderBook book = order.getOrderBook();

        long remainingQuantity = order.getRemainingQuantity();

        long canceledQuantity = Math.min(quantity, remainingQuantity);

        boolean onBestLevel = order.isOnBestLevel();

        book.update(order.getSide(), order.getPrice(), -canceledQuantity);

        if (canceledQuantity == remainingQuantity)
            orders.remove(orderId);
        else
            order.reduce(canceledQuantity);

        if (onBestLevel)
            book.bbo(listener);
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

        OrderBook book = order.getOrderBook();

        boolean onBestLevel = order.isOnBestLevel();

        book.update(order.getSide(), order.getPrice(), -order.getRemainingQuantity());

        orders.remove(orderId);

        if (onBestLevel)
            book.bbo(listener);
    }

}
