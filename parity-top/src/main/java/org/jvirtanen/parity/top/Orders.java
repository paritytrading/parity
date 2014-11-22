package org.jvirtanen.parity.top;

import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongComparator;

class Orders {

    private OrderBook parent;

    private Side side;

    private Long2ObjectRBTreeMap<Level> levels;

    public Orders(OrderBook parent, Side side, LongComparator priceComparator) {
        this.parent = parent;

        this.side = side;

        this.levels = new Long2ObjectRBTreeMap<>(priceComparator);
    }

    public OrderBook getParent() {
        return parent;
    }

    public Side getSide() {
        return side;
    }

    public Level getBestLevel() {
        if (levels.isEmpty())
            return null;

        return levels.get(levels.firstLongKey());
    }

    public Order add(long price, long size) {
        Level level = levels.get(price);
        if (level == null) {
            level = new Level(this, price);
            levels.put(price, level);
        }

        return level.add(size);
    }

    public void delete(Level level) {
        levels.remove(level.getPrice());
    }

}
