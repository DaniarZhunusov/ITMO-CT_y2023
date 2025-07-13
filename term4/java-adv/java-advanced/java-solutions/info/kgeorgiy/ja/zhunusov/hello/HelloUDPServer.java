package info.kgeorgiy.ja.zhunusov.hello;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer extends AbstractHelloUDPServer {
    private DatagramSocket socket;
    private ExecutorService workers;
    private ExecutorService listener;
    private final Object socketLock = new Object();

    @Override
    public void start(int port, int threads) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println("Failed to open socket: " + e.getMessage());
            return;
        }

        workers = Executors.newFixedThreadPool(threads);
        listener = Executors.newSingleThreadExecutor();

        listener.submit(() -> {
            while (!socket.isClosed()) {
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    workers.submit(() -> handle(packet));
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Receive error: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void handle(DatagramPacket request) {
        String message = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8);
        String response = processRequest(message);

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        DatagramPacket responsePacket = new DatagramPacket(
                responseBytes,
                responseBytes.length,
                request.getSocketAddress()
        );

        try {
            synchronized (socketLock) {
                if (!socket.isClosed()) {
                    socket.send(responsePacket);
                }
            }
        } catch (IOException e) {
            System.err.println("Send error: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        shutdownExecutor(listener, "listener");
        shutdownExecutor(workers, "workers");
    }

    public static void main(String[] args) {
        parseAndRun(args, new HelloUDPServer());
    }
}