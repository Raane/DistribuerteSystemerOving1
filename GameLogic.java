import java.rmi.RemoteException;


public class GameLogic {
	
	private char turn;
	private char player;
	
	/**
	 * When a new game is created we make a new GameLogic.
	 */
	public GameLogic(char player) {
		this.player = player;
		setTurn('X');				// By default it's Xs turn at the start of a game.
	}
	
	/**
	 * Switches to the next players turn.
	 */
	public void nextTurn() {
		if(turn=='X') {
			turn = 'O';
		} else {
			turn = 'X';
		}
	}
	
	/**
	 * Setter for the turn
	 */
	public void setTurn(char turn) {
		this.turn = turn;
	}
	
	/**
	 * Checks if there is a winner.
	 */
	public static boolean isWon(Square[][] board) {
		return isWinner('X', board)||isWinner('O', board);
	}
	
	/**
	 * Checks if a player is the winner. 
	 */
	public static boolean isWinner(char player, Square[][] board) {
		for(int i=0; i<Constants.BOARD_SIZE; i++) {
			int counter = 0;
			for(int j=0; j<Constants.BOARD_SIZE; j++) {
				if(board[i][j].getMark()==player) {
					counter++;
				} else {
					counter=0;
				}
				if (counter>=Constants.WINNER_LENGTH) return true;
			}
		}
		for(int i=0; i<Constants.BOARD_SIZE; i++) {
			int counter = 0;
			for(int j=0; j<Constants.BOARD_SIZE; j++) {
				if(board[j][i].getMark()==player) {
					counter++;
				} else {
					counter=0;
				}
				if (counter>=Constants.WINNER_LENGTH) return true;
			}
		}
		for(int i=-Constants.BOARD_SIZE;i<Constants.BOARD_SIZE*2;i++) {
			for(int dir=0;dir<2;dir++) {
				int counter = 0;
				for(int j=0;j<Constants.BOARD_SIZE;j++) {
					if(i+((dir*2)-1)*j>=0 && i+((dir*2)-1)*j<Constants.BOARD_SIZE) {
						if(board[i+((dir*2)-1)*j][j].getMark()==player) {
							counter++;
						} else {
							counter=0;	
						}
						if(counter==Constants.WINNER_LENGTH) return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Does a move for the player.
	 */
	public void doPlayerMove(int row, int column, TicTacToeGui gui, Square[][] board) {
		if(isLegalMove(row,column,board)) {
			doMove(row, column, gui, player, board);
			try {
				gui.opponent.doOpponentMove(row, column);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			if(board[row][column].getMark()!=' ') gui.println("Den valgte ruten er ikke ledig.");
			if(turn!=player && turn!=' ') gui.println("Det er ikke din tur.");
		}
	}
	
	/**
	 * Does a move for the opponent.
	 */
	public void doOpponentMove(int row, int column, TicTacToeGui gui, Square[][] board) {
		doMove(row, column, gui, turn, board);
	}

	/**
	 * Does the move in logic and graphics.
	 */
	private void doMove(int row, int column, TicTacToeGui gui, char player, Square[][] board) {
		board[row][column].setMark(player);
		nextTurn();
		gui.paintBoard(board);
		if(isWon(board)) {
			turn = ' ';
			gui.println(Constants.WINNER_LENGTH + " in a row! Game over!");
		} else {
			gui.println("It is now " + turn + "s turn.");
		}
	}
	
	/**
	 * Checks if a move is legal in the current game state.
	 */
	private boolean isLegalMove(int row, int column, Square[][] board) {
		return board[row][column].getMark()==' ' && player==turn; 
	}
}
