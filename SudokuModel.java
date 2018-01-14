package sudoku;

import java.util.Arrays;

//////////////////////////////////////////////////////////////////// SudokuModel
public class SudokuModel {
    //================================================================ constants
    private static final int BOARD_SIZE = 9;
    private static final int SUBSQUARE   = 3;   // Size of subsquare.

    //=================================================================== fields
    private int[][] _board;
    
    //============================================================== constructor
    public SudokuModel() {
        _board = new int[BOARD_SIZE][BOARD_SIZE];
    }
    
    //============================================================== constructor
    public SudokuModel(String initialBoard) {
        this();       // Call no parameter constructor first.
        // initializeFromString(initialBoard);
    }
    
    //===================================================== initializeFromString
    public void initializeFromString(final String boardStr) {
        clear();  // Clear all values from the board.
        int row = 0;
        int col = 0;
        int boardLength = BOARD_SIZE*BOARD_SIZE;
        //... Loop over every character.
        for (int i = 0; i < boardLength; i++) {
            char c = boardStr.charAt(i);
            if (c >= '1' && c <='9') {
                if (row > BOARD_SIZE || col > BOARD_SIZE) {
                    throw new IllegalArgumentException("SudokuModel: "
                            + " Attempt to initialize outside 1-9 "
                            + " at row " + (row+1) + " and col " + (col+1));
                }
                _board[row][col] = c - '0';  // Translate digit to int.
                col++;
            } else if (c == '.') {
                col++;
            } else if (c == '/') {
                row++;
                col = 0;
            } else {
                throw new IllegalArgumentException("SudokuModel: Character '" + c
                        + "' not allowed in board specification");
            }
        }
    }
    
    //============================================================== islegalMove
    public boolean isLegalMove(int row, int col, int val) {
        return row>=0 && row<BOARD_SIZE && col>=0 && col<BOARD_SIZE
                && val>0 && val<=9 && _board[row][col]==0;
    }
    
    //=================================================================== setVal
    public void setVal(int r, int c, int v) {
        _board[r][c] = v;
    }
    
    //=================================================================== getVal
    public int getVal(int row, int col) {
        return _board[row][col];
    }
    
    //===================================================================== clear
    public void clear() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                setVal(row, col, 0);
            }
        }
    }

    /* ======================= Solver ====================================== */
    public void makeMove(int row,int col, int value) {
        if(this.isValidMove(row,col,value)) {
            this._board[row][col] = value;
            // this.mutable[row][col] = isMutable;
        }
    }
    
    public boolean isValidMove(int row,int col,int value) {
        if(this.isLegalMove(row, col, value)) {
            if(!this.numInCol(col,value) && !this.numInRow(row,value) && !this.numInBox(row,col,value)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean numInCol(int col,int value) {
        if(col <= BOARD_SIZE) {
            for(int row=0;row < BOARD_SIZE;row++) {
                if(this._board[row][col] == value) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean numInRow(int row,int value) {
        if(row <= BOARD_SIZE) {
            for(int col=0;col < BOARD_SIZE;col++) {
                if(this._board[row][col] == value) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean numInBox(int row,int col,int value) {
        // if(this.inRange(row, col)) {
            int boxRow = row / SUBSQUARE;
            int boxCol = col / SUBSQUARE;
            
            int startingRow = (boxRow*SUBSQUARE);
            int startingCol = (boxCol*SUBSQUARE);
            
            for(int r = startingRow;r <= (startingRow+SUBSQUARE)-1;r++) {
                for(int c = startingCol;c <= (startingCol+SUBSQUARE)-1;c++) {
                    if(this._board[r][c] == value) {
                        return true;
                    }
                }
            }
        // }
        return false;
    }

    public int[][] deepCopyArray() {
        int[][] a2 = new int[this._board.length][this._board[0].length];
        for (int i = 0; i < this._board.length; i++) {
            for (int j = 0; j < this._board[0].length; j++) {
                a2[i][j] = this._board[i][j];
            }
        }
        return a2;
    }

    public boolean deepEquals(int[][] a2){
        return Arrays.deepEquals(this._board, a2);
    }

    public boolean solved(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(this._board[i][j] == 0){
                    return false;
                }
            }
        }

        return true;
    }

    public void printBoard() { //prints
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0)System.out.println(" -----------------------");
            for (int j = 0; j < 9; ++j) {
                if (j % 3 == 0) System.out.print("| ");
                System.out.print(this._board[i][j] == 0 ? " " : this._board[i][j]);
                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
        System.out.println();
    }
}