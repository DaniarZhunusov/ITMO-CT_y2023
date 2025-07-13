package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private int m;
    private int n;

    private final Random random = new Random();

    public RandomPlayer(int m, int n) {
        this.m = m;
        this.n = n;
    }
    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            int r = random.nextInt(m);
            int c = random.nextInt(n);
            final Move move = new Move(r, c, cell);
            if (position.isValid(move)) {
                return move;
            }
        }
    }

    @Override
    public Move makeMove(Position position) {
        return null;
    }
}
