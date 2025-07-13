package info.kgeorgiy.java.advanced.implementor;

import org.junit.jupiter.api.Assertions;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public enum Compiler {
    ;

    public static void compile(
            final List<Path> files,
            final List<Class<?>> dependencies,
            final Charset charset
    ) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Assertions.assertNotNull(compiler, "Could not find java compiler, include tools.jar to classpath");
        final String classpath = getClassPath(dependencies).stream()
                .map(Path::toString)
                .collect(Collectors.joining(File.pathSeparator));
        final String[] args = Stream.concat(
                Stream.of("-cp", classpath, "-encoding", charset.name()),
                files.stream().map(Path::toString)
        ).toArray(String[]::new);
        final int exitCode = compiler.run(null, null, null, args);
        Assertions.assertEquals(0, exitCode, "Compiler exit code");
    }

    private static List<Path> getClassPath(final List<Class<?>> dependencies) {
        return dependencies.stream()
                .map(dependency -> {
                    try {
                        return Path.of(dependency.getProtectionDomain().getCodeSource().getLocation().toURI());
                    } catch (final URISyntaxException e) {
                        throw new AssertionError(e);
                    }
                })
                .toList();
    }
}
