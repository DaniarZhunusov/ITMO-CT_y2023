import java.io.*;
import java.util.*;

public class FairyTail {
    private static int n;
    private static List<Integer> lamps = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        n = Integer.parseInt(reader.readLine().trim());
        
        for (int i = 1; i <= n; i++) {
            lamps.add(i);
        }

        lamps.sort((i, j) -> {
            try {
                return ask(i, j, reader, writer) ? -1 : 1;
            } catch (IOException e) {
                throw new RuntimeException(e); 
            }
        });

        writer.write("0");
        for (int lamp : lamps) {
            writer.write(" " + lamp);
        }
        writer.write("\n");
        writer.flush();
    }

    private static boolean ask(int i, int j, BufferedReader reader, BufferedWriter writer) throws IOException {
        writer.write("1 " + i + " " + j + "\n");
        writer.flush();

        String answer = reader.readLine().trim();
        return answer.equalsIgnoreCase("YES");
    }
}
