package info.kgeorgiy.java.advanced.implementor.advanced;

/**
 * Sealed classes test.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public sealed class SealedClass {
    public static final Class<?>[] OK = {
            NonSealedChild.class,
    };

    public static final Class<?>[] FAILED = {
            SealedClass.class, FinalChild.class, SealedChild.class, SealedGrandchild.class
    };

    public static final class FinalChild extends SealedClass {}

    public static sealed class SealedChild extends SealedClass {}

    public static final class SealedGrandchild extends SealedChild {}

    public static non-sealed class NonSealedChild extends SealedClass {}
}
