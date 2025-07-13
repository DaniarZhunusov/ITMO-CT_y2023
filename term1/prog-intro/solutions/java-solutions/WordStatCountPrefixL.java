import java.io.*;
import java.util.*;

public class WordStatCountPrefixL {
    public static void main(String[] args) {
        try {
            BufferedReader fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
            try {
                BufferedWriter fileOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));
                try {
                    LinkedHashMap <String, Integer> prefixCountMap = new LinkedHashMap<>();
                    List <String> wordsList = new ArrayList<>();
                    Map <String, Integer> firstMap = new HashMap<>();

                    int line;
                    StringBuilder word = new StringBuilder();

                    while ((line = fileInput.read()) != -1) {
                        char symbol = (char) line;
                        symbol = Character.toLowerCase(symbol);

                        if (Character.isLetter(symbol) || Character.getType(symbol) == Character.DASH_PUNCTUATION || symbol == '\'') {
                            word.append(symbol);
                        } else if (word.length() >= 3) {
                            String wordStr = word.toString();
                            wordsList.add(wordStr);

                            String prefix = wordStr.substring(0, 3);
                            prefixCountMap.put(prefix, prefixCountMap.getOrDefault(prefix, 0) + 1);

                            if (!firstMap.containsKey(wordStr)) {
                                firstMap.put(wordStr, wordsList.size());
                            }

                            word.setLength(0);
                        } else {
                            word.setLength(0);
                        }
                    }

                    List <Map.Entry<String, Integer>> prefixEntry = new ArrayList<>(prefixCountMap.entrySet());

                    Comparator <Map.Entry<String, Integer>> comparator = (entry1, entry2) -> {
                        int count1 = entry1.getValue();
                        int count2 = entry2.getValue();

                        if (count1 != count2) {
                            return Integer.compare(count1, count2);
                        } else {
                            int index1 = wordsList.indexOf(entry1.getKey() + " ");
                            int index2 = wordsList.indexOf(entry2.getKey() + " ");
                            return Integer.compare(index1, index2);
                        }
                    };

                    prefixEntry.sort(comparator);

                    for (Map.Entry <String, Integer> entry : prefixEntry) {
                        fileOutput.write(entry.getKey() + " " + entry.getValue() + "\n");
                    }


                    } finally {
                        fileOutput.close();
                    }
                } finally {    
                    fileInput.close();
                }


                } catch (FileNotFoundException e) {
                    System.err.println("Input file not found: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Unknown Error: " + e.getMessage());
        }
    }
}





























