import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements Opponent{
	
	TicTacToeGui gui;
	Opponent opponent;
	
	/**
	 * A constructor setting up the client and starting a new connection to the server.
	 */
	public Client(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
		
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		
		try {
		      String url = "rmi://"+ adresse + "/Server";
		      gui.println("Atempting to find an existing server.");
		      opponent = (Opponent)Naming.lookup(url);
		      gui.setOpponent(opponent);
		      gui.println("An existing server was found.");
		      gui.clientConnected = true;
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
	 * The client will run this to become ready to receive a connection from the server.
	 * It will also notify the server when it is ready.
	 */
	@Override
	public void startConnection() throws RemoteException{
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		try {
			Naming.rebind("rmi://" + Constants.IP + "/Client", this);
			gui.println("Clienten er registrert og venter på hevendelser fra klienter.");
		}
		catch (ConnectException ce) {
		      System.err.println("Fant ikke RMI registry på adressen "+adresse);
		}
		catch(Exception e){
			System.err.println("En feil oppsto i naming rebind: " + e.getMessage());
		}
		opponent.connectToClient();
	}
	
	/**
	  * A method to write to the console. It is even remotely callable.
	  */
	@Override
	public void writeToConsole(String text) throws RemoteException {
		gui.println(text);
	}
	
	/**
	 * Dummy method, used in the Server and needed in the interface.
	 */
	@Override
	public void connectToClient() throws RemoteException {
	}
	
	/**
	 * Reseting the game.
	 */
	@Override
	public void newGame() throws RemoteException {
		gui.setGameLogic(new GameLogic('O'));
	}
	/**
	 * Remotely callable method to enable the opponent to share what move he just did.
	 */
	@Override
	public void doOpponentMove(int row,int column) throws RemoteException {
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
