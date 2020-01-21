package boardgame;

public class Piece {

    protected Position position;
    private Board board;

    public Piece(Position position, Board board) {
        this.position = null;
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
