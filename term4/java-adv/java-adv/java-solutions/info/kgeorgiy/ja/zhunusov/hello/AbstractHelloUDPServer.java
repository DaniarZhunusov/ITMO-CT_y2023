package info.kgeorgiy.ja.zhunusov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractHelloUDPServer implements HelloServer {
    protected static final int BUFFER_SIZE = 1024;
    protected static final int SHUTDOWN_TIMEOUT_MS = 1000;

    protected static void validateArgs(String[] args) {
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException("Usage: <port> <threads>");
        }
    }

    protected static void parseAndRun(String[] args, HelloServer server) {
        try {
            validateArgs(args);
            int port = Integer.parseInt(args[0]);
            int threads = Integer.parseInt(args[1]);

            server.start(port, threads);
            Thread.currentThread().join();
        } catch (NumberFormatException e) {
            System.err.println("Port and threads must be valid integers.");
        } catch (InterruptedException e) {
            System.err.println("Server interrupted.");
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } finally {
            server.close();
        }
    }

    protected void shutdownExecutor(ExecutorService executor, String name) {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.printf("Shutdown interrupted: %s%n", name);
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    protected String processRequest(String request) {
        return "Hello, " + request;
    }
}
