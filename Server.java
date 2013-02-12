import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject{
	
	TicTacToeGui gui;
	
	public Server(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
	}
	
	public void startServer() {
		String adresse = "localhost:1099";
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
	
	public void writeToConsole(String text) throws java.rmi.RemoteException{
		gui.println(text);
	}
	
}
