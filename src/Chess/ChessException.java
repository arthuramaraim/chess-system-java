package Chess;

import boardgame.BoardException;

public class ChessException extends BoardException {

    private static final  long serialVerisonUID = 1L;

    public ChessException(String msg) {
        super(msg);
    }
}
