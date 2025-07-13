package info.kgeorgiy.java.advanced.lambda;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Full tests for easy version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class EasyLambdaTest<L extends EasyLambda> extends LambdaTest<L> {
    public EasyLambdaTest() {
    }

    // === Spliterators

    @Test
    public void test01_binaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.Binary<String>>plain(binary(Trees.Binary.Branch::new)),
                lambda::binaryTreeSpliterator,
                checker(s -> s == 1, Characteristic.IMMUTABLE)
        );
    }

    @Test
    public void test02_sizedBinaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.SizedBinary<String>>plain(binary(Trees.SizedBinary.Branch::new)),
                lambda::sizedBinaryTreeSpliterator,
                checker(s -> s >= 0, Characteristic.SIZED, Characteristic.SUBSIZED, Characteristic.IMMUTABLE)
        );
    }

    @Test
    public void test03_naryTreeSpliterator() {
        testSpliterator(
                this.<Trees.Nary<String>>plain(nary(Trees.Nary.Node::new)),
                lambda::naryTreeSpliterator,
                checker(s -> s == 1, Characteristic.IMMUTABLE)
        );
    }

    <T, M> Stream<Named<Consumer<TreeTest<T, M>>>> checkers(final TestSpliterator<T, M> spliterator) {
        return Stream.concat(super.checkers(spliterator), Stream.of(
                spliterator.checker(".trySplit()", tail -> {
                    if (tail.getExactSizeIfKnown() != 1) {
                        final Spliterator<T> head = tail.trySplit();
                        spliterator.checker().accept(head, -1L);
                        return Stream.of(head, tail).flatMap(LambdaTest::stream);
                    } else {
                        return stream(tail);
                    }
                }),
                spliterator.checker(".tryAdvance() [single]", split -> {
                    final List<T> result = new ArrayList<>();
                    int size = 0;
                    while (split.tryAdvance(result::add)) {
                        Assertions.assertEquals(1, result.size() - size, "accept calls");
                        size++;
                    }
                    return result.stream();
                }),
                spliterator.performance(10_000)
        ));
    }


    // === Collectors

    @Test
    public void test11_first() {
        testCollectorIndex("first()", lambda.first(), size -> 0);
    }

    @Test
    public void test12_last() {
        testCollectorIndex("last()", lambda.last(), size -> size - 1);
    }

    @Test
    public void test13_middle() {
        testCollectorIndex("middle()", lambda.middle(), size -> size / 2);
    }

    @Test
    public void test21_commonPrefix() {
        testCommon("commonPrefix()", lambda.commonPrefix(), (a, b) -> b + a, LambdaTest::commonPrefix);
    }

    @Test
    public void test22_commonSuffix() {
        testCommon("commonSuffix()", lambda.commonSuffix(), String::concat, (a, b) -> reverse(commonPrefix(reverse(a), reverse(b))));
    }

    private static String reverse(final String string) {
        return new StringBuilder(string).reverse().toString();
    }

    @Override
    int indexOrigin() {
        return -1;
    }

    @Override
    int maxStreamSize() {
        return 1 << 17;
    }
}
