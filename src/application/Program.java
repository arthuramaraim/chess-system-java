package application;

import Chess.ChessMatch;
import boardgame.Board;
import boardgame.Position;

public class Program {


    public static void main(String[] args){

        ChessMatch chessMatch = new ChessMatch();

        UI.printBoard(chessMatch.getPieces());


}
    }