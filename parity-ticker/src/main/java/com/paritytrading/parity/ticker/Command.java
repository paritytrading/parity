package com.paritytrading.parity.ticker;

import java.io.IOException;
import java.util.List;

interface Command {

    void execute(List<String> arguments) throws IOException;

    String getName();

    String getDescription();

}
