import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements Opponent{
	
	TicTacToeGui gui;
	Opponent opponent;
	
	public Server(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
	}
	
	public void startServer() {
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		try {
			Naming.rebind("rmi://" + adresse + "/Server", this);
			gui.println("Serveren er registrert og venter p� hevendelser fra klienter.");
		}
		catch (ConnectException ce) {
		      System.err.println("Fant ikke RMI registry p� adressen "+adresse);
		}
		catch(Exception e){
			System.err.println("En feil oppsto i naming rebind: " + e.getMessage());
		}
	}
	
	public void connectToClient() throws RemoteException {
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		
		try {
		      String url = "rmi://"+ adresse + "/Client";
		      gui.println("Atempting to connect to client.");
		      opponent = (Opponent)Naming.lookup(url);
		      gui.setOpponent(opponent);
		      gui.println("Connection succesfull.");
		      opponent.writeToConsole("Dagrun er digg");
		      newGame();
		      opponent.newGame();
		}
		catch (NotBoundException nbe) {
		     System.err.println("Ingen Server er registrert!");
		} 
		catch (ConnectException ce) {
		     System.err.println("Fant ikke RMI registry p� adressen "+adresse);
		}
		catch(Exception e){
			System.err.println("En feil oppsto hos klient: " + e.getMessage());
		}
	}
	
	public void writeToConsole(String text) throws java.rmi.RemoteException{
		gui.println(text);
	}
	/*
	@Override
	public void setOpponent(Opponent client) {
		this.opponent = client;
	}

	@Override
	public Opponent getOpponent() {
		// TODO Auto-generated method stub
		return null;
	}*/

	@Override
	public void startConnection() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newGame() throws RemoteException {
		gui.setGameLogic(new GameLogic('X'));
	}

	@Override
	public void doOpponentMove(int row, int column) throws RemoteException {
		gui.getGameLogic().doOpponentMove(row, column, gui, gui.getBoard());
	}
	@Override
	public void newGameRequest() throws RemoteException {
		gui.newGameRequest();
	}
}
