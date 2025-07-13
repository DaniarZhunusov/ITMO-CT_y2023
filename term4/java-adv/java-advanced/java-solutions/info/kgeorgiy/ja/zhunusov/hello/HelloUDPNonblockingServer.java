package info.kgeorgiy.ja.zhunusov.hello;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class HelloUDPNonblockingServer extends AbstractHelloUDPServer {
    private Selector selector;
    private DatagramChannel channel;
    private Thread serverThread;
    private volatile boolean running = false;

    @Override
    public void start(int port, int threads) {
        try {
            selector = Selector.open();
            channel = DatagramChannel.open();
            channel.configureBlocking(false);

            channel.bind(new InetSocketAddress(port));
            channel.register(selector, SelectionKey.OP_READ);

            running = true;
            serverThread = new Thread(this::run);
            serverThread.start();
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }

    private void run() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isReadable()) {
                        buffer.clear();
                        SocketAddress clientAddress = channel.receive(buffer);
                        if (clientAddress != null) {
                            buffer.flip();
                            String request = StandardCharsets.UTF_8.decode(buffer).toString();
                            String response = processRequest(request);

                            ByteBuffer responseBuffer = StandardCharsets.UTF_8.encode(response);
                            channel.send(responseBuffer, clientAddress);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        running = false;
        try {
            if (selector != null) {
                selector.wakeup();
            }
            if (serverThread != null) {
                serverThread.join();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        try {
            if (channel != null) {
                channel.close();
            }
            if (selector != null) {
                selector.close();
            }
        } catch (IOException ignored) {
        }
    }

    public static void main(String[] args) {
        parseAndRun(args, new HelloUDPNonblockingServer());
    }
}