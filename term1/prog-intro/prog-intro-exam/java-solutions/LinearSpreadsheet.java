import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LinearSpreadsheet {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //System.out.println("Введите значения для каждой ячейки таблицы в формате 10|20|$1 + $2");
        String input = scanner.nextLine();

        String[] cellFormulas = input.split("\\|");
        evaluateSpreadsheet(cellFormulas);

        scanner.close();
    }

    private static void evaluateSpreadsheet(String[] cellFormulas) {
        Map<String, Integer> cellValues = new HashMap<>();
        for (int i = 0; i < cellFormulas.length; i++) {
            String formula = cellFormulas[i];
            int value = evaluateCellFormula(formula, cellValues);
            cellValues.put("$" + (i + 1), value);
            System.out.print(value);
            if (i < cellFormulas.length - 1) {
                System.out.print("|");
            }
        }
        System.out.println();
    }

    private static int evaluateCellFormula(String formula, Map<String, Integer> cellValues) {
        String[] parts = formula.split("\\s*\\|\\s*");
        int result = 0;
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (part.contains("+")) {
                String[] operands = part.split("\\s*\\+\\s*");
                for (String operand : operands) {
                    if (Character.isDigit(operand.charAt(0))) {
                        result += Integer.parseInt(operand);
                    } else {
                        result += cellValues.getOrDefault(operand, 0);
                    }
                }
            } else if (part.contains("-")) {
                String[] operands = part.split("\\s*-\\s*");
                for (int i = 0; i < operands.length; i++) {
                    if (i == 0) {
                        if (Character.isDigit(operands[i].charAt(0))) {
                            result = Integer.parseInt(operands[i]);
                        } else {
                            result = cellValues.getOrDefault(operands[i], 0);
                        }
                    } else {
                        if (Character.isDigit(operands[i].charAt(0))) {
                            result -= Integer.parseInt(operands[i]);
                        } else {
                            result -= cellValues.getOrDefault(operands[i], 0);
                        }
                    }
                }
            } else if (part.contains("*")) {
                String[] operands = part.split("\\s*\\*\\s*");
                result = 1;
                for (String operand : operands) {
                    if (Character.isDigit(operand.charAt(0))) {
                        result *= Integer.parseInt(operand);
                    } else {
                        result *= cellValues.getOrDefault(operand, 0);
                    }
                }
            } else if (part.contains("/")) {
                String[] operands = part.split("\\s*/\\s*");
                result = Integer.parseInt(operands[0]);
                for (int i = 1; i < operands.length; i++) {
                    if (Character.isDigit(operands[i].charAt(0))) {
                        result /= Integer.parseInt(operands[i]);
                    } else {
                        result /= cellValues.getOrDefault(operands[i], 0);
                    }
                }
            } else {
                if (Character.isDigit(part.charAt(0))) {
                    result += Integer.parseInt(part);
                } else {
                    result += cellValues.getOrDefault(part, 0);
                }
            }
        }
        return result;
    }
}