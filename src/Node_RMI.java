import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node_RMI extends Remote {

	public void connect(int idSender ,int L) throws RemoteException;
	
	public void initiate(int idSender, int L, String F, String S) throws RemoteException;
	
	public void test(int idSender, int L, String F) throws RemoteException;
	
	public void reject(int idSender) throws RemoteException;
	
	public void accept(int idSender) throws RemoteException;
	
	public void report(int idSender, int W) throws RemoteException;
	
	public void change_root() throws RemoteException;

	
	
	
}
