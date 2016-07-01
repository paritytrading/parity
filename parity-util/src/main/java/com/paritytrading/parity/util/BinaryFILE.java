package com.paritytrading.parity.util;

import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.binaryfile.BinaryFILEReader;
import com.paritytrading.nassau.binaryfile.BinaryFILEStatusListener;
import com.paritytrading.nassau.binaryfile.BinaryFILEStatusParser;
import java.io.File;
import java.io.IOException;

/**
 * This class contains utility methods for BinaryFILE.
 */
public class BinaryFILE {

    private BinaryFILE() {
    }

    /**
     * Read messages.
     *
     * @param file a file
     * @param listener a message listener
     * @throws IOException if an I/O error occurs
     */
    public static void read(File file, MessageListener listener) throws IOException {
        BinaryFILEStatusListener statusListener = new BinaryFILEStatusListener() {

            @Override
            public void endOfSession() {
            }

        };

        BinaryFILEStatusParser statusParser = new BinaryFILEStatusParser(listener, statusListener);

        try (BinaryFILEReader reader = BinaryFILEReader.open(file, statusParser)) {
            while (reader.read());
        }
    }
}
