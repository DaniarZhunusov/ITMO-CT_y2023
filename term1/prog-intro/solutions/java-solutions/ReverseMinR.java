import java.util.Scanner;

public class ReverseMinR {
    public static void main(String[] args) {
        int id = 0;
        int[][] mas = new int[10000][];
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

            int[] min = new int[index];

            for (int i = 0; i < index; i++) {
                min[i] = ints[i];
                for (int j = 0; j < i; j++) {
                    if (ints[j] < min[i]) {
                        min[i] = ints[j];
                    }
                }
            }

            mas[id] = new int[index];
            for (int i = 0; i < index; i++) {
                mas[id][i] = min[i];
            }
            id++;
        }
        
        input.close();

        for (int i = 0; i < id; i++) {
            for (int j = 0; j < mas[i].length; j++) {
                System.out.print(mas[i][j] + " ");
            }
            System.out.println();
        }
    }
}

