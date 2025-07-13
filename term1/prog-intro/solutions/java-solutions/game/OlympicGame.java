package game;

import java.util.*;

public class OlympicGame {
    private final Board board;
    private final List<Player> players;

    public OlympicGame(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public void play(boolean log) {
        int numPlayers = players.size();

        while ((numPlayers != 2 && numPlayers != 4 && numPlayers != 8) && numPlayers > 0) {
            Qualification(log);
            numPlayers = players.size();
        }

        // Групповой этап
        if (numPlayers == 8) {
            groupStage(log);
        }

        // Плей-офф
        if (numPlayers == 4) {
            playOff(players, log);
        }
        // Финальный матч
        if (numPlayers == 2) {
            finalMatches(players,log);
        }
    }

    public void Qualification(boolean log) {
        int numPlayers = players.size();

        while ((numPlayers & (numPlayers - 1)) != 0) {
            List<Player> winners = new ArrayList<>();
            for (int i = 0; i < players.size(); i += 2) {
                if (i + 1 < players.size()) {
                    System.out.format("\nRound between %d(X) %d(O)\n", i + 1, i + 2);
                    int result = startRound(players.get(i), players.get(i + 1), log);
                    if (result == 1) {
                        winners.add(players.get(i));
                        players.remove(i + 1);
                        printWinner(i + 1);
                    } else if (result == 2) {
                        winners.add(players.get(i + 1));
                        players.remove(i);
                        printWinner(i + 2);
                    } else if (result == 0) {
                        System.out.println("DRAW");
                        i -= 2;
                    } else {
                        throw new AssertionError("Unknown startRound result " + result);
                    }
                    board.reset();
                }
            }
            if (players.size() > 0) {
                players.clear();
                players.addAll(winners);
                numPlayers = players.size();
            } else {
                break;
            }
        }
    }


    private void groupStage(boolean log) {
        List<Player> winners = new ArrayList<>();
        List<Player> losers = new ArrayList<>();
        for (int i = 0; i < players.size(); i += 2) {
                System.out.format("\nRound between %d(X) %d(O)\n", i + 1, i + 2);
                int result = startRound(players.get(i), players.get(i + 1), log);
                if (result == 1) {
                    winners.add(players.get(i));
                    losers.add(players.get(i + 1));
                    printWinner(i + 1);
                } else if (result == 2) {
                    winners.add(players.get(i + 1));
                    losers.add(players.get(i));
                    printWinner(i + 2);
                } else if (result == 0) {
                    System.out.println("DRAW");
                    i -= 2;
                } else {
                    throw new AssertionError("Unknown startRound result " + result);
                }
                board.reset();
            }
        players.removeAll(losers);
        playOff(winners, log);
    }

    private void playOff(List<Player> win, boolean log) {
        // Определение победителей группового этапа для плей-офф
        List<Player> lose = new ArrayList<>();

        // Формирование пар для плей-офф
        for (int i = 0; i < win.size(); i += 2) {
            int player1Index = i;
            int player2Index = i + 1;

            System.out.format("\nPlayoff Round between %d(X) %d(O)\n", player1Index + 1, player2Index + 1);
            int result = startRound(win.get(player1Index), win.get(player2Index), log);
            if (result == 1) {
                printWinner(player1Index + 1);
                lose.add(win.get(player2Index));
            } else if (result == 2) {
                printWinner(player2Index + 1);
                lose.add(win.get(player1Index));
            } else if (result == 0) {
                System.out.println("DRAW");
                i -= 2;
            } else {
                throw new AssertionError("Unknown playoff startRound result " + result);
            }
            board.reset();
        }
        third(lose, log);
        finalMatches(win, log);
    }

    private void finalMatches(List<Player> finalists, boolean log) {
        // Финальный матч
        int player1Index = players.indexOf(finalists.get(0));
        int player2Index = players.indexOf(finalists.get(1));

        System.out.format("\nFinal Round between %d(X) %d(O)\n", player1Index + 1, player2Index + 1);
        int result = startRound(players.get(player1Index), players.get(player2Index), log);
        if (result == 1) {
            System.out.println("1st: Player " + finalists.get(0));
            System.out.println("2nd: Player " + finalists.get(1));
        } else if (result == 2) {
            System.out.println("1st: Player " + finalists.get(1));
            System.out.println("2nd: Player " + finalists.get(0));
        } else {
            throw new AssertionError("Unknown final match result " + result);
        }
    }

    private void third(List<Player> lose, boolean log) {
        // Борьба за 3 место
        int player1Index = players.indexOf(lose.get(0));
        int player2Index = players.indexOf(lose.get(1));

        System.out.format("\n1/2 Final Round between %d(X) %d(O)\n", player1Index + 1, player2Index + 1);
        int result = startRound(players.get(player1Index), players.get(player2Index), log);
        if (result == 1) {
            System.out.println("3nd: Player " + lose.get(0));
            System.out.println("4th: Player " + lose.get(1));
        } else if (result == 2) {
            System.out.println("3nd: Player " + lose.get(1));
            System.out.println("4th: Player " + lose.get(0));
        } else {
            throw new AssertionError("Unknown final match result " + result);
        }
    }

    private void printWinner(int no) {
        System.out.format("%d Wins\n", no);
    }

    private int startRound(Player pl1, Player pl2, boolean log) {
        boolean firstTurn = true;
        while (true) {
            final int result = playerMoves(firstTurn ? pl1 : pl2, firstTurn ? 1 : 2, log);
            firstTurn = !firstTurn;
            if (result != -1) {
                System.out.println("Final board");
                System.out.println(board);
                return result;
            }
        }
    }

    private int playerMoves(Player player, int no, boolean log) {
        try {
            return move(player, no, log);
        } catch (Exception e) {
            technicalLose(no);
            return 3 - no;
        }
    }

    private void technicalLose(int id) {
        System.out.format("Player %d crashed. Technical lose\n", id);
    }

    private int move(Player player, int no, boolean log) {
        final Move move = player.move(board.getPosition(), board.getCell());
        final Result result = board.makeMove(move);
        if (log) {
            System.out.println("Player: " + no);
            System.out.println(move);
            System.out.println(board);
            System.out.println("Result: " + result);
        }
        switch (result) {
            case WIN:
                return no;
            case LOSE:
                return 3 - no;
            case DRAW:
                return 0;
            case UNKNOWN:
                return -1;
            default:
                throw new AssertionError("Unknown result " + result);
        }
    }
}
