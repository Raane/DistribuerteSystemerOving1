import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements Opponent{
	
	TicTacToeGui gui;
	Opponent opponent;
	
	public Client(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
		
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		
		try {
		      String url = "rmi://"+ adresse + "/Server";
		      gui.println("Atempting to find an existing server.");
		      opponent = (Opponent)Naming.lookup(url);
		      gui.println("An existing server was found.");
		      opponent.writeToConsole("Rune er digg");
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
	/*
	 * The client will run this to become ready to receive a connection from the server.
	 * It will also notify the server when it is ready.
	 */
	@Override
	public void startConnection() throws RemoteException{
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		try {
			Naming.rebind("rmi://" + adresse + "/Client", this);
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
	
	@Override
	public void writeToConsole(String text) throws RemoteException {
		gui.println(text);
	}
	/*@Override
	public void setOpponent(Opponent opponent) {
		// TODO Auto-generated method stub
	}
	@Override
	public Opponent getOpponent() {
		// TODO Auto-generated method stub
		return null;
	}*/
	@Override
	public void connectToClient() throws RemoteException {
	}
}
