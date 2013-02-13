import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Opponent extends Remote{
	public void writeToConsole(String streng) throws RemoteException;
	public void startConnection() throws RemoteException;
	public void connectToClient() throws RemoteException;
}
