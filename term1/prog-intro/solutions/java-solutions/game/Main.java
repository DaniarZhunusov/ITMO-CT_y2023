package game;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("Enter the values m");
                Scanner scanner = new Scanner(System.in);
                int n = scanner.nextInt();
                System.out.println("Enter the values n");
                int m = scanner.nextInt();
                System.out.println("Enter the values k");
                int k = scanner.nextInt();
                System.out.println("Enter the number of players");
                int numPlayers = scanner.nextInt();
                if (numPlayers < 1) {
                    throw new Exception();
                }
                if (k > n || k > m) {
                    throw new IllegalArgumentException();
                }
                if (m < 1 || n < 1 || k < 1) {
                    throw new InputMismatchException();
                }

                List<Player> players = new ArrayList<>();

                for (int i = 0; i < numPlayers; i++) {
                    while (true) {
                        System.out.format("Type of player %d: [R|H|S]", i + 1);
                        String type = scanner.next();
                        if (type.startsWith("H")) {
                            players.add(new HumanPlayer());
                        } else if (type.startsWith("R")) {
                            players.add(new RandomPlayer(m, n));
                        } else if (type.startsWith("S")) {
                            players.add(new SequentialPlayer(m, n));
                        } else {
                            System.out.println("Unknown type.");
                            continue;
                        }
                        break;
                    }
                }

                mnkBoard board = new mnkBoard(m, n, k);
                new OlympicGame(board, players).play(false);

                /*final TwoPlayer game = new TwoPlayer(false, new HumanPlayer(), new RandomPlayer(m, n));
                int result;
                do {
                    result = game.play(new mnkBoard(m, n, k));
                    System.out.println("Game result: " + result);
                } while (result != 0);*/

            } catch (InputMismatchException e) {
                System.out.println("Input error, try again");
            } catch (IllegalArgumentException e) {
                System.out.println("k cannot be greater than m or n");
            } catch (NoSuchElementException | IllegalStateException e) {
                System.out.println("Scanner closed.");
                break;
            } catch (Exception e) {
                System.out.println("The number of players cannot be less than zero");
            }
        }
    }
}