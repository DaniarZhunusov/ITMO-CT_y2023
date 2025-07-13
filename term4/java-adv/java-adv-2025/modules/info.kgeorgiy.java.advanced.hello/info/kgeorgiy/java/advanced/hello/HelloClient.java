package info.kgeorgiy.java.advanced.hello;

/**
 * Client interface for {@link HelloServer}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface HelloClient {
    /**
     * Runs Hello client.
     * This method should return when all requests are completed.
     *
     * @param host     server host.
     * @param port     server port.
     * @param prefix   request prefix.
     * @param requests number of requests per thread.
     * @param threads  number of request threads.
     */
    void run(String host, int port, String prefix, int requests, int threads);
}
