package boardgame;

import Chess.Color;

public class Piece {

    protected Position position;
    private Board board;

    public Piece(Board board) {

        this.board = board;
    }

    public Board getBoard() {
        return board;
    }


}
