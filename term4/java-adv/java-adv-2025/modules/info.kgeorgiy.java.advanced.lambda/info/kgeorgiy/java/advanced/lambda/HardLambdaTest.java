package info.kgeorgiy.java.advanced.lambda;

import info.kgeorgiy.java.advanced.lambda.Trees.Binary;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Full tests for hard version.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HardLambdaTest<L extends HardLambda> extends EasyLambdaTest<L> {
    public HardLambdaTest() {
    }

    // === Spliterators

    @Test
    public void test51_nestedBinaryTreeSpliterator() {
        testSpliterator(
                this.<Binary<List<String>>>nested(binary(Binary.Branch::new)),
                lambda::nestedBinaryTreeSpliterator,
                checker(s -> false)
        );
    }

    @Test
    public void test52_nestedSizedBinaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.SizedBinary<List<String>>>nested(binary(Trees.SizedBinary.Branch::new)),
                lambda::nestedSizedBinaryTreeSpliterator,
                checker(s -> false)
        );
    }

    @Test
    public void test53_nestedNaryTreeSpliterator() {
        testSpliterator(
                this.<Trees.Nary<List<String>>>nested(nary(Trees.Nary.Node::new)),
                lambda::nestedNaryTreeSpliterator,
                checker(s -> false)
        );
    }

    // === Consumers
    @Test
    public void test61_head() {
        testSized("head(%d)", lambda::head, (values, k) -> values.subList(0, k));
    }

    @Test
    public void test62_tail() {
        testSized("tail(%d)", lambda::tail, (values, k) -> values.subList(values.size() - k, values.size()));
    }

    @Test
    public void test63_kth() {
        testSizedCollector("kth(%d)", lambda::kth, LambdaTest::get);
    }
}
