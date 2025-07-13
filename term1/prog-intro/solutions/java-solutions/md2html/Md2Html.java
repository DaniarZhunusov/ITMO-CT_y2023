package md2html;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

public class Md2Html {
    public static void main(String[] args) {
        // :NOTE: args != null, len(args) == 2, args[0] != null, args[1] != null
        String inputFile = args[0];
        String outputFile = args[1];

        try {
            List<String> lines = readFile(inputFile);
            String html = converter(lines);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
                writer.write(html);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static List<String> readFile(String filename) throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


    private static final Map<String, String> symbols = new HashMap<>();
    static {
        symbols.put("--", "s");
        symbols.put("*", "em");
        symbols.put("**", "strong");
        symbols.put("_", "em");
        symbols.put("__", "strong");
        symbols.put("`", "code");
        symbols.put("''", "q");
    }

    public static String converter(List<String> text) {
        int n = System.lineSeparator().length();
        StringBuilder html = new StringBuilder();
        Iterator<String> iterator = text.iterator();

        while (iterator.hasNext()) {
            StringBuilder block = new StringBuilder();
            String line = iterator.next();

            while (!line.isEmpty()) {
                block.append(line).append(System.lineSeparator());
                if (iterator.hasNext()) {
                    line = iterator.next();
                } else {
                    break;
                }
            }

            if (!block.isEmpty()) {
                block.setLength(block.length() - n);
                convertIndenttoHtml(html, block.toString());
            }
        }

        return html.toString();
    }

    private static void convertIndenttoHtml(StringBuilder html, String block) {
        int Hlevel = levelHead(block);
        Map<Integer, Integer> element = new HashMap<>();

        String open;
        open = openorcloseTag(Hlevel, true);
        html.append(open);

        Scanelements(element, block);

        int length = block.length();
        int startPosition;
        if (Hlevel == 0) {
            startPosition = 0;
        } else {
            startPosition = Hlevel + 1;
        }
        convertHtml(element, html, block, startPosition, length);

        String close;
        close = openorcloseTag(Hlevel, false);
        html.append(close);
        html.append(System.lineSeparator());
    }

    private static void Scanelements(Map<Integer, Integer> element, String block) {
        int i = 0;
        Map<String, Integer> last = new HashMap<>();
        while (i < block.length()) {
            if (block.charAt(i) == '\\') {
                i += 2;
            }
            if (markdownSymbol(block, i)) {
                final String elements = search(block, i);
                updatePosition(element, last, elements, i);
                i += elements.length() - 1;
            }
            i++;
        }
    }

    private static boolean markdownSymbol(String block, int i) {
        int maxLength = Math.min(2, block.length() - i);
        for (int j = 0; j <= maxLength; j++) {
            if (symbols.containsKey(block.substring(i, i + j))) {
                return true;
            }
        }
        return false;
    }

    private static String search(String block, int i) {
        int elements = 2;
        while (elements > 0) {
            if (i + elements <= block.length()) {
                String symbol = block.substring(i, i + elements);
                if (symbols.containsKey(symbol)) {
                    return symbol;
                }
            }
            elements--;
        }
        return "";
    }

    private static int levelHead(String line) {
        int level = 0;
        while (level < line.length() && line.charAt(level) == '#') {
            level++;
        }
        if (level > 0 && level < line.length() && line.charAt(level) == ' ') {
            return level;
        }
        return 0;
    }

    private static void convertHtml(Map<Integer, Integer> symbol, StringBuilder sb, String line, int pos1, int pos2) {
        boolean ecranirovanie = false;

        for (int i = pos1; i < pos2; i++) {
            char Char = line.charAt(i);

            if (ecranirovanie) {
                ecranirovanie = false;
                sb.append(Char);
                continue;
            }

            if (symbol.containsKey(i)) {
                i = replaceHtml(symbol, sb, line, i);
            } else if (Char == '\\') {
                ecranirovanie = true;
            } else if (specialSymbols.containsKey(Char)) {
                sb.append(specialSymbols.get(Char));
            } else {
                sb.append(Char);
            }
        }
    }

    private static String openorcloseTag(int level, boolean opened) {
        String tag = "";
        if (opened) {
            tag = "";
        } else {
            tag = "/";
        }

        String result = "";
        if (level == 0) {
            result = "p";
        } else {
            result = "h" + level;
        }

        return "<" + tag + result + ">";
    }

    private static void updatePosition(Map<Integer, Integer> symbol, Map<String, Integer> last, String elements, int i) {
        Integer lastPosition = last.getOrDefault(elements, null);
        if (lastPosition != null) {
            last.remove(elements);
            symbol.put(lastPosition, i);
        } else {
            last.put(elements, i);
        }
    }

    private static final Map<Character, String> specialSymbols = new HashMap<>();
    static {
        specialSymbols.put('>', "&gt;");
        specialSymbols.put('&', "&amp;");
        specialSymbols.put('<', "&lt;");
    }

    private static int replaceHtml(Map<Integer, Integer> elements, StringBuilder sb, String str, int index) {
        String mark = search(str, index);
        if (symbols.containsKey(mark)) {
            String htmlMark = symbols.get(mark);
            sb.append("<" + htmlMark + ">");
            Integer close = elements.get(index);
            if (close != null) {
                convertHtml(elements, sb, str, index + mark.length(), close);
                sb.append("</" + htmlMark + ">");
                index = close + mark.length() - 1;
            } else {
                sb.append(str, index, index + mark.length());
            }
        } else {
            sb.append(str.charAt(index));
        }
        return index;
    }
}
