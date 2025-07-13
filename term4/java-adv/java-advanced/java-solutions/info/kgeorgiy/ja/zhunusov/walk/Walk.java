package info.kgeorgiy.ja.zhunusov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Walk {
    private static final String ZERO_HASH = "0000000000000000";

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid Arguments");
            return;
        }

        Path inputPath;
        Path outputPath;
        try {
            inputPath = Paths.get(args[0]);
            outputPath = Paths.get(args[1]);
        } catch (InvalidPathException e) {
            System.err.println("Invalid path: " + e.getMessage());
            return;
        }

        try {
            Files.createDirectories(outputPath.getParent());
        } catch (IOException | NullPointerException e) {
            System.err.println("Can't create files directories: " + e.getMessage());
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String filePath;

            while ((filePath = reader.readLine()) != null) {
                String hash = HashFile(filePath, digest);
                writer.write(hash + " " + filePath);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found: " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("Invalid path: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Security exception: " + e.getMessage());
        }
    }

    private static String HashFile(String filePath, MessageDigest digest) {
        Path path;
        try {
            path = Paths.get(filePath);
        } catch (InvalidPathException e) {
            return ZERO_HASH;
        }

        digest.reset();

        try (InputStream inputStream = Files.newInputStream(path)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return ZERO_HASH;
        }

        return bytesToShortHex(digest.digest());
    }

    private static String bytesToShortHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            hexString.append(String.format("%02x", bytes[i]));
        }
        return hexString.toString();
    }
}
