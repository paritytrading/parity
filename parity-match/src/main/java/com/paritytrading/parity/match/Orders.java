package com.paritytrading.parity.match;

import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongComparator;

class Orders {

    private Long2ObjectRBTreeMap<Level> levels;

    public Orders(LongComparator priceComparator) {
        this.levels = new Long2ObjectRBTreeMap<>(priceComparator);
    }

    public Level getBestLevel() {
        if (levels.isEmpty())
            return null;

        return levels.get(levels.firstLongKey());
    }

    public Order add(long orderId, long price, long size) {
        Level level = levels.get(price);
        if (level == null) {
            level = new Level(this, price);
            levels.put(price, level);
        }

        return level.add(orderId, size);
    }

    public void delete(Level level) {
        levels.remove(level.getPrice());
    }

}
