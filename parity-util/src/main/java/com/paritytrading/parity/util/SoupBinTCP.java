package com.paritytrading.parity.util;

import static com.paritytrading.nassau.soupbintcp.SoupBinTCP.*;

import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * This class contains utility methods for SoupBinTCP.
 */
public class SoupBinTCP {

    private static final int TIMEOUT = 1000;

    private SoupBinTCP() {
    }

    /**
     * Receive messages.
     *
     * @param address the address
     * @param username the username
     * @param password the password
     * @param listener the message listener
     * @throws IOException if an I/O error occurs
     */
    public static void receive(InetSocketAddress address, String username,
            String password, MessageListener listener) throws IOException {
        SocketChannel channel = SocketChannel.open();

        channel.connect(address);
        channel.configureBlocking(false);

        SoupBinTCPClientStatusListener statusListener = new SoupBinTCPClientStatusListener() {

            @Override
            public void heartbeatTimeout(SoupBinTCPClient session) throws IOException {
                throw new IOException("Heartbeat timeout");
            }

            @Override
            public void loginAccepted(SoupBinTCPClient session, LoginAccepted payload) {
            }

            @Override
            public void loginRejected(SoupBinTCPClient session, LoginRejected payload) throws IOException {
                throw new IOException("Login rejected");
            }

            @Override
            public void endOfSession(SoupBinTCPClient session) {
            }

        };

        SoupBinTCPClient client = new SoupBinTCPClient(channel, listener, statusListener);

        LoginRequest message = new LoginRequest();

        message.username = username;
        message.password = password;
        message.requestedSession = "";
        message.requestedSequenceNumber = 0;

        client.login(message);

        receive(client);
    }

    private static void receive(SoupBinTCPClient client) throws IOException {
        Selector selector = Selector.open();

        client.getChannel().register(selector, SelectionKey.OP_READ);

        while (true) {
            selector.select(TIMEOUT);

            if (!selector.selectedKeys().isEmpty()) {
                client.receive();

                selector.selectedKeys().clear();
            }

            client.keepAlive();
        }
    }

}
