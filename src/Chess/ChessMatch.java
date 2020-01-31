package Chess;

import Chess.pieces.King;
import Chess.pieces.Rook;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check=false;
    private boolean checkmate;

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

    public boolean getCheck(){
        return check;
    }
    public Color getCurrentPlayer(){
        return currentPlayer;
    }
    public boolean getCheckMate(){
        return checkmate;
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

        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("Você não pode se colocar em cheque!");
        }
        check = (testCheck(Opponent(currentPlayer))) ? true : false;

        if(testCheckMate(Opponent(currentPlayer))){

            checkmate = true;

        }else{
            nextTurn();
        }


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
    private void undoMove(Position source, Position target, Piece capturedPiece){
        Piece p = board.removePiece(target);
        board.placePiece(p, source);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add((ChessPiece) capturedPiece);
        }
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

    private boolean testCheckMate(Color color){

        if(!testCheck(color)){
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());

        for(Piece p : list){
            boolean mat[][] = p.possibleMoves();
             for(int i = 0; i<board.getRows(); i++){
                 for (int j = 0; j<board.getColumns(); j++){
                     if (mat[i][j]){
                         Position source = ((ChessPiece)p).getChessPosition().toPosition();
                         Position target = new Position(i, j);
                         Piece capturedPiece = makeMove(source, target);
                         boolean testCheck = testCheck(color);
                         undoMove(source, target, capturedPiece);
                         if(!testCheck){
                             return false;
                         }
                     }
                 }
             }
        }
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
    board.placePiece(piece, new ChessPosition(column, row).toPosition() );
    piecesOnTheBoard.add(piece);

    }

    private Color Opponent(Color color){

        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

    }

    private ChessPiece King(Color color)    {

        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());

        for(Piece p : list){
            if(p instanceof King){
                 return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("Não existe um rei da cor "+color+" no tabuleiro." );
    }

    private boolean testCheck(Color color){

        Position kingPosition = King(color).getChessPosition().toPosition();

        List<Piece> opponentPieces =  piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == Opponent(color)).collect(Collectors.toList());

        for(Piece p :  opponentPieces){

            boolean [][]mat  = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }

        return false;
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
