package info.kgeorgiy.java.advanced.implementor.full.interfaces;

/**
 * Nested interfaces.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum NestedInterfaces {
    ;

    public static final Class<?>[] OK = {
            PublicInterface.class,
            PackagePrivateInterface.class,
            InheritedInterface.class,
            ProtectedInterface.class
    };
    public static final Class<?>[] FAILED = {
            PrivateInterface.class
    };

    public interface PublicInterface {
        String hello();
    }

    protected interface ProtectedInterface {
        String hello();
    }

    interface PackagePrivateInterface {
        String hello();
    }

    private interface PrivateInterface {
        String hello();
    }

    interface InheritedInterface extends PrivateInterface {
    }
}
