import java.io.*;
import java.util.*;

public class ShortWords {
    static List<List<Integer>> adj = new ArrayList<>();
    static Map<String, Integer> abbrToId = new HashMap<>();
    static Map<Integer, String> abbrList = new HashMap<>();
    static int[] match;
    static boolean[] used;
    static int count = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        PrintWriter pw = new PrintWriter("output.txt");

        int n = Integer.parseInt(br.readLine());
        String[] words = new String[n];
        for (int i = 0; i < n; i++) {
            words[i] = br.readLine();
            adj.add(new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            Set<String> abbrs = abbreviations(words[i]);
            for (String abbr : abbrs) {
                int id = abbrToId.computeIfAbsent(abbr, key -> {
                    abbrList.put(count, key);
                    return count++;
                });
                adj.get(i).add(id);
            }
        }

        match = new int[count];
        Arrays.fill(match, -1);

        boolean possible = true;
        for (int i = 0; i < n; i++) {
            used = new boolean[n];
            if (!dfs(i)) {
                possible = false;
                break;
            }
        }

        if (!possible) {
            pw.println("-1");
        } else {
            String[] result = new String[n];
            for (int abbr = 0; abbr < count; abbr++) {
                int word = match[abbr];
                if (word != -1) {
                    result[word] = abbrList.get(abbr);
                }
            }
            for (String s : result) {
                pw.println(s);
            }
        }

        pw.close();
    }

    public static boolean dfs(int word) {
        if (used[word]) {
            return false;
        }

        used[word] = true;
        for (int abbr : adj.get(word)) {
            if (match[abbr] == -1 || dfs(match[abbr])) {
                match[abbr] = word;
                return true;
            }
        }
        return false;
    }

    public static Set<String> abbreviations(String word) {
        Set<String> result = new HashSet<>();
        generate(word, 0, new StringBuilder(), result);
        return result;
    }

    public static void generate(String word, int index, StringBuilder current, Set<String> result) {
        if (current.length() > 0 && current.length() <= 4) {
            result.add(current.toString());
        }
        if (current.length() == 4 || index == word.length()) {
            return;
        }
        for (int i = index; i < word.length(); i++) {
            current.append(word.charAt(i));
            generate(word, i + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
