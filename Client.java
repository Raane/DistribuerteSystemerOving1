import java.rmi.*;

public class Client {
	
	TicTacToeGui gui;
	
	public Client(TicTacToeGui gui) throws java.rmi.RemoteException{
		this.gui = gui;
		
		String adresse = "localhost:1337";
		System.setSecurityManager( new LiberalSecurityManager() );
		
		try {
		      String url = "rmi://"+ adresse + "/Server";
		      ServerInterface server = (ServerInterface)Naming.lookup(url);
		      //server.receiveClientObject(this);
		      server.writeToConsole("Rune er digg");
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

	/*public void writeToConsole(String text) {
		gui.println(text);
		
	}*/
}
