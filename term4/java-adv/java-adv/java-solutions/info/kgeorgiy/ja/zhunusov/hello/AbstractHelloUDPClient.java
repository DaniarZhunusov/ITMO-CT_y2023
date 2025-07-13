package info.kgeorgiy.ja.zhunusov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntFunction;

public abstract class AbstractHelloUDPClient implements NewHelloClient {
    protected static final int MAX_RETRIES = 100;
    protected static final int TIMEOUT_MS = 300;

    protected static class RequestContext {
        final InetSocketAddress address;
        final String message;
        final String expected;
        int attempts;
        boolean waiting;

        RequestContext(InetSocketAddress address, String message, String expected) {
            this.address = address;
            this.message = message;
            this.expected = expected;
            this.attempts = 0;
            this.waiting = false;
        }
    }

    protected void runClient(int threads, IntFunction<Runnable> taskSupplier) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Phaser phaser = new Phaser(1);

        for (int thread = 1; thread <= threads; thread++) {
            int threadId = thread;
            phaser.register();
            executor.submit(() -> {
                try {
                    taskSupplier.apply(threadId).run();
                } finally {
                    phaser.arriveAndDeregister();
                }
            });
        }

        phaser.arriveAndAwaitAdvance();
        executor.shutdown();
    }

    protected void sendAndReceive(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] requestBytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, address, port);

        String expected = "Hello, " + message;
        String response = "";
        int attempts = 0;

        while (!expected.equals(response)) {
            if (attempts++ >= MAX_RETRIES) {
                throw new IOException("Max retries exceeded for message: " + message);
            }

            try {
                socket.send(requestPacket);
                byte[] buffer = new byte[socket.getReceiveBufferSize()];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                response = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
            } catch (SocketTimeoutException ignored) {
                // retry
            }
        }

        System.out.printf("Request: %s%n", message);
        System.out.printf("Response: %s%n", response);
    }

    protected static void validateArgs(String[] args) {
        if (args == null || args.length < 5) {
            throw new IllegalArgumentException("Expected arguments: <host> <port> <prefix> <threads> <requests>");
        }
    }

    protected static void parseAndRun(String[] args, NewHelloClient client) {
        try {
            validateArgs(args);
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String prefix = args[2];
            int threads = Integer.parseInt(args[3]);
            int requests = Integer.parseInt(args[4]);

            client.run(host, port, prefix, requests, threads);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in arguments");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}