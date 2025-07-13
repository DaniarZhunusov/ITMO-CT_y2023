import java.io.*;

public class WordStatInput {
    public static void main(String[] args) {
        try {
            BufferedReader fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),"UTF-8"));
            try {
                BufferedWriter fileOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));
                try {
                    String[] words = new String[1024]; 
                    int[] counts = new int[1024];    
                    int wordCount = 0;                

                    int line;
                    StringBuilder word = new StringBuilder();
                    while ((line = fileInput.read()) != -1) {
                        char symbol = (char) line;
                        symbol = Character.toLowerCase(symbol);
                        if (Character.isLetter(symbol) || Character.DASH_PUNCTUATION == Character.getType(symbol) || symbol == '\'') {
                            word.append(symbol);
                        } else if (word.length() > 0) {
                            String wordStr = word.toString();
                            int isNewWord = 1;
                            for (int j = 0; j < wordCount; j++) {
                                if (words[j].equals(wordStr)) {
                                    counts[j]++;
                                    isNewWord = 0;
                                    break;
                                }
                            }

                            if (isNewWord == 1) {
                                words[wordCount] = wordStr;
                                counts[wordCount] = 1;
                                wordCount++;
                            }

                            word.setLength(0); 
                        }
                    }

                for (int i = 0; i < wordCount; i++) {
                    fileOutput.write(words[i] + " " + counts[i] + "\n");
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







