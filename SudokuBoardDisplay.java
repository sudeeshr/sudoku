package sudoku;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

public class SudokuBoardDisplay extends JComponent {
	//================================================================ constants
    private static final int CELL_PIXELS = 50;  // Size of each cell.
    private static final int PUZZLE_SIZE = 9;   // Number of rows/cols
    private static final int SUBSQUARE   = 3;   // Size of subsquare.
    private static final int BOARD_PIXELS = CELL_PIXELS * PUZZLE_SIZE;
    private static final int TEXT_OFFSET = 15;  // Fine tuning placement of text.
    
	public static final Color OPEN_CELL_BGCOLOR = Color.WHITE;
	public static final Color OPEN_CELL_TEXT_YES = new Color(0, 255, 0);  // RGB
	public static final Color OPEN_CELL_TEXT_NO = Color.RED;
	public static final Color CLOSED_CELL_BGCOLOR = Color.YELLOW; // RGB
	public static final Color CLOSED_CELL_TEXT = Color.BLACK;
	public static final Font TEXT_FONT = new Font("Monospaced", Font.BOLD, 20);

	public JTextArea console = new JTextArea(4, 1);
	static List<String> log = new ArrayList<String>(4);
    
    static List[][] candidates = new ArrayList[9][9];
    
    // The game board composes of 9x9 JTextFields,
   	// each containing String "1" to "9", or empty String
   	private JTextField[][] tfCells = new JTextField[PUZZLE_SIZE][PUZZLE_SIZE];

    //================================================================ fields
    private SudokuModel _model;      // Set in constructor.
    
    //============================================================== constructor
    public SudokuBoardDisplay(SudokuModel model) {
    	setLayout(new GridLayout(PUZZLE_SIZE, PUZZLE_SIZE));  // 9x9 GridLayout
        setPreferredSize(new Dimension(BOARD_PIXELS + 2, BOARD_PIXELS + 2));
        setBackground(Color.WHITE);

        console.setBackground(new Color(0, 255, 255));
        
        _model = model;

        drawGridLines();
        logToConsole("Ready.");
    }

    public void logToConsole(String msg){
    	if(log.size() >= 4){
    		log.remove(0);
    	}

		log.add(msg);

    	console.setText(String.join("\n", log));
    }
        
