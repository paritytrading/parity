package com.paritytrading.parity.reporter;

import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.TableHeader;

class DisplayFormat extends TradeListener {

    private final Instruments instruments;

    DisplayFormat(Instruments instruments) {
        this.instruments = instruments;

        int priceWidth = instruments.getPriceWidth();
        int sizeWidth  = instruments.getSizeWidth();

        TableHeader header = new TableHeader();

        header.add("Timestamp",       12);
        header.add("Inst",             8);
        header.add("Quantity", sizeWidth);
        header.add("Price",   priceWidth);
        header.add("Buyer",            8);
        header.add("Seller",           8);

        printf("\n");
        printf(header.format());
    }

    @Override
    void trade(Trade event) {
        Instrument instrument = instruments.get(event.instrument);

        printf("%12s %-8s ", event.timestamp, event.instrument);
        printf(instrument.getSizeFormat(), event.quantity / instrument.getSizeFactor());
        printf(" ");
        printf(instrument.getPriceFormat(), event.price / instrument.getPriceFactor());
        printf(" %-8s %-8s\n", event.buyer, event.seller);
    }

}
