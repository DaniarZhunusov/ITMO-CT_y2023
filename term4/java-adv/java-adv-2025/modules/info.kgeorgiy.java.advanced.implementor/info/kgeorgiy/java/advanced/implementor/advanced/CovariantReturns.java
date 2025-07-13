package info.kgeorgiy.java.advanced.implementor.advanced;

import java.util.Set;

/**
 * Inheritance hierarchy with generic covariant return types.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum CovariantReturns {
    ;

    public static final Class<?>[] OK = {
            CovariantReturns.Parent.class,
            CovariantReturns.StringChild.class,
            CovariantReturns.CharSequenceChild.class,
            CovariantReturns.TypedChild.class
    };

    public abstract static class Parent<T> {
        public abstract T getValue();
    }

    public interface StringParent {
        String getValue();
    }

    public abstract static class StringChild extends Parent<String> implements StringParent {
        @Override
        public abstract String getValue();
    }

    public abstract static class CharSequenceChild extends Parent<CharSequence> {
        @Override
        public abstract String getValue();
    }

    public abstract static class TypedChild<T extends Set<T>> extends Parent<Set<T>> {
        @Override
        public abstract T getValue();
    }
}
