
public class GameLogic {
	
	private char turn;
	public char getTurn() {
		return turn;
	}
	public void setTurn(char turn) {
		this.turn = turn;
	}
	public static boolean hasWon(Square[][] board) {
		return isWinner('X', board,null)||isWinner('O', board, null);
	}
	public static boolean isWinner(char player, Square[][] board, TicTacToeGui gui) {
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
					gui.println(i+((dir*2)-1)*j+","+j);
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
}
