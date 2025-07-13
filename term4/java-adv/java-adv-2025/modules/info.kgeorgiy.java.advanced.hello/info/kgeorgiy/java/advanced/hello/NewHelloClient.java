package info.kgeorgiy.java.advanced.hello;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Multi-server interface for {@link HelloServer}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface NewHelloClient extends HelloClient {
    /**
     * Runs Hello client.
     * This method should return when all requests are completed.
     *
     * @param requests requests to perform in each thread.
     * @param threads  number of request threads.
     */
    void newRun(List<Request> requests, int threads);

    @Override
    default void run(final String host, final int port, final String prefix, final int requests, final int threads) {
        final List<Request> reqs = IntStream.rangeClosed(1, requests)
                .mapToObj(i -> new Request(host, port, prefix + i + "_$"))
                .toList();
        newRun(reqs, threads);
    }

    /**
     * Client request.
     * Request template is specified as string, where each {@code $} symbol should
     * be replaced by thread no.
     *
     * @param host     server host.
     * @param port     server port.
     * @param template request template.
     *
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    record Request(String host, int port, String template) {
    }
}
