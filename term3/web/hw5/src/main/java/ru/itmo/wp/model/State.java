package ru.itmo.wp.model;

import java.util.Arrays;

public class State {
    private final int SIZE = 3;

    private Phase phase = Phase.RUNNING;
    private boolean crossesMove = true;
    private final Cell[][] cells = new Cell[SIZE][SIZE];

    public int getSize() {
        return SIZE;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Phase getPhase() {
        return phase;
    }

    public boolean isCrossesMove() {
        return crossesMove;
    }

    public void makeMove(int row, int col) {
        if (phase == Phase.RUNNING && cells[row][col] == null) {
            cells[row][col] = crossesMove ? Cell.X : Cell.O;
            phase = checkPhase();
            crossesMove = !crossesMove;
        }
    }

    private Phase checkPhase() {
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] != null && cells[i][0] == cells[i][1] && cells[i][1] == cells[i][2]) {
                return cells[i][0] == Cell.X ? Phase.WON_X : Phase.WON_O;
            }
            if (cells[0][i] != null && cells[0][i] == cells[1][i] && cells[1][i] == cells[2][i]) {
                return cells[0][i] == Cell.X ? Phase.WON_X : Phase.WON_O;
            }
        }

        if (cells[0][0] != null && cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2]) {
            return cells[0][0] == Cell.X ? Phase.WON_X : Phase.WON_O;
        }
        if (cells[0][2] != null && cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0]) {
            return cells[0][2] == Cell.X ? Phase.WON_X : Phase.WON_O;
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (cells[i][j] == null) {
                    return Phase.RUNNING;
                }
            }
        }
        return Phase.DRAW;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE && cells[row][col] == null;
    }
}
