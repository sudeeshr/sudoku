package sudoku;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Sudoku extends JFrame {

	private static String INITIAL_BOARD =
            "8156....4/" +
            "6...75.8./" +
            "....9..../" +
            "9...417../" +
            ".4.....2./" +
            "..623...8/" +
            "....5..../" +
            ".5.91...6/" +
            "1....7895";
    
    //=================================================================== fields
    private SudokuModel        _sudokuLogic = new SudokuModel(INITIAL_BOARD);
    private SudokuBoardDisplay _sudokuBoard = new SudokuBoardDisplay(_sudokuLogic);

    Thread queryThread;

	public Sudoku() {
		JButton btnLoad = new JButton("Load");
		JButton btnRun = new JButton("Run");
		JButton btnInterrupt = new JButton("Interrupt");
		JButton btnClear = new JButton("Clear");
		JButton btnQuit = new JButton("Quit");

		QuitBtnListener quitListener = new QuitBtnListener();
		btnQuit.addActionListener(quitListener);

		ClearBtnListener clearListener = new ClearBtnListener();
		btnClear.addActionListener(clearListener);

		LoadBtnListener loadListener = new LoadBtnListener();
		btnLoad.addActionListener(loadListener);

		RunBtnListener runListener = new RunBtnListener();
		btnRun.addActionListener(runListener);

		InterruptBtnListener interruptListener = new InterruptBtnListener();
		btnInterrupt.addActionListener(interruptListener);

		JPanel controlPanel = new JPanel();
		controlPanel.add(btnLoad);
		controlPanel.add(btnRun);
		controlPanel.add(btnInterrupt);
		controlPanel.add(btnClear);
		controlPanel.add(btnQuit);

		// 2... Create content panel, set layout
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        
        // 3... Add the components to the content panel.
        content.add(controlPanel, BorderLayout.NORTH);
        content.add(_sudokuBoard, BorderLayout.CENTER);
        content.add(_sudokuBoard.console, BorderLayout.SOUTH);

        // 4... Set this window's attributes, and pack it.
     	// setSize(500, 500);
        setContentPane(content);
        setTitle("Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);               // Don't let user resize it.
        pack();
        setLocationRelativeTo(null);       // Center it.
	}

	public static void main(String[] args) {
        new Sudoku().setVisible(true);
    }

    // Inner class to be used as ActionEvent listener for ALL JTextFields
   	private class QuitBtnListener implements ActionListener {
   		@Override
      	public void actionPerformed(ActionEvent e) {
      		Sudoku.this.dispatchEvent(new WindowEvent(Sudoku.this, WindowEvent.WINDOW_CLOSING));
      	}
   	}

   	// Inner class to be used as ActionEvent listener for ALL JTextFields
   	private class ClearBtnListener implements ActionListener {
   		@Override
      	public void actionPerformed(ActionEvent e) {
      		_sudokuBoard.clear();
      	}
   	}

   	// Inner class to be used as ActionEvent listener for ALL JTextFields
   	private class LoadBtnListener implements ActionListener {
   		@Override
      	public void actionPerformed(ActionEvent e) {
      		_sudokuBoard.openPuzzle();
      	}
   	}

   	// Inner class to be used as ActionEvent listener for ALL JTextFields
   	private class RunBtnListener implements ActionListener {
   		@Override
      	public void actionPerformed(ActionEvent e) {
      		queryThread = new Thread() {
	      		public void run() {
	      			_sudokuBoard.solve();
	      		}
			};
      		queryThread.start();
      	}
   	}

   	private class InterruptBtnListener implements ActionListener {
   		@Override
      	public void actionPerformed(ActionEvent e) {
      		queryThread.interrupt();
      		_sudokuBoard.logToConsole("Interrupted.");
      	}
   	}
}