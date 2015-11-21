package org.jvirtanen.parity.util;

import static org.jvirtanen.nassau.soupbintcp.SoupBinTCP.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.jvirtanen.nassau.MessageListener;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPClient;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPClientStatusListener;

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
            public void heartbeatTimeout() throws IOException {
                throw new IOException("Heartbeat timeout");
            }

            @Override
            public void loginAccepted(LoginAccepted payload) {
            }

            @Override
            public void loginRejected(LoginRejected payload) throws IOException {
                throw new IOException("Login rejected");
            }

            @Override
            public void endOfSession() {
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
