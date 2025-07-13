package info.kgeorgiy.ja.zhunusov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HelloUDPNonblockingClient extends AbstractHelloUDPClient {
    private static final int BUFFER_SIZE = 1024;

    @Override
    public void run(String host, int port, String prefix, int requests, int threads) {
        List<Request> requestsList = new ArrayList<>();
        for (int i = 1; i <= requests; i++) {
            requestsList.add(new Request(host, port, prefix + i + "_$"));
        }
        newRun(requestsList, threads);
    }

    @Override
    public void newRun(List<Request> requests, int threads) {
        try (Selector selector = Selector.open()) {
            Map<DatagramChannel, Queue<RequestContext>> tasks = new HashMap<>();

            for (int id = 1; id <= threads; id++) {
                DatagramChannel channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_WRITE);

                Queue<RequestContext> queue = new ArrayDeque<>();
                for (Request r : requests) {
                    String msg = r.template().replace("$", String.valueOf(id));
                    queue.add(new RequestContext(new InetSocketAddress(r.host(), r.port()), msg, "Hello, " + msg));
                }
                tasks.put(channel, queue);
            }

            handle(selector, tasks);
        } catch (IOException e) {
            System.err.println("Selector error: " + e.getMessage());
        }
    }

    private void handle(Selector selector, Map<DatagramChannel, Queue<RequestContext>> tasks) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (!tasks.isEmpty()) {
            try {
                if (selector.select(TIMEOUT_MS) == 0) {
                    retry(selector, tasks);
                    continue;
                }

                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid()) continue;

                    DatagramChannel channel = (DatagramChannel) key.channel();
                    Queue<RequestContext> queue = tasks.get(channel);
                    if (queue == null || queue.isEmpty()) continue;

                    RequestContext ctx = queue.peek();

                    try {
                        if (key.isWritable() && !ctx.waiting) {
                            channel.send(ByteBuffer.wrap(ctx.message.getBytes(StandardCharsets.UTF_8)), ctx.address);
                            ctx.waiting = true;
                            ctx.attempts++;
                            key.interestOps(SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            buffer.clear();
                            SocketAddress respAddr = channel.receive(buffer);
                            if (respAddr != null && respAddr.equals(ctx.address)) {
                                String response = StandardCharsets.UTF_8.decode((ByteBuffer) buffer.flip()).toString();
                                if (response.equals(ctx.expected)) {
                                    System.out.println(response);
                                    queue.poll();
                                    if (queue.isEmpty()) {
                                        tasks.remove(channel);
                                        close(channel);
                                    } else {
                                        key.interestOps(SelectionKey.OP_WRITE);
                                    }
                                    continue;
                                }
                            }

                            if (ctx.attempts >= MAX_RETRIES) {
                                System.err.println("Max retries exceeded for: " + ctx.message);
                                tasks.remove(channel);
                                close(channel);
                            } else {
                                ctx.waiting = false;
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Request error: " + e.getMessage());
                        tasks.remove(channel);
                        close(channel);
                    }
                }
            } catch (IOException e) {
                System.err.println("Selector failure: " + e.getMessage());
                break;
            }
        }
    }

    private static void retry(Selector selector, Map<DatagramChannel, Queue<RequestContext>> tasks) {
        for (Map.Entry<DatagramChannel, Queue<RequestContext>> entry : tasks.entrySet()) {
            DatagramChannel channel = entry.getKey();
            Queue<RequestContext> queue = entry.getValue();

            if (!queue.isEmpty()) {
                RequestContext ctx = queue.peek();
                if (ctx.waiting && ctx.attempts < MAX_RETRIES) {
                    ctx.waiting = false;
                    SelectionKey key = channel.keyFor(selector);
                    if (key != null && key.isValid()) {
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }

    private static void close(DatagramChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            System.err.println("Failed to close channel: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        parseAndRun(args, new HelloUDPNonblockingClient());
    }
}