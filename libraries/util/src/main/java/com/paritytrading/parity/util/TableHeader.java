package com.paritytrading.parity.util;

import static com.paritytrading.parity.util.Strings.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A table header.
 */
public class TableHeader {

    private List<Column> columns;

    /**
     * Construct a new instance.
     */
    public TableHeader() {
        columns = new ArrayList<>();
    }

    /**
     * Add a column.
     *
     * @param name the column name
     * @param width the column width
     */
    public void add(String name, int width) {
        columns.add(new Column(name, width));
    }

    /**
     * Format this table header for display.
     *
     * @return this table header formatted for display
     */
    public String format() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);

            builder.append(format(column.name, column.width));
            builder.append(i == columns.size() - 1 ? '\n' : ' ');
        }

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);

            builder.append(repeat('-', column.width));
            builder.append(i == columns.size() - 1 ? '\n' : ' ');
        }

        return builder.toString();
    }

    private static class Column {
        public String name;
        public int    width;

        public Column(String name, int width) {
            this.name  = name;
            this.width = width;
        }
    }

    private static String format(String name, int width) {
        return String.format("%-" + width + "." + width + "s", name);
    }

}
