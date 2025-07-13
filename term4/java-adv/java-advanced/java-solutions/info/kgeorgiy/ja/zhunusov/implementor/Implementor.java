package info.kgeorgiy.ja.zhunusov.implementor;

import info.kgeorgiy.java.advanced.implementor.*;
import info.kgeorgiy.java.advanced.implementor.tools.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.*;

/**
 * Implementation of {@link Impler} and {@link JarImpler} interfaces that generates
 * class implementations for interfaces and creates JAR files containing these implementations.
 */
public class Implementor implements Impler, JarImpler {

    /**
     * Generates implementation of the specified interface and writes it to the specified directory.
     *
     * @param token the type token of the interface to implement
     * @param root the root directory where the implementation file will be created
     * @throws ImplerException if the specified token is not an interface or the interface is private
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        validateInput(token, root);

        String className = token.getSimpleName() + "Impl";
        Path outputPath = getOutputPath(token, root, className);

        createDirectories(outputPath.getParent());
        writeImplementation(outputPath, generateImplementation(token));
    }

    /**
     * Validates input parameters for the implement method.
     *
     * @param token the type token to validate
     * @param root the root directory to validate
     * @throws ImplerException if the token is not an interface or is private
     */
    private void validateInput(Class<?> token, Path root) throws ImplerException {
        if (!token.isInterface()) {
            throw new ImplerException("Only interface implementation is supported");
        }
        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Private interfaces cannot be implemented");
        }
    }

    /**
     * Constructs the output path for the implementation file.
     *
     * @param token the type token of the interface
     * @param root the root directory
     * @param className the name of the implementation class
     * @return the complete path for the implementation file
     */
    private Path getOutputPath(Class<?> token, Path root, String className) {
        return root.resolve(token.getPackageName().replace(".", "/"))
                .resolve(className + ".java");
    }

    /**
     * Writes the generated implementation to a file using UTF-8 encoding.
     *
     * @param outputPath path to output file
     * @param code the implementation code to write
     * @throws ImplerException if an IO error occurs
     */
    private void writeImplementation(Path outputPath, String code) throws ImplerException {
        try {
            Files.writeString(outputPath, code, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ImplerException("Failed to write implementation file: " + outputPath, e);
        }
    }

    /**
     * Creates all necessary directories for the implementation file.
     *
     * @param path the directory path to create
     * @throws ImplerException if directory creation fails
     */
    private void createDirectories(Path path) throws ImplerException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ImplerException("Failed to create directories: " + path, e);
        }
    }

    /**
     * Generates the implementation source code for the specified interface.
     *
     * @param token the type token of the interface to implement
     * @return the generated implementation source code
     */
    private String generateImplementation(Class<?> token) {
        StringBuilder classCode = new StringBuilder();

        appendPackage(token, classCode);
        appendClassHeader(token, classCode);

        if (token.getMethods().length > 0) {
            appendMethods(token, classCode);
        }

        appendClassFooter(classCode);
        return classCode.toString();
    }

    /**
     * Appends method implementations to the class code.
     *
     * @param token the interface type token
     * @param classCode the StringBuilder containing the class code
     */
    private void appendMethods(Class<?> token, StringBuilder classCode) {
        for (Method method : token.getMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                classCode.append(generateMethod(method));
            }
        }
    }

    /**
     * Appends the package declaration to the class code.
     *
     * @param token the interface type token
     * @param classCode the StringBuilder containing the class code
     */
    private void appendPackage(Class<?> token, StringBuilder classCode) {
        String packageName = token.getPackageName();
        if (!packageName.isEmpty()) {
            classCode.append("package ").append(packageName).append(";\n\n");
        }
    }

    /**
     * Appends the class header to the class code.
     * Non-ASCII characters in class names are automatically converted to Unicode escape sequences.
     *
     * @param token the interface type token
     * @param classCode the StringBuilder containing the class code
     */
    private void appendClassHeader(Class<?> token, StringBuilder classCode) {
        classCode.append("public class ")
                .append(toUnicodeEscapes(token.getSimpleName())).append("Impl implements ")
                .append(toUnicodeEscapes(token.getCanonicalName()))
                .append(" {\n\n");
    }

    /**
     * Appends the class footer to the class code.
     *
     * @param classCode the StringBuilder containing the class code
     */
    private void appendClassFooter(StringBuilder classCode) {
        classCode.append("}\n");
    }

    /**
     * Generates a method implementation.
     * Non-ASCII characters in method names and return types are automatically converted to Unicode escape sequences.
     *
     * @param method the method to implement
     * @return the string containing method implementation
     */
    private String generateMethod(Method method) {
        StringBuilder methodCode = new StringBuilder();

        methodCode.append("    @Override\n    public ")
                .append(toUnicodeEscapes(method.getReturnType().getCanonicalName())).append(" ")
                .append(toUnicodeEscapes(method.getName())).append("(");

        appendParameters(method, methodCode);
        appendMethodBody(method, methodCode);

        return methodCode.toString();
    }

    /**
     * Returns the default value for a given type.
     *
     * @param token the type to get default value for
     * @return the string representation of default value
     */
    private String getDefaultValue(Class<?> token) {
        if (!token.isPrimitive()) return "null";
        if (token == boolean.class) return "false";
        if (token == void.class) return "";
        return "0";
    }

    /**
     * Appends method parameters to the method code.
     * Non-ASCII characters in parameter types are automatically converted to Unicode escape sequences.
     *
     * @param method the method being implemented
     * @param methodCode the StringBuilder containing method code
     */
    private void appendParameters(Method method, StringBuilder methodCode) {
        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) methodCode.append(", ");
            methodCode.append(toUnicodeEscapes(params[i].getCanonicalName())).append(" arg").append(i);
        }
    }

    /**
     * Appends method body to the method code.
     *
     * @param method the method being implemented
     * @param methodCode the StringBuilder containing method code
     */
    private void appendMethodBody(Method method, StringBuilder methodCode) {
        methodCode.append(") {\n        return ")
                .append(getDefaultValue(method.getReturnType()))
                .append(";\n    }\n\n");
    }

    /**
     * Generates a JAR file containing the implementation of the specified interface.
     *
     * @param token the type token of the interface to implement
     * @param jarFile the target JAR file path
     * @throws ImplerException if implementation or JAR creation fails
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory(Paths.get("."), "implementor-tmp");
        } catch (IOException e) {
            throw new ImplerException("Cannot create temp directory", e);
        }

        try {
            implement(token, tempDir);
            compile(token, tempDir);
            createJar(tempDir, jarFile);
        } catch (IOException e) {
            throw new ImplerException("Failed to generate JAR: " + e.getMessage());
        } finally {
            try {
                deleteDirectory(tempDir);
            } catch (IOException e) {
                System.err.println("Failed to delete temp directory: " + e.getMessage());
            }
        }
    }

    /**
     * Compiles the generated implementation.
     *
     * @param root the root directory containing source files
     * @param token the interface type token
     * @throws ImplerException if compilation fails
     */
    private void compile(Class<?> token, Path root) throws ImplerException {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new ImplerException("Compiler not found");
            }

            String classpath = root + File.pathSeparator +
                    Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI());

            String[] args = {
                    "-cp", classpath,
                    "-encoding", "UTF-8",
                    root.resolve(token.getPackageName().replace('.', '/'))
                            .resolve(token.getSimpleName() + "Impl.java").toString()
            };

            if (compiler.run(null, null, null, args) != 0) {
                throw new ImplerException("Compilation failed");
            }
        } catch (URISyntaxException e) {
            throw new ImplerException("Invalid URI", e);
        }
    }

    /**
     * Gets the classpath for the specified token.
     *
     * @param token the type token
     * @return the classpath string
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (URISyntaxException e) {
            throw new AssertionError("Failed to get classpath for token: " + token.getCanonicalName(), e);
        }
    }

    /**
     * Creates a JAR file from the compiled implementation.
     *
     * @param root the root directory containing compiled files
     * @param jarFile the target JAR file path
     * @throws IOException if JAR creation fails
     */
    private void createJar(Path root, Path jarFile) throws IOException {
        try (var jarOutputStream = new JarOutputStream(Files.newOutputStream(jarFile))) {
            Files.walk(root).filter(path -> !Files.isDirectory(path)).forEach(path -> {
                try {
                    String entryName = root.relativize(path).toString().replace("\\", "/");
                    JarEntry jarEntry = new JarEntry(entryName);
                    jarOutputStream.putNextEntry(jarEntry);
                    Files.copy(path, jarOutputStream);
                    jarOutputStream.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param dir the directory to delete
     * @throws IOException if deletion fails
     */
    private void deleteDirectory(Path dir) throws IOException {
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Converts a string to Unicode escape sequences for non-ASCII characters.
     *
     * @param input the string to convert
     * @return the string with non-ASCII characters converted to Unicode escapes
     */
    private static String toUnicodeEscapes(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c < 128) {
                sb.append(c);
            } else {
                sb.append(String.format("\\u%04x", (int) c));
            }
        }
        return sb.toString();
    }

    /**
     * Main method for command-line interface.
     *
     * <ul>
     *     <li>{@code Implementor -jar <class-name> <output-path>} - generates JAR file with implementation</li>
     * </ul>
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.err.println("Usage: Implementor [-jar] <class-name> <output-path>");
            return;
        }

        boolean jars = "-jar".equals(args[0]);
        String className = jars ? args[1] : args[0];
        Path outputPath = Path.of(jars ? args[2] : args[1]);

        try {
            Implementor implementor = new Implementor();
            Class<?> token = Class.forName(className);

            if (jars) {
                implementor.implementJar(token, outputPath);
            } else {
                implementor.implement(token, outputPath);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
        } catch (ImplerException | InvalidPathException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}