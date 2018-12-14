import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface Node_RMI extends Remote {

	public void message(int idSender, HashMap<Integer, int[]> b, int[] t, List<Object> message) throws RemoteException;
	
	public void wakeup() throws RemoteException;
}
