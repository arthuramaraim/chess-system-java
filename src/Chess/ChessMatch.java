package Chess;

import Chess.pieces.King;
import Chess.pieces.Rook;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;

    private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
    private List<ChessPiece> capturedPieces = new ArrayList<>();

    public ChessMatch(){
        board = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn(){

        return turn ;
    }

    public Color getCurrentPlayer(){
        return currentPlayer;
    }

    private  void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public ChessPiece[][] getPieces(){

    ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
    for (int i = 0; i < board.getRows();i++){
        for (int j = 0; j< board.getColumns(); j++){

            mat[i][j] = (ChessPiece) board.piece(i,j);
        }
    }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosistion){
    Position position = sourcePosistion.toPosition();
    validateSourcePosition(position);
    return board.piece(position).possibleMoves();

    }




    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source =  sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        nextTurn();
        return (ChessPiece)capturedPiece;

    }
    private Piece makeMove(Position source, Position target){

        Piece p = board.removePiece(source);
        Piece captured = board.removePiece(target);

        if (captured != null){
            piecesOnTheBoard.remove(captured);
            capturedPieces.add((ChessPiece)captured);

        }
        board.placePiece(p, target);
        return captured;
    }

    private void validateSourcePosition(Position position){
    if (!board.thereIsAPiece(position)){
        throw  new ChessException("Não existe peça na possivel de origem");
    }
    if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
    throw new ChessException("A peça esolhida não é sua");
    }
    if (!board.piece(position).isTherePossibleMoves()){
        throw new ChessException("Não tem movimentos possiveis");
    }

    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMoves(target)){
            throw new ChessException("A peça escolhida não pode se mover para a posição de destino");
        }

    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
    board.placePiece(piece, new ChessPosition(column, row).toPosition() );
    piecesOnTheBoard.add(piece);

    }



    private void initialSetup(){
        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));

    }


}
