package info.kgeorgiy.java.advanced.implementor.advanced;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "NonFinalUtilityClass"})
public class WeirdConstructors {
    public static final Class<?>[] OK = {WeirdConstructors.class, AccessibleConstructorArg.class, SingleConstructorArg.class};
    public static final Class<?>[] FAILED = {InaccessibleConstructorArg.class, DoubleConstructorArg.class};

    public static class InaccessibleConstructorArg {
        public InaccessibleConstructorArg(@SuppressWarnings("ClassEscapesDefinedScope") final Inaccessible ignored) {
        }
    }

    public static class AccessibleConstructorArg {
        public AccessibleConstructorArg(final Accessible ignored) {
        }
    }

    public static class SingleConstructorArg {
        public SingleConstructorArg(@SuppressWarnings("ClassEscapesDefinedScope") final Inaccessible ignored) {
        }

        public SingleConstructorArg(final Accessible ignored) {
        }
    }

    public static class DoubleConstructorArg {
        public DoubleConstructorArg(
                @SuppressWarnings("ClassEscapesDefinedScope") final Inaccessible ignoredI,
                final Accessible ignoredA
        ) {
        }

        public DoubleConstructorArg(
                final Accessible ignoredA,
                @SuppressWarnings("ClassEscapesDefinedScope") final Inaccessible ignoredI
        ) {
        }
    }

    private static class Inaccessible {};
    public static class Accessible {};
}
