import java.util.Scanner;

public class AccurateMovement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        String[] numbers = input.split(" ");

        double a = Double.parseDouble(numbers[0]);
        double b = Double.parseDouble(numbers[1]);
        double n = Double.parseDouble(numbers[2]);
        double result = 2 * Math.ceil((n - b)/(b - a)) + 1;
        int res = (int) result;
        System.out.println(res);
    	scanner.close();
    }
}

