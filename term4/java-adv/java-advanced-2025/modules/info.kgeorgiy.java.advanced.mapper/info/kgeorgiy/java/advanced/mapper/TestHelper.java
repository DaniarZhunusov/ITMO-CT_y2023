package info.kgeorgiy.java.advanced.mapper;

import info.kgeorgiy.java.advanced.iterative.ScalarIP;

/**
 * Test helper class for {@link ParallelMapper} tests.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum TestHelper {
    ;

    private static ParallelMapper parallelMapper;

    @SafeVarargs
    static <P extends ScalarIP> P instance(final int threads, final P... type) {
        assert type.length == 0;

        close();
        final String ipName = System.getProperty("cut");
        final String parallelMapperName = ipName.replace("IterativeParallelism", "ParallelMapperImpl");
        parallelMapper = (ParallelMapper) create(parallelMapperName, int.class, threads);
        @SuppressWarnings("unchecked")
        final Class<P> t = (Class<P>) type.getClass().componentType();
        return t.cast(create(ipName, ParallelMapper.class, parallelMapper));
    }

    static Object create(final String className, final Class<?> argType, final Object value) {
        try {
            final Class<?> clazz = Class.forName(className);
            return clazz.getConstructor(argType).newInstance(value);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    public static void close() {
        if (parallelMapper != null) {
            parallelMapper.close();
            parallelMapper = null;
        }
    }

    static int subtasks(final int totalThreads) {
        return totalThreads * 2;
    }
}
