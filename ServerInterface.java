import java.rmi.*;

public interface ServerInterface extends Remote{
	public void writeToConsole(String streng) throws RemoteException;
	public void connectToClient() throws RemoteException;
}
