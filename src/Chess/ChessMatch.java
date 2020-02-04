package Chess;

import Chess.pieces.*;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

import java.security.InvalidParameterException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check=false;
    private boolean checkmate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

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


    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted(){
        return promoted;
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

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        //special move promotion
        promoted = null;
        if(movedPiece instanceof Pawn){
            if(movedPiece.getColor() == Color.WHITE && target.getRow() == 0 ||movedPiece.getColor() == Color.BLACK && target.getRow() == 7 ){
            promoted  = (ChessPiece)board.piece(target);
            promoted = replacePromotedPiece("Q");
            }
        }

        check = (testCheck(Opponent(currentPlayer))) ? true : false;


        if(testCheckMate(Opponent(currentPlayer))){

            checkmate = true;

        }else{
            nextTurn();
        }

        //special move enPassant
        if(movedPiece instanceof Pawn && target.getRow() == source.getRow() -2 || target.getRow() == source.getRow() +2){
            enPassantVulnerable = movedPiece;
        }else{
            enPassantVulnerable = null;
        }

        return (ChessPiece)capturedPiece;

    }

    public ChessPiece replacePromotedPiece(String type){
        if(promoted == null){
            throw  new IllegalStateException("Não há peça a ser promovida.");
        }
        if(type.equals("B") && type.equals("N") && type.equals("R") && type.equals("Q")){
            throw new InvalidParameterException("Tipo invalido para a promoção.");
        }
        Position pos  = promoted.getChessPosition().toPosition();
        Piece p  = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);

        return newPiece;


    }


    private ChessPiece newPiece(String type, Color color){
        if(type.equals("B")){
            return new Bishop(board,color);
        }
        if(type.equals("Q")){
            return new Queen(board,color);

        }
        if(type.equals("N")){
            return new Knight(board,color);
        }
        return new Rook(board,color);
    }

    private Piece makeMove(Position source, Position target){
        ChessPiece p  = (ChessPiece)board.removePiece(source);
        p.increaseMoveCount();
        //Piece p = board.removePiece(source);
        Piece captured = board.removePiece(target);
        board.placePiece(p, target);

        if (captured != null){
            piecesOnTheBoard.remove(captured);
            capturedPieces.add((ChessPiece)captured);

        }
       //Special move Castiling kingside rook
        if(p instanceof King  && target.getColumn() == source.getColumn() +2){
            Position sourceT  = new Position(source.getRow(), source.getColumn() +3);
            Position targetT = new Position(source.getRow(), source.getColumn() +1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        //special move Castiling Queenside rook
        if(p instanceof King  && target.getColumn() == source.getColumn() -2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();

        }

        //special move En Passant

        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && captured == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(target.getRow() +1, target.getColumn());
                }
                else{
                    pawnPosition = new Position(target.getRow() -1, target.getColumn());
                }
                captured = board.removePiece(pawnPosition);
                capturedPieces.add((ChessPiece) captured);
                piecesOnTheBoard.remove(captured);
            }
        }

        return captured;
    }
    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece)board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add((ChessPiece) capturedPiece);
        }

        //Special move Castiling kingside rook
        if(p instanceof King  && target.getColumn() == source.getColumn() +2){
            Position sourceT  = new Position(source.getRow(), source.getColumn() +3);
            Position targetT = new Position(source.getRow(), source.getColumn() +1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }

        //special move Castiling Queenside rook
        if(p instanceof King  && target.getColumn() == source.getColumn() -2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }

        //special move En Passant

        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                Position pawnPosition;
                ChessPiece pawn = (ChessPiece)board.removePiece(target);

                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());
                }
                else{
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);

            }
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
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

    }


}
