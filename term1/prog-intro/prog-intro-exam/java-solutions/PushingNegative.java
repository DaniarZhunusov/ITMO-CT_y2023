import java.util.Scanner;

public class PushingNegative {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //System.out.print("Введите логическое выражение: ");
        String str = scanner.nextLine();
        String result = "";
        int first = 0;
        int second = 0;
        int skobka = 0;

        String s = str.replace("~~", "");

        if (s.contains("~)") || s.contains("~&") || s.contains("~|")) {
            System.out.println("Неверное логическое выражение");
            System.exit(0);
        }

        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) == '(') {
                first++;
            }
            if (s.charAt(j) == ')') {
                second++;
            }
        }

        if (first != second) {
            System.out.println("Неверное логическое выражение");
            System.exit(0);
        }

        for (int l = 0; l < 1; l++) {
            int i = 0;
            while (i < s.length()) {
                if (s.startsWith("~(", i)) {
                    skobka = s.indexOf(")", i);
                    if (skobka != -1) {
                        i += 2;
                        for (int j = i; j < skobka; j++) {
                            if (s.charAt(j) == '~') {
                                result += s.charAt(j + 1);
                            } else if (Character.isLetter(s.charAt(j))) {
                                if (j > 0 && s.charAt(j - 1) != '~') {
                                    result += "~" + s.charAt(j);
                                } else {
                                    result += s.charAt(j);
                                }
                            } else if (j + 1 < skobka && s.charAt(j) == '|' && s.charAt(j + 1) != '~') {
                                result += " & ";
                            } else if (j + 1 < skobka && s.charAt(j) == '&' && s.charAt(j + 1) != '~') {
                                result += " | ";
                            }
                        }
                        i = skobka + 1;
                    } else {
                        System.out.println("Неверное логическое выражение");
                        System.exit(0);
                    }
                } else {
                    result += s.charAt(i);
                    i++;
                }
            }
            System.out.println(result);
            s = result;
            result = "";
        }
        scanner.close();
    }
}



















