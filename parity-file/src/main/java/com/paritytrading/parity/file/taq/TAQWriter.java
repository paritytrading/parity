package com.paritytrading.parity.file.taq;

import static com.paritytrading.parity.file.taq.TAQ.*;

import com.paritytrading.parity.util.Timestamps;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A writer.
 */
public class TAQWriter implements Closeable, Flushable {

    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);

    private static final int BUFFER_CAPACITY = 32;

    private static final String HEADER = "" +
        "Date"        + FIELD_SEPARATOR +
        "Timestamp"   + FIELD_SEPARATOR +
        "Instrument"  + FIELD_SEPARATOR +
        "Record Type" + FIELD_SEPARATOR +
        "Bid Price"   + FIELD_SEPARATOR +
        "Bid Size"    + FIELD_SEPARATOR +
        "Ask Price"   + FIELD_SEPARATOR +
        "Ask Size"    + FIELD_SEPARATOR +
        "Trade Price" + FIELD_SEPARATOR +
        "Trade Size"  + FIELD_SEPARATOR +
        "Trade Side"  + RECORD_SEPARATOR;

    private DecimalFormat priceFormat;
    private DecimalFormat sizeFormat;

    private FieldPosition position;

    private StringBuffer buffer;

    private PrintWriter sink;

    /**
     * Create a writer that writes to the specified file using the default
     * configuration.
     *
     * @param file a file
     * @throws FileNotFoundException if the file cannot be opened
     */
    public TAQWriter(File file) throws FileNotFoundException {
        this(file, TAQConfig.DEFAULTS);
    }

    /**
     * Create a writer that writes to the specified file.
     *
     * @param file a file
     * @param config the configuration
     * @throws FileNotFoundException if the file cannot be opened
     */
    public TAQWriter(File file, TAQConfig config) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(file)), config);
    }

    /**
     * Create a writer that writes to the specified output stream using the
     * default configuration.
     *
     * @param out an output stream
     */
    public TAQWriter(OutputStream out) {
        this(out, TAQConfig.DEFAULTS);
    }

    /**
     * Create a writer that writes to the specified output stream.
     *
     * @param out an output stream
     * @param config the configuration
     */
    public TAQWriter(OutputStream out, TAQConfig config) {
        this(new OutputStreamWriter(out, config.getEncoding()), config);
    }

    private TAQWriter(Writer writer, TAQConfig config) {
        priceFormat = new DecimalFormat("0.00", SYMBOLS);

        priceFormat.setMinimumFractionDigits(config.getPriceFractionDigits());
        priceFormat.setMaximumFractionDigits(config.getPriceFractionDigits());

        sizeFormat  = new DecimalFormat("0", SYMBOLS);

        sizeFormat.setMinimumFractionDigits(config.getSizeFractionDigits());
        sizeFormat.setMaximumFractionDigits(config.getSizeFractionDigits());

        position = new FieldPosition(NumberFormat.INTEGER_FIELD);

        buffer = new StringBuffer(BUFFER_CAPACITY);

        sink = new PrintWriter(writer);

        sink.print(HEADER);
    }

    /**
     * Write a Trade record.
     *
     * @param record a Trade record
     */
    public void write(Trade record) {
        sink.print(record.date);
        sink.print(FIELD_SEPARATOR);
        sink.print(Timestamps.format(record.timestampMillis));
        sink.print(FIELD_SEPARATOR);
        sink.print(record.instrument);
        sink.print(FIELD_SEPARATOR);
        sink.print(RECORD_TYPE_TRADE);
        sink.print(FIELD_SEPARATOR);
        // bid price
        sink.print(FIELD_SEPARATOR);
        // bid size
        sink.print(FIELD_SEPARATOR);
        // ask price
        sink.print(FIELD_SEPARATOR);
        // ask size
        sink.print(FIELD_SEPARATOR);
        writePrice(record.price);
        sink.print(FIELD_SEPARATOR);
        writeSize(record.size);
        sink.print(FIELD_SEPARATOR);

        if (record.side != UNKNOWN)
            sink.print(record.side);

        sink.print(RECORD_SEPARATOR);
    }

    /**
     * Write a Quote record.
     *
     * @param record a Quote record
     */
    public void write(Quote record) {
        sink.print(record.date);
        sink.print(FIELD_SEPARATOR);
        sink.print(Timestamps.format(record.timestampMillis));
        sink.print(FIELD_SEPARATOR);
        sink.print(record.instrument);
        sink.print(FIELD_SEPARATOR);
        sink.print(RECORD_TYPE_QUOTE);
        sink.print(FIELD_SEPARATOR);

        if (record.bidSize > 0)
            writePrice(record.bidPrice);

        sink.print(FIELD_SEPARATOR);

        if (record.bidSize > 0)
            writeSize(record.bidSize);

        sink.print(FIELD_SEPARATOR);

        if (record.askSize > 0)
            writePrice(record.askPrice);

        sink.print(FIELD_SEPARATOR);

        if (record.askSize > 0)
            writeSize(record.askSize);

        sink.print(FIELD_SEPARATOR);
        // trade price
        sink.print(FIELD_SEPARATOR);
        // trade size
        sink.print(FIELD_SEPARATOR);
        // trade side
        sink.print(RECORD_SEPARATOR);
    }

    @Override
    public void close() {
        sink.close();
    }

    @Override
    public void flush() {
        sink.flush();
    }

    private void writePrice(double price) {
        buffer.setLength(0);

        priceFormat.format(price, buffer, position);

        sink.append(buffer);
    }

    private void writeSize(double size) {
        buffer.setLength(0);

        sizeFormat.format(size, buffer, position);

        sink.append(buffer);
    }

}
