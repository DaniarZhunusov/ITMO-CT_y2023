package game;

import java.util.Arrays;
import java.util.Map;

public class mnkBoard implements Board, Position {
    private final int m;
    private final int n;
    private final int k;
    private int empty;
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;

    public mnkBoard(int m, int n, int k) {
        this.m = m;
        this.n = n;
        this.k = k;
        this.cells = new Cell[m][n];
        empty = m * n;
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }
        empty--;
        cells[move.getRow()][move.getColumn()] = move.getValue();


        if (Win(move)) {
            return Result.WIN;
        }

        if (empty == 0) {
            return Result.DRAW;
        }
        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;
    }

    private boolean Win(Move move) {
        Cell cell = getCell();
        int column = move.getColumn();
        int row = move.getRow();

        int i = 1;
        int down = 0;
        if (row + i < m && cells[row + i][column] == cell) {
            do {
                down++;
                i++;
            } while (row + i < m && cells[row + i][column] == cell);
        }
        int up = 0;
        i = 1;
        while (!(0 > row - i || cells[row - i][column] != cell)) {
            up++;
            i++;
        }
        if (up + down + 1 < k) {
            i = 1;
            int right = 0;
            while (column + i < n && cells[row][column + i] == cell) {
                right++;
                i++;
            }
            int left = 0;
            i = 1;
            while (0 <= column - i && cells[row][column - i] == cell) {
                left++;
                i++;
            }
            if (left + right + 1 >= k) {
                return true;
            }

            i = 1;
            int inRight = 0;
            while (!(column + i >= n || row + i >= m || cells[row + i][column + i] != cell)) {
                inRight++;
                i++;
            }
            int inLeft = 0;
            i = 1;
            while (!(0 > column - i || 0 > row - i || cells[row - i][column - i] != cell)) {
                inLeft++;
                i++;
            }
            if (inLeft + inRight + 1 < k) {
                i = 1;
                int rightRow = 0;
                while (0 <= column - i && row + i < m && cells[row + i][column - i] == cell) {
                    rightRow++;
                    i++;
                }
                int leftColumn = 0;
                i = 1;
                while (column + i < n && 0 <= row - i && cells[row - i][column + i] == cell) {
                    leftColumn++;
                    i++;
                }
                if (leftColumn + rightRow + 1 >= k) {
                    return true;
                }

                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }

    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(" ");
        for (int c = 0; c < n; c++) {
            sb.append(c);
        }
        for (int r = 0; r < m; r++) {
            sb.append("\n");
            sb.append(r);
            for (int c = 0; c < n; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
            }
        }
        return sb.toString();
    }

    @Override
    public void reset() {
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
    }
}
