package parity.match;

class Order {

    private Level parent;

    private long id;

    private int remainingQuantity;

    public Order(Level parent, long id, int size) {
        this.parent = parent;

        this.id = id;

        this.remainingQuantity = size;
    }

    public long getId() {
        return id;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void reduce(int quantity) {
        remainingQuantity -= quantity;
    }

    public void delete() {
        parent.delete(this);
    }

}
