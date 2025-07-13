package info.kgeorgiy.java.advanced.hello;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * Full tests for {@link HelloClient}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HelloClientTest extends BaseTest {
    protected static final int PORT = 28888;
    public static final String PREFIX = HelloClientTest.class.getName();
    public static final int SOCKET_FREE_TIME = 500;

    public HelloClientTest() {
    }

    @Test
    public void test01_singleRequest() throws SocketException {
        test(1, 1, 1);
    }

    @Test
    public void test02_sequence() throws SocketException {
        test(100, 1, 1);
    }

    @Test
    public void test03_singleWithFailures() throws SocketException {
        test(1, 1, 0.1);
    }

    @Test
    public void test04_sequenceWithFailures() throws SocketException {
        test(20, 1, 0.5);
    }

    @Test
    public void test05_singleMultithreaded() throws SocketException {
        test(1, 10, 1);
    }

    @Test
    public void test06_sequenceMultithreaded() throws SocketException {
        test(10, 10, 1);
    }

    @Test
    public void test07_sequenceMultithreadedWithFails() throws SocketException {
        test(10, 10, 0.5);
    }

    @SuppressWarnings("try")
    protected void test(final int requests, final int threads, final double p) throws SocketException {
        final String prefix = prefix();
        final List<Server<Integer>> serverPorts = List.of(new Server<>(templates(prefix, 0, requests), PORT));
        test(threads, p, serverPorts, (client, t) -> client.run("localhost", PORT, prefix, requests, t));
    }

    protected String prefix() {
        return PREFIX + "_" + testName.replaceAll("\\d+", "") + "_";
    }

    protected <C extends HelloClient> void test(
            final int threads,
            final double p,
            final List<Server<Integer>> serverPorts,
            final BiConsumer<C, Integer> clientRunner
    ) throws SocketException {
        try {
            final List<Server<DatagramSocket>> serverSockets = new ArrayList<>();
            final ExecutorService executor = Executors.newFixedThreadPool(serverPorts.size() + 1);
            try {
                for (final Server<Integer> server : serverPorts) {
                    serverSockets.add(server.with(new DatagramSocket(server.value)));
                }
                final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
                for (final Server<DatagramSocket> server : serverSockets) {
                    completionService.submit(() -> {
                        server(server.templates, threads, p, server.value);
                        return null;
                    });
                }
                completionService.submit(() -> {
                    clientRunner.accept(BaseTest.createCUT(), threads);
                    closeSockets(serverSockets);
                    return null;
                });

                for (int i = 0; i <= serverSockets.size(); i++) {
                    try {
                        completionService.take().get();
                    } catch (final ExecutionException e) {
                        final Throwable cause = e.getCause();
                        if (cause instanceof RuntimeException) {
                            throw (RuntimeException) cause;
                        } else if (cause instanceof Error) {
                            throw (Error) cause;
                        } else {
                            throw new AssertionError("Unexpected exception", e.getCause());
                        }
                    }
                }
            } finally {
                executor.shutdownNow();
                closeSockets(serverSockets);
                Thread.sleep(SOCKET_FREE_TIME);
            }
        } catch (final InterruptedException e) {
            throw new AssertionError("Test thread interrupted", e);
        }
    }

    protected void server(
            final List<String> templates, final int threads,
            final double p,
            final DatagramSocket socket
    ) throws IOException {
        Util.server(templates, threads, p, socket);
    }

    private static void closeSockets(final List<Server<DatagramSocket>> serverSockets) {
        for (final Server<DatagramSocket> server : serverSockets) {
            server.value.close();
        }
    }

    protected static List<String> templates(final String prefix, final int requestsFrom, final int requestsTo) {
        return IntStream.range(requestsFrom, requestsTo)
                .map(i -> i + 1)
                .mapToObj(i -> prefix + i + "_$").toList();
    }

    protected record Server<T>(List<String> templates, T value) {
        public <R> Server<R> with(final R value) {
            return new Server<>(templates, value);
        }
    }
}
