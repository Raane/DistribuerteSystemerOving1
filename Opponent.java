import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Opponent extends Remote{
	/**
	 * These methods are explained in the server and client class, 
	 * as some of them have slightly different behaviour in the two classes.
	 */
	public void writeToConsole(String streng) throws RemoteException;
	public void startConnection() throws RemoteException;
	public void connectToClient() throws RemoteException;
	public void newGame() throws RemoteException;
	public void newGameRequest() throws RemoteException;
	public void doOpponentMove(int row,int column) throws RemoteException;
}
