package info.kgeorgiy.java.advanced.implementor.full.interfaces;

/**
 * Method with clashing local type names.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("unused")
public interface SameTypesInterface {
    java.lang.reflect.Proxy getReflectionProxy();

    java.net.Proxy getNetworkProxy();

    java.lang.reflect.Proxy identity(java.lang.reflect.Proxy proxy);

    java.net.Proxy identity(java.net.Proxy proxy);
}
