package boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
        if(rows < 1 || columns < 1){
            throw new BoardException("Erro ao criar o tabuleiro, é necessário que tenha pelo menos uma" +
                    " linha e uma coluna");
        }

        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return rows;
    }


    public int getColumns() {
        return columns;
    }


    //metodo para retornar uma peça numa devida posição com base na sua linha e coluna
    public Piece piece(int row, int column){
        if(!positionExists(row, column)){

        throw  new BoardException("Posição não está no tabuleiro");
    }
        return pieces[row][column];

    }

    //metodo para retonar uma peça numa devida posição com base na sua posição
    public Piece piece(Position position){
        if(!positionExists(position)){

            throw  new BoardException("Posição não está no tabuleiro");
        }

        return pieces[position.getRow()][position.getColumn()];
    }


    //metodo para colocar uma peça numa devida posição
    public void placePiece(Piece piece, Position position){

        if(thereIsAPiece(position)){

            throw new BoardException("Já tem uma peça, "+ piece + ",nessa posição: " + position);
        }

        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    //metodo para remover uma peça de uma devida posição
    public Piece removePiece(Position position){

        if(!positionExists(position)){
            throw new BoardException("A posição não existe no tabuleiro");
        }

        if(piece(position) == null){
            return  null;
        }

        Piece aux = piece(position);
        aux.position = null;
        pieces[position.getRow()][position.getColumn()] = null;
        return  aux;
    }


    //
    private boolean positionExists(int row, int column) {

        return row >= 0 && row < rows && column >= 0 && column < columns;
    }


    //verificando se existe uma posição no tabuleiro
    public boolean positionExists(Position position){

        return  positionExists(position.getRow(), position.getColumn());
    }

    //metodo para verificar se existe uma peça numa devida posição, retornando um boolean
    public boolean thereIsAPiece(Position position){

        if(!positionExists(position)){

            throw  new BoardException("Posição não está no tabuleiro");
        }

       return piece(position) != null;
    }



}
