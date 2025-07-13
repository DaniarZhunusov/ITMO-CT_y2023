package info.kgeorgiy.ja.zhunusov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloClient;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.function.IntFunction;

public class HelloUDPClient extends AbstractHelloUDPClient {
    @Override
    public void run(String host, int port, String prefix, int requests, int threads) {
        runClient(threads, threadId -> () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT_MS);
                InetAddress address = InetAddress.getByName(host);

                for (int request = 1; request <= requests; request++) {
                    String message = prefix + request + "_" + threadId;
                    sendAndReceive(socket, address, port, message);
                }
            } catch (IOException e) {
                System.err.println("Error in thread " + threadId + ": " + e.getMessage());
            }
        });
    }

    @Override
    public void newRun(List<Request> requests, int threads) {
        runClient(threads, threadId -> () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT_MS);

                for (Request req : requests) {
                    String message = req.template().replace("$", String.valueOf(threadId));
                    sendAndReceive(socket, InetAddress.getByName(req.host()), req.port(), message);
                }
            } catch (IOException e) {
                System.err.println("Error in thread " + threadId + ": " + e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        parseAndRun(args, new HelloUDPClient());
    }
}