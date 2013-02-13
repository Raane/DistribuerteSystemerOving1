import java.rmi.*;

public interface ClientInterface extends Remote{
	public void writeToConsole(String streng) throws RemoteException;
	public void startClientConnection() throws RemoteException;
}
