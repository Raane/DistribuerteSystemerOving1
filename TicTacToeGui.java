import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

import javax.swing.*;
import java.rmi.*;
/**
 * Graphical user interface to a Tic Tac Toe application.
 * The GUI is incomplete because it has no associated player.
 * It also needs a routine for checking if one of the players
 * have got five marks in a row.
 */
public class TicTacToeGui extends JFrame implements Constants, ActionListener {
	/** Textfield showing what mark you use ('X' or 'O') */
	private JTextField id;
	/** TextArea giving feedback to the user */
	private TextArea display;
	/** The panel containing the board */
	private JPanel boardPanel;
	/** The squares of the board */
	private Square board[][];
	/** The menu bar */
	private JMenuBar menuBar;
	/** The game submenu */
	private JMenu gameMenu;
	/** Game submenu choices */
	private JMenuItem newGameItem, quitItem;
	
	/** The name of the player using this GUI */
	private String myName;
	/** The mark used by this player ('X' or 'O') */
	private char myMark;
	/** The server and client is used to create the connection. After the connection is establised they have no further use. */
	protected Server server;
	protected Client client;
	/** When a connection have been established both the server and the client cast their opponent to Opponent. */
	protected Opponent opponent;
	/** All the gameLogic is stored and done in this. Some of it is however static and do not use the instance. */
	private GameLogic gameLogic;
	/** A variable that keeps track of whether the program successfully connected as a client. */
	public boolean clientConnected;

	/**
	 * Creates a new GUI.
	 * @param name	The name of that player.
	 * @param mark	The mark used by that player.
	 * @throws RemoteException 
	 */
	public TicTacToeGui(String name, char mark) throws RemoteException {
		myName = name;
		myMark = mark;

		// Create GUI components:
		// The display at the bottom:
		display = new TextArea("", 4, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		// The name field at the top:
		id = new JTextField();
		id.setEditable(false);
		id.setText(myName + ": You are player " + myMark);
		// The board:
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE, 0, 0));
		board = new Square[BOARD_SIZE][BOARD_SIZE];
		for(int row = 0; row < board.length; row++) 
			for(int col = 0; col < board[row].length; col++) {
				board[row][col] = new Square(this, row, col);
				gridPanel.add(board[row][col]);
			}
		boardPanel = new JPanel();
		boardPanel.add(gridPanel);

		// Place the components:
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add("South", display);
		cp.add("North", id);
		cp.add("Center", boardPanel);

		// Create the menu.
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(gameMenu);
		newGameItem = new JMenuItem("New game", KeyEvent.VK_N);
		newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		gameMenu.add(newGameItem);
		quitItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		gameMenu.add(quitItem);

		// Add listeners
		newGameItem.addActionListener(this);
		quitItem.addActionListener(this);
		// Add an anonymous WindowListener which calls quit() when the window is closing
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});

		// Place and show the window:
		setTitle("Tic Tac Toe: " + name);
		setSize(WIN_WIDTH, WIN_HEIGHT);
		setLocation(200, 200);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		/*
		 *  Try to create a client, and establish a two way connection. 
		 *	If there is no server in existence, the creation will fail and a server is created instead.
		 */
		clientConnected = false;
		client = new Client(this);
		if(clientConnected) {
			client.startConnection();
			myMark = 'O';
			id.setText(myName + ": You are player " + myMark);
		} else {
			println("Client creation failed, creating server.");
			server = new Server(this);
			server.startServer();
		}		
	}

	/**
	 * Called by the Square class when an empty square is clicked.
	 * @param row		The row of the square that was clicked.
	 * @param column	The column of the square that was clicked.
	 */
	public void squareClicked(int row, int column) {
		gameLogic.doPlayerMove(row, column, this, board);
	}

	/**
	 * Marks the specified square of the board with the specified mark.
	 * @param row		The row of the square to mark.
	 * @param column	The column of the square to mark.
	 * @param mark		The mark to use.
	 */
	public void setMark(int row, int column, char mark) {
		board[row][column].setMark(mark);
		repaint();
	}

	/**
	 * Called when a menu item has been selected.
	 * @param e	The ActionEvent that occured.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newGameItem)
			newGame();
		else if(e.getSource() == quitItem)
			quit();
	}

	/**
	 * Starts a new game, if the user confirms it in a dialog box.
	 * If the last game was not over, the opponent will get a message declaring your surrender	 
	 */
	public void newGame() {
		if(JOptionPane.showConfirmDialog(this, "Are you sure you want to start a new game?", "Start over?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if(!GameLogic.isWon(board)) println("Du har gitt opp.");		// Let the opponent know if you surrender a game that is not yet won by any of you.
			println("Du har utfordret motstenderen til et nytt spill.");
			clearBoard();
			gameLogic = new GameLogic('O');	// Create a new game. The challenger is always O.  
			id.setText(myName + ": You are player " + 'O');
			try {
				opponent.newGameRequest();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Handles a new game challenge by the opponent.
	 */
	public void newGameRequest() {
		if(!GameLogic.isWon(board)) println("Din motstander har gitt opp.");
		println("Du har blitt utfordret til et nytt spill.");
		clearBoard();
		gameLogic = new GameLogic('X'); // The challenged is always X
		id.setText(myName + ": You are player " + 'X');
	}
	
	/**
	 * Removes all marks from the board.
	 */
	public void clearBoard() {
		for(int row = 0; row < board.length; row++)
			for(int col = 0; col < board[row].length; col++)
				board[row][col].setMark(' ');
		repaint();
	}

	/**
	 * Exits the game, if the user confirms it in a dialog box.
	 * Should notify the opponent that we left the game.
	 */
	public void quit() {
		// This method should be modified!
		if(JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Really quit?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if(!GameLogic.isWon(board)){
				try {
					opponent.writeToConsole("Din motstander har gitt opp.");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			opponent.writeToConsole("Din motstander har avsluttet spillet.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Outputs a message to the user.
	 * @param s	The string to display. Adds a newline to the end of the string.
	 */
	public void println(String s) {
		display.append(s + "\n");
	}

	/**
	 * Outputs a message to the user.
	 * @param s	The string to display.
	 */
	public void print(String s) {
		display.append(s);
	}
	
	/**
	 * Paints a board specified.
	 */
	public void paintBoard(Square[][] board) {
		for(int i=0;i<Constants.BOARD_SIZE;i++) {
			for(int j=0;j<Constants.BOARD_SIZE;j++) {
				setMark(i, j, board[i][j].getMark());
			}
		}
	}
	
	/**
	 * Getter for the current gamelogic
	 */
	public GameLogic getGameLogic() {
		return gameLogic;
	}
	
	/**
	 * Setter for the current gameLogic
	 */
	
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
	}
	/**
	 * This method is called to set the opponent. This object allows for remotely methodcalls.
	 */
	
	public void setOpponent(Opponent opponent) {
		this.opponent = opponent;
	}
	
	/**
	 * Getter for the board.
	 */
	public Square[][] getBoard() {
		return board;
	}
	
	/**
	 * Starts up a GUI without an associated player, in order
	 * to check the appearance of the GUI.
	 * @throws RemoteException 
	 */
	public static void main(String args[]) throws RemoteException {
		TicTacToeGui hisGui = new TicTacToeGui("Rune & Dagrun", 'X');
	}
}
