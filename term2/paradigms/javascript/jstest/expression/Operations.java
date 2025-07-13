package jstest.expression;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.function.*;
import java.util.stream.DoubleStream;

/**
 * Known expression operations.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Operations {
    Operation ARITH = checker -> {
        checker.alias("negate", "Negate");
        checker.alias("+", "Add");
        checker.alias("-", "Subtract");
        checker.alias("*", "Multiply");
        checker.alias("/", "Divide");
    };

    Operation NARY_ARITH = checker -> {
        checker.unary("negate", "Negate", a -> -a, null);

        checker.any("+", "Add", 0, 2, arith(0, Double::sum));
        checker.any("-", "Subtract", 1, 2, arith(0, (a, b) -> a - b));
        checker.any("*", "Multiply", 0, 2, arith(1, (a, b) -> a * b));
        checker.any("/", "Divide", 1, 2, arith(1, (a, b) -> a / b));
    };

    /// Pie

    Operation PI = constant("pi", Math.PI);
    Operation E = constant("e", Math.E);

    /// Exp, Ln
    Operation EXP = unary("exp", "Exp", Math::exp,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {48, 48, 37}, {27, 22, 22}});

    Operation LN = unary("ln", "Ln", Math::log,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {46, 46, 36}, {26, 22, 22}});

    /// Pow, log
    Operation POW = binary("pow", "Pow", Math::pow,
            new int[][]{{1, 1, 1}, {1, 28, 1}, {11, 1, 1}, {15, 33, 1}, {51, 51, 35}, {53, 38, 33}, {71, 89, 39}, {53, 71, 57}});
    Operation LOG = binary("log", "Log", (a, b) -> Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
            new int[][]{{1, 1, 1}, {1, 22, 1}, {44, 1, 1}, {44, 27, 1}, {38, 38, 74}, {43, 68, 63}, {87, 70, 76}, {116, 99, 45}});

    // Square, Sqrt

    Operation SQUARE = unary("square", "Square", a -> a * a,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {54, 54, 40}, {30, 6, 6}});
    Operation SQRT = unary("sqrt", "Sqrt", Math::sqrt,
            new int[][]{{1, 1, 1}, {32, 1, 1}, {48, 53, 1}, {48, 48, 1}, {116, 116, 131}, {127, 5, 5}});

    // Cube, Cbrt
    Operation CUBE = unary("cube", "Cube", a -> a * a * a,
            new int[][]{{1, 1, 1}, {9, 1, 1}, {17, 22, 1}, {17, 17, 1}, {67, 67, 59}, {51, 6, 6}});
    Operation CBRT = unary("cbrt", "Cbrt", Math::cbrt,
            new int[][]{{1, 1, 1}, {36, 1, 1}, {44, 49, 1}, {44, 44, 1}, {94, 94, 113}, {105, 22, 22}});

    /// Sin, cos
    Operation SIN = unary("sin", "Sin", Math::sin,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {48, 48, 37}, {27, 23, 23}});
    Operation COS = unary("cos", "Cos", Math::cos,
            new int[][]{{1, 1, 1}, {12, 1, 1}, {16, 21, 1}, {16, 16, 1}, {55, 55, 51}, {41, 23, 23}});

    /// Sinh, cosh
    Operation SINH = unary("sinh", "Sinh", Math::sinh,
            new int[][]{{1, 1, 1}, {6, 1, 1}, {10, 15, 1}, {10, 10, 1}, {51, 51, 40}, {30, 21, 21}});
    Operation COSH = unary("cosh", "Cosh", Math::cosh,
            new int[][]{{1, 1, 1}, {6, 1, 1}, {10, 15, 1}, {10, 10, 1}, {51, 51, 40}, {30, 22, 22}});


    /// MinMax
    Operation MIN = min(-2);
    Operation MAX = min(-2);


    static Operation min(final int arity) {
        return fix("min", "Min", arity, DoubleStream::min);
    }

    static Operation max(final int arity) {
        return fix("max", "Max", arity, DoubleStream::max);
    }

    /// AvgMed

    static Operation avg(final int arity) {
        return fix("avg", "Avg", arity, DoubleStream::average);
    }

    static Operation med(final int arity) {
        return fix("med", "Med", arity, args -> {
            final double[] sorted = args.sorted().toArray();
            return OptionalDouble.of(sorted[sorted.length / 2]);
        });
    }

    /// ArcTan
    Operation ATAN = unary("atan", "ArcTan", Math::atan,
                           new int[][]{{1, 1, 1}, {13, 1, 1}, {21, 26, 1}, {21, 21, 1}, {71, 71, 67}, {59, 22, 22}});
    Operation ATAN2 = binary("atan2", "ArcTan2", Math::atan2,
                             new int[][]{{1, 1, 1}, {1, 17, 1}, {16, 1, 1}, {23, 30, 1}, {48, 48, 43}, {50, 46, 41}, {78, 85, 51}, {71, 78, 58}});

    /// Harmonic
    Operation HYPOT = binary("hypot", "Hypot", (a, b) -> a * a + b * b,
                             new int[][]{{1, 1, 1}, {1, 5, 1}, {5, 1, 1}, {5, 5, 1}, {9, 9, 17}, {17, 14, 9}, {21, 21, 17}, {21, 21, 17}});
    Operation HMEAN = binary("hmean", "HMean", (a, b) -> 2 / (1 / a + 1 / b),
                             new int[][]{{1, 1, 1}, {1, 21, 1}, {21, 1, 1}, {21, 21, 1}, {39, 39, 41}, {41, 44, 39}, {67, 67, 49}, {67, 67, 49}});

    /// Mean, Var

    Operation MEAN = any("mean", "Mean", 1, 5, Operations::mean);
    Operation VAR = any("var", "Var", 1, 5, Operations::var);
    Operation VARN = any("varn", "Varn", 1, 5, Operations::var);

    private static double mean(final double[] args) {
        return Arrays.stream(args).sum() / args.length;
    }

    private static double var(final double[] args) {
        final double mean = mean(args);
        return Arrays.stream(args).map(a -> a - mean).map(a -> a * a).sum() / args.length;
    }

    Operation PRODUCT = any("product", "Product", 0, 3, args -> product(Arrays.stream(args)));
    Operation GEOM = any("geom", "Geom", 1, 5, mean((args, n) -> Math.pow(Math.abs(product(args)), 1 / n)));


    /// Means

    Operation ARITH_MEAN = any("arithMean", "ArithMean", 1, 5, mean((args, n) -> args.sum() / n));
    Operation GEOM_MEAN = any("geomMean", "GeomMean", 1, 5, mean((args, n) -> Math.pow(Math.abs(product(args)), 1 / n)));
    Operation HARM_MEAN = any("harmMean", "HarmMean", 1, 5, mean((args, n) -> n / args.map(a -> 1 / a).sum()));

    private static BaseTester.Func mean(final ToDoubleBiFunction<DoubleStream, Double> f) {
        return args -> f.applyAsDouble(Arrays.stream(args), (double) args.length);
    }

    private static double product(final DoubleStream args) {
        return args.reduce(1, (a, b) -> a * b);
    }

    /// Complex

    Operation INFIX_PHI = infix("phic", "PhiC", -250, Math::atan2);
    Operation INFIX_ABS = infix("absc", "AbsC", -250, Math::hypot);

    /// Complex Sin, Cos
    Operation INFIX_SIN = infix("sinc", "SinC", -250, (y, x) -> Math.sin(x) * Math.cosh(y));
    Operation INFIX_COS = infix("cosc", "CosC", -250, (y, x) -> Math.cos(x) * Math.cosh(y));
    Operation POSTFIX_SIN = unary(":sinp", "SinP", x -> 0, null);
    Operation POSTFIX_COS = unary(":cosp", "CosP", Math::cosh, null);

    /// IntBit
    Operation NOT           = unary("~", "BitNot", a -> ~ (long) a, null);
    Operation AND     = infix("&", "BitAnd", 90, intBit((a, b) -> a & b));
    Operation OR      = infix("|", "BitOr", 80, intBit((a, b) -> a | b));
    Operation XOR     = infix("^", "BitXor", 70, intBit((a, b) -> a ^ b));

    private static DoubleBinaryOperator intBit(final LongBinaryOperator op) {
        return (a, b) -> op.applyAsLong((long) a, (long) b);
    }

    /// IntBitMux
    Operation IF      = fixed("?:", "BitIf", 3, args -> ((long) args[0] & 1) == 1 ? args[1] : args[2], null);
    Operation MUX     = fixed("¿:", "BitMux", 3, args -> intMux((long) args[0], (long) args[1], (long) args[2]), null);

    private static long intMux(long s, long a, long b) {
        long result = 0;
        for (int i = 0; i < 64; i++) {
            final long mask = 1L << i;
            result |= ((s & mask) == 0 ? a : b) & mask;
        }
        return result;
    }


    /// Common

    private static Operation constant(final String name, final double value) {
        return checker -> checker.constant(name, value);
    }

    private static Operation unary(final String name, final String alias, final DoubleUnaryOperator op, final int[][] simplifications) {
        return checker -> checker.unary(name, alias, op, simplifications);
    }

    private static Operation binary(final String name, final String alias, final DoubleBinaryOperator op, final int[][] simplifications) {
        return checker -> checker.binary(name, alias, op, simplifications);
    }
    private static Operation fix(final String name, final String alias, final int arity, final Function<DoubleStream, OptionalDouble> f) {
        final BaseTester.Func wf = args -> f.apply(Arrays.stream(args)).orElseThrow();
        final int[][] simplifications = null;
        return arity >= 0
               ? fix(name, alias, arity, wf, simplifications)
               : any(name, alias, -arity - 1, -arity - 1, wf);
    }

    private static Operation fix(
            final String name,
            final String alias,
            final int arity,
            final BaseTester.Func wf,
            final int[][] simplifications
    ) {
        return fixed(name + arity, alias + arity, arity, wf, simplifications);
    }

    private static Operation fixed(final String name, final String alias, final int arity, final BaseTester.Func f, final int[][] simplifications) {
        return checker -> checker.fixed(name, alias, arity, f, simplifications);
    }

    private static Operation any(final String name, final String alias, final int minArity, final int fixedArity, final BaseTester.Func f) {
        return checker -> checker.any(name, alias, minArity, fixedArity, f);
    }

    private static BaseTester.Func arith(final double zero, final DoubleBinaryOperator f) {
        return args -> args.length == 0 ? zero
                : args.length == 1 ? f.applyAsDouble(zero, args[0])
                : Arrays.stream(args).reduce(f).orElseThrow();
    }

    private static Operation infix(final String name, final String alias, final int priority, final DoubleBinaryOperator op) {
        return checker -> checker.infix(name, alias, priority, op);
    }
}