    //============================================================ drawGridLines
    // Separate method to simlify paintComponent.
    private void drawGridLines() {

		InputListener listener = new InputListener();

    	// Construct 9x9 JTextFields and add to the content-pane
      	for (int row = 0; row < PUZZLE_SIZE; ++row) {

         	for (int col = 0; col < PUZZLE_SIZE; ++col) {
	            tfCells[row][col] = new JTextField(); // Allocate element of array
	            add(tfCells[row][col]);            // ContentPane adds JTextField

	            setVal(row, col);

            	// Add ActionEvent listener to process the input
               	tfCells[row][col].addActionListener(listener);   // For all editable rows and cols

	            // Beautify all the cells
	            tfCells[row][col].setHorizontalAlignment(JTextField.CENTER);
	            tfCells[row][col].setFont(TEXT_FONT);
				
				if (row == 0) {
            		if (col == 0) {
            			// Top left corner, draw all sides
	            		tfCells[row][col].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
	            	}
	            	else{
            			// Top edge, draw all sides except left edge
	            		if(col % SUBSQUARE == 0){
	            			tfCells[row][col].setBorder(BorderFactory.createMatteBorder(1, 2, 1, 1, CLOSED_CELL_TEXT));
	            		}
	            		else{
	            			tfCells[row][col].setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, CLOSED_CELL_TEXT));
	            		}
	            	}
            	}
            	else{
            		if (col == 0) {
                		// Left-hand edge, draw all sides except top
                		if(row % SUBSQUARE == 0){
	            			tfCells[row][col].setBorder(BorderFactory.createMatteBorder(2, 1, 1, 1, CLOSED_CELL_TEXT));
		            	}
		            	else{
	            			tfCells[row][col].setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, CLOSED_CELL_TEXT));
		            	}
	            	}
	            	else {
                  		// Neither top edge nor left edge, skip both top and left lines
	            		if(col % SUBSQUARE == 0 && row % SUBSQUARE == 0){
				      		tfCells[row][col].setBorder(BorderFactory.createMatteBorder(2, 2, 1, 1, CLOSED_CELL_TEXT));
	            		}
	            		else if(col % SUBSQUARE == 0){
				      		tfCells[row][col].setBorder(BorderFactory.createMatteBorder(0, 2, 1, 1, CLOSED_CELL_TEXT));
	            		}
	            		else if(row % SUBSQUARE == 0){
				      		tfCells[row][col].setBorder(BorderFactory.createMatteBorder(2, 0, 1, 1, CLOSED_CELL_TEXT));
	            		}
	            		else{
				      		tfCells[row][col].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, CLOSED_CELL_TEXT));
	            		}
	            	}
            	}

    	 	}
      	}

    }

    public void setVal(int row, int col){
    	if (_model.getVal(row, col) == 0) {
           tfCells[row][col].setText("");     // set to empty string
           tfCells[row][col].setEditable(true);
           tfCells[row][col].setBackground(OPEN_CELL_BGCOLOR);
        } else {
           tfCells[row][col].setText(_model.getVal(row, col) + "");
           tfCells[row][col].setEditable(true);
           tfCells[row][col].setBackground(CLOSED_CELL_BGCOLOR);
           tfCells[row][col].setForeground(CLOSED_CELL_TEXT);
        }
    }

    public void print(int row, int col, int value){
    	tfCells[row][col].setText(value + "");
       	// tfCells[row][col].setEditable(false);
       	if(value <= PUZZLE_SIZE){
	       	tfCells[row][col].setBackground(CLOSED_CELL_BGCOLOR);
	       	tfCells[row][col].setForeground(CLOSED_CELL_TEXT);
       	}
    }

    private void Solver(){ //solves
        logToConsole("Working..");
		try{
	        fillCandidates();
	        int counter = 0;
	        boolean epicWin = false;
	        while(!epicWin){
	            int[][] lastMove = _model.deepCopyArray();
	            for (int i = 0; i < 9; i++) {
	                for (int j = 0; j < 9; j++) {
	                    if(_model.getVal(i, j) == 0){
	                        eliminateAndInsert(i, j);
	                        _model.printBoard();
	                        counter++;
	                    }

	                    if(_model.solved()){
	                        epicWin=true;
	                        break;
	                    }
	                }
	            }
	            if(_model.deepEquals(lastMove)){
	        		logToConsole("FAIL! Iterations: " + counter);
	                System.out.println("FAIL! Iterations: " + counter);
	                return;
	            }
	        }

	        logToConsole("WIN! Iterations: " + counter);
        	System.out.println("WIN! Iterations: " + counter);
		}
        catch(Exception ex){
        	logToConsole("Interrupted.");
        }
    }

    private void fillCandidates(){ //fills the candidate lists, 
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if (_model.getVal(i, j) == 0){
                    List<Integer> oneToNine = new ArrayList<Integer>();
                    for (int k = 1; k < 10; k++)
                        oneToNine.add(k);
                    candidates[i][j] = oneToNine;
					
                    String str = "";
                    for(int list : oneToNine)
                    	str += list;
                	
                	print(i,j, Integer.parseInt(str));
                }
            }
        }
        eliminateAll();
    }

    public int arrayListToInt(List aList){
    	String str = "";
        for(Object list : aList)
        	str += list.toString();

        return Integer.parseInt(str);
    }

    private void eliminateAll() {
    	try{
	        for (int i = 0; i < 9; i++){
	            for (int j = 0; j < 9; j++)
	                if(_model.getVal(i, j) == 0){
	                    eliminateCandidates(i, j);
						Thread.sleep(10);
	        			insertNakedSingles(i, j);
	        			Thread.sleep(10);
	                }
	        }
    	}
    	catch(InterruptedException ex){
    		logToConsole("Interrupted.");
    	}
    }

    private void eliminateAndInsert(int x, int y){
        eliminateCandidates(x, y);
        insertNakedSingles(x, y);
        insertHiddenSingles(); //bugged
    }

    private void eliminateCandidates(int x, int y){ //works fine
        int rowOffset = (x/3)*3; int colOffset = (y/3)*3;
        for (int i = 0; i < 9; i++)//xy's row
            if (candidates[x][y].contains(_model.getVal(x, i)) && _model.getVal(x, i) != 0){
            	if(candidates[x][y].size() > 1){
            		candidates[x][y].remove(candidates[x][y].indexOf(_model.getVal(x, i)));
                	print(x, y, arrayListToInt(candidates[x][y]));
            	}
            }
        //          remove numbers from candidate list if they appear in the row
        for (int i = 0; i < 9; i++)//xy's column
            if (candidates[x][y].contains(_model.getVal(i, y)) && _model.getVal(i, y) != 0){
            	if(candidates[x][y].size() > 1){
            		candidates[x][y].remove(candidates[x][y].indexOf(_model.getVal(i, y)));
                	print(x, y, arrayListToInt(candidates[x][y]));
            	}
            }
        //          remove numbers from candidate list if they appear in the column
        for (int i = 0; i < 3; i++) //xy's box
            for (int j = 0; j < 3; j++)
                if (candidates[x][y].contains(_model.getVal(rowOffset+i, colOffset+j)) && _model.getVal(rowOffset+i, colOffset+j) != 0){
            		if(candidates[x][y].size() > 1){
                		candidates[x][y].remove(candidates[x][y].indexOf(_model.getVal(rowOffset+i, colOffset+j)));
                    	print(x, y, arrayListToInt(candidates[x][y]));
            		}
                }
        //              remove numbers from candidate list if they appear in the box
    }

    private void insertNakedSingles(int x, int y) { //obviously works
        if(candidates[x][y].size() == 1){ //if only one candidate, insert
        	int num = (int)candidates[x][y].get(0);
            _model.setVal(x, y, num);
            print(x, y, num);
        }
    }

    private void insertHiddenSingles(){ //fucked up
         
        eliminateAll();
         
        for (int row = 0; row < 9; row++){ //rows
            int [] multiplicityOf = new int[10];
            for (int col = 0; col < 9; col++)
                if (_model.getVal(row, col) == 0)
                    for (Object candidate : candidates[row][col])
                        multiplicityOf[(int)candidate] += 1;
 
            for (int candidate = 1; candidate <= 9; candidate++)
                if (multiplicityOf[candidate] == 1)
                    for (int col = 0; col < 9; col++)
                    	if(_model.isValidMove(row, col, candidate))
                        	tryInsert(row, col, candidate);
        }
         
        eliminateAll();
 
        for (int col = 0; col < 9; col++){ //columns
            int [] multiplicityOf = new int[10];
            for (int row = 0; row < 9; row++)
                if (_model.getVal(row, col) == 0)
                    for (Object candidate : candidates[row][col])
                        multiplicityOf[(int)candidate] += 1;
 
            for (int candidate = 1; candidate <= 9; candidate++)
                if (multiplicityOf[candidate] == 1)
                    for (int row = 0; row < 9; row++)
                    	if(_model.isValidMove(row, col, candidate))
        	                tryInsert(row, col, candidate);
        }
         
        eliminateAll();
 
        for (int x = 0; x < 3; x++){ //all nine boxes
            for (int y = 0; y < 3; y++){
                int [] multiplicityOf = new int[10]; 
                int rowOffset = x*3; int colOffset = y*3;
                for (int i = 0; i < 3; i++){
                    for (int j = 0; j < 3; j++)
                        if (_model.getVal(rowOffset+i, colOffset+j) == 0)
                            for (Object candidate : candidates[rowOffset+i][colOffset+j])
                                multiplicityOf[(int)candidate] += 1; 
                }
 
                for (Integer candidate = 1; candidate <= 9; candidate++)
                    if (multiplicityOf[candidate] == 1)
                        for (int i = 0; i < 3; i++)
                            for (int j = 0; j < 3; j++)
                    			if(_model.isValidMove(rowOffset+i, colOffset+j, candidate))
                                	tryInsert(rowOffset+i, colOffset+j, candidate);
            }
        }
    }

    private void tryInsert(int row, int col, int candidate){
        if(_model.getVal(row, col) == 0 && candidates[row][col].contains(candidate)){
        	candidates[row][col].remove((int)candidates[row][col].indexOf(candidate));
            _model.setVal(row, col, candidate);
            print(row, col, candidate);
        }
    }

    public void solve() {
    	Solver();
    }

    public void clear(){
    	for (int row = 0; row < PUZZLE_SIZE; row++) {
            for (int col = 0; col < PUZZLE_SIZE; col++) {
                _model.setVal(row, col, 0);
                tfCells[row][col].setText("");
                tfCells[row][col].setBackground(OPEN_CELL_BGCOLOR);
                tfCells[row][col].setEditable(true);
            }
        }
    }

    public void openPuzzle() {
    	JFileChooser fileChooser = new JFileChooser();
	    int returnVal = fileChooser.showDialog(this, "Open");

	    File file = fileChooser.getSelectedFile();
	    // Scanner readFile = new Scanner(file);
	    logToConsole("Loading file "+ file.getName());

	    // If the user has canceled, no need to continue with open process
	    if (returnVal != JFileChooser.APPROVE_OPTION) {
	        return;
	    }

	    // Row
	    int r = 0;

		BufferedReader br = null;

	    try{
			String line;
	    	br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {

				// process the line.
		        String[] splitLine = line.split("");
				
				// Verify the length of the row
		        if (splitLine.length < PUZZLE_SIZE) {
		            throw new IllegalArgumentException(String.format("Row length(%d) not correct in %s at row %d",
		                    splitLine.length, file, r));
		        }

		        for (int c = 0; c < PUZZLE_SIZE; c++) {

		            // Verify each item in row
		            try {
						int i = Integer.parseInt(splitLine[c]);
						// store the value of i in the cell and mark it solved
		            	_model.setVal(r, c, i);
					}
					catch (NumberFormatException e) {
						// mark the cell as having no specified value yet
		            	_model.setVal(r, c, 0);
					}
		            
		            setVal(r, c);
		        }

		        if(r == PUZZLE_SIZE){
		        	break;
		        }

		        // Move to next row
		        r++;
			}

			logToConsole("File loaded.");			

		}
		catch(IOException ex){
			logToConsole(ex.getMessage());
		}
		catch(IllegalArgumentException ex){
			logToConsole(ex.getMessage());
			System.out.println(ex.getMessage());
		}
		finally{
			try{
				br.close();
			}
			catch(IOException ex){

			}
		}

	    // Update squares with data from file
	    /*while (readFile.hasNextLine()) {
	        String[] splitLine = readFile.nextLine().split("\\R");

	        // Verify the length of the row
	        if (splitLine.length < PUZZLE_SIZE) {
	            throw new IllegalArgumentException(String.format("Row length(%d) not correct in %s at row %d",
	                    splitLine.length, file, r));
	        }

	        for (int c = 0; c < PUZZLE_SIZE; c++) {
	            // Verify each item in row
	            if(Character.isDigit(splitLine[c].charAt(0))){
	            	// Update square
	            	_model.setVal(r, c, splitLine[c] - '0');
	            }
	            else{
	            	// Update square
	            	_model.setVal(r, c, 0);
	            }
	            
	        }
	        // Move to next row
	        r++;
	    }*/
    }

    // Inner class to be used as ActionEvent listener for ALL JTextFields
   	private class InputListener implements ActionListener {
 
      	@Override
      	public void actionPerformed(ActionEvent e) {
         	// All the 9*9 JTextFileds invoke this handler. We need to determine
         	// which JTextField (which row and column) is the source for this invocation.
         	int rowSelected = -1;
         	int colSelected = -1;

         	// Get the source object that fired the event
         	JTextField source = (JTextField)e.getSource();System.out.println("here");
         	// Scan JTextFileds for all rows and columns, and match with the source object
         	boolean found = false;
         	for (int row = 0; row < PUZZLE_SIZE && !found; ++row) {
            	for (int col = 0; col < PUZZLE_SIZE && !found; ++col) {
	               	if (tfCells[row][col] == source) {
	                  	rowSelected = row;
	                  	colSelected = col;
	                  	found = true;  // break the inner/outer loops
	               	}
	            }
	     	}

			if(found){
 
				/*
				* [TODO 5]
				* 1. Get the input String via tfCells[rowSelected][colSelected].getText()
				* 2. Convert the String to int via Integer.parseInt().
				* 3. Assume that the solution is unique. Compare the input number with
				*    the number in the puzzle[rowSelected][colSelected].  If they are the same,
				*    set the background to green (Color.GREEN); otherwise, set to red (Color.RED).
				*/
				String str = tfCells[rowSelected][colSelected].getText();

				_model.setVal(rowSelected, colSelected, 0);
				tfCells[rowSelected][colSelected].setBackground(OPEN_CELL_BGCOLOR);

				try{
					if(str != null && !str.isEmpty()){
						int num = Integer.parseInt(str);
						if(_model.isValidMove(rowSelected, colSelected, num)){
							_model.setVal(rowSelected, colSelected, num);
						}
						else{
							tfCells[rowSelected][colSelected].setBackground(OPEN_CELL_TEXT_NO);
						}
					}
				}
				catch(NumberFormatException nfe){
					tfCells[rowSelected][colSelected].setBackground(OPEN_CELL_TEXT_NO);
				}

				/* 
				* [TODO 6] Check if the player has solved the puzzle after this move.
				* You could update the masks[][] on correct guess, and check the masks[][] if
				* any input cell pending.
				*/
 			}
      	}
   	}
    
}