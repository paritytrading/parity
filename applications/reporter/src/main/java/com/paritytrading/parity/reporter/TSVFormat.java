package com.paritytrading.parity.reporter;

class TSVFormat extends TradeListener {

    private static final String HEADER = "" +
        "Timestamp\t" +
        "Match Number\t" +
        "Instrument\t" +
        "Quantity\t" +
        "Price\t" +
        "Buyer\t" +
        "Buy Order Number\t" +
        "Seller\t" +
        "Sell Order Number\n";

    public TSVFormat() {
        printf(HEADER);
    }

    @Override
    public void trade(Trade event) {
        printf("%s\t%d\t%s\t%d\t%.2f\t%s\t%d\t%s\t%d\n",
                event.timestamp, event.matchNumber, event.instrument, event.quantity,
                event.price, event.buyer, event.buyOrderNumber, event.seller,
                event.sellOrderNumber);
    }

}
