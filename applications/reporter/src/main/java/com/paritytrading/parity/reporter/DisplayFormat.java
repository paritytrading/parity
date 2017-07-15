package com.paritytrading.parity.reporter;

class DisplayFormat extends TradeListener {

    private static final String HEADER = "" +
        "Timestamp    Inst     Quantity   Price     Buyer    Seller\n" +
        "------------ -------- ---------- --------- -------- --------";

    public DisplayFormat() {
        printf("\n%s\n", HEADER);
    }

    @Override
    public void trade(Trade event) {
        printf("%12s %-8s %10d %9.2f %-8s %-8s\n",
                event.timestamp, event.instrument, event.quantity, event.price,
                event.buyer, event.seller);
    }

}
