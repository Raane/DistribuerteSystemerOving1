import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements Opponent{
	
	TicTacToeGui gui;
	Opponent opponent;
	
	/**
	 * Basic constructor for the server.
	 */
	public Server(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
	}
	
	/**
	 * Starting up the server and preparing to receive a connection.
	 */
	public void startServer() {
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		try {
			Naming.rebind("rmi://" + adresse + "/Server", this);
			gui.println("Serveren er registrert og venter på hevendelser fra klienter.");
		}
		catch (ConnectException ce) {
		      System.err.println("Fant ikke RMI registry på adressen "+adresse);
		}
		catch(Exception e){
			System.err.println("En feil oppsto i naming rebind: " + e.getMessage());
		}
	}
	
	/**
	 * When the client have connected to the server it will remotely call this method, making the server connect to the client.
	 */
	public void connectToClient() throws RemoteException {
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		
		try {
		      String url = "rmi://"+ Constants.IP + "/Client";
		      gui.println("Atempting to connect to client.");
		      opponent = (Opponent)Naming.lookup(url);
		      gui.setOpponent(opponent);
		      gui.println("Connection succesfull.");
		      newGame();
		      opponent.newGame();
		}
		catch (NotBoundException nbe) {
		     System.err.println("Ingen Server er registrert!");
		} 
		catch (ConnectException ce) {
		     System.err.println("Fant ikke RMI registry på adressen "+adresse);
		}
		catch(Exception e){
			System.err.println("En feil oppsto hos klient: " + e.getMessage());
		}
	}
	
	 /**
	  * A method to write to the console. It is even remotely callable.
	  */
	public void writeToConsole(String text) throws java.rmi.RemoteException{
		gui.println(text);
	}

	/**
	 * Dummy method, used in the Client and needed in the interface.
	 */
	@Override
	public void startConnection() throws RemoteException {
	}
	
	/**
	 * Reseting the game.
	 */
	@Override
	public void newGame() throws RemoteException {
		gui.setGameLogic(new GameLogic('X'));
	}
	
	/**
	 * Remotely callable method to enable the opponent to share what move he just did.
	 */
	@Override
	public void doOpponentMove(int row, int column) throws RemoteException {
		gui.getGameLogic().doOpponentMove(row, column, gui, gui.getBoard());
	}
	
	/**
	 * A remotelly callable method to accept a challenge for a new game.
	 */
	@Override
	public void newGameRequest() throws RemoteException {
		gui.newGameRequest();
	}
}
