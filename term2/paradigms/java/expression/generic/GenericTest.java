package expression.generic;

import base.Selector;
import expression.common.Op;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class GenericTest {
    private static final Consumer<GenericTester> ADD = binary("+", 200);
    private static final Consumer<GenericTester> SUBTRACT = binary("-", -200);
    private static final Consumer<GenericTester> MULTIPLY = binary("*", 301);
    private static final Consumer<GenericTester> DIVIDE = binary("/", -300);
    private static final Consumer<GenericTester> NEGATE = unary("-");

    private static final Consumer<GenericTester> COUNT = unary("count");
    private static final Consumer<GenericTester> MIN = binary("min", 50);
    private static final Consumer<GenericTester> MAX = binary("max", 50);


    // === Checked integers

    private static Integer i(final long v) {
        if (v != (int) v) {
            throw new ArithmeticException("Overflow");
        }
        return (int) v;
    }

    private static final Mode<Integer> INTEGER_CHECKED = mode("i", c -> c)
            .binary("+", (a, b) -> i(a + (long) b))
            .binary("-", (a, b) -> i(a - (long) b))
            .binary("*", (a, b) -> i(a * (long) b))
            .binary("/", (a, b) -> i(a / (long) b))
            .unary("-", a -> i(- (long) a))

            .unary("count", Integer::bitCount)
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;

    // === Doubles

    @SuppressWarnings("Convert2MethodRef")
    private static final Mode<Double> DOUBLE = mode("d", c -> (double) c)
            .binary("+", (a, b) -> a + b)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("count", a -> (double) Long.bitCount(Double.doubleToLongBits(a)))
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;

    // === BigIntegers

    private static final Mode<BigInteger> BIG_INTEGER = mode("bi", BigInteger::valueOf)
            .binary("+", BigInteger::add)
            .binary("-", BigInteger::subtract)
            .binary("*", BigInteger::multiply)
            .binary("/", BigInteger::divide)
            .unary("-", BigInteger::negate)

            .unary("count", a -> BigInteger.valueOf(a.bitCount()))
            .binary("min", BigInteger::min)
            .binary("max", BigInteger::max)
            ;

    // === Unchecked integers

    @SuppressWarnings("Convert2MethodRef")
    private static final Mode<Integer> INTEGER_UNCHECKED = mode("u", c -> c)
            .binary("+", (a, b) -> a + b)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("count", Integer::bitCount)
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;

    // === Bytes

    private static byte b(final int x) {
        return (byte) x;
    }
    private static final Mode<Byte> BYTE = mode("b", c -> (byte) c, c -> (byte) c)
            .binary("+", (a, b) -> b(a + b))
            .binary("-", (a, b) -> b(a - b))
            .binary("*", (a, b) -> b(a * b))
            .binary("/", (a, b) -> b(a / b))
            .unary("-", a -> b(-a))

            .unary("count", a -> b(Integer.bitCount(a & 0xff)))
            .binary("min", (a, b) -> b(Math.min(a, b)))
            .binary("max", (a, b) -> b(Math.max(a, b)))
            ;

    // === Booleans

    private static boolean bool(final int x) {
        return x != 0;
    }
    private static final Mode<Boolean> BOOLEAN = mode("bool", GenericTest::bool)
            .binary("+", (a, b) -> a | b)
            .binary("-", (a, b) -> a ^ b)
            .binary("*", (a, b) -> a & b)
            .binary("/", (a, b) -> (a ? 1 : 0) / (b ? 1 : 0) != 0)
            .unary("-", a -> a)

            .unary("count", a -> a)
            .binary("min", (a, b) -> a & b)
            .binary("max", (a, b) -> a | b)
            ;

    // === Saturated integers

    private static final Mode<Integer> INTEGER_SATURATED = mode("sat", GenericTest::saturation)
            .binary("+", (a, b) -> saturation(a + (long) b))
            .binary("-", (a, b) -> saturation(a - (long) b))
            .binary("*", (a, b) -> saturation(a * (long) b))
            .binary("/", (a, b) -> {
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return saturation(a / (long) b);
            })
            .unary("-", a -> saturation(- (long) a))

            .unary("abs", a -> saturation(Math.abs((long) a)))
            .unary("square", a -> saturation(a * (long) a))
            .binary("mod", (a, b) -> saturation(a % (long) b))

            .unary("count", Integer::bitCount)
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;


    private static int saturation(final long a) {
        return (int) Math.max(Math.min(a, Integer.MAX_VALUE), Integer.MIN_VALUE);
    }

    private GenericTest() {
    }

    /* package-private */ static Consumer<GenericTester> unary(final String name) {
        return tester -> tester.unary(name);
    }

    /* package-private */ static Consumer<GenericTester> binary(final String name, final int priority) {
        return tester -> tester.binary(name, priority);
    }

    public static final Selector SELECTOR = Selector.composite(GenericTest.class, GenericTester::new, "easy", "hard")
            .variant("Base", INTEGER_CHECKED, DOUBLE, BIG_INTEGER, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("Ub", INTEGER_UNCHECKED, BYTE)
            .variant("Ubb", INTEGER_UNCHECKED, BYTE, BOOLEAN)
            .variant("CmmUbb", COUNT, MIN, MAX, INTEGER_UNCHECKED, BYTE, BOOLEAN)
            .variant("CmmUbs", COUNT, MIN, MAX, INTEGER_UNCHECKED, BYTE, INTEGER_SATURATED)
            .selector();

    private static <T> Mode<T> mode(final String mode, final IntFunction<T> constant) {
        return new Mode<>(mode, constant, IntUnaryOperator.identity());
    }

    private static <T> Mode<T> mode(final String mode, final IntFunction<T> constant, final IntUnaryOperator fixer) {
        return new Mode<>(mode, constant, fixer);
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }


    /* package-private */ static class Mode<T> implements Consumer<GenericTester> {
        private final String mode;
        private final IntFunction<T> constant;
        private final List<Op<UnaryOperator<GenericTester.F<T>>>> unary = new ArrayList<>();
        private final List<Op<BinaryOperator<GenericTester.F<T>>>> binary = new ArrayList<>();
        private final IntUnaryOperator fixer;

        public Mode(final String mode, final IntFunction<T> constant, final IntUnaryOperator fixer) {
            this.mode = mode;
            this.constant = constant;
            this.fixer = fixer;
        }

        public Mode<T> unary(final String name, final UnaryOperator<T> op) {
            unary.add(Op.of(name, arg -> (x, y, z) -> op.apply(arg.apply(x, y, z))));
            return this;
        }

        public Mode<T> binary(final String name, final BinaryOperator<T> op) {
            binary.add(Op.of(name, (a, b) -> (x, y, z) -> op.apply(a.apply(x, y, z), b.apply(x, y, z))));
            return this;
        }

        @Override
        public void accept(final GenericTester tester) {
            tester.mode(mode, constant, unary, binary, fixer);
        }
    }
}
