import java.util.Scanner; 

public class Reverse {
    public static void main(String[] args) {
        int id = 0;
        String[] lines = new String[10000];
        Scanner input = new Scanner(System.in);

        while (input.hasNextLine()) {
            int index = 0;
            Scanner scan = new Scanner(input.nextLine());
            int[] ints = new int[10000];

            while (scan.hasNextInt()) { 
                ints[index] = scan.nextInt();
                index++;
            }
            
            scan.close(); 

            for (int i = 0; i < index / 2; i++) {
                int last = ints[i];
                ints[i] = ints[index - i - 1];
                ints[index - i - 1] = last;
            }

            StringBuilder reversedLine = new StringBuilder();
            for (int i = 0; i < index; i++) {
                reversedLine.append(ints[i]);
                if (i < index - 1) {
                    reversedLine.append(" ");
                }
            }
            lines[id] = reversedLine.toString();
            id++;
        }
        input.close();
        
        for (int i = id - 1; i >= 0; i--) {
            System.out.println(lines[i]);
        }
    }
}


