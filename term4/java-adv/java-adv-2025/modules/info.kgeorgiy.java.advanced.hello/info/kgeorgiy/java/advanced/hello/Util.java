//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package info.kgeorgiy.java.advanced.hello;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Util {
    private static final Charset CHARSET;
    private static final String DIGITS_STR;
    private static final String NON_ZERO_DIGITS_STR;
    private static final Pattern DIGIT;
    private static final Pattern NON_DIGIT;
    private static final Pattern NON_ZERO_DIGIT;
    private static final Pattern NUMBER;
    private static final List<String> ANSWER;
    private static final List<NumberFormat> FORMATS;
    private static final boolean NUMBERS = true;
    private static final Pattern NON_NUMBER;
    private static Mode mode;
    private static final List<BiFunction<String, Random, String>> EVIL_MODIFICATIONS;

    private Util() {
    }

    public static String getString(DatagramPacket packet) {
        return getString(packet.getData(), packet.getOffset(), packet.getLength());
    }

    public static String getString(byte[] data, int offset, int length) {
        return new String(data, offset, length, CHARSET);
    }

    private static void send(DatagramSocket socket, DatagramPacket packet, String request) throws IOException {
        byte[] bytes = getBytes(request);
        packet.setData(bytes);
        packet.setLength(packet.getData().length);
        socket.send(packet);
    }

    public static byte[] getBytes(String string) {
        return string.getBytes(CHARSET);
    }

    public static DatagramPacket createPacket(DatagramSocket socket) throws SocketException {
        return new DatagramPacket(new byte[socket.getReceiveBufferSize()], socket.getReceiveBufferSize());
    }

    public static String request(String string, DatagramSocket socket, SocketAddress address) throws IOException {
        send(socket, string, address);
        return receive(socket);
    }

    public static String receive(DatagramSocket socket) throws IOException {
        DatagramPacket inPacket = createPacket(socket);
        socket.receive(inPacket);
        return getString(inPacket);
    }

    public static void send(DatagramSocket socket, String request, SocketAddress address) throws IOException {
        DatagramPacket outPacket = new DatagramPacket(new byte[0], 0);
        outPacket.setSocketAddress(address);
        send(socket, outPacket, request);
    }

    public static String response(String request) {
        return response(request, "Hello, $");
    }

    public static String response(String request, String format) {
        return format.replaceAll("\\$", request);
    }

    public static void server(List<String> templates, int threads, double p, DatagramSocket socket) throws IOException {
        int[] expected = new int[threads];
        Random random = new Random(4702347503224875082L + (long)Objects.hash(new Object[]{templates, threads, p}));

        try {
            while(true) {
                DatagramPacket packet = createPacket(socket);
                socket.receive(packet);
                String request = getString(packet);
                String message = "Invalid or unexpected request " + request;
                String[] parts = NON_NUMBER.split(request);
                Assertions.assertTrue(parts.length > 1, message);

                try {
                    int thread = Integer.parseInt(parts[parts.length - 1]) - 1;
                    Assertions.assertTrue(0 <= thread && thread < expected.length, message);
                    int no = expected[thread];
                    Assertions.assertTrue(no < templates.size(), message);
                    Assertions.assertEquals(expected(templates, expected, thread), request, message);
                    String response = mode.apply(request, random);
                    if (no != 0 && !(p >= random.nextDouble())) {
                        if (random.nextBoolean()) {
                            String corrupt = mode.corrupt(response, random);
                            if (!corrupt.equals(response)) {
                                send(socket, packet, corrupt);
                            }
                        }
                    } else {
                        int var10002 = expected[thread]++;
                        send(socket, packet, response);
                    }
                } catch (NumberFormatException var15) {
                    throw new AssertionError(message);
                }
            }
        } catch (IOException e) {
            if (socket.isClosed()) {
                IntStream.range(0, threads).forEach((i) -> Assertions.assertEquals(templates.size(), expected[i], () -> "Invalid number of requests on thread %d, last: %s".formatted(i, expected(templates, expected, i))));
            } else {
                throw e;
            }
        }
    }

    private static String expected(List<String> templates, int[] no, int thread) {
        return ((String)templates.get(no[thread])).replaceAll("\\$", String.valueOf(thread + 1));
    }

    private static <T> T select(List<T> items, Random random) {
        return (T)items.get(random.nextInt(items.size()));
    }

    static void setMode(String test) {
        mode = test.endsWith("-i18n") ? Mode.I18N : (test.endsWith("-evil") ? Mode.EVIL : Mode.NORMAL);
    }

    static {
        CHARSET = StandardCharsets.UTF_8;
        DIGITS_STR = (String)IntStream.rangeClosed(0, 65535).filter(Character::isDigit).mapToObj(Character::toString).collect(Collectors.joining());
        NON_ZERO_DIGITS_STR = (String)IntStream.rangeClosed(0, 65535).filter(Character::isDigit).filter((c) -> Character.getNumericValue(c) != 0).mapToObj(Character::toString).collect(Collectors.joining());
        DIGIT = Pattern.compile("([" + DIGITS_STR + "])");
        NON_DIGIT = Pattern.compile("([^" + DIGITS_STR + "])");
        NON_ZERO_DIGIT = Pattern.compile("([" + NON_ZERO_DIGITS_STR + "])");
        NUMBER = Pattern.compile("([" + DIGITS_STR + "]+)");
        ANSWER = List.of("Hello, %s", "%s ආයුබෝවන්", "Բարեւ, %s", "مرحبا %s", "Салом %s", "Здраво %s", "Здравейте %s", "Прывітанне %s", "Привіт %s", "Привет, %s", "Поздрав %s", "سلام به %s", "שלום %s", "Γεια σας %s", "העלא %s", "ہیل%s٪ ے", "Bonjou %s", "Bonjour %s", "Bună ziua %s", "Ciao %s", "Dia duit %s", "Dobrý deň %s", "Dobrý den, %s", "Habari %s", "Halló %s", "Hallo %s", "Halo %s", "Hei %s", "Hej %s", "Hello  %s", "Hello %s", "Hello %s", "Helo %s", "Hola %s", "Kaixo %s", "Kamusta %s", "Merhaba %s", "Olá %s", "Ola %s", "Përshëndetje %s", "Pozdrav %s", "Pozdravljeni %s", "Salom %s", "Sawubona %s", "Sveiki %s", "Tere %s", "Witaj %s", "Xin chào %s", "ສະບາຍດີ %s", "สวัสดี %s", "ഹലോ %s", "ಹಲೋ %s", "హలో %s", "हॅलो %s", "नमस्कार%sको", "হ্যালো %s", "ਹੈਲੋ %s", "હેલો %s", "வணக்கம் %s", "ကို %s မင်္ဂလာပါ", "გამარჯობა %s", "ជំរាបសួរ %s បាន", "こんにちは%s", "你好%s", "안녕하세요  %s");
        FORMATS = List.copyOf(((Map)Arrays.stream(Locale.getAvailableLocales()).map(NumberFormat::getNumberInstance).peek((numberFormat) -> numberFormat.setGroupingUsed(false)).collect(Collectors.toMap((format) -> format.format(123L), Function.identity(), (a, b) -> a))).values());
        NON_NUMBER = Pattern.compile("[^\\p{IsDigit}]+");
        EVIL_MODIFICATIONS = List.of((BiFunction)(s, r) -> s, (BiFunction)(s, r) -> s, (BiFunction)(s, r) -> s, (BiFunction)(s, r) -> s, (BiFunction)(s, r) -> s, (BiFunction)(s, r) -> s, Mode.replaceAll(NON_DIGIT, (s) -> "_"), Mode.replaceAll(NON_DIGIT, (s) -> "-"), Mode.replaceAll(NON_DIGIT, (s) -> "$1$1"), Mode::i18n);
    }

    static enum Mode {
        NORMAL((request, random) -> Util.response(request), List.of((BiFunction)(s, r) -> "", (BiFunction)(s, r) -> "~", replaceAll(Pattern.compile("[_\\-]"), (c) -> "1"), replaceAll(Util.NUMBER, (n) -> "0"), replaceOne(Util.NUMBER, (n) -> "0"), replaceOne(Util.NON_ZERO_DIGIT, (d) -> "0"), replaceAll(Util.DIGIT, (d) -> d + d), replaceOne(Util.DIGIT, (d) -> "-"), replaceOne(Util.DIGIT, (d) -> ""))),
        I18N(Mode::i18n, NORMAL.corruptions),
        EVIL((request, random) -> I18N.apply((String)((BiFunction)Util.select(Util.EVIL_MODIFICATIONS, random)).apply(request, random), random), Stream.concat(NORMAL.corruptions.stream(), Stream.of(replaceOne(Util.NUMBER, (n) -> n + n), replaceOne(Util.NUMBER, (n) -> n + n.charAt(0)), replaceOne(Util.NUMBER, (n) -> {
            char var10000 = n.charAt(0);
            return var10000 + n;
        }))).toList());

        private final BiFunction<String, Random, String> f;
        private final List<BiFunction<String, Random, String>> corruptions;

        private static String i18n(String request, Random random) {
            if (random.nextBoolean()) {
                NumberFormat format = (NumberFormat)Util.select(Util.FORMATS, random);
                request = Util.response(Util.NUMBER.matcher(request).replaceAll((match) -> format.format((long)Integer.parseInt(match.group()))));
            }

            return ((String)Util.select(Util.ANSWER, random)).formatted(request);
        }

        private static BiFunction<String, Random, String> replaceOne(Pattern pattern, Function<String, String> f) {
            return (s, r) -> {
                MatchResult result = (MatchResult)Util.select(pattern.matcher(s).results().toList(), r);
                String var10000 = s.substring(0, result.start());
                return var10000 + (String)f.apply(result.group()) + s.substring(result.end());
            };
        }

        private static BiFunction<String, Random, String> replaceAll(Pattern pattern, Function<String, String> f) {
            return (s, r) -> pattern.matcher(s).replaceAll(f.compose(MatchResult::group));
        }

        private Mode(BiFunction<String, Random, String> f, List<BiFunction<String, Random, String>> corruptions) {
            this.f = f;
            this.corruptions = corruptions;
        }

        public String apply(String request, Random random) {
            return (String)this.f.apply(request, random);
        }

        public String corrupt(String request, Random random) {
            return (String)((BiFunction)Util.select(this.corruptions, random)).apply(request, random);
        }
    }
}
