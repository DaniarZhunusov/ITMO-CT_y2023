package game;

public interface Player {
    Move move(Position position, Cell cell);

    Move makeMove(Position position);

    //Move makeMove(Position position, Cell cell);
}
