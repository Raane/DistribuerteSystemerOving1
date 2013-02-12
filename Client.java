import java.rmi.*;

public class Client {
	
	TicTacToeGui gui;
	
	public Client(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
		
		String adresse = "localhost:1099";
		System.setSecurityManager( new LiberalSecurityManager() );
		
		try {
		      String url = "rmi://"+ adresse + "/Server";
		      Server server = (Server)Naming.lookup(url);
		      server.writeToConsole("Rune er digg");
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
	

}
